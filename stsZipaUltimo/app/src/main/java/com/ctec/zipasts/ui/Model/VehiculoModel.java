package com.ctec.zipasts.ui.Model;


import android.content.ContentValues;

public class VehiculoModel {

    private String codEmpresa;
    private String numInterno;
    private String placa;
    private String estado;


    public VehiculoModel() {
    }

    public VehiculoModel(String codEmpresa, String numInterno, String placa, String estado) {
        this.codEmpresa = codEmpresa;
        this.numInterno = numInterno;
        this.placa = placa;
        this.estado = estado;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getNumInterno() {
        return numInterno;
    }

    public void setNumInterno(String numInterno) {
        this.numInterno = numInterno;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(4);

        contentValues.put(COL_PLACA,placa);
        contentValues.put(COL_INTERNO,numInterno);
        contentValues.put(COL_CODIGO,codEmpresa);
        contentValues.put(COL_ESTADO,estado);

        return contentValues;
    }


//region DATABASE

    public static final String NOMBRETABLA="vehiculos";

    public static final String COL_PLACA ="placa";
    public static final String COL_INTERNO ="numInterno";
    public static final String COL_CODIGO ="codEmpresa";
    public static final String COL_ESTADO ="estado";

    public static final String[] COL_TODAS = { COL_PLACA,COL_INTERNO,COL_CODIGO, COL_ESTADO,
            };
    public static final String WHERE_CODIGO = COL_CODIGO +"=?";
    public static final String WHERE_INTERNO = COL_INTERNO +"=?";
    public static final String WHERE_PLACA = COL_PLACA + "=?";

    /*public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            COL_ID +" integer primary key autoincrement, "+
            COL_CODIGO + " text not null," +
            COL_INTERNO +" text not null, "+
            COL_PLACA + " text not null," +
            COL_ESTADO + " text not null," +
            COL_PUESTOS +" text not null);";*/
    public static final String CREARTABLA= "create table vehiculos("+COL_INTERNO+" text primary key,"+COL_CODIGO+" text,"+COL_PLACA+"  text, ,"+COL_ESTADO+"  text)";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;
    public static final String DELETE_SEQUENCE = "DELETE FROM sqlite_sequence where name='" +NOMBRETABLA +"'";

//endregion
}