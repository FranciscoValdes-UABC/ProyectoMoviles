package com.example.proyectomoviles;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

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
        setupData();
        setupList(v);
        setupOnClickListener();

        setupSpinner(v);
        setupSearch(v);

        return v;
    }

    private void setupData(){
        Lectures lecture = new Lectures(0, 1, "A379B218", "Wed Oct 20 20:03:41 2021", "Francisco Daniel Valdes Escarrega", R.drawable.io);
        lecturesList.add(lecture);
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