package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class ReciboModel {
    public static final String NOMBRETABLA="recibos";
    public static final String NUM_RECIBO ="numRecibo";
    public static final String FECHA_HORA ="fechaHora";
    public static final String PLACA ="placa";
    public static final String ID_TARJETA ="IDTarjeta";
    public static final String VAL_RECIBO ="valRecibo";
    public static final String TAR_SALDO ="tarSaldo";
    public static final String ENCADENA ="enCadena";
    public static final String FORM_PAGO ="EFE";
    public static final String SIN_ORIGEN ="0";
    public static final String TIP_DOC ="REC";
    public static final Boolean PROCESO =true;
    public static final Boolean OCUPADO =true;

    public static final String WHERE_NUM_RECIBO= NUM_RECIBO +"=?";

    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  String numRecibo;
    private  String fechaHora;
    private  String placa;
    private  String IDTarjeta;
    private String valRecibo;
    private String tarSaldo;
    private String enCadena;

    public ReciboModel(){

    }

    public String getEnCadena() {
        return enCadena;
    }

    public void setEnCadena(String enCadena) {
        this.enCadena = enCadena;
    }

    public String getTarSaldo() {
        return tarSaldo;
    }

    public void setTarSaldo(String tarSaldo) {
        this.tarSaldo = tarSaldo;
    }

    public String getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo) {
        this.numRecibo = numRecibo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getIDTarjeta() {
        return IDTarjeta;
    }

    public void setIDTarjeta(String IDTarjeta) {
        this.IDTarjeta = IDTarjeta;
    }

    public String getValRecibo() {
        return valRecibo;
    }

    public void setValRecibo(String valRecibo) {
        this.valRecibo = valRecibo;
    }

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(7);

        contentValues.put(NUM_RECIBO,numRecibo);
        contentValues.put(FECHA_HORA,fechaHora);
        contentValues.put(PLACA,placa);
        contentValues.put(ID_TARJETA,IDTarjeta);
        contentValues.put(VAL_RECIBO,valRecibo);
        contentValues.put(TAR_SALDO,tarSaldo);
        contentValues.put(ENCADENA,enCadena);


        return contentValues;
    }


}
