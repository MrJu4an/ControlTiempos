package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class FrecuenciaModel {
    public static final String NOMBRETABLA="frecuencias";
    public static final String COD_ORIGEN ="codOrigen";
    public static final String COD_AGENCIA ="codAgencia";
    public static final String HORA_INI ="horaIni";
    public static final String HORA_FIN ="horaFin";
    public static final String TIEMP_FREC ="tiempFrec";
    public static final String WHERE_COD_AGENCIA = COD_AGENCIA +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String codOrigen;
    private  String codAgencia;
    private  String horaIni;
    private  String horaFin;
    private  String tiempFrec;

    public FrecuenciaModel() {
    }

    public FrecuenciaModel(String codOrigen, String codAgencia, String horaIni, String horaFin, String tiempFrec) {
        this.codOrigen = codOrigen;
        this.codAgencia = codAgencia;
        this.horaIni = horaIni;
        this.horaFin = horaFin;
        this.tiempFrec = tiempFrec;
    }

    public String getCodOrigen() {
        return codOrigen;
    }

    public void setCodOrigen(String codOrigen) {
        this.codOrigen = codOrigen;
    }

    public String getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(String codAgencia) {
        this.codAgencia = codAgencia;
    }

    public String getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(String horaIni) {
        this.horaIni = horaIni;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getTiempFrec() {
        return tiempFrec;
    }

    public void setTiempFrec(String tiempFrec) {
        this.tiempFrec = tiempFrec;
    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(5);

        contentValues.put(COD_ORIGEN,codOrigen);
        contentValues.put(COD_AGENCIA,codAgencia);
        contentValues.put(HORA_INI,horaIni);
        contentValues.put(HORA_FIN,horaFin);
        contentValues.put(TIEMP_FREC,tiempFrec);
        return contentValues;
    }


}
