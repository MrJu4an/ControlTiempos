package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class ControlTiempoModel {

    public static final String NOMBRETABLA="control";
    public static final String PLACA ="placa";
    public static final String AGENGIA_ORIGEN ="agenciaOri";
    public static final String FECHA="fecha";
    public static final String SECUENCIA ="secuencia";
    public static final String HORA_ORIGEN ="horaOrigen";
    public static final String HORA_AGENCIA ="horaAgencia";
    public static final String DEMORA ="demora";
    public static final String FRECUENCIA ="frecuencia";
    public static final String ENCADENA ="enCadena";
    public static final String CODEMPANT ="codempant";
    public static final String PLACAANT ="placaAnt";
    public static final String FECHAANT="fechaAnt";
    public static final String HORA_ORIGENANT ="horaOrigenAnt";
    public static final String HORA_AGENCIAANT ="horaAgenciaAnt";
    public static final String DEMORAANT ="demoraAnt";
    //public static final String CODDES="coddes";
    public static final String CODPTO = "codPto";
    public static final String[] COL_TODAS ={PLACA,AGENGIA_ORIGEN,FECHA,SECUENCIA,HORA_ORIGEN
            ,HORA_AGENCIA,DEMORA,FRECUENCIA,ENCADENA,PLACAANT,FECHAANT,HORA_ORIGENANT,HORA_AGENCIAANT,DEMORAANT};
    public static final String PREF_CONTADOR ="contador";
    public static final String WHERE_PLACA= PLACA +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String placa;
    private  String agenciaOri;
    private  String fecha;
    private  String secuencia;
    private  String horaOrigen;
    private  String horaAgencia;
    private String demora;
    private String frecuencia;
    private String enCadena;
    private int codempant;
    private  String placaAnt;
    private  String fechaAnt;
    private  String horaOrigenAnt;
    private  String horaAgenciaAnt;
    private String demoraAnt;
    //private int coddes;
    private String codPto;

    public ControlTiempoModel() {
    }

    public String getEnCadena() {
        return enCadena;
    }

    public void setEnCadena(String enCadena) {
        this.enCadena = enCadena;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getAgenciaOri() {
        return agenciaOri;
    }

    public void setAgenciaOri(String agenciaOri) {
        this.agenciaOri = agenciaOri;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(String secuencia) {
        this.secuencia = secuencia;
    }

    public String getHoraOrigen() {
        return horaOrigen;
    }

    public void setHoraOrigen(String horaOrigen) {
        this.horaOrigen = horaOrigen;
    }

    public String getHoraAgencia() {
        return horaAgencia;
    }

    public void setHoraAgencia(String horaAgencia) {
        this.horaAgencia = horaAgencia;
    }

    public String getDemora() {
        return demora;
    }

    public void setDemora(String demora) {
        this.demora = demora;
    }

    public int getCodempant() {
        return codempant;
    }

    public void setCodempant(int codempant) {
        this.codempant = codempant;
    }

    public String getPlacaAnt() {
        return placaAnt;
    }

    public void setPlacaAnt(String placaAnt) {
        this.placaAnt = placaAnt;
    }

    public String getFechaAnt() {
        return fechaAnt;
    }

    public void setFechaAnt(String fechaAnt) {
        this.fechaAnt = fechaAnt;
    }

    public String getHoraOrigenAnt() {
        return horaOrigenAnt;
    }

    public void setHoraOrigenAnt(String horaOrigenAnt) {
        this.horaOrigenAnt = horaOrigenAnt;
    }

    public String getHoraAgenciaAnt() {
        return horaAgenciaAnt;
    }

    public void setHoraAgenciaAnt(String horaAgenciaAnt) {
        this.horaAgenciaAnt = horaAgenciaAnt;
    }

    public String getDemoraAnt() {
        return demoraAnt;
    }

    public void setDemoraAnt(String demoraAnt) {
        this.demoraAnt = demoraAnt;
    }

    //public int getCoddes() {
        //return coddes;
    //}

    //public void setCoddes(int coddes) {
        //this.coddes = coddes;
    //}

    public void setCodPto(String codpto){ this.codPto = codpto;}

    public String getCodPto() { return codPto; }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(9);

        contentValues.put(PLACA,placa);
        contentValues.put(AGENGIA_ORIGEN,agenciaOri);
        contentValues.put(FECHA,fecha);
        contentValues.put(SECUENCIA,secuencia);
        contentValues.put(HORA_ORIGEN,horaOrigen);
        contentValues.put(HORA_AGENCIA,horaAgencia);
        contentValues.put(DEMORA,demora);
        contentValues.put(FRECUENCIA,frecuencia);
        contentValues.put(ENCADENA,enCadena);
        contentValues.put(CODEMPANT,codempant);
        contentValues.put(PLACAANT,placaAnt);
        contentValues.put(FECHAANT,fechaAnt);
        contentValues.put(HORA_ORIGENANT,horaOrigenAnt);
        contentValues.put(HORA_AGENCIAANT,horaAgenciaAnt);
        contentValues.put(DEMORAANT,demoraAnt);
        //contentValues.put(CODDES,coddes);
        //contentValues.put(CODPTO, codPto);
        return contentValues;
    }

}
