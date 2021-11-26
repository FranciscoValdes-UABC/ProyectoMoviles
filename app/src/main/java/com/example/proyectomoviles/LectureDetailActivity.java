package com.example.proyectomoviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class LectureDetailActivity extends AppCompatActivity {
    Lectures selectedLecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSelectedLecture();
        try {
            setValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSelectedLecture(){
        Intent previousIntent = getIntent();
        int parsedID = previousIntent.getIntExtra("id", 0);
        selectedLecture = ListFragment.lecturesList.get(Integer.valueOf(parsedID));
    }

    private void setValues() throws IOException {
        TextView idTextView = (TextView) findViewById(R.id.idTextView2);
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView2);
        TextView nfcTextView = (TextView) findViewById(R.id.nfcTextView2);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView2);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        idTextView.setText(String.valueOf(selectedLecture.getUserId()));
        nameTextView.setText(selectedLecture.getName());
        nfcTextView.setText(selectedLecture.getNFC());
        dateTextView.setText(selectedLecture.getDate());
        Picasso.get().load(selectedLecture.getImage()).into(imageView);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}