package com.example.proyectomoviles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class LectureDetailActivity extends AppCompatActivity {
    Lectures selectedLecture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        //Se crea el botón de "Back" dentro del ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Se maanda a llamar el método que obtiene la instancia del elemento, si se hace de manera exitose se le asignan los valores
        getSelectedLecture();
        try {
            setValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSelectedLecture(){
        //Se obtiene una instancia del elemento de la lista que se seleccionó, esto dentro de la lista de lecturas
        Intent previousIntent = getIntent();
        int parsedID = previousIntent.getIntExtra("id", 0);
        selectedLecture = ListFragment.lecturesList.get(Integer.valueOf(parsedID));
    }

    private void setValues() throws IOException {
        //Se inicializan las variables creadas anteriormente con el widget dentro del XML
        TextView idTextView = (TextView) findViewById(R.id.idTextView2);
        TextView nameTextView = (TextView) findViewById(R.id.nameTextView2);
        TextView nfcTextView = (TextView) findViewById(R.id.nfcTextView2);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView2);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //Se le asignan los valores necesarios a los TextView y la imagen a partir del elemento de la lista obtenido anteriormente
        idTextView.setText(String.valueOf(selectedLecture.getUserId()));
        nameTextView.setText(selectedLecture.getName());
        nfcTextView.setText(selectedLecture.getNFC());
        dateTextView.setText(selectedLecture.getDate());
        //Se utilizó picasso por el hecho de que el método getImage() devuelve un URL en donde se encuentra la imágen dentro de Firebase
        Picasso.get().load(selectedLecture.getImage()).into(imageView);
    }

    @Override
    public void finish() {
        super.finish();
        //Al finalizar la activity se realiza una animación de salida
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Se crea el menu de opciones, con el xml llamado "main" dentro de la carpeta de menú
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Se realiza un switch case para verificar que botón se seleccionó dentro de la actionBar
        switch (item.getItemId()){
            case R.id.action_about:
                //En caso de ser el acerca de se empieza dicha actividad
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                //En caso de ser back se finaliza la activity
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}