package com.example.proyectomoviles;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    //Se crean las variables a utilizar con su tipo correspondiente
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Se le asigna el tema por defecto una ves se inicializó la aplicación, esto para quitar el tema de la splashscreen
        setTheme(R.style.Theme_ProyectoMoviles);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se inicializa el tabLayout y el viewPager donde se mostrarán los fragmentos
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager);

        //Se crea un FragmentManaget y se crea el adapter necesario para asignarlo al viewPager,
        //esto para que muestre el fragmento correcto
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());
        viewPager2.setAdapter(adapter);

        //Se crea y se le asigna el nombre a cada Tab en la aplicación
        tabLayout.addTab(tabLayout.newTab().setText(R.string.UploadTabString));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.ListTabtString));

        //Se crea un listener para verificar cuando se selecciona una Tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            //Al momento de seleccionar una Tab el viewPager asigna el fragmento indicado
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Se obtiene la posición en la que se encuentra el viewPager y se le asigna a la Tab indicada
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    //Se crea el menu de opciones, en este caso sería el Acerca de
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Se verfica si se presionó el botón de acerca de, de ser así se inicia dicho activity
        switch (item.getItemId()){
            case R.id.action_about:
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}