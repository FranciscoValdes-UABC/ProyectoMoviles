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

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lecture_cell, parent, false);
        }
        TextView idTextView = (TextView) convertView.findViewById(R.id.idTextView);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        TextView nfcTextView = (TextView) convertView.findViewById(R.id.nfcTextView);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);

        idTextView.setText(String.valueOf(lectures.getUserId()));
        nameTextView.setText(lectures.getName());
        nfcTextView.setText(lectures.getNFC());
        dateTextView.setText(lectures.getDate());

        return convertView;
    }
}
