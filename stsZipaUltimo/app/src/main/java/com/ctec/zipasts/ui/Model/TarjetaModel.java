package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class TarjetaModel {
    public static final String NOMBRETABLA = "tarjetas";
    public static final String PLACA = "placa";
    public static final String ID = "id";
    public static final String ESTADO = "estado";
    public static final String TIPO = "tipo";
    public static final String[] COL_TODAS = { PLACA,ID,ESTADO, TIPO,
    };
    public static final String WHERE_PLACA = PLACA +"=?";
    public static final String WHERE_ID = ID +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String placa;
    private  String id;
    private  String estado;
    private  String tipo;

    public TarjetaModel() {
    }

    public TarjetaModel(String placa, String id, String estado, String tipo) {
        this.placa = placa;
        this.id = id;
        this.estado = estado;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }



    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(4);

        contentValues.put(ID,id);
        contentValues.put(ESTADO,estado);
        contentValues.put(TIPO,tipo);
        contentValues.put(PLACA,placa);

        return contentValues;
    }


}
