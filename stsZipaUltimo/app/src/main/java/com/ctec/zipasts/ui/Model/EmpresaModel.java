package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class EmpresaModel {

    public static final String NOMBRETABLA="empresas";
    public static final String CODIGO ="codigo";
    public static final String NOMBRE ="nombre";
    public static final String NIT ="nit";
    public static final String VALORCOBRO ="valorcobro";
    public static final String TIPOCOBRO ="tipocobro";
    public static final String WHERE_CODIGO = CODIGO +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

    public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";

    private  String codigo;
    private  String nombre;
    private  String nit;
    private  int valorcobro;
    private  String tipoCobro;

    public EmpresaModel() {
    }

    public EmpresaModel(String codigo, String nombre, String nit,  int valorcobro, String tipoCobro) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.nit = nit;
        this.valorcobro = valorcobro;
        this.tipoCobro = tipoCobro;

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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public int getValorcobro() {return valorcobro;}

    public void setValorcobro(int valorcobro) {this.valorcobro = valorcobro;}

    public String getTipoCobro() {return tipoCobro;}

    public void setTipoCobro(String tipoCobro) {this.tipoCobro = tipoCobro;}

    public void insertarEmpresa(SQLiteDatabase sqLiteDatabase ){
        ContentValues valores = new ContentValues();
        valores.put(CODIGO,this.codigo);
        valores.put(NOMBRE,this.nombre);
        valores.put(NIT,this.nit);
        valores.put(VALORCOBRO,this.valorcobro);
        valores.put(TIPOCOBRO,this.tipoCobro);

        sqLiteDatabase.insertOrThrow(NOMBRETABLA,null,valores);
        sqLiteDatabase.close();
    }
    public  void insertEmpresas(){

        ContentValues valores = new ContentValues();
        valores.put(CODIGO,this.codigo);
        valores.put(NOMBRE,this.nombre);
        valores.put(NIT,this.nit);
        valores.put(VALORCOBRO,this.valorcobro);
        valores.put(TIPOCOBRO,this.tipoCobro);

    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(3);

        contentValues.put(CODIGO,codigo);
        contentValues.put(NOMBRE,nombre);
        contentValues.put(NIT,nit);
        // contentValues.put(COL_ESTADO,estado);
        // contentValues.put(COL_PUESTOS,cantPuestos);

        return contentValues;
    }



}
