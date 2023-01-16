package com.ctec.zipasts.ui.Helper;

public class respuesta {
    Boolean estado;
    int codigo;
    String mensaje;
    String respuesta;
    String fecHora;
    Object datos;
    String[] placas;

    public String[] getPlacas() {
        return placas;
    }

    public void setPlacas(String[] placas) {
        this.placas = placas;
    }

    public String getFecHora() {
        return fecHora;
    }

    public void setFecHora(String fecHora) {
        this.fecHora = fecHora;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }


    public respuesta() {
    }

    public respuesta(Boolean estado, int codigo, String mensaje, Object datos) {
        this.estado = estado;
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.datos = datos;
    }
    public respuesta(Boolean estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public respuesta(String [] placas) {
        this.placas=placas;
    }
}
