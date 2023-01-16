package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class ConceptoModel {

    public static final String NOMBRETABLA="conceptos";
    public static final String CODIGO ="codigo";
    public static final String NOMBRE ="nombre";
    public static final String VALTOT ="total";
    public static final String PORIVA ="poriva";
    public static final String VALIVA ="iva";
    public static final String[] COL_TODAS={CODIGO,NOMBRE,VALTOT,PORIVA,VALIVA};
    public static final String WHERE_CODIGO = CODIGO +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String codigo;
    private  String nombre;
    private  String valTot;
    private  String porcentajeIva;
    private  String valIva;

    public ConceptoModel() {
    }

    public ConceptoModel(String codigo, String nombre, String valTot, String porcentajeIva, String valIva) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.valTot = valTot;
        this.porcentajeIva = porcentajeIva;
        this.valIva = valIva;
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

    public String getValTot() {
        return valTot;
    }

    public void setValTot(String valTot) {
        this.valTot = valTot;
    }

    public String getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(String porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public String getValIva() {
        return valIva;
    }

    public void setValIva(String valIva) {
        this.valIva = valIva;
    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(5);

        contentValues.put(CODIGO,codigo);
        contentValues.put(NOMBRE,nombre);
        contentValues.put(VALTOT,valTot);
        contentValues.put(PORIVA,porcentajeIva);
            contentValues.put(VALIVA,valIva);
        return contentValues;
    }

}
