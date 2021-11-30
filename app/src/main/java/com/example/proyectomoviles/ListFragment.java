package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    public static ArrayList<Lectures> lecturesList = new ArrayList<Lectures>();
    private SearchView searchView;
    private ListView listView;
    private Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Se realiza el Inflate para el Layout de este fragmento
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        //Se mandan a llamar todos los métodos creados para crear la lista y los elementos de el fragment
        //---1 Métodos para inicializar o utilizar la lista
        setupData(v);
        setupList(v);
        setupOnClickListener();
        //---1
        //---2 Métodos para la busqueda
        setupSpinner(v);
        setupSearch(v);
        //---2

        return v;
    }

    private void setupData(View v){
        //Se obtiene la instancia de la base de datos y posteriormente una a la seccion de lectures y otra a la de usuarios
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference lecturasDB = database.getReference("Lecturas");
        DatabaseReference usuariosDB = database.getReference("Usuarios");

        //Se crea una variable de conteo
        final int[] count = {0};

        lecturasDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    //Se obtienen los valores de la DataSnapshot en las variables NFC y Date
                    String NFC = ds.child("NFC").getValue(String.class);
                    String Date = ds.child("Fecha").getValue(String.class);

                    //Posteriormente se realiza una busqueda del NFC en la base de datos de los usuarios
                    //esto debido a que cada usuario solamente tiene un NFC, por lo tanto se encontrará el usuario que realizó la lectura
                    Query userInfo = usuariosDB.orderByChild("NFC").equalTo(NFC);
                    //Se crea un EventListener para obtener toda la información del usuario encontrado a partir del NFC de la lectura
                    userInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds2 : snapshot.getChildren()) {
                                //Se guarda el identificador único del usuario en userId
                                String userId = ds2.getKey();
                                //Se guarda en Name el nombre del usuario y en Url la dirección a donde se encuentra la imagen del usuario
                                String Name = ds2.child("Nombre").getValue(String.class);
                                String Url = ds2.child("URL").getValue(String.class);
                                //Una ves obtenidos los datos anteriores se añade a la lista una nueva observación con los valores obtenidos
                                //y utilizando la variable count como id de el respectivo elemento de la lista.
                                //Cabe restaltar que es diferente el ID de la lista al ID del usuario
                                lecturesList.add(new Lectures(count[0], userId, NFC, Date, Name, Url));

                                count[0] = count[0] +1;
                            }
                            //Una ves obtenidos los datos se llama al método setupList
                            setupList(v);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setupList(View v){
        //Se le asigna la lista creada al listView, esto utilizando el Adapter personalizado que se creó
        listView = (ListView) v.findViewById(R.id.lecturesListView);

        LecturesAdapter adapter = new LecturesAdapter(getContext(), 0, lecturesList);
        listView.setAdapter(adapter);
    }

    //Si es que se selecciona un elemento de la lista se realiza este método
    private void setupOnClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //se obtiene la referencia a dicho elemento de la lista
                Lectures selectedLecture = (Lectures) (listView.getItemAtPosition(position));
                //Se crea un instent para mandar a llamar a la activity donde se muestra con detalle los elementos de la lectura
                Intent showDetail = new Intent(getContext(), LectureDetailActivity.class);
                //Se le añade al intent el ID de la lista, para que obtenga la referencia
                showDetail.putExtra("id", selectedLecture.getId());
                //Se inicia el activity y se realiza una animación de deslizamiento a la izquierda
                startActivity(showDetail);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void setupSearch(View v){
        //Se hace la inicialización del searchView
        searchView = (SearchView) v.findViewById(R.id.searchView);
        //Se crea un Listener para la busqueda y se utiliza cuando se cambia el texto
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Se crea una nueva lista donde se guardarán los resultados de la busqueda que se indicó, este texto se tiene en la variable newText
                ArrayList<Lectures> filteredLectures = new ArrayList<Lectures>();
                //Para todas las lecturas dentro de la lista de lecturas se realiza un switch para verificar en donde está situado el spínner
                for(Lectures lecture: lecturesList){
                    switch(spinner.getSelectedItem().toString()){
                        case "ID":
                                //En caso de filtrar por ID se obtienen los datos donde el ID sea igual al texto indicado
                                if(String.valueOf(lecture.getUserId()).contains(newText)){
                                    filteredLectures.add(lecture);
                                }
                            break;
                        case "Name":
                        case "Nombre":
                                //En caso de filtrar por Nombre o Name (dependiendo del idioma) se obtienen los datos donde el nombre sea igual al texto indicado
                                if(lecture.getName().toLowerCase().contains(newText.toLowerCase())){
                                    filteredLectures.add(lecture);
                                }
                            break;
                        case "NFC":
                            if(lecture.getNFC().toLowerCase().contains(newText.toLowerCase())){
                                filteredLectures.add(lecture);
                            }
                            break;
                        case "Date":
                        case "Fecha":
                            //En caso de filtrar por Fecha o Date (dependiendo del idioma) se obtienen los datos donde la fecha sea igual al texto indicado
                            if(lecture.getDate().toLowerCase().contains(newText.toLowerCase())){
                                filteredLectures.add(lecture);
                            }
                            break;
                    }
                }

                //Al finalizar la busqueda se le asigna al listView el nuevo adapter que tiene los elementos del arreglo "filteredLectures"
                //por lo tanto solo se mueistran los resultados
                LecturesAdapter adapter = new LecturesAdapter(getContext(), 0, filteredLectures);
                listView.setAdapter(adapter);
                return false;
            }
        });
    }

    private void setupSpinner(View v){
        //Para inicializar el spinner primeramente se inicializa la variable creada anteriormente
        spinner = (Spinner) v.findViewById(R.id.spinner);
        //Para mostrar las opciones se creó un xml llamado "options_array", y se crea un adapter en base a este xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        //Se le asigna al adapter el tipo de dropwon item, por lo tanto se crea un elemento de una lista dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Se le asigna el adapter al spinner
        spinner.setAdapter(adapter);
    }
}