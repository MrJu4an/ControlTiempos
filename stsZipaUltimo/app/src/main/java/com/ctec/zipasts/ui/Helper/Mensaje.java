package com.ctec.zipasts.ui.Helper;


import android.content.Context;

import com.ctec.zipasts.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Mensaje {

    public static Boolean confirmacion;
    public static final String MSG_INFO = "Informacion";
    public static final String MEN_ADV="Advertencia";
    public static final String MSG_ERROR = "Error";
    public static final String MEN_INFO = "Informacion";
    public static final String MEN_CONFIRMA="Confirmacion Exitosa";
    public static final String MEN_PREG_CONFIRMA = "Confirmación de transacción";
    public static final String MEN_SUGER_TAR="Por favor acerque la tarjeta";
    public static final String MSG_MOVILSINCX = "Movil sin conexión a internet";
    public static final String MSG_EHOSTUNREACH = "Servidor no responde";
    public static final String MSG_ECONNREFUSED = "Conexión rechazada por el servidor";
    public static final String MSG_TIMEOUTERROR = "Se agoto el tiempo para la solicitud";

    public SweetAlertDialog progreso(Context context, String Descripcion){

        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.message_color);
        pDialog.setTitleText(Descripcion);
        pDialog.setCancelable(false);

        return pDialog;
    }



    public void MensajeNormal(Context context,String titulo,String descripcion){
        new SweetAlertDialog(context)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

    public void MensajeError(Context context,String titulo,String descripcion){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }

    public void MensajeAdvertencia(Context context,String titulo,String descripcion){

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();
    }


    public void MensajeExitoso(Context context,String titulo,String descripcion){

        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .show();

    }



    public SweetAlertDialog MensajeConfirmacionAdvertencia(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar");
        /**sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_LONG).show();
        sDialog.dismissWithAnimation();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionAdvertenciaConBotones(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setCancelText("Cancelar")
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionExitosoConUnBoton(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }


    public SweetAlertDialog MensajeConfirmacionAdvertenciaConUnBoton(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar")
                .showCancelButton(true);

        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }

    public SweetAlertDialog MensajeConfirmacionError(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setConfirmText("Aceptar");
        /**sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_LONG).show();
        sDialog.dismissWithAnimation();
        }
        })
         .show();*/
        return sweetAlertDialog;
    }

    public SweetAlertDialog MensajeConfirmacionErrorConBotones(Context contex,String titulo,String descripcion){

        SweetAlertDialog sweetAlertDialog  = new SweetAlertDialog(contex, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(titulo)
                .setContentText(descripcion)
                .setCancelText("Cancelar")
                .setConfirmText("Aceptar")
                .showCancelButton(true);
        /**.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sDialog) {
        sDialog.cancel();
        }
        })
         .show();*/
        return sweetAlertDialog;

    }


}
