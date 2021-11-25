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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        setupData(v);
        setupList(v);
        setupOnClickListener();

        setupSpinner(v);
        setupSearch(v);

        return v;
    }

    private void setupData(View v){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference lecturasDB = database.getReference("Lecturas");
        DatabaseReference usuariosDB = database.getReference("Usuarios");

        final int[] count = {0};

        lecturasDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //createList((Map<String,Object>) snapshot.getValue());
                for(DataSnapshot ds : snapshot.getChildren()){
                    String NFC = ds.child("NFC").getValue(String.class);
                    String Date = ds.child("Fecha").getValue(String.class);

                    Query userInfo = usuariosDB.orderByChild("NFC").equalTo(NFC);
                    userInfo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds2 : snapshot.getChildren()) {
                                String userId = ds2.getKey();
                                String Name = ds2.child("Nombre").getValue(String.class);
                                String Url = ds2.child("URL").getValue(String.class);
                                lecturesList.add(new Lectures(count[0], userId, NFC, Date, Name, Url));

                                count[0] = count[0] +1;
                            }
                            setupList(v);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //lecturesList.add(new Lectures(0, userId, NFC, Date, "Francisco Daniel Valdes Escarrega", R.drawable.io));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //lecturesList.add(new Lectures(0, 1, "aaaaa", "Wed Oct 20 20:03:41 2021", "Francisco Daniel Valdes Escarrega", R.drawable.io));
    }

    private void setupList(View v){
        listView = (ListView) v.findViewById(R.id.lecturesListView);

        LecturesAdapter adapter = new LecturesAdapter(getContext(), 0, lecturesList);
        listView.setAdapter(adapter);
    }

    private void setupOnClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lectures selectedLecture = (Lectures) (listView.getItemAtPosition(position));
                Intent showDetail = new Intent(getContext(), LectureDetailActivity.class);
                showDetail.putExtra("id", selectedLecture.getId());
                startActivity(showDetail);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void setupSearch(View v){
        searchView = (SearchView) v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Lectures> filteredLectures = new ArrayList<Lectures>();
                for(Lectures lecture: lecturesList){
                    switch(spinner.getSelectedItem().toString()){
                        case "ID":
                                if(String.valueOf(lecture.getUserId()).contains(newText)){
                                    filteredLectures.add(lecture);
                                }
                            break;
                        case "Name":
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
                            if(lecture.getDate().toLowerCase().contains(newText.toLowerCase())){
                                filteredLectures.add(lecture);
                            }
                            break;
                    }
                }

                LecturesAdapter adapter = new LecturesAdapter(getContext(), 0, filteredLectures);
                listView.setAdapter(adapter);
                return false;
            }
        });
    }

    private void setupSpinner(View v){
        spinner = (Spinner) v.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}