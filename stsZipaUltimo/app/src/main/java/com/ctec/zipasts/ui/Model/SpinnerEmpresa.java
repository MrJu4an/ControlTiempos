package com.ctec.zipasts.ui.Model;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

public class SpinnerEmpresa extends ArrayAdapter<EmpresaModel> {

    private final EmpresaModel[] values;

    public SpinnerEmpresa(@NonNull Context context, int resource, @NonNull EmpresaModel[] values) {
        super(context, resource, values);
        this.values = values;
    }

    public int getCount() {
        return this.values.length;
    }

    public EmpresaModel getItem(int position) {
        return this.values[position];
    }

    public long getItemId(int position) {
        return (long) position;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.WHITE);
        label.setText(this.values[position].getNombre());
        return label;
    }

    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(ViewCompat.MEASURED_STATE_MASK);
        label.setText(this.values[position].getNombre());
        return label;
    }
}
