package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class PuntoVentaModel {
    public static final String NOMBRETABLA="puntoventa";
    public static final String COD_PUNTO ="codPunto";
    public static final String COD_CIUDAD ="codCiudad";
    public static final String NOMBRE ="nombre";
    public static final String PUERTO ="puerto";
    public static final String COD_AGE_ASO ="codAgenAso";

    public static final String WHERE_COD_PUNTO = COD_PUNTO +"=?";

    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String codPunto;
    private  String codCiudad;
    private  String nombre;
    private  String puerto;
    private String codAgenAso;

    public PuntoVentaModel() {
    }

    public PuntoVentaModel(String codPunto, String codCiudad, String nombre, String puerto, String codAgenAso) {
        this.codPunto = codPunto;
        this.codCiudad = codCiudad;
        this.nombre = nombre;
        this.puerto = puerto;
        this.codAgenAso = codAgenAso;
    }

    public String getCodAgenAso() {
        return codAgenAso;
    }

    public void setCodAgenAso(String codAgenAso) {
        this.codAgenAso = codAgenAso;
    }

    public String getcodpunto() {
        return codPunto;
    }

    public void setcodpunto(String codPunto) {
        this.codPunto = codPunto;
    }

    public String getcodciudad() {
        return codCiudad;
    }

    public void setcodciudad(String codCiudad) {
        this.codCiudad = codCiudad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(5);

        contentValues.put(COD_PUNTO,codPunto);
        contentValues.put(COD_CIUDAD,codCiudad);
        contentValues.put(NOMBRE,nombre);
        contentValues.put(PUERTO,puerto);
        contentValues.put(COD_AGE_ASO,codAgenAso);

         return contentValues;
    }


}
