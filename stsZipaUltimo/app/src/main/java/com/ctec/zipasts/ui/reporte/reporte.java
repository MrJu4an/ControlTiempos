package com.ctec.zipasts.ui.reporte;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import com.ctec.zipasts.MainActivity;
import com.ctec.zipasts.R;
import com.ctec.zipasts.ui.Card.ActionCallbackImpl;
import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Constants;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Loguin.Loguin_Activity;
import com.ctec.zipasts.ui.Model.ControlTiempoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.ctec.zipasts.ui.Model.SpinnerEmpresa;
import com.ctec.zipasts.ui.Model.VehiculoModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wizarpos.mvc.base.ActionCallback;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class reporte extends Fragment {


    private String[] tipBusq={"Interno","Empresa"};
    private Context context;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private Spinner spEmpresa;
    private TextView txtCodBusq;
    private Button btnBuscar;
    private Data data;
    private ArrayList<ControlTiempoModel> listControles;
    private TableLayout tblReport;
    private boolean esOrigen;
    private PuntoVentaModel agencia;
    private SpinnerEmpresa spinnerEmpresa;
    private EmpresaModel[] list;
    private EmpresaModel empresaModel;
    private List<EmpresaModel> empresaList;
    private String resDIAN="";
    private String fecDIAN="";
    private String iniDIAN="";
    private String finDIAN="";
    private String actEcon="";
    private String nomOrigen="";
    private String nomDestino="";
    private String frecuencia="";
    public EmpresaModel empresa;
    private Calendar calendar;
    SimpleDateFormat sdfN = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "CO"));
    SimpleDateFormat sdfImp = new SimpleDateFormat("MMM/dd/yyyy", new Locale("es", "CO"));
    private PrinterDevice printerDevice;
    private Handler handler;
    private int bandera = 0;
    private Handler handlerp;
    private ActionCallback actionCallback;
    private Handler mHandler;
    private Format format;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reporte, container, false);
        spEmpresa= root.findViewById(R.id.spnTipBusq);
        btnBuscar= root.findViewById(R.id.btnBuscReport);
        txtCodBusq= root.findViewById(R.id.txtCodBusq);
        tblReport= root.findViewById(R.id.tblControlesReport);
        context= getContext();
        //cargaSpn();
        data= new Data(context);
        mensaje= new Mensaje();
        sweetAlertDialog= new SweetAlertDialog(context);
        calendar= Calendar.getInstance();
        handlerp = new Handler();
        //Inicialización Variables de tarjeta
        mHandler = new Handler();
        actionCallback = new ActionCallbackImpl(getContext(), handler);
        this.empresaList= data.getEmpresas();
        Collections.sort(this.empresaList,(empresaModel, t1) -> Integer.parseInt(t1.getCodigo()));
        this.list = new EmpresaModel[this.empresaList.size()];
        for (int j = 0; j < this.empresaList.size(); j++) {
            this.list[j] = this.empresaList.get(j);
        }
        spinnerEmpresa = new SpinnerEmpresa(context,R.layout.support_simple_spinner_dropdown_item,this.list);

        spEmpresa.setAdapter(spinnerEmpresa);
        spEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reporte reporte = com.ctec.zipasts.ui.reporte.reporte.this;
                empresaModel = reporte.spinnerEmpresa.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnBuscar.setOnClickListener(view -> {

            if (spEmpresa.getSelectedItemPosition() == 0) {
                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Debe seleccionar una empresa");
                return;
            }
            limpiarTabla();
            GetControles();
            buscarControles();
        });
        return root;
    }


    public void cargaSpn(){
        //ArrayAdapter<String> adapter= new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, tipBusq);
        //spnTip.setAdapter(adapter);
    }

    public void cargaDatosIni(){
        String puerto = readPreference(ConfigActivity.PREF_PUERTO);
        agencia = data.getPuntoPuerto(puerto);
        esOrigen = (agencia.getcodciudad().equals("52") || agencia.getcodciudad().equals("90")) ? true : false;
    }


    public void llenarTabla(){
        for(int i =0; i< listControles.size();i++){
            View l= LayoutInflater.from(getContext()).inflate(R.layout.item_table_layout,null,false);
            TextView tvTPlaca = l.findViewById(R.id.tvTPlaca);
            TextView tvTOrigen = l.findViewById(R.id.tvTOrigen);
            // TextView tvTFecha = l.findViewById(R.id.tvTFecha);
            TextView tvTHorigen = l.findViewById(R.id.tvTHorigen);
            Button btnImprimir = l.findViewById(R.id.button);
            //TextView tvTHoraAgencia = l.findViewById(R.id.tvTHoraAgencia);
            //TextView tvTDemora = l.findViewById(R.id.tvTDemora);
            //String punto;
            //punto= String.format("%.7s", data.getPuntoCodCiudad(listControles.get(i).getAgenciaOri()).getNombre());

            PuntoVentaModel punto = data.getPuntoPuerto(readPreference(ConfigActivity.PREF_PUERTO));
            listControles.get(i).setCodPto(punto.getcodpunto());
            String punto1;
            punto1 = String.format("%.7s", data.getPuntoCodigo(punto.getcodpunto()).getNombre());

            tvTPlaca.setText(listControles.get(i).getPlaca());
            tvTOrigen.setText(punto1);
            // tvTFecha.setText((listControles.get(i).getFecha()));
            //tvTHorigen.setText(listControles.get(i).getHoraOrigen());
            tvTHorigen.setText(listControles.get(i).getHoraAgencia());
            //tvTHoraAgencia.setText(listControles.get(i).getHoraOrigen());
            //tvTDemora.setText(listControles.get(i).getDemora());
            final ControlTiempoModel model= listControles.get(i);
            btnImprimir.setOnClickListener(view -> imprimir(model));
            tblReport.addView(l);

        }

    }

    private Runnable myRunnable = new Runnable() {
        public void run() {

        }
    };

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

    private void verificarImp( ControlTiempoModel model) {

        if(bandera==0)
        {
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context,Mensaje.MEN_INFO,Mensaje.MEN_CONFIRMA);
            sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                limpiarTabla();
            });
            sweetAlertDialog.show();

        }else if (bandera==1)
        {

            try {

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(context, "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();

                    try {
                        bandera=0;
                        imprimir(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //startCounter = false;
                        //counter.cancel();
                        mensaje.MensajeAdvertencia(context, "Advertencia", e.getMessage());
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

    public void imprimir(ControlTiempoModel model){
        try {

            bandera=0;
            empresa= data.getEmpresa(empresaModel.getCodigo());
            VehiculoModel vehiculoModel= data.getVehiculoPlaca(model.getPlaca());
            //PuntoVentaModel agenciaDestino = data.getPuntoCodCiudad(String.valueOf(model.getCoddes()));
            PuntoVentaModel agenciaDestino = data.getPuntoCodigo(String.valueOf(model.getCodPto()));
            nomDestino= agenciaDestino.getNombre();
            PuntoVentaModel agenciaOrigen = data.getPuntoCodCiudad(String.valueOf(model.getAgenciaOri()));
            nomOrigen= agenciaOrigen.getNombre();
            if(nomDestino== null)
                nomDestino= nomOrigen;
            if (printerDevice==null) {
                printerDevice = (PrinterDevice) POSTerminal.getInstance(context).getDevice(
                        "cloudpos.device.printer");
            }
            handlerp.post(myRunnable);
            printerDevice.open();
            handlerp.post(myRunnable);
            format = new Format();
            String usuario= readPreference(ConfigActivity.PREF_USUARIO_ACTIVO);
            try {
                if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                    handlerp.post(myRunnable);
                    mensaje.MensajeAdvertencia(context,Mensaje.MEN_INFO,"Impresora sin papel");
                    closePrinter();
                    bandera=1;
                    verificarImp(model);
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
                                printerDevice.printText(format, "NIT: "+Constants.NIT_EMPRESA);
                                printerDevice.printText("\n");
                                formatPiePagina();
                                //* printerDevice.printText(format, Constants.LINEA_DERECHOS);
                                printerDevice.printText("\n");
                                printerDevice.printText(format, Constants.LINEA_DIAN+" "+resDIAN+" del "+fecDIAN);
                                printerDevice.printText("\n");
                                printerDevice.printText(format, Constants.LINEA_RANGO+iniDIAN+" al "+finDIAN);
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText(format,Constants.REGIMEN);
                                printerDevice.printText("\n");
                                printerDevice.printText(format,Constants.ACT_ECON+actEcon+" "+Constants.TARIFA);
                                printerDevice.printText("\n");
                                //*
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatTitulo();
                                printerDevice.printText(format, "DETALLE CONTROL\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                //Verifica que exista papel
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                formatTexto();
                                printerDevice.printText(format,"SUPERVISOR: "+ readPreference(Loguin_Activity.NOM_USU));
                                printerDevice.printText("\n");
                                printerDevice.printText(format,"TRAYECTO:\n");
                                printerDevice.printText(format,nomOrigen+" -> "+nomDestino);
                                printerDevice.printText("\n");
                                if(!nomOrigen.equals(nomDestino))
                                {
                                    printerDevice.printText(format,String.format("Frecuenia : %s ",frecuencia)) ;
                                    printerDevice.printText("\n");
                                }
                                printerDevice.printText(format,"EMPRESA :"+empresa.getCodigo()+" - "+ empresa.getNombre());
                                printerDevice.printText("\n");
                                printerDevice.printText(format,"NIT :"+empresa.getNit());
                                printerDevice.printText("\n");
                                printerDevice.printText(format,"PLACA   :"+model.getPlaca()+" INTERNO:"+vehiculoModel.getNumInterno());
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss", new Locale("es","CO"));
                                String  FecImp  = sdf.format(calendar.getTime());
                                printerDevice.printText(format, "Fecha Act : "+ FecImp.substring(0,12)+"\n");
                                //imprimirTiemControl(FecImp);
                                if(!nomOrigen.equals(nomDestino))
                                {
                                    printerDevice.printText(format, "Hora Reg Origen: "+ model.getHoraOrigen()+"\n");
                                }
                                //printerDevice.printText(format, "Hora Reg Control: "+ model.getHoraAgencia()+"\n");
                                printerDevice.printText(format, "Hora Reg Control: "+ model.getHoraOrigen()+"\n");
                                if(!nomOrigen.equals(nomDestino))
                                {
                                    printerDevice.printText(format,"Demora  : "+model.getDemora()+"\n");
                                }

                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText("VEHICULO ANTERIOR");
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");

                                if(model.getPlacaAnt()== null){
                                    printerDevice.printText("No registra controles anteriores");
                                    printerDevice.printText("\n");
                                    printerDevice.printText("\n");
                                }else{
                                    String dem= model.getDemoraAnt();
                                    String empAnterior= model.getPlacaAnt();
                                    VehiculoModel vehiculoAnterior = data.getVehiculoPlaca(empAnterior);
                                    EmpresaModel empAnt= data.getEmpresa(vehiculoAnterior.getCodEmpresa());
                                    Date fecDate= sdfN.parse(model.getFechaAnt());
                                    String fecFormat= sdfImp.format(fecDate);
                                    printerDevice.printText(format,"EMPRESA: "+empAnt.getCodigo()+" - "+empAnt.getNombre());
                                    printerDevice.printText(format,"PLACA   :"+vehiculoAnterior.getPlaca()+" INTERNO:"+vehiculoAnterior.getNumInterno());
                                    printerDevice.printText(format,"Fecha  : " +fecFormat);
                                    printerDevice.printText(format,"Salida Origen   : "+model.getHoraOrigenAnt());
                                    printerDevice.printText(format,"Hora Registrada : "+model.getHoraAgenciaAnt());
                                    if(!nomOrigen.equals(nomDestino))
                                    {
                                        printerDevice.printText(format,"Demora : "+model.getDemoraAnt());
                                        //if(controlAnterior.)
                                    }


                                    printerDevice.printText("\n");
                                    printerDevice.printText("\n");


                                }
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatPiePagina();
                                printerDevice.printText(format, "Derechos Reservados\n");
                                printerDevice.printText(format, "Consultores Tecnologicos\n");
                                printerDevice.printText(format, "www.consultorestecnologicos.net\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.cutPaper();


                                getActivity().runOnUiThread(() -> {
                                    verificarImp(model);

                                    /*sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(ControlPunta.this,"Info","Correcto");
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            *//*Intent intent = new Intent(getApplicationContext(), Menu.class);
                                            startActivity(intent);*//*
                                            finish();
                                            //return;
                                        }
                                    });
                                    sweetAlertDialog.show();*/
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
                            } catch (ParseException e) {
                                e.printStackTrace();
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

    public void buscarControles(){
        String buscar= txtCodBusq.getText().toString().replace(" ","");
        //int seleccion= spnTip.getSelectedItemPosition();
        buscar=data.getVehiculo(buscar,empresaModel.getCodigo()).getPlaca();
        listControles = data.getControlesPlaca(buscar);
        if(listControles.size()==0){
            mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"No se han encontrado registros grabados localmente");
        }else{
            llenarTabla();
        }
        txtCodBusq.setText("");
    }

    @SuppressLint("ResourceAsColor")

    public void limpiarTabla(){
        Integer count = tblReport.getChildCount();
        if (count > 1){
            tblReport.removeViews(1, count - 1);
        }

    }

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

    private void GetControles(){
        try {

            final Data data = new Data(context);
            sweetAlertDialog = mensaje.progreso(context, "Consultando");
            sweetAlertDialog.show();
            String PUERTO = "?puerto=" + readPreference(ConfigActivity.PREF_PUERTO);
            String interno = txtCodBusq.getText().toString();
            String URL = readPreference(ConfigActivity.PREF_API)+Utils.CONSULTAR_CONTROLES+PUERTO+"&interno="+interno+"&empresa="+empresaModel.getCodigo();
            //String URL = "http://192.168.1.2:53220/api/stszipa/GetControles?puerto="+PUERTO+"&Interno="+txtCodBusq+"&Empresa="+empresaModel.getCodigo();
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    response -> {
                        Gson gson = new Gson();
                        try {
                            Type listType = new TypeToken<ArrayList<ControlTiempoModel>>(){}.getType();
                            ArrayList<ControlTiempoModel> listControles = gson.fromJson(response,listType);

                            if (listControles != null && listControles.size() > 0){
                                for(int i =0; i< listControles.size();i++) {
                                    View l = LayoutInflater.from(getContext()).inflate(R.layout.item_table_layout, null, false);
                                    TextView tvTPlaca = l.findViewById(R.id.tvTPlaca);
                                    TextView tvTOrigen = l.findViewById(R.id.tvTOrigen);
                                    // TextView tvTFecha = l.findViewById(R.id.tvTFecha);
                                    TextView tvTHorigen = l.findViewById(R.id.tvTHorigen);
                                    Button btnImprimir = l.findViewById(R.id.button);
                                    //TextView tvTHoraAgencia = l.findViewById(R.id.tvTHoraAgencia);
                                    //TextView tvTDemora = l.findViewById(R.id.tvTDemora);
                                    String punto;
                                    //punto = String.format("%.7s", data.getPuntoCodCiudad(listControles.get(i).getAgenciaOri()).getNombre());
                                    punto = String.format("%.7s", data.getPuntoCodigo(listControles.get(i).getCodPto()).getNombre());

                                    tvTPlaca.setText(listControles.get(i).getPlaca());
                                    tvTOrigen.setText(punto);
                                    // tvTFecha.setText((listControles.get(i).getFecha()));
                                    //tvTHorigen.setText(listControles.get(i).getHoraOrigen());
                                    tvTHorigen.setText(listControles.get(i).getHoraAgencia());
                                    //tvTHoraAgencia.setText(listControles.get(i).getHoraOrigen());
                                    //tvTDemora.setText(listControles.get(i).getDemora());
                                    final ControlTiempoModel model= listControles.get(i);
                                    btnImprimir.setOnClickListener(view -> imprimir(model));
                                    tblReport.addView(l);
                                }
                                sweetAlertDialog.dismiss();

                            }else{
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"No se encontraron registros grabados anteriormente");

                            }

                        }catch (Exception e){
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(context,
                                    Mensaje.MEN_ADV,"Error al descargar: ");
                            e.printStackTrace();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mostrarError(error);
                }
            }
            ) ;
            //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000,
                    0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}