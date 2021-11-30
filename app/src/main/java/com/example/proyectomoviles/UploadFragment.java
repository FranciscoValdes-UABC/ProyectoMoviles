package com.example.proyectomoviles;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadFragment extends Fragment {

    //Se crean las variables que se le asignarán a los widgets
    Button selectButton;
    ImageView imageView;
    FloatingActionButton fab;
    EditText personNameEditText, nfcEditText;

    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance() {
        UploadFragment fragment = new UploadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Se obtiene el resultado si es que se seleccionó una imagen al dar click en el botón de seleccionar imagen
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    //Se le asigna el uri de la imagen obtenida al imageView
                    imageView.setImageURI(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Se realiza el Inflate al layour de este fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        //Se inicializan las variables anteriormente declaradas, con los widget del xml
        imageView = (ImageView) v.findViewById(R.id.imageView2);
        selectButton = (Button) v.findViewById(R.id.selectButton);
        fab = (FloatingActionButton) v.findViewById(R.id.fabUpload);
        personNameEditText = (EditText) v.findViewById(R.id.personNameEditText);
        nfcEditText = (EditText) v.findViewById(R.id.nfcEditText);

        //Se crea una instancia para el realTimeDatabase y para Storage de Firebase
        //Dentro de database se guardarán todos los datos numericos y cadenas de caracteres
        //En Storage se guardarán las imagenes de los usuarios
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Se realiza la operación de selección de imagen, para esto se obtiene del telefono del usuario
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        //Si se presiona el FAB se desea subir lo ingresado a Firebase
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se verifica si es se ha seleccionado una imagen para el usuario
                if(imageView.getDrawable() != null) {
                    //Se verifica si cualquiera de los dos EditText es vacío, de serlo se marca dicho error
                    if(personNameEditText.getText().toString().matches("") || nfcEditText.getText().toString().matches("")){
                        Toast.makeText(getContext(), "Alguno de los campos se encuentra vacío", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Se obtiene una referencia para ámbas bases de datos en su rama definida
                        //En el caso de storage se creará la referencia a donde se desea la imagen en un folder "Usuarios" bajo el nombre del usuario se le indicó
                        StorageReference storageRef = storage.getReference("Usuarios/" + personNameEditText.getText().toString());
                        //En el caso de RealtimeDatabase se obtiene la referencia al nodo de Usuarios
                        DatabaseReference myRef = database.getReference("Usuarios");

                        //Se obtiene la imagen guardandola en un mapa de bits, posteriormente se comprime en formato JPEG y se pasa el mapa de bits a un Arreglo de bits
                        //Esto con el fin de subirlo a Firebase
                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        //Se crea una tarea, en este caso se realizará la subida de datos a Firebase
                        UploadTask uploadTask = storageRef.putBytes(data);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                //Si la tarea no fue exitosa se manda el error correspondiente
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                //De ser correcta se obtiene el URL donde se guardó la imagen, para posteriormente guardarlo como cadena de texto
                                return storageRef.getDownloadUrl();
                            }
                            //Cuando se sube la imagen correctamente se continua la tarea, pero ahora se guardará en RealtimeDatabase
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //Se verifica si la tarea fue exitosa
                                if (task.isSuccessful()) {
                                    //Se obtiene el Uri donde se tiene el link de la imagen
                                    Uri downloadUri = task.getResult();
                                    //Se realiza un mapeo para subir todos los datos en un solo push de Firebase
                                    Map<String, Object> taskMap = new HashMap<>();
                                    //Se le asignan los valores a querer guardar bajo el nombre que se le colocó en un inicio
                                    //Esto se realiza con el método put
                                    taskMap.put("URL", downloadUri.toString());
                                    taskMap.put("Nombre", personNameEditText.getText().toString());
                                    taskMap.put("NFC", nfcEditText.getText().toString());

                                    //Se realiza un push(), esto debido a que es una entrada nueva y no una edición
                                    //Al hacer push se crea el usuario en una nueva entrada de la base de datos
                                    myRef.push().updateChildren(taskMap);
                                    Log.i(TAG, "Link: " + downloadUri.toString());
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }
                }
                //De no tener una imagen seleccionada se muestra dicho error
                else{
                    Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

}