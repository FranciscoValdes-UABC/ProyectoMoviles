package com.example.proyectomoviles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class LecturesAdapter extends ArrayAdapter<Lectures> {
    public LecturesAdapter(Context context, int resource, List<Lectures> lecturesList){
        super(context, resource, lecturesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lectures lectures = getItem(position);

        //Se verifica si el View en este caso llamado convertView es nulo
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lecture_cell, parent, false);
        }
        //De no serlo se crean las variables inicializadas con el elemento del xml para el elemento de la lista
        TextView idTextView = (TextView) convertView.findViewById(R.id.idTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView nfcTextView = (TextView) convertView.findViewById(R.id.nfcTextView);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);

        //Se le asignan los valores solicitados al elemento de la lista a desplegar
        idTextView.setText(lectures.getUserId());
        nameTextView.setText(lectures.getName());
        nfcTextView.setText(lectures.getNFC());
        dateTextView.setText(lectures.getDate());

        return convertView;
    }
}
