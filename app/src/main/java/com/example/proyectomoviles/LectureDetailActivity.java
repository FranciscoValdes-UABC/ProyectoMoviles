package com.example.proyectomoviles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LectureDetailActivity extends AppCompatActivity {
    Lectures selectedLecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);

        getSelectedLecture();
        setValues();
    }

    private void getSelectedLecture(){
        Intent previousIntent = getIntent();
        int parsedID = previousIntent.getIntExtra("id", 0);
        selectedLecture = ListFragment.lecturesList.get(Integer.valueOf(parsedID));
    }

    private void setValues(){
        TextView idTextView = (TextView) findViewById(R.id.idTextView2);
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView2);
        TextView nfcTextView = (TextView) findViewById(R.id.nfcTextView2);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView2);

        idTextView.setText(String.valueOf(selectedLecture.getUserId()));
        nameTextView.setText(selectedLecture.getName());
        nfcTextView.setText(selectedLecture.getNFC());
        dateTextView.setText(selectedLecture.getDate());
    }
}