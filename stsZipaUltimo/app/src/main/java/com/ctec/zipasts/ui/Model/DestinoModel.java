package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class DestinoModel {

    public static final String NOMBRETABLA="destinos";
    public static final String CODIGO ="codigo";
    public static final String NOMBRE ="nombre";
    public static final String ESTADO ="estado";
    public static final String[] COL_TODAS = { CODIGO,NOMBRE,ESTADO
    };
    public static final String WHERE_CODIGO = CODIGO +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;
    public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            ESTADO +" text not null );";

    private  String codigo;
    private  String nombre;
    private  String estado;


    public DestinoModel() {
    }

    public DestinoModel(String codigo, String nombre, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }



    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(3);

        contentValues.put(CODIGO,codigo);
        contentValues.put(NOMBRE,nombre);
        contentValues.put(ESTADO,estado);
        // contentValues.put(COL_ESTADO,estado);
        // contentValues.put(COL_PUESTOS,cantPuestos);

        return contentValues;
    }
}
