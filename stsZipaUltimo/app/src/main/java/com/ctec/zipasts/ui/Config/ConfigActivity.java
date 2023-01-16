package com.ctec.zipasts.ui.Config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ctec.zipasts.R;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Global;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Helper.respuesta;
import com.ctec.zipasts.ui.Loguin.Loguin_Activity;
import com.ctec.zipasts.ui.Model.ConceptoModel;
import com.ctec.zipasts.ui.Model.DestinoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.FrecuenciaModel;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.ctec.zipasts.ui.Model.TarjetaModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigActivity extends AppCompatActivity {

    private Mensaje mensaje;
    private EditText edTPuerto;
    private EditText edTDirecApi;
    private Button btnGuardar;
    private SweetAlertDialog sweetAlertDialog;
    public static final String PREF_GENERAL = "stszipa";
    public static final String PREF_USUARIO_ACTIVO = "Usuario";
    public static final String PREF_API = "Api";
    public static final String PREF_PUERTO = "Puerto";
    public static final String PREF_NOM_PUNTO = "nomPunto";
    public static final String PREF_NUMRES = "NUMRES";
    public static final String PREF_FECRES = "FECRES ";
    public static final String PREF_INIRES = "INIRES";
    public static final String PREF_FINRES = "FINRES";
    public static final String PREF_PREFRES = "PREFRES";
    public static final String PREF_NUMREC = "NUMREC";
    public static final String PREF_NUMFAC = "NUMFAC";
    public static final String PREF_IVA = "NUMFAC";
    public static final String PREF_ACT_ECON = "ECON";
    public static final String PREF_COD_RECARGA = "RECARGA";
    public static final String PREF_COD_CONTROL= "CONTROL";
    public static final String PREF_TOTALEFECTIVO ="totalefectivo";
    public static final String PREF_EMP_NO = "EMPNOCOBRO";
    public static final String PREF_TOTALTARJETA ="totaltarjeta";
    public static final String PREF_DOWNLOADCOUNTER="contador";
    public static final String PREF_TOTAL_CONTROL="totalcontrol";
    public static final String PREF_TOTAL_RECARGAS="totalrecargas";
    private Data data;
    private String descarga;
    private Context contexto = ConfigActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        mensaje = new Mensaje();
        sweetAlertDialog = new SweetAlertDialog(this);
        edTPuerto = findViewById(R.id.edTPuerto);
        edTDirecApi = findViewById(R.id.edTDirecApi);
        btnGuardar = findViewById(R.id.btnActualizarDatos);
        data= new Data(ConfigActivity.this);

        btnGuardar.setOnClickListener(view -> grabarPreferencias());
    }
    public void cargarDatos(){

       //writePreference(PREF_API,"http://190.60.235.204/stszipa/api/stszipa/");
        descarga = readPreference("DescargaCompleta");
        DescargaEmpresas();
    }

    private void DescargaEmpresas(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Empresas");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_EMPRESAS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<EmpresaModel>>(){}.getType();
                            ArrayList<EmpresaModel> respon = gson.fromJson(response,listType);

                            if (respon != null){
                                data.eliminarEmpresas();
                                data.insertarEmpresas(respon);

                               // descargaDestinos();

                                descargaVehiculos();
                            }else{
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo destinos");

                            }

                            }catch (Exception e){

                            if(sweetAlertDialog.isShowing())
                                sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaDestinos(){
        try {
            if (Utils.isOnline(ConfigActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Destinos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_DESTINOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<DestinoModel>>(){}.getType();
                                ArrayList<DestinoModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarDestinos();
                                    data.insertarDestinos(respon);
                                    descargaVehiculos();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo destinos");
                                }

                                /*
                                int count = Integer.parseInt(readPreference(PREF_DOWNLOADCOUNTER));
                                int counter = count +1 ;
                                writePreference(PREF_DOWNLOADCOUNTER, String.valueOf(counter));
                                */




                            }catch (Exception e){
                                /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                                preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                                */
                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaVehiculos(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                if(sweetAlertDialog.isShowing())
                    sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando Vehiculos"));
            }).start();

            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+ Utils.DESCARGA_VEHICULOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<VehiculoModel>>(){}.getType();
                            ArrayList<VehiculoModel> respon = gson.fromJson(response,listType);

                            if (respon != null){
                                data.eliminarVehiculos();
                                data.insertarVehiculos(respon);

                                descargaTarjetas();

                            }else{
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo vehículos");
                            }
                            /*
                            int count = Integer.parseInt(readPreference(PREF_DOWNLOADCOUNTER));
                            int counter = count +1 ;
                            writePreference(PREF_DOWNLOADCOUNTER, String.valueOf(counter));
                            */



                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaTarjetas(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando Tarjetas"));
            }).start();

            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_TARJETAS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<TarjetaModel>>(){}.getType();
                            ArrayList<TarjetaModel> respon = gson.fromJson(response,listType);

                            if (respon != null){
                                data.eliminarTarjetas();
                                data.insertarTarjetas(respon);

                                // long num= data.countTarjeta();
                                //ArrayList<TarjetaModel>t= data.getTarjetas();
                                //  sweetAlertDialog.dismiss();

                                descargaConceptos();
                            }else{
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo tarjetas");
                            }




                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaConceptos(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando conceptos"));
            }).start();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_CONCEPTOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {

                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<ConceptoModel>>(){}.getType();
                            ArrayList<ConceptoModel> respon = gson.fromJson(response,listType);

                            if (respon != null){

                                data.eliminarConceptos();
                                data.insertarConceptos(respon);
                                consultarResolucion();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo conceptos");
                            }



                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaPuntosVenta(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando puntos de venta"));
            }).start();

            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_PUNTOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {

                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<PuntoVentaModel>>(){}.getType();
                            ArrayList<PuntoVentaModel> respon = gson.fromJson(response,listType);

                            if (respon != null){

                                data.eliminarPuntos();
                                data.insertarPuntos(respon);
                                String nomPunto= data.getPuntoPuerto(edTPuerto.getText().toString()).getNombre();
                                writePreference(PREF_NOM_PUNTO,nomPunto);
                                descargaFrecuencias();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo Puntos de venta");
                            }




                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void consultarConsecutivos(){
        try {
            if (Utils.isOnline(ConfigActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Consecutivos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.CONSULTAR_CONSEC+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            try {
                                respuesta rpta = new Gson().fromJson(response,respuesta.class);
                                if(rpta.getEstado()){

                                    String[] consecutivos= rpta.getRespuesta().split(";");
                                    int nueNumFac=Integer.parseInt(consecutivos[0])+1;
                                    int nueNumRec=Integer.parseInt(consecutivos[1])+1;
                                    writePreference(PREF_NUMFAC,String.valueOf(nueNumFac));
                                    writePreference(PREF_NUMREC,String.valueOf(nueNumRec));
                                    sweetAlertDialog.dismiss();
                                    descargaPuntosVenta();

                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo consecutivos");
                                }



                            }catch (Exception e){
                                /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                                preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                                */
                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void consultarResolucion(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando Resoluciones"));
            }).start();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.CONSULTAR_RESOL+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            if(rpta.getEstado()){
                                desglosaResolucion(rpta.getRespuesta());
                                //consultarConsecutivos();
                                descargaPuntosVenta();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo resolucion");
                            }



                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void consultarParametros(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando Parametros"));
            }).start();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.CONSULTAR_PARAM+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            if(rpta.getEstado()){

                                desglosaParametros(rpta.getRespuesta());
                                consultarEmpresasNoCobro();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,    "No descargo parametros");
                            }


                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void consultarEmpresasNoCobro(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando empresas no cobro"));
            }).start();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.CONSULTAR_EMP_NO_COBRO+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            if(rpta.getEstado()){

                                writePreference(PREF_EMP_NO,rpta.getRespuesta());
                                //writePreference(PREF_API,"http://190.60.235.204/stszipa/api/stszipa/");
                                sweetAlertDialog.dismiss();
                                writePreference("DescargaCompleta","S");
                                volverLoguin();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(ConfigActivity.this,Mensaje.MEN_ADV,"No descargo empresas no cobro");
                                sweetAlertDialog.show();
                            }

                            //  volverLoguin();


                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void descargaFrecuencias(){
        try {
            if (!Utils.isOnline(ConfigActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            final Handler handler=new Handler();
            new Thread(() -> {
                //your code
                handler.post(() -> sweetAlertDialog.setTitleText("Descargando Frecuencias"));
            }).start();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(PREF_API)+Utils.DESCARGA_FRECUENCIAS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {

                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<FrecuenciaModel>>(){}.getType();
                            ArrayList<FrecuenciaModel> respon = gson.fromJson(response,listType);

                            if (respon != null){
                                data.eliminarFrecuencia();
                                data.insertarFrecuencias(respon);
                                sweetAlertDialog.dismiss();
                                consultarParametros();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo frecuencias");
                            }




                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = "";
                    /*
                    String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                    preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                    if (error.toString().equals("com.android.volley.TimeoutError")){
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto,
                                Mensajes.MSG_ALERTA, Mensajes.MSG_TIMEOUTERROR);


                        return;
                    } */

                    NetworkResponse networkResponse = error.networkResponse;
                    String msj = error.getMessage();
                    try{
                        if (msj.contains("EHOSTUNREACH"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                            return;
                        }
                        if (msj.contains("ECONNREFUSED"))
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_ECONNREFUSED);
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("Eresponse", "onErrorResponse: "+e.getMessage());
                    }

                    try {

                        String respErr = new String(error.networkResponse.data);
                        JSONObject data = new JSONObject(respErr);
                        try{
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            message = jsonMessage.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            message = data.getString("Message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                        Log.d("Download", "onErrorResponse: " + message);
                    }catch (Exception e){
                        try{
                            String respErr = new String(error.networkResponse.data);
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,respErr);
                        }catch (Exception ex){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MSG_ERROR,ex.getMessage());
                            e.printStackTrace();
                        }

                    }
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void volverLoguin(){
        sweetAlertDialog.dismiss();
        sweetAlertDialog=mensaje.MensajeConfirmacionExitosoConUnBoton(ConfigActivity.this, Mensaje.MEN_INFO, "Datos de Configuracion Grabados Exitosamente");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                Intent intent= new Intent(ConfigActivity.this,Loguin_Activity.class);
                startActivity(intent);
                finish();

            }

        });
        sweetAlertDialog.show();
    }

    public void desglosaResolucion(String respuesta){
        String[] desglose = respuesta.split(";");
        writePreference(PREF_NUMRES,desglose[0]);
        writePreference(PREF_FECRES,desglose[1]);
        writePreference(PREF_INIRES,desglose[2] );
        writePreference(PREF_FINRES,desglose[3] );
        writePreference(PREF_PREFRES,desglose[4] );

    }

    public void desglosaParametros(String respuesta){
        String[] desglose = respuesta.split(";");
        writePreference(PREF_IVA,desglose[0]);
        writePreference(PREF_ACT_ECON,desglose[1] );
        writePreference(PREF_COD_RECARGA,desglose[2] );
        writePreference(PREF_COD_CONTROL,desglose[3] );

    }


    public void grabarPreferencias() {
        if(edTPuerto.getText().toString().length()==0 ){
            sweetAlertDialog =mensaje.MensajeConfirmacionAdvertencia(ConfigActivity.this, Mensaje.MEN_ADV, "No a ingresado el puerto!");
            sweetAlertDialog.show();
            edTPuerto.findFocus();
            return;
        }
        if(edTDirecApi.getText().toString().length()==0 ){
            sweetAlertDialog =mensaje.MensajeConfirmacionAdvertencia(ConfigActivity.this, Mensaje.MEN_ADV, "No a ingresado la url!");
            sweetAlertDialog.show();
            edTDirecApi.findFocus();
            return;
        }
        if (!writePreference(PREF_API, edTDirecApi.getText().toString())) {

            sweetAlertDialog= mensaje.MensajeConfirmacionAdvertencia(ConfigActivity.this, Mensaje.MEN_ADV, "Inconvenientes al grabar dirección de la api");
            sweetAlertDialog.show();
            return;
        }else{
            Global.g_DirecApi= edTDirecApi.getText().toString();
        }
        if (!writePreference(PREF_PUERTO, edTPuerto.getText().toString())) {
            sweetAlertDialog= mensaje.MensajeConfirmacionAdvertencia(ConfigActivity.this, Mensaje.MEN_ADV, "Inconvenientes al grabar puerto de la api");
            sweetAlertDialog.show();
            return;
        }else{
            Global.g_puerto= edTPuerto.getText().toString();
        }
        cargarDatos();

    }

    public boolean writePreference(String key, String value) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_GENERAL, MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", ex.getMessage());
            return false;
        }
        return true;
    }

    public String readPreference(String key) {
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences(PREF_GENERAL, MODE_PRIVATE);

            valor = prefs.getString(key, "");
        } catch (Exception ex) {
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(ConfigActivity.this, "Advertencia", ex.getMessage());
            return "-1";
        }
        return valor;
    }
    @ Override
    public void onBackPressed () {
        super.onBackPressed ();
        startActivity(new Intent(ConfigActivity.this, Loguin_Activity.class));
    }
}
