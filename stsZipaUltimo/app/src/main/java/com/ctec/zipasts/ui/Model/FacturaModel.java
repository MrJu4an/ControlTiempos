package com.ctec.zipasts.ui.Model;

import android.content.ContentValues;

public class FacturaModel {
    public static final String NOMBRETABLA="facturas";
    public static final String NOMBRETABLABASE="facturabase";
    public static final String NUM_FAC ="numFac";
    public static final String COD_AGENCIA ="codAgencia";
    public static final String FECHA ="fecha";
    public static final String FACFECSALES ="facfecsales";
    public static final String HORAENTRADA ="horaentrada";
    public static final String HORASALIDA ="horasalida";
    public static final String FACFECSALR ="facfecsalr";
    public static final String FACFECSALEN ="facfecsalen";
    public static final String COD_USUARIO ="usuario";
    public static final String VALOR ="valor";
    public static final String FORMA_PAGO ="forma";
    public static final String COD_DESTINO ="destino";
    public static final String COD_ORIGEN ="origen";
    public static final String COD_EMPRESA ="empresa";
    public static final String PLACA ="placa";
    public static final String COD_AGE_ASO ="asociada";
    public static  final String TURNO ="turno";
    public static  final String ID_TARJETA ="idTarjeta";
    public static  final String SALDO_TARJETA ="saldo";
    public static  final String ID_PLANILLA ="planilla";
    public static final String ENCADENA ="enCadena";
    public static final String FACBAN ="facban";
    // Estos datos son siempre los mismos
    public static  final String TIP_DOC ="FAC";
    public static  final String MOD ="P";
    public static  final String CED_CONDUCTOR ="1";
    public static final String  NUM_PLANILLA="0";
    public static final String  FAC_DESTINO="BOGOTA";
    public static final String  FAC_COD_DESTINO="7";

    public static final String WHERE_NUMFAC= NUM_FAC +"=?";
    public static final String DELETE_ALL = "delete from "+ NOMBRETABLA;
    public static final String DELETE_TABLE = "drop table IF EXISTS " + NOMBRETABLA;

    public ContentValues valores(){
        ContentValues contentValues = new ContentValues(15);

        contentValues.put(NUM_FAC,numFac);
        contentValues.put(COD_AGENCIA,codAgencia);
        contentValues.put(FECHA,fecha);
        contentValues.put(COD_USUARIO,usuario);
        contentValues.put(VALOR, valor);
        contentValues.put(FORMA_PAGO,forma);
        contentValues.put(COD_DESTINO,destino);
        contentValues.put(COD_EMPRESA,empresa);
        contentValues.put(PLACA,placa);
        contentValues.put(COD_AGE_ASO,asociada);
        contentValues.put(TURNO,turno);
        contentValues.put(ID_TARJETA,idTarjeta);
        contentValues.put(SALDO_TARJETA,saldo);
        contentValues.put(ID_PLANILLA,planilla);
        contentValues.put(ENCADENA,enCadena);

        return contentValues;
    }

 /*   public static final String CREARTABLA="create table if not exists "+ NOMBRETABLA + "( "+
            CODIGO + " text not null," +
            NOMBRE + " text not null," +
            NIT +" text not null, "+
            VALORCOBRO + " text ," +
            TIPOCOBRO +" text not null );";*/

    private  long numFac;
    private  String codAgencia;
    private  String fecha;
    private  String usuario;
    private String forma;
    private String destino;
    private String origen;
    private String empresa;
    private String placa;
    private String asociada;
    private String turno;
    private String idTarjeta;
    private String saldo;
    private String planilla;
    private String valor;
    private String enCadena;
    private String facfecsales;
    private String horaentrada;
    private String horasalida;
    private String facfecsalr;
    private String facfecsalen;
    private  int facban;

    public FacturaModel() {

    }

    public String getEnCadena() {
        return enCadena;
    }

    public void setEnCadena(String enCadena) {
        this.enCadena = enCadena;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public long getNumFac() {
        return numFac;
    }

    public void setNumFac(long numFac) {
        this.numFac = numFac;
    }

    public String getCodAgencia() {
        return codAgencia;
    }

    public void setCodAgencia(String codAgencia) {
        this.codAgencia = codAgencia;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getAsociada() {
        return asociada;
    }

    public void setAsociada(String asociada) {
        this.asociada = asociada;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getIdTarjeta() {
        return idTarjeta;
    }

    public void setIdTarjeta(String idTarjeta) {
        this.idTarjeta = idTarjeta;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getPlanilla() {
        return planilla;
    }

    public void setPlanilla(String planilla) {
        this.planilla = planilla;
    }

    public String getFacfecsales() {
        return facfecsales;
    }

    public void setFacfecsales(String facfecsales) {
        this.facfecsales = facfecsales;
    }

    public String getHoraentrada() {
        return horaentrada;
    }

    public void setHoraentrada(String horaentrada) {
        this.horaentrada = horaentrada;
    }

    public String getHorasalida() {
        return horasalida;
    }

    public void setHorasalida(String horasalida) {
        this.horasalida = horasalida;
    }

    public String getFacfecsalr() {
        return facfecsalr;
    }

    public void setFacfecsalr(String facfecsalr) {
        this.facfecsalr = facfecsalr;
    }

    public String getFacfecsalen() {
        return facfecsalen;
    }

    public void setFacfecsalen(String facfecsalen) {
        this.facfecsalen = facfecsalen;
    }

    public int getFacban() {
        return facban;
    }

    public void setFacban(int facban) {
        this.facban = facban;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}
