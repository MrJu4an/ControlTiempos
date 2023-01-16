package com.ctec.zipasts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.ctec.zipasts.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Helper.Global;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Helper.respuesta;
import com.ctec.zipasts.ui.Loguin.Loguin_Activity;
import com.ctec.zipasts.ui.Model.ConceptoModel;
import com.ctec.zipasts.ui.Model.ControlTiempoModel;
import com.ctec.zipasts.ui.Model.DestinoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.FacturaModel;
import com.ctec.zipasts.ui.Model.FrecuenciaModel;
import com.ctec.zipasts.ui.Model.ReciboModel;
import com.ctec.zipasts.ui.Model.TarjetaModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private static String EJECUTA_ISO ;
    private ProgressBar simpleProgressBar;
    private int i=0;
    private int j=0;
    private int k=0;
    int canCtrl;
    int canFac;
    int canRec;
    private int contTramas=0;
    private Data data;
    private PuntoVentaModel punto;
    Gson gson;
    String tramFac="";
    String tramRec="";
    String tramCtrl="";
    ArrayList<String> outFac;
    ArrayList<String>outRec;
    ArrayList<String>outCtrl;
    public static final String CAD_FAC="cadFacturas";
    public static  final String CAD_REC="cadRecibos";
    public static  final String CAD_CTRL="cadCtrl";
    Boolean entCtrl = false;
    Boolean entFac=false;
    Boolean entRec=false;
    Context contexto= MainActivity.this;
    int cantTramas=0;
    int cantEnvio=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //writePreference(ConfigActivity.PREF_API,"http://190.60.235.204/stszipa/api/stszipa/");
        super.onCreate(savedInstanceState);

        sweetAlertDialog = new SweetAlertDialog(MainActivity.this);
        mensaje = new Mensaje();
        String puerto = readPreference(ConfigActivity.PREF_PUERTO);
        if (puerto.equals(""))
        {
            Intent i = new Intent(getApplicationContext(),Loguin_Activity.class);
            startActivity(i);
            return;
        }

        String descarga= readPreference("DescargaCompleta");
        if (!descarga.equals("S"))
        {
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(contexto,"Advertencia","Descarga de Datos incompleta, verifique");
            Intent i = new Intent(getApplicationContext(),Loguin_Activity.class);
            startActivity(i);
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        simpleProgressBar= findViewById(R.id.progressBar);

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   enviarCantidadTramas();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_reporte,R.id.nav_cierre)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //writePreference(ConfigActivity.PREF_USUARIO_ACTIVO,"");
        if (readPreference(ConfigActivity.PREF_USUARIO_ACTIVO).equals("") || readPreference(ConfigActivity.PREF_USUARIO_ACTIVO).equals("-1")) {
            lanzarLoguin();
        }else{
            EJECUTA_ISO = readPreference(ConfigActivity.PREF_API)+ Utils.EJECUTA_ISO;
            data = new Data(getApplicationContext());
            punto = data.getPuntoPuerto(readPreference(ConfigActivity.PREF_PUERTO));
            gson = new Gson();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void enviarCantidadTramas(){
        final EditText editText = new EditText(getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTextColor(Color.BLACK);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(editText);
        generarTramas();
        String cantidad= String.valueOf(contTramas);
        if(contTramas>0){
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(
                    MainActivity.this,"Hay "+cantidad +" tramas, por favor digite cuantas desea enviar","");
            sweetAlertDialog.setCustomView(linearLayout);
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    cantEnvio = Integer.parseInt(editText.getText().toString().trim());
                    enviarDatos();
                }
            });
            sweetAlertDialog.show();
        }else{
            mensaje.MensajeAdvertencia(MainActivity.this,Mensaje.MEN_ADV,"No hay datos pendientes");
        }


    }

    public void generarTramas(){
        Global.g_Turno= Integer.parseInt(readPreference(Loguin_Activity.PREF_TURNO));
        generarTramaControl();
        generarTramaFacturas();
        generarTramaRecibos();
        tramCtrl = readPreference(CAD_CTRL);
        tramFac = readPreference(CAD_FAC);
        tramRec = readPreference(CAD_REC);
        outCtrl=(tramCtrl.equals("")? null: gson.fromJson(tramCtrl,ArrayList.class));
        outFac= (tramFac.equals("")? null: gson.fromJson(tramFac,ArrayList.class));
        outRec= (tramRec.equals("")? null: gson.fromJson(tramRec,ArrayList.class));
        canCtrl=tramCtrl.equals("")?0:outCtrl.size();
        canFac=tramFac.equals("")?0:outFac.size();
        canRec=tramRec.equals("")?0:outRec.size();
        contTramas= canCtrl+canFac+canRec;
    }

    public void enviarDatos(){
        simpleProgressBar.setProgress(0);
        simpleProgressBar.setMax(Integer.parseInt("" + (contTramas) ));
        simpleProgressBar.setVisibility(View.VISIBLE);
            i=0;
            j=0;
            k=0;
            if(canCtrl>0){
                entCtrl=true;
                entFac= false;
                entRec= false;
                Transmitir(Utils.ISO_CONTROL,outCtrl.get(i));

            }else if(canFac>0){
                entFac=true;
                entCtrl=false;
                entRec=false;
                Transmitir(Utils.ISO_FACTURA,outFac.get(j));

            }else if(canRec>0){
                entRec= true;
                entCtrl=false;
                entRec=false;
                Transmitir(Utils.ISO_FACTURA,outRec.get(k));

            }
    }
    /*
  //  @SuppressLint("HardwareIds")
    public String obtenerIdUnicoDispositivo(){
     //   TelephonyManager telephonyManager;
      //  telephonyManager = (TelephonyManager) getSystemService (Context.TELEPHONY_SERVICE);
      // String idDispositivo= telephonyManager.getDeviceId();
      // String idDevice = Settings.Secure.getString( activity.getContentResolver( ), Settings.Secure.ANDROID_ID );
        String idDevice = android.provider.Settings.Secure.getString(
                MainActivity
                .this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
       return idDevice;
    }
    */


    public void generarTramaControl(){
      /*  data.eliminarControles();
        data.eliminarFacturas();
        data.eliminarRecibos();
        writePreference(CAD_FAC,"");
        writePreference(CAD_CTRL,"");
        writePreference(CAD_REC,"");*/
        ArrayList<ControlTiempoModel> registros= data.getControles();
        if(registros.size()>0){
            ArrayList<String> tramaControl= new ArrayList<>();
            for(ControlTiempoModel reg: registros){
                if(reg.getEnCadena().equals("0")){
                    EmpresaModel empresa= data.getEmpresa(data.getVehiculoPlaca(reg.getPlaca()).getCodEmpresa());
                    String numInterno= data.getVehiculoPlaca(reg.getPlaca()).getNumInterno();
                    String trama=empresa.getCodigo()+";"+empresa.getNombre()+";"+reg.getPlaca()+";"+numInterno+";"
                            +reg.getFecha()+";"+reg.getHoraOrigen()+";"+reg.getHoraAgencia()+";"+reg.getDemora()+";"+reg.getFrecuencia()+";"+reg.getAgenciaOri()+";"+punto.getcodpunto()+";"
                            +reg.getCodempant()+";"+reg.getPlacaAnt()+";"+reg.getFechaAnt()+";"+reg.getHoraOrigenAnt()+";"+reg.getHoraAgenciaAnt()+";"
                            +reg.getDemoraAnt();
                    tramaControl.add(trama);
                    data.confirmarEnCadenaControl(reg.getSecuencia());

                }

            }
            if(tramaControl.size()>0) {
                tramCtrl = readPreference(CAD_CTRL);
                if(tramCtrl.equals("")){
                    String jsonFac = gson.toJson(tramaControl);
                    writePreference(CAD_CTRL,jsonFac);
                }else{
                    ArrayList<String> temp= (gson.fromJson(tramCtrl,ArrayList.class));
                    for(String r: tramaControl){
                        temp.add(r);
                    }
                    String jsonFac = gson.toJson(temp);
                    writePreference(CAD_CTRL,jsonFac);
                }
            }

        }

    }

    public  void generarTramaRecibos(){
        ArrayList<ReciboModel> recibos = data.getRecibos();
      //  ArrayList<VehiculoModel> vehiculos= data.getVehiculos();
        if(recibos.size()>0){
            ArrayList<String> tramaRecibos = new ArrayList<>();
            String usuario= readPreference(ConfigActivity.PREF_USUARIO_ACTIVO);
            for (ReciboModel r : recibos) {
                if(r.getEnCadena().equals("0")){
                    VehiculoModel v= data.getVehiculoPlaca(r.getPlaca());
                    String trama =  r.getNumRecibo()+";"+punto.getcodpunto()+";"+r.getFechaHora()+";"
                            +usuario+";"+r.getValRecibo()+";"+ReciboModel.FORM_PAGO+";"
                            +r.getFechaHora().substring(11,16)+";"+ReciboModel.SIN_ORIGEN+";"+v.getCodEmpresa()+";"+v.getNumInterno()
                            +";"+v.getPlaca()+";"+FacturaModel.CED_CONDUCTOR+";"+punto.getCodAgenAso()+";"+ String.valueOf(Global.g_Turno)
                            +";"+ReciboModel.TIP_DOC+";"+r.getIDTarjeta()+";"+FacturaModel.MOD
                            +";"+r.getTarSaldo()+";"+FacturaModel.NUM_PLANILLA+";";
                    trama= trama+readPreference(ConfigActivity.PREF_COD_RECARGA)+"/"+r.getValRecibo()+"/"+"0"+"/"+"0"+"/"+r.getValRecibo()+"/"+">";
                    tramaRecibos.add(trama);
                    data.confirmarEnCadenaRecibo(r.getNumRecibo());

                }
            }
            if(tramaRecibos.size()>0) {
                tramRec = readPreference(CAD_REC);
                if(tramRec.equals("")){
                    String jsonRec = gson.toJson(tramaRecibos);
                    writePreference(CAD_REC, jsonRec);
                }else{
                   ArrayList<String> temp= (gson.fromJson(tramRec,ArrayList.class));
                    for(String r: tramaRecibos){
                        temp.add(r);
                    }
                    String jsonRec = gson.toJson(temp);
                    writePreference(CAD_REC, jsonRec);
                }
            }

        }



    }

    public void generarTramaFacturas() {
        ArrayList<FacturaModel> facturas = data.getFacturas();
        if(facturas.size()>0){
            ArrayList<String> tramaFac = new ArrayList<>();
            for (FacturaModel f : facturas) {
                if(f.getEnCadena().equals("0")){
                    String interno= data.getVehiculoPlaca(f.getPlaca()).getNumInterno();
                    String trama =  f.getNumFac()+";"+punto.getcodpunto()+";"
                            +f.getFecha()+";"+f.getUsuario()+";"+f.getValor()+";"+f.getForma()+";"
                            +f.getFecha().substring(11,16)+";"+FacturaModel.FAC_COD_DESTINO+";"+f.getEmpresa()+";"+interno+";"+f.getPlaca()
                            +";"+FacturaModel.CED_CONDUCTOR+";"+f.getAsociada()+";"+Global.g_Turno.toString()+";"+FacturaModel.TIP_DOC
                            +";"+f.getIdTarjeta()+";"+FacturaModel.MOD+";"+f.getSaldo()+";"+FacturaModel.NUM_PLANILLA+";";
                    trama= trama+readPreference(ConfigActivity.PREF_COD_CONTROL)+"/"+f.getValor()+"/"+"0"+"/"+"0"+"/"+f.getValor()+"/"+">";
                    tramaFac.add(trama);
                    data.confirmarEnCadenaFactura(String.valueOf(f.getNumFac()));

                }

            }
            if(tramaFac.size()>0) {
                tramFac = readPreference(CAD_FAC);
                if(tramFac.equals("")){
                    String jsonFac = gson.toJson(tramaFac);
                    writePreference(CAD_FAC,jsonFac);
                }else{
                    ArrayList<String> temp= (gson.fromJson(tramFac,ArrayList.class));
                    for(String r: tramaFac){
                        temp.add(r);
                    }
                    String jsonFac = gson.toJson(temp);
                    writePreference(CAD_FAC,jsonFac);
                }
            }


        }


    }
    // Se envían los datos del control de tiempos al servidor

    public void limpiarDatos(){
        String tramFac="";
        String tramRec="";
        String tramCtrl="";
        ArrayList<String>outFac=null;
        ArrayList<String>outRec=null;
        ArrayList<String>outCtrl=null;
        Boolean entCtrl = false;
        Boolean entFac=false;
        Boolean entRec=false;
        contTramas=0;
    }

    public void Transmitir(String ISO, String parametro){

        String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
        String iso = "&ISO=" + ISO;
        String param = "&parametro=" + parametro;
        String url = EJECUTA_ISO;
        boolean conectado = Utils.isOnline(MainActivity.this);
        sweetAlertDialog.dismiss();
        if (conectado) {
            sweetAlertDialog = mensaje.progreso(contexto, "Enviando Datos");
            sweetAlertDialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                respuesta rpta = new Gson().fromJson(response, respuesta.class);
                                final Boolean usuVer = (rpta.getEstado()) ? true : false;
                                String resp = rpta.getRespuesta();
                                if (usuVer) {
                                    simpleProgressBar.setProgress(simpleProgressBar.getProgress() + 1);
                                    contTramas--;
                                    cantTramas++;
                                    if (entCtrl) {
                                        outCtrl.remove(i);
                                        canCtrl--;
                                    } else if (entFac) {
                                        outFac.remove(j);
                                        canFac--;

                                    } else if (entRec) {
                                        outRec.remove(k);
                                        canRec--;

                                    }
                                    if (contTramas > 0) {
                                        if(cantTramas== cantEnvio){
                                            if (canCtrl> 0) {
                                                String jsonCtrl = gson.toJson(outCtrl);
                                                writePreference(CAD_CTRL, jsonCtrl);
                                            }else {
                                                writePreference(CAD_CTRL, "");
                                            }if (canFac > 0) {
                                                    String jsonFac = gson.toJson(outFac);
                                                    writePreference(CAD_FAC, jsonFac);

                                                }else {
                                                writePreference(CAD_FAC, "");
                                            }
                                            if (canRec > 0) {
                                                String jsonRec = gson.toJson(outRec);
                                                writePreference(CAD_REC, jsonRec);

                                            }else {
                                                writePreference(CAD_REC, "");
                                            }
                                            limpiarDatos();
                                            simpleProgressBar.setVisibility(View.INVISIBLE);
                                            sweetAlertDialog.dismiss();
                                            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(MainActivity.this, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();

                                                }
                                            });
                                            sweetAlertDialog.show();
                                            cantTramas=0;
                                            cantEnvio=0;
                                            }else {
                                            if (canCtrl > 0) {
                                                entCtrl = true;
                                                entFac = false;
                                                entRec = false;
                                                Transmitir(Utils.ISO_CONTROL, outCtrl.get(i));
                                            } else if (canFac > 0) {
                                                entCtrl = false;
                                                entFac = true;
                                                entRec = false;
                                                Transmitir(Utils.ISO_FACTURA, outFac.get(j));
                                            } else if (canRec > 0) {
                                                entCtrl = false;
                                                entFac = false;
                                                entRec = true;
                                                Transmitir(Utils.ISO_FACTURA, outRec.get(k));
                                            }
                                        }

                                    } else {
                                        limpiarDatos();
                                        writePreference(CAD_CTRL, "");
                                        writePreference(CAD_FAC, "");
                                        writePreference(CAD_REC, "");
                                        simpleProgressBar.setVisibility(View.INVISIBLE);
                                        sweetAlertDialog.dismiss();
                                        sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(MainActivity.this, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                                        cantTramas=0;
                                        cantEnvio=0;
                                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();

                                            }
                                        });
                                        sweetAlertDialog.show();
                                    }
                                } else {
                                    if (canCtrl> 0) {
                                        String jsonCtrl = gson.toJson(outCtrl);
                                        writePreference(CAD_CTRL, jsonCtrl);
                                    }else {
                                        writePreference(CAD_CTRL, "");
                                    }if (canFac > 0) {
                                        String jsonFac = gson.toJson(outFac);
                                        writePreference(CAD_FAC, jsonFac);

                                    }else {
                                        writePreference(CAD_FAC, "");
                                    }
                                    if (canRec > 0) {
                                        String jsonRec = gson.toJson(outRec);
                                        writePreference(CAD_REC, jsonRec);

                                    }else {
                                        writePreference(CAD_REC, "");
                                    }
                                    limpiarDatos();
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    sweetAlertDialog.dismiss();
                                    sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(MainActivity.this, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();

                                        }
                                    });
                                    sweetAlertDialog.show();
                                    cantTramas=0;
                                    cantEnvio=0;
                                    mensaje.MensajeAdvertencia(getApplicationContext(),Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                                }

                            }catch (Exception e) {
                                if (canCtrl> 0) {
                                    String jsonCtrl = gson.toJson(outCtrl);
                                    writePreference(CAD_CTRL, jsonCtrl);
                                }else {
                                    writePreference(CAD_CTRL, "");
                                }if (canFac > 0) {
                                    String jsonFac = gson.toJson(outFac);
                                    writePreference(CAD_FAC, jsonFac);

                                }else {
                                    writePreference(CAD_FAC, "");
                                }
                                if (canRec > 0) {
                                    String jsonRec = gson.toJson(outRec);
                                    writePreference(CAD_REC, jsonRec);

                                }else {
                                    writePreference(CAD_REC, "");
                                }
                                limpiarDatos();
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                sweetAlertDialog.dismiss();
                                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(MainActivity.this, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();

                                    }
                                });
                                sweetAlertDialog.show();
                                cantTramas=0;
                                cantEnvio=0;
                                mensaje.MensajeAdvertencia(getApplicationContext(),Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                                e.printStackTrace();

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (canCtrl> 0) {
                                String jsonCtrl = gson.toJson(outCtrl);
                                writePreference(CAD_CTRL, jsonCtrl);
                            }else {
                                writePreference(CAD_CTRL, "");
                            }if (canFac > 0) {
                                String jsonFac = gson.toJson(outFac);
                                writePreference(CAD_FAC, jsonFac);

                            }else {
                                writePreference(CAD_FAC, "");
                            }
                            if (canRec > 0) {
                                String jsonRec = gson.toJson(outRec);
                                writePreference(CAD_REC, jsonRec);

                            }else {
                                writePreference(CAD_REC, "");
                            }
                            limpiarDatos();
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            sweetAlertDialog.dismiss();
                            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(MainActivity.this, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();

                                }
                            });
                            sweetAlertDialog.show();
                            cantTramas=0;
                            cantEnvio=0;
                            mensaje.MensajeAdvertencia(MainActivity.this,Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                            cantTramas=0;
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse == null ) {
                                error.printStackTrace();
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this,"Info","No se pudo conectar con el servidor");
                            }
                            else if (networkResponse != null ) {
                                error.printStackTrace();
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this,"Info","No se pudo conectar con el servidor");
                            }
                        }
                    }
            ) {

                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    params.put("puerto", readPreference(ConfigActivity.PREF_PUERTO));
                    params.put("ISO",ISO );
                    params.put("parametro",parametro);
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(MainActivity.this).add(postRequest);

        }else{
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(MainActivity.this,"Advertencia", "Movil sin conexión");
        }
    }



    public void lanzarLoguin(){
        startActivity(new Intent(MainActivity.this, Loguin_Activity.class));
    }

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs = getSharedPreferences(ConfigActivity.PREF_GENERAL, MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
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
            mensaje.MensajeAdvertencia(getApplicationContext(),"Advertencia",ex.getMessage() );
            return "-1";
        }
        return valor;
    }



    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id== R.id.action_settings){
            DescargaEmpresas();
        }
        return super.onOptionsItemSelected(item);
    }

    public void mostrarError(VolleyError error){
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


      private void DescargaEmpresas(){
        try {
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Empresas");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_EMPRESAS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<EmpresaModel>>(){}.getType();
                                ArrayList<EmpresaModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarEmpresas();
                                    data.insertarEmpresas(respon);
                                   // descargaDestinos();
                                    sweetAlertDialog.dismiss();
                                    descargaVehiculos();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo destinos");

                                }

                            }catch (Exception e){
                                  sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Destinos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_DESTINOS+PUERTO;

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


                            }catch (Exception e){
                              sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Vehiculos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_VEHICULOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<VehiculoModel>>(){}.getType();
                                ArrayList<VehiculoModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarVehiculos();
                                    data.insertarVehiculos(respon);
                                    sweetAlertDialog.dismiss();
                                    descargaTarjetas();

                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo vehículos");
                                }

                            }catch (Exception e){
                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Tarjetas");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_TARJETAS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<TarjetaModel>>(){}.getType();
                                ArrayList<TarjetaModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarTarjetas();
                                    data.insertarTarjetas(respon);
                                    sweetAlertDialog.dismiss();
                                    descargaConceptos();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo tarjetas");
                                }




                            }catch (Exception e){

                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Conceptos");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_CONCEPTOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {

                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<ConceptoModel>>(){}.getType();
                                ArrayList<ConceptoModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarConceptos();
                                    data.insertarConceptos(respon);
                                    sweetAlertDialog.dismiss();
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

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Puntos de Venta");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_PUNTOS+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {

                            Gson gson = new Gson();
                            try {
                                Type listType = new TypeToken<ArrayList<PuntoVentaModel>>(){}.getType();
                                ArrayList<PuntoVentaModel> respon = gson.fromJson(response,listType);

                                if (respon != null){
                                    data.eliminarPuntos();
                                    data.insertarPuntos(respon);
                                    String nomPunto= data.getPuntoPuerto(readPreference(ConfigActivity.PREF_PUERTO)).getNombre();
                                    writePreference(ConfigActivity.PREF_NOM_PUNTO,nomPunto);
                                    sweetAlertDialog.dismiss();
                                    descargaFrecuencias();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo Puntos de venta");
                                }

                            }catch (Exception e){

                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Resoluciones");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.CONSULTAR_RESOL+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            try {
                                respuesta rpta = new Gson().fromJson(response,respuesta.class);
                                if(rpta.getEstado()){
                                    desglosaResolucion(rpta.getRespuesta());
                                    sweetAlertDialog.dismiss();
                                    descargaPuntosVenta();
                                    // consultarConsecutivos();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,"No descargo resolucion");
                                }

                            }catch (Exception e){

                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                mostrarError(error);
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
            if (!Utils.isOnline(MainActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Parametros");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.CONSULTAR_PARAM+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            try {
                                respuesta rpta = new Gson().fromJson(response,respuesta.class);
                                if(rpta.getEstado()){
                                    desglosaParametros(rpta.getRespuesta());
                                    sweetAlertDialog.dismiss();
                                    consultarEmpresasNoCobro();
                                }else{
                                    sweetAlertDialog.dismiss();
                                    mensaje.MensajeAdvertencia(contexto,Mensaje.MEN_ADV,    "No descargo parametros");
                                }


                            }catch (Exception e){
                                sweetAlertDialog.dismiss();

                                mensaje.MensajeAdvertencia(contexto,
                                        Mensaje.MEN_ADV,"Error al descargar: ");
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                   mostrarError(error);
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
            if (!Utils.isOnline(MainActivity.this)) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando empresas no cobro");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.CONSULTAR_EMP_NO_COBRO+PUERTO;

            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        try {
                            respuesta rpta = new Gson().fromJson(response,respuesta.class);
                            if(rpta.getEstado()){
                                sweetAlertDialog.dismiss();
                                writePreference(ConfigActivity.PREF_EMP_NO,rpta.getRespuesta());
                                sweetAlertDialog= mensaje.MensajeConfirmacionExitosoConUnBoton(MainActivity.this
                                        ,Mensaje.MEN_CONFIRMA,"Datos Actualizados!");
                                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss());
                                sweetAlertDialog.show();
                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this,Mensaje.MEN_ADV,"No descargo empresas no cobro");
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
                mostrarError(error);
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
            if (Utils.isOnline(MainActivity.this) != true) {
                mensaje.MensajeAdvertencia(contexto , Mensaje.MEN_ADV, Mensaje.MSG_MOVILSINCX);
                return;
            }
            final Data data = new Data(contexto);
            sweetAlertDialog = mensaje.progreso(contexto, "Descargando Frecuencias");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.DESCARGA_FRECUENCIAS+PUERTO;

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

                    }, error -> mostrarError(error)
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(contexto).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void desglosaResolucion(String respuesta){
        String[] desglose = respuesta.split(";");
        writePreference(ConfigActivity.PREF_NUMRES,desglose[0]);
        writePreference(ConfigActivity.PREF_FECRES,desglose[1]);
        writePreference(ConfigActivity.PREF_INIRES,desglose[2] );
        writePreference(ConfigActivity.PREF_FINRES,desglose[3] );
        writePreference(ConfigActivity.PREF_PREFRES,desglose[4] );

    }

    public void desglosaParametros(String respuesta){
        String[] desglose = respuesta.split(";");
        writePreference(ConfigActivity.PREF_IVA,desglose[0]);
        writePreference(ConfigActivity.PREF_ACT_ECON,desglose[1] );
        writePreference(ConfigActivity.PREF_COD_RECARGA,desglose[2] );
        writePreference(ConfigActivity.PREF_COD_CONTROL,desglose[3] );

    }


    
}