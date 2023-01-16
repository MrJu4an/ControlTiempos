package com.ctec.zipasts.ui.cerrarturno;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cloudpos.DeviceException;
import com.cloudpos.POSTerminal;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterDeviceSpec;
import com.ctec.zipasts.R;
import com.google.gson.Gson;
import com.ctec.zipasts.MainActivity;
import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Constants;
import com.ctec.zipasts.ui.Helper.Global;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Helper.respuesta;
import com.ctec.zipasts.ui.Loguin.Loguin_Activity;
import com.ctec.zipasts.ui.Model.ControlTiempoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.FacturaModel;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.ctec.zipasts.ui.Model.ReciboModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class cerrarturno extends Fragment {

    public static cerrarturno newInstance() {
        return new cerrarturno();
    }

    private Context context;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private TextView txtEfectivo;
    private TextView txtTarjeta;
    private TextView txtTotal;
    private Format format;
    private int bandera = 0;
    private PrinterDevice printerDevice;
    private Handler handlerp;
    private Handler handler;
    private Calendar calendario;
    private String totalControl;
    private String totalRecargas;
    private Data data;
    private Button btnCerrarTurno;
    private ArrayList<Integer> contVentaEmpresas;
    private ArrayList<Integer> contRecibosEmpresas;
    private Gson gson;
    private PuntoVentaModel punto;
    String tramFac="";
    String tramRec="";
    String tramCtrl="";
    ArrayList<String>outFac;
    ArrayList<String>outRec;
    ArrayList<String>outCtrl;
    private ProgressBar simpleProgressBar;
    private int i=0;
    private int j=0;
    private int k=0;
    Boolean entCtrl = false;
    Boolean entFac=false;
    Boolean entRec=false;
    private static String EJECUTA_ISO ;
    private int contTramas=0;
    int canCtrl;
    int canFac;
    int canRec;
    int cantTramas;
    HashMap<String,Integer> contadorPagosEmpresa= new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root= inflater.inflate(R.layout.fragment_cerrarturno, container, false);
        txtEfectivo= root.findViewById(R.id.txtEfecti);
        txtTarjeta= root.findViewById(R.id.txtTar);
        txtTotal= root.findViewById(R.id.txtTotCierre);
        btnCerrarTurno= root.findViewById(R.id.btnCierreCaja);
        simpleProgressBar= root.findViewById(R.id.progressBarCierre);
        context= getContext();
        Utils.dollarFormat.setMaximumFractionDigits(0);
        mensaje= new Mensaje();
        sweetAlertDialog= new SweetAlertDialog(context);
        handlerp = new Handler();
        handler = new Handler();
        calendario = Calendar.getInstance();
        data= new Data(context);
        gson = new Gson();
        punto = data.getPuntoPuerto(readPreference(ConfigActivity.PREF_PUERTO));
        EJECUTA_ISO = readPreference(ConfigActivity.PREF_API)+ Utils.EJECUTA_ISO;
        cargarDatosCierre();
        btnCerrarTurno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarDatos();

            }
        });

        return root;
    }

    public void generarTramaControl(){
       /* data.eliminarControles();
        data.eliminarFacturas();
        data.eliminarRecibos();
        writePreference(MainActivity.CAD_FAC,"");
        writePreference(MainActivity.CAD_CTRL,"");
        writePreference(MainActivity.CAD_REC,"");*/
        ArrayList<ControlTiempoModel> registros= data.getControles();
        if(registros.size()>0){
            ArrayList<String> tramaControl= new ArrayList<>();
            for(ControlTiempoModel reg: registros){
                if(reg.getEnCadena().equals("0")){
                    EmpresaModel empresa= data.getEmpresa(data.getVehiculoPlaca(reg.getPlaca()).getCodEmpresa());
                    String numInterno= data.getVehiculoPlaca(reg.getPlaca()).getNumInterno();
                    String trama=empresa.getCodigo()+";"+empresa.getNombre()+";"+reg.getPlaca()+";"+numInterno+";"
                            +reg.getFecha()+";"+reg.getHoraOrigen()+";"+reg.getHoraAgencia()+";"+reg.getDemora()+";"+reg.getFrecuencia()+";"+reg.getAgenciaOri()+";"+punto.getcodpunto()+";"
                            +reg.getCodempant()+";"+reg.getPlacaAnt()+";"+reg.getFechaAnt()+";"+reg.getHoraOrigenAnt()+";"+reg.getHoraAgenciaAnt()+";"+reg.getDemoraAnt()+";";
                    tramaControl.add(trama);
                    data.confirmarEnCadenaControl(reg.getSecuencia());

                }

            }
            if(tramaControl.size()>0) {
                tramCtrl = readPreference(MainActivity.CAD_CTRL);
                if(tramCtrl.equals("")){
                    String jsonFac = gson.toJson(tramaControl);
                    writePreference(MainActivity.CAD_CTRL,jsonFac);
                }else{
                    ArrayList<String> temp= (gson.fromJson(tramCtrl,ArrayList.class));
                    for(String r: tramaControl){
                        temp.add(r);
                    }
                    String jsonFac = gson.toJson(temp);
                    writePreference(MainActivity.CAD_CTRL,jsonFac);
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
                tramRec = readPreference(MainActivity.CAD_REC);
                if(tramRec.equals("")){
                    String jsonRec = gson.toJson(tramaRecibos);
                    writePreference(MainActivity.CAD_REC, jsonRec);
                }else{
                    ArrayList<String> temp= (gson.fromJson(tramRec,ArrayList.class));
                    for(String r: tramaRecibos){
                        temp.add(r);
                    }
                    String jsonRec = gson.toJson(temp);
                    writePreference(MainActivity.CAD_REC, jsonRec);
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
                tramFac = readPreference(MainActivity.CAD_FAC);
                if(tramFac.equals("")){
                    String jsonFac = gson.toJson(tramaFac);
                    writePreference(MainActivity.CAD_FAC,jsonFac);
                }else{
                    ArrayList<String> temp= (gson.fromJson(tramFac,ArrayList.class));
                    for(String r: tramaFac){
                        temp.add(r);
                    }
                    String jsonFac = gson.toJson(temp);
                    writePreference(MainActivity.CAD_FAC,jsonFac);
                }
            }


        }


    }
    public void lanzarLoguin(){
        startActivity(new Intent(context, Loguin_Activity.class));
    }
    public void borrarPreferencias(){
        writePreference(ConfigActivity.PREF_USUARIO_ACTIVO,"");
        writePreference(ConfigActivity.PREF_NUMFAC,"");
        writePreference(ConfigActivity.PREF_NUMREC,"");
        writePreference(ConfigActivity.PREF_TOTAL_CONTROL,"");
        writePreference(ConfigActivity.PREF_TOTAL_RECARGAS,"");
        writePreference(ConfigActivity.PREF_TOTALEFECTIVO,"");
        writePreference(ConfigActivity.PREF_TOTALTARJETA,"");
         writePreference(Loguin_Activity.NOM_USU,"");

    }

    public void enviarDatos(){
        try {
            Global.g_Turno= Integer.parseInt(readPreference(Loguin_Activity.PREF_TURNO));
            generarTramaControl();
            generarTramaFacturas();
            generarTramaRecibos();
            tramCtrl = readPreference(MainActivity.CAD_CTRL);
            tramFac = readPreference(MainActivity.CAD_FAC);
            tramRec = readPreference(MainActivity.CAD_REC);
            outCtrl=(tramCtrl.equals("")? null: gson.fromJson(tramCtrl,ArrayList.class));
            outFac= (tramFac.equals("")? null: gson.fromJson(tramFac,ArrayList.class));
            outRec= (tramRec.equals("")? null: gson.fromJson(tramRec,ArrayList.class));
            canCtrl=tramCtrl.equals("")?0:outCtrl.size();
            canFac=tramFac.equals("")?0:outFac.size();
            canRec=tramRec.equals("")?0:outRec.size();
            contTramas= canCtrl+canFac+canRec;

            if (contTramas > 0){

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


            }else{
                cantTramas=0;
                imprimirCierre();
                sweetAlertDialog.dismiss();
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context, "Informacion","Sin datos para enviar" );
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    borrarDatosEnviados();
                    borrarPreferencias();
                    lanzarLoguin();

                });
                sweetAlertDialog.show();
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }



    public void Transmitir(String ISO, String parametro){

        String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
        String iso = "&ISO=" + ISO;
        String param = "&parametro=" + parametro;
        String url = EJECUTA_ISO;
        sweetAlertDialog.dismiss();
        /*  if (conectado) {*/
        sweetAlertDialog = mensaje.progreso(context, "Enviando Datos");
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

                                    if(canCtrl> 0){
                                        entCtrl=true;
                                        entFac= false;
                                        entRec= false;
                                        Transmitir(Utils.ISO_CONTROL, outCtrl.get(i));
                                    } else if(canFac> 0){
                                        entCtrl=false;
                                        entFac= true;
                                        entRec= false;
                                        Transmitir(Utils.ISO_FACTURA, outFac.get(j));
                                    }else if(canRec > 0){
                                        entCtrl=false;
                                        entFac= false;
                                        entRec= true;
                                        Transmitir(Utils.ISO_FACTURA, outRec.get(k));
                                    }

                                } else {
                                    writePreference(MainActivity.CAD_CTRL, "");
                                    writePreference(MainActivity.CAD_FAC, "");
                                    writePreference(MainActivity.CAD_REC, "");

                                    sweetAlertDialog.dismiss();
                                    sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context, "Informacion", "Tramas Transmitidas : " + (cantTramas));
                                    cantTramas=0;
                                    imprimirCierre();
                                    simpleProgressBar.setVisibility(View.INVISIBLE);
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            borrarDatosEnviados();
                                            borrarPreferencias();
                                            lanzarLoguin();

                                        }
                                    });
                                    sweetAlertDialog.show();
                                }
                            } else {

                                if (canCtrl> 0) {
                                    String jsonCtrl = gson.toJson(outCtrl);
                                    writePreference(MainActivity.CAD_CTRL, jsonCtrl);
                                }else{
                                    writePreference(MainActivity.CAD_CTRL, "");
                                }
                                if (canFac > 0) {
                                    String jsonFac = gson.toJson(outFac);
                                    writePreference(MainActivity.CAD_FAC, jsonFac);

                                }else{
                                    writePreference(MainActivity.CAD_FAC, "");
                                }
                                if (canRec > 0) {
                                    String jsonRec = gson.toJson(outRec);
                                    writePreference(MainActivity.CAD_REC, jsonRec);

                                }else{
                                    writePreference(MainActivity.CAD_REC, "");
                                }
                                limpiarDatos();
                                simpleProgressBar.setVisibility(View.INVISIBLE);
                                sweetAlertDialog.dismiss();
                                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context, "Informacion", "Tramas Transmitidas : " + (cantTramas + 1));
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();

                                    }
                                });
                                sweetAlertDialog.show();
                                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                                cantTramas=0;
                            }

                        }catch (Exception e) {
                            if (canCtrl> 0) {
                                String jsonCtrl = gson.toJson(outCtrl);
                                writePreference(MainActivity.CAD_CTRL, jsonCtrl);
                            }else{
                                writePreference(MainActivity.CAD_CTRL, "");
                            }
                            if (canFac > 0) {
                                String jsonFac = gson.toJson(outFac);
                                writePreference(MainActivity.CAD_FAC, jsonFac);

                            }else{
                                writePreference(MainActivity.CAD_FAC, "");
                            }
                            if (canRec > 0) {
                                String jsonRec = gson.toJson(outRec);
                                writePreference(MainActivity.CAD_REC, jsonRec);

                            }else{
                                writePreference(MainActivity.CAD_REC, "");
                            }
                            simpleProgressBar.setVisibility(View.INVISIBLE);
                            sweetAlertDialog.dismiss();
                            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context, "Informacion", "Tramas Transmitidas : " + (cantTramas + 1));
                            cantTramas=0;
                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();

                                }
                            });
                            sweetAlertDialog.show();
                            mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (canCtrl> 0) {
                            String jsonCtrl = gson.toJson(outCtrl);
                            writePreference(MainActivity.CAD_CTRL, jsonCtrl);
                        }else{
                            writePreference(MainActivity.CAD_CTRL, "");
                        }
                        if (canFac > 0) {
                            String jsonFac = gson.toJson(outFac);
                            writePreference(MainActivity.CAD_FAC, jsonFac);

                        }else{
                            writePreference(MainActivity.CAD_FAC, "");
                        }
                        if (canRec > 0) {
                            String jsonRec = gson.toJson(outRec);
                            writePreference(MainActivity.CAD_REC, jsonRec);

                        }else{
                            writePreference(MainActivity.CAD_REC, "");
                        }
                        simpleProgressBar.setVisibility(View.INVISIBLE);
                        sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Tramas Transmitidas : " + (cantTramas));
                        mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Problema la transmitir datos intente mas tarde");
                        cantTramas=0;
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse == null ) {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(context,"Info","No se pudo conectar con el servidor");
                        }
                        else if (networkResponse != null ) {
                            error.printStackTrace();
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(context,"Info","No se pudo conectar con el servidor");
                        }

                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("puerto", readPreference(ConfigActivity.PREF_PUERTO));
                params.put("ISO",ISO );
                params.put("parametro",parametro);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(20000,
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(postRequest);

      /*  }else{
            sweetAlertDialog.dismiss();
            mensaje.MensajeAdvertencia(getContext(),"Advertencia", "Movil sin conexión");
        }*/
    }

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

    public void cargarDatosCierre(){
        String prefEfectivo= readPreference(ConfigActivity.PREF_TOTALEFECTIVO);
        String prefTarjeta= readPreference(ConfigActivity.PREF_TOTALTARJETA);
        String efectivo=(prefEfectivo.equals(""))?"0":prefEfectivo;
        String tarjeta=(prefTarjeta.equals(""))?"0":prefTarjeta;
        totalControl=(readPreference(ConfigActivity.PREF_TOTAL_CONTROL));
        if(totalControl.equals("")){
            totalControl="0";
        }else{
            double valor= Double.valueOf(totalControl);
         totalControl=Utils.dollarFormat.format(valor);
        }
        totalRecargas=(readPreference(ConfigActivity.PREF_TOTAL_RECARGAS));
        if(totalRecargas.equals("")){
            totalRecargas="0";
        }else{
            double valor= Double.valueOf(totalRecargas);
            totalRecargas=Utils.dollarFormat.format(valor);
        }
        txtEfectivo.setText(Utils.dollarFormat.format(Double.valueOf(efectivo)));
        txtTarjeta.setText(Utils.dollarFormat.format(Double.valueOf(tarjeta)));
        txtTotal.setText(Utils.dollarFormat.format(Double.valueOf(efectivo)+Double.valueOf(tarjeta)));

    }

      public void borrarDatosEnviados(){
        data.eliminarRecibos();
        data.eliminarControles();
        data.eliminarFacturas();
    }

    public void imprimirVentasEmpresa() throws DeviceException {
        HashMap<String,Integer> ventasEmpresa= datosVentaEmpresa();
        HashMap<String,Integer> recibosEmpresa= datosRecargasEmpresa();
        int cant= ventasEmpresa.size();
        formatTitulo();
        printerDevice.printText(format, "Control Tiempos" + "\n");
        formatTexto();
        printerDevice.printText("- - - - - - - - - - - - - - - - ");
        printerDevice.printText("\n");
        for (HashMap.Entry<String, Integer> entry : ventasEmpresa.entrySet()) {
            String valFormat= Utils.dollarFormat.format(entry.getValue());
            String nomEmpre= data.getEmpresa(entry.getKey()).getNombre();
            int cantidad= contadorPagosEmpresa.get(entry.getKey());
            printerDevice.printText(format,nomEmpre+" - "+cantidad+" - "+valFormat);
            cant--;
        }
        formatTexto();
        printerDevice.printText("\n");
        printerDevice.printText("- - - - - - - - - - - - - - - - ");
        printerDevice.printText("\n");
        formatTitulo();
        printerDevice.printText(format, "Recargas" + "\n");
        formatTexto();
        cant= recibosEmpresa.size();
        for (HashMap.Entry<String, Integer> entry : recibosEmpresa.entrySet()) {
            String valFormat= Utils.dollarFormat.format(entry.getValue());
            String nomEmpre= data.getEmpresa(entry.getKey()).getNombre();
            printerDevice.printText(format,nomEmpre+" - "+contRecibosEmpresas.get(cant-1)+" - "+valFormat);
            cant++;
        }

    }

    public HashMap<String,Integer> datosVentaEmpresa(){
        ArrayList<FacturaModel> facturas= data.getFacturas();
        ArrayList<String> empEnLista= new ArrayList<>();
        HashMap<String,Integer> pagosEmpresas= new HashMap<>();
        contadorPagosEmpresa= new HashMap<>();
        HashSet empFac = new HashSet();
        contVentaEmpresas= new ArrayList<>();
        for(FacturaModel f: facturas){
            empFac.add(f.getEmpresa());
        }
        for(Object empresa: empFac){
            String codEmpresa= String.valueOf(empresa);
        }
        for(Object e: empFac){
             int sumFac=0;
            int contVent=0;
            for(FacturaModel f: facturas){
                if(f.getEmpresa().equals(String.valueOf(e))){
                    sumFac+= Integer.parseInt( f.getValor());
                    contVent++;
                }
            }
             pagosEmpresas.put(String.valueOf(e),Integer.valueOf( sumFac));
            contadorPagosEmpresa.put(String.valueOf(e),Integer.valueOf( contVent));
        }
        return  pagosEmpresas;

    }

    public HashMap<String,Integer> datosRecargasEmpresa(){
        ArrayList<ReciboModel> recibos= data.getRecibos();
        ArrayList<String> empEnLista= new ArrayList<>();
        HashMap<String,Integer> pagosEmpresas= new HashMap<>();
        HashSet empFac = new HashSet();
        contRecibosEmpresas= new ArrayList<>();
        for(ReciboModel f: recibos){
            String codEmp= data.getVehiculoPlaca(f.getPlaca()).getCodEmpresa();
            empFac.add(codEmp);
        }
        for(Object empresa: empFac){
            String e = String.valueOf(empresa);
            int sumRec=0;
            int contVent=0;
            for(ReciboModel f: recibos){
                String codEmp= data.getVehiculoPlaca(f.getPlaca()).getCodEmpresa();
                if(codEmp.equals(e)){
                    sumRec+= Integer.parseInt( f.getValRecibo());
                    contVent++;
                }
            }
            contRecibosEmpresas.add(contVent);
            pagosEmpresas.put(e,Integer.valueOf(sumRec));
        }
        return  pagosEmpresas;

    }




    public void imprimirCierre(){
        try {
            bandera=0;
            if (printerDevice==null) {
                printerDevice = (PrinterDevice) POSTerminal.getInstance(context).getDevice(
                        "cloudpos.device.printer");
            }
            handlerp.post(myRunnable);
            printerDevice.open();
            handlerp.post(myRunnable);
            format = new Format();
            String usuario= readPreference(Loguin_Activity.NOM_USU);
            String puntoVenta= readPreference(ConfigActivity.PREF_NOM_PUNTO);
            try {
                if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                    handlerp.post(myRunnable);
                    mensaje.MensajeAdvertencia(context,Mensaje.MEN_INFO,"Impresora sin papel");
                    closePrinter();
                    bandera=1;
                    verificarImp();
                } else if (printerDevice.queryStatus() == 1) {
                    handlerp.post(myRunnable);
                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                format.setParameter("align", "center");
                                PrinterDeviceSpec printerDeviceSpec = (PrinterDeviceSpec) POSTerminal.getInstance(
                                        context).getDeviceSpec("cloudpos.device.printer");
                                format = new Format();
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                formatTitulo();
                                printerDevice.printText(format, Constants.NOM_EMPRESA_1);
                                printerDevice.printText("\n");
                                printerDevice.printText(format, Constants.NOM_EMPRESA_2);
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "NIT: " + Constants.NIT_EMPRESA);
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm", new Locale("es", "CO"));
                                String FecImp = sdf.format(calendario.getTime());
                                formatTexto();
                                printerDevice.printText(format, "Fecha Hora : " + FecImp + "\n");
                                printerDevice.printText(format, "SUPERVISOR : " + usuario + "\n");
                                printerDevice.printText(format, "SITIO      : " + puntoVenta + "\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                //Verifica que exista papel
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                formatTitulo();
                                printerDevice.printText(format, "TOTAL POR EMPRESA" + "\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                imprimirVentasEmpresa();
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatTitulo();
                                printerDevice.printText(format, "TOTAL POR CONCEPTO" + "\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatTexto();
                                String cadena = String.format("POR CONTROL TIEMPO : %s\n",totalControl);
                                printerDevice.printText(format,cadena);
                                cadena = String.format("POR RECARGA TARJETA: %s\n",totalRecargas);
                                printerDevice.printText(format,cadena);
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "RESUMEN DE CIERRE"+"\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                cadena = String.format("TOTAL EFECTIVO : %s\n",txtEfectivo.getText().toString());
                                printerDevice.printText(format,cadena);
                                cadena = String.format("TOTAL TARJETAS  : %s\n",txtTarjeta.getText().toString());
                                printerDevice.printText(format,cadena);
                                cadena = String.format("TOTAL CAJA      : %s\n",txtTotal.getText().toString());
                                printerDevice.printText(format,cadena);
                                cadena = String.format("VALOR CONSIGNAR : %s\n",txtEfectivo.getText().toString());;
                                printerDevice.printText(format,cadena);
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "RECIBIDO"+"\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                //Verifica que exita papel
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                formatPiePagina();
                                printerDevice.printText(format, "Derechos Reservados\n");
                                printerDevice.printText(format, "Consultores Tecnologicos\n");
                                printerDevice.printText(format, "www.consultorestecnologicos.net\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.cutPaper();

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        verificarImp();
                                        /*sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(Recarga.this,"Info","Correcto");
                                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), Menu.class);
                                                startActivity(intent);
                                                finish();
                                                return;
                                            }
                                        });
                                        sweetAlertDialog.show();*/
                                    }
                                });
                            } catch (DeviceException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                try {
                                    if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                                        handlerp.post(myRunnable);
                                    } else if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                                        handlerp.post(myRunnable);
                                    } else {
                                        handlerp.post(myRunnable);
                                    }
                                } catch (DeviceException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                    handlerp.post(myRunnable);
                                }
                            } finally {
                                closePrinter();
                            }
                        }
                    });
                    thread.start();
                }
            } catch (DeviceException de) {
                handlerp.post(myRunnable);
                de.printStackTrace();

            }
        } catch (DeviceException de) {
            de.printStackTrace();
            handlerp.post(myRunnable);

        }
    }

    public void formatPiePagina(){
        format.clear();
        format.setParameter("align", "center");
        format.setParameter("bold", "true");
        format.setParameter("size", "small");
    }

    public void formatTitulo(){
        format.clear();
        format.setParameter("align", "center");
        format.setParameter("bold", "true");
        format.setParameter("size", "medium");
    }

    public void formatTexto(){
        format.clear();
        format.setParameter("align", "left");
        format.setParameter("size", "medium");
    }

    public void formatCodigos(){
        format.clear();
        format.setParameter("align", Format.FORMAT_ALIGN_RIGHT);
        format.setParameter("size", "large");
        format.setParameter("bold", "true");
    }
    private void verificarImp() {

        if(bandera==0)
        {
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context,Mensaje.MEN_INFO,Mensaje.MEN_CONFIRMA);
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    /*Intent intent = new Intent(getApplicationContext(), Menu.class);
                    startActivity(intent);*/
                }
            });
            sweetAlertDialog.show();

        }else if (bandera==1)
        {

            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(context, "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            bandera=0;
                            imprimirCierre();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //startCounter = false;
                            //counter.cancel();
                            mensaje.MensajeAdvertencia(context, "Advertencia", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            sweetAlertDialog.dismiss();
                            /*Intent intent = new Intent(getApplicationContext(), Menu.class);
                            startActivity(intent);*/

                        } catch (Exception e) {
                            e.printStackTrace();
                            //startCounter = false;
                            //counter.cancel();
                            mensaje.MensajeAdvertencia(context, Mensaje.MEN_ADV, e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(context, Mensaje.MEN_ADV, e.getMessage());
                sweetAlertDialog.dismiss();
                //startCounter = false;
                //counter.cancel();
            }
        }

    }


    private void closePrinter() {
        try {
            printerDevice.close();
            handlerp.post(myRunnable);
        } catch (DeviceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            handlerp.post(myRunnable);
        }
    }

    private Runnable myRunnable = new Runnable() {
        public void run() {

        }
    };

    public boolean writePreference(String key, String value){
        try {
            SharedPreferences prefs = context.getSharedPreferences(ConfigActivity.PREF_GENERAL, context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.commit();
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(context, Mensaje.MEN_ADV,ex.getMessage() );
            return false;
        }
        return true;
    }

    public String readPreference(String key){
        String valor = "";
        try {
            SharedPreferences prefs = context.getSharedPreferences(ConfigActivity.PREF_GENERAL,context.MODE_PRIVATE);

            valor = prefs.getString(key, "");
        }
        catch (Exception ex){
            ex.printStackTrace();
            mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,ex.getMessage());
            return "-1";
        }
        return valor;
    }

}