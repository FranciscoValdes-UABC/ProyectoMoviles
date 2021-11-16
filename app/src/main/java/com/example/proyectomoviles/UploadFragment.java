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

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    imageView.setImageURI(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload, container, false);

        imageView = (ImageView) v.findViewById(R.id.imageView2);
        selectButton = (Button) v.findViewById(R.id.selectButton);
        fab = (FloatingActionButton) v.findViewById(R.id.fabUpload);
        personNameEditText = (EditText) v.findViewById(R.id.personNameEditText);
        nfcEditText = (EditText) v.findViewById(R.id.nfcEditText);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getDrawable() != null) {
                    if(personNameEditText.getText().toString().matches("") || nfcEditText.getText().toString().matches("")){
                        Toast.makeText(getContext(), "Alguno de los campos se encuentra vacío", Toast.LENGTH_LONG).show();
                    }
                    else {
                        // Write a message to the database
                        StorageReference storageRef = storage.getReference("Usuarios/" + personNameEditText.getText().toString());
                        DatabaseReference myRef = database.getReference("Usuarios");

                        imageView.setDrawingCacheEnabled(true);
                        imageView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.putBytes(data);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Map<String, Object> taskMap = new HashMap<>();
                                    taskMap.put("URL", downloadUri.toString());
                                    taskMap.put("Nombre", personNameEditText.getText().toString());
                                    taskMap.put("NFC", nfcEditText.getText().toString());

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
                else{
                    Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

}