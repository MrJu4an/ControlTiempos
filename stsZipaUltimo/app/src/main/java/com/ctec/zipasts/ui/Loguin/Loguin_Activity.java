package com.ctec.zipasts.ui.Loguin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ctec.zipasts.BuildConfig;
import com.ctec.zipasts.MainActivity;
import com.ctec.zipasts.R;
import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Global;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Helper.respuesta;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Loguin_Activity extends AppCompatActivity {

    Context context = Loguin_Activity.this;
    private EditText edTPassWord, edTUsuario;
    private ImageView btnAcceso;
    private ImageView btnConfig;
    TextView version;
    private String VALIDA_USUARIO;
    private String menApi;
    private boolean usuVer=false;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private Context contexto ;
    public final static String NOM_USU= "NOMUSU";
    public  final static String PREF_TURNO="turno";
    private String nomUsu="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loguin);
        contexto = Loguin_Activity.this;
        sweetAlertDialog = new SweetAlertDialog(Loguin_Activity.this);
        mensaje = new Mensaje();
        edTPassWord= findViewById(R.id.edTPassWord);
        edTUsuario = findViewById(R.id.edTUsuario);
        btnAcceso = findViewById(R.id.btnAcceso);
        btnConfig= findViewById(R.id.btnConfig);
        version = (TextView) findViewById(R.id.txtVVersión);
        version.setText("Version: " + BuildConfig.VERSION_NAME);
        edTUsuario.requestFocus();
        cargarDatos();
        btnConfig.setOnClickListener(view -> validaAdministrador());

        btnAcceso.setOnClickListener(view -> {
            if (edTUsuario.length() == 0 || edTPassWord.length()==0){
                if(sweetAlertDialog.isShowing())
                    sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Debe Ingresar el usuario y contraseña ");
            }else {
                    VALIDA_USUARIO = readPreference(ConfigActivity.PREF_API)+Utils.VALIDA_USUARIO;
                 verificarUsuario(VALIDA_USUARIO, edTUsuario.getText().toString(), edTPassWord.getText().toString());
            }
        });
    }
    /**
     * envia petición a la Api para loguear y crear turno
     * @param peticion constante para acceder a metodo de la Api
     * @param usuario usuario ingresado por el usuario
     * @param password contraseña ingresada por el usuario
     */
    public void  verificarUsuario(String peticion,String usuario,String password)  {
        try{
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.progreso(context,"Verificando Usuario");
            sweetAlertDialog.show();

            StringRequest postRequest = new StringRequest(Request.Method.POST, peticion,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            usuVer = rpta.getEstado();
                            menApi= rpta.getMensaje();
                            nomUsu=rpta.getRespuesta();
                            sweetAlertDialog.dismiss();
                            consultarConsecutivos();

                        } catch (Exception e) {
                            sweetAlertDialog.dismiss();
                            e.printStackTrace();
                        }

                    },
                    error -> {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 400) {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(context,"Error en la conexión",error.getMessage());
                            edTUsuario.requestFocus();
                            return;
                        }
                        String msj = error.getMessage();
                        if (msj == null)
                        {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(context,"Advertencia","Servidor No Responde");
                            return;
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("nombre", usuario);
                    params.put("contraseña",password);
                    return params;
                }
            };
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            postRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(Loguin_Activity.this).add(postRequest);
            //sweetAlertDialog.dismiss();
        }
        catch(Exception ex){
            mensaje.MensajeError(Loguin_Activity.this, Mensaje.MEN_ADV, ex.getMessage());
        }
    }

    private void consultarConsecutivos(){
        try {
            final Data data = new Data(contexto);
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Consecutivos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + Global.g_puerto;
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.CONSULTAR_CONSEC+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            if(rpta.getEstado()){
                                String[] consecutivos= rpta.getRespuesta().split(";");
                                int nueNumFac=Integer.parseInt(consecutivos[0])+1;
                                int nueNumRec=Integer.parseInt(consecutivos[1])+1;
                                writePreference(ConfigActivity.PREF_NUMFAC,String.valueOf(nueNumFac));
                                writePreference(ConfigActivity.PREF_NUMREC,String.valueOf(nueNumRec));
                                asignarTurno();
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                iniciarMenu(usuVer,menApi);
                            }else{
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo consecutivos");
                            }



                        }catch (Exception e){
                            /*String ErrorDes = preferencias.ReadSharedPreference(contexto, Constantes.PREF_DOWNLOADERROR);
                            preferencias.updateSharedPreference(contexto,Constantes.PREF_DOWNLOADERROR, ErrorDes + " Empresas");
                            */
                            if(sweetAlertDialog.isShowing())
                                sweetAlertDialog.dismiss();

                            mensaje.MensajeAdvertencia(contexto,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, error -> {
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
                            assert msj != null;
                            if (msj.contains("EHOSTUNREACH"))
                            {
                                error.printStackTrace();
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,Mensaje.MSG_EHOSTUNREACH);
                                return;
                            }
                            if (msj.contains("ECONNREFUSED"))
                            {
                                error.printStackTrace();
                                if(sweetAlertDialog.isShowing())
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
                            JSONObject data1 = new JSONObject(respErr);
                            try{
                                JSONArray errors = data1.getJSONArray("errors");
                                JSONObject jsonMessage = errors.getJSONObject(0);
                                message = jsonMessage.getString("message");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            try{
                                message = data1.getString("Message");
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            if(sweetAlertDialog.isShowing())
                                sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(contexto, Mensaje.MEN_ADV, message);
                            Log.d("Download", "onErrorResponse: " + message);
                        }catch (Exception e){
                            try{
                                String respErr = new String(error.networkResponse.data);
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MSG_ERROR,respErr);
                            }catch (Exception ex){
                                if(sweetAlertDialog.isShowing())
                                    sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MSG_ERROR,ex.getMessage());
                                e.printStackTrace();
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


    public void asignarTurno(){
        Date act= new Date();
        SimpleDateFormat sdh= new SimpleDateFormat("HH",new Locale("es","CO"));
        int hora= Integer.parseInt(sdh.format(act));
        int turno=0;
        if( ( hora >= 3 ) && ( hora <= 12 ) )
           turno = 1;
        if( ( hora >= 13 ) && ( hora <= 22 ) )
            turno=2;
        Global.g_Turno= turno;
        writePreference(PREF_TURNO,String.valueOf(turno));

    }

    private void iniciarMenu(boolean estado,String menApi){
        if(estado) {
            if(readPreference(ConfigActivity.PREF_USUARIO_ACTIVO).equals("") || readPreference(ConfigActivity.PREF_USUARIO_ACTIVO).equals("-1") ){
                if(writePreference(ConfigActivity.PREF_USUARIO_ACTIVO,edTUsuario.getText().toString())){
                    writePreference(NOM_USU,nomUsu);
                    Global.g_NomUsu= nomUsu;
                    Global.g_usuario= edTUsuario.getText().toString();
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    sweetAlertDialog = mensaje.MensajeConfirmacionExitosoConUnBoton(context,Mensaje.MEN_CONFIRMA, menApi);
                    sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                        Global.g_Modulo = "STS";
                        Global.g_Sesion = true;
                        if(sweetAlertDialog.isShowing())
                            sweetAlertDialog.dismiss();
                        mensaje.MensajeExitoso(context, Mensaje.MEN_CONFIRMA, "Descarga Completa");
                        startActivity(new Intent(context, MainActivity.class));

                    });
                    sweetAlertDialog.show();
                }
            }else{
                Global.g_usuario= readPreference(ConfigActivity.PREF_USUARIO_ACTIVO);
                startActivity(new Intent(context, MainActivity.class));
            }

        }else{
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertencia(getApplicationContext(),Mensaje.MEN_ADV, menApi);
            sweetAlertDialog.show();
        }
    }

    private void validaAdministrador() {
        final EditText editText = new EditText(getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextColor(Color.BLACK);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        if(sweetAlertDialog.isShowing())
            sweetAlertDialog.dismiss();
        sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(
                Loguin_Activity.this,"Digite la Clave del administrador","");
        sweetAlertDialog.setCustomView(linearLayout);
        sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
            String clave = editText.getText().toString().trim();
            if (clave.equals("0423")) {
                if(sweetAlertDialog.isShowing())
                    sweetAlertDialog.dismiss();
                Intent intent = new Intent(Loguin_Activity.this,ConfigActivity.class);
                startActivity(intent);
                finish();

                return;
            } else {
                if(sweetAlertDialog.isShowing())
                    sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(context,
                        Mensaje.MEN_ADV, "Clave invalida");
                return;
            }
        });
        sweetAlertDialog.show();
    }

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs =  getSharedPreferences(ConfigActivity.PREF_GENERAL, MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }
        catch (Exception ex){
            ex.printStackTrace();
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(getApplicationContext(),"Advertencia",ex.getMessage() );
            return false;
        }
        return true;
    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = getSharedPreferences(ConfigActivity.PREF_GENERAL, MODE_PRIVATE);

            valor = prefs.getString(key, "");
        }
        catch (Exception ex){
            ex.printStackTrace();
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(getApplicationContext(),"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }

    /**
     * Carga las preferencias guardadas en la movil y las valida
     * @return
     */
    public boolean cargarDatos(){

        Global.g_DirecApi= readPreference(ConfigActivity.PREF_API);
        if (Global.g_DirecApi.equals("")) {
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(contexto,"Advertencia","Dato api vacio, configure la app ");
            return false;
        }
        Global.g_puerto= readPreference(ConfigActivity.PREF_PUERTO);
        if (Global.g_puerto.equals("")) {
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(contexto,"Advertencia","Dato del puerto vacio, configure la app ");
            return false;
        }

        String descarga= readPreference("DescargaCompleta");
        if (!descarga.equals("S"))
        {
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(contexto,"Advertencia","Descarga de Datos incompleta, verifique");
            return false;
        }

       // writePreference(ConfigActivity.PREF_USUARIO_ACTIVO,"");
        if (!readPreference(ConfigActivity.PREF_USUARIO_ACTIVO).equals("") ) {
            iniciarMenu(true,"Usuario validado correctamente");
            return false;
        }

        return true;
    }

    @ Override
    public void onBackPressed () {
        super.onBackPressed ();
        //aquí cerramos el actícity actual

        //creamos un nuevo intent de action_main para el cierre de todo lo que esté abierto
       Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
