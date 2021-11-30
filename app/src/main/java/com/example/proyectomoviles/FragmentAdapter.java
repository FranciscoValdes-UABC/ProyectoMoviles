package com.example.proyectomoviles;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        //Se realiza este switch para que al momento de seleccionar el tab con posición 1 se obtiene el fragmento donde está la lista
        switch (position){
            case 1:
                return new ListFragment();
        }
        //De cualquier otra forma se regresa el fragmento donde se crea el usuario
        return new UploadFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
