package com.ctec.zipasts.ui.control;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
/*import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

 */

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.card.Card;
import com.cloudpos.printer.Format;
import com.cloudpos.printer.PrinterDevice;
import com.cloudpos.printer.PrinterDeviceSpec;
import com.cloudpos.rfcardreader.RFCardReaderDevice;
import com.cloudpos.rfcardreader.RFCardReaderOperationResult;
import com.ctec.zipasts.R;
import com.ctec.zipasts.databinding.FragmentControlBinding;
import com.ctec.zipasts.ui.Card.ActionCallbackImpl;
import com.ctec.zipasts.ui.Card.DatoTarjeta;
import com.ctec.zipasts.ui.Card.RFCardAction;
import com.ctec.zipasts.ui.Card.mifare;
import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Constants;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Loguin.Loguin_Activity;
import com.ctec.zipasts.ui.Model.ControlTiempoModel;
import com.ctec.zipasts.ui.Model.DestinoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.FacturaModel;
import com.ctec.zipasts.ui.Model.FrecuenciaModel;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.ctec.zipasts.ui.Model.TarjetaModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;
import com.google.gson.Gson;
import com.wizarpos.mvc.base.ActionCallback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;
import com.cloudpos.jniinterface.RFCardInterface;

public class ControlFragment extends Fragment {

    //  private SlideshowViewModel slideshowViewModel;
    private FragmentControlBinding binding;
    private TextView txtAgencia, txtFechaAct, txtHoraAct,txtCodOrigen;
    private Button btnRegistrar;
    private Context context;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private Data data ;
    private Boolean runReloj=true;
    Handler handlerReloj;
    private SimpleDateFormat formatFec;
    public PuntoVentaModel agencia;
    public PuntoVentaModel agenciaOrigen;
    public EmpresaModel empresa;
    public VehiculoModel vehiculo;
    public ControlTiempoModel control;
    public ControlTiempoModel controlAnterior;
    private TarjetaModel tarjeta;
    public DestinoModel destinoModel;
    public Boolean origen;
    private RFCardReaderDevice device;
    private Card rfCard;
    private RFCardAction rfc;
    private PrinterDevice printerDevice;
    private DatoTarjeta datoTarjeta;
    private String respuesta;
    private Handler handler;
    private ActionCallback actionCallback;
    private Handler mHandler;
    private com.ctec.zipasts.ui.Card.mifare mifare;
    private Calendar calendar;
    private TarjetaModel tarjetaModel;
    private Format format;
    private int bandera = 0;
    private Handler handlerp;
    private String resDIAN="";
    private String fecDIAN="";
    private String iniDIAN="";
    private String finDIAN="";
    private String prefDIAN="";
    private String actEcon="";
    private String nomOrigen="";
    private String nomDestino="";
    private Boolean sinVehAnter;
    private Boolean noImprimirCobro=true;
    private Boolean clickEfectivo=false;
    private Boolean clickTartjeta=false;
    private Boolean clickCancel= false;
    private String prefFac,numFac,concepControl,valorConcep,valorFormat,nomPunto;
    private FrecuenciaModel frecuenciaModel;
    private String demora="00:00:00";
    private String horaAnterior="";
    private String fecActual;
    private String fecAnt;
    private ControlTiempoModel ultControl;
    private String formPago="";
    private String planilla="";
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", new Locale("es", "CO"));
    SimpleDateFormat sdfN = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "CO"));
    SimpleDateFormat sdfImp = new SimpleDateFormat("MMM/dd/yyyy", new Locale("es", "CO"));
    SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss", new Locale("es", "CO"));
    SimpleDateFormat sdfhN = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("es", "CO"));
    SimpleDateFormat sdfhParse = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", new Locale("es", "CO"));
    SimpleDateFormat sdh = new SimpleDateFormat("HH:mm:ss", new Locale("es", "CO"));
    private String codDestino="";
    private String frecuencia="";
    private FacturaModel facturaModel = new FacturaModel();
    private FacturaModel factBase = new FacturaModel();
    private static boolean isOpened=false;
    private Date horaControl;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
      /*  slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);*/

        binding = FragmentControlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
      /*  slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        runReloj=true;
        txtAgencia= root.findViewById(R.id.txtAgencia);
        txtFechaAct=root.findViewById(R.id.txtFecAct);
        txtHoraAct=root.findViewById(R.id.txtHoraAct);
        txtCodOrigen= root.findViewById(R.id.txtCodOrigen);
        btnRegistrar=root.findViewById(R.id.btnRegistrar);
        context= getContext();
        mensaje= new Mensaje();
        sweetAlertDialog= new SweetAlertDialog(context);
        data = new Data(context);
        handlerReloj = new Handler();
        formatFec = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",new Locale("es","CO"));
        calendar= Calendar.getInstance();
        handlerp = new Handler();
        //Inicialización Variables de tarjeta
        mHandler = new Handler();
        handler = new Handler(handlerCallback);
        actionCallback = new ActionCallbackImpl(getContext(), handler);
        mifare = new mifare();
        datoTarjeta = new DatoTarjeta();
        rfc = new RFCardAction();


        btnRegistrar.setOnClickListener(view -> {
            //String txt1 = txtFechaAct.getText().toString();
            //if(txt1.equals("______________")){
                //reloj();
                //cargarDatosIni();
                //mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Vuelva a intentar");
                //return;
            //}
            String aux = txtFechaAct.getText().toString()+" "+ txtHoraAct.getText().toString();
            try {
                horaControl =formatFec.parse(aux);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            noImprimirCobro= true;
            if(!txtCodOrigen.getText().toString().trim().equals("")){
                if(!origen) {
                    String codigo =txtCodOrigen.getText().toString().trim();
                    agenciaOrigen = data.getPuntoCodCiudad(codigo);
                    nomOrigen= agenciaOrigen.getNombre();
                    if(agenciaOrigen!= null) {
                        leerInfoTarjeta();
                    }else{
                        mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"No se ha encontrado el origen con codigo:"+txtCodOrigen.getText().toString().trim());
                    }
                }else{
                    leerInfoTarjeta();


                }
            }else{
                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Debe ingresar algun codigo de origen");
                txtCodOrigen.requestFocus();
            }

        });
        reloj();
        cargarDatosIni();
        return root;
    }
    public void limpiarDatos(){
        if(!origen){
            txtCodOrigen.setText("");
        }

    }

    public void cargarDatosIni(){
        String puerto = readPreference(ConfigActivity.PREF_PUERTO);

        agencia = data.getPuntoPuerto(puerto);
        txtAgencia.setText(agencia.getNombre());
        origen= agencia.getcodciudad().equals("52") || agencia.getcodciudad().equals("90");
        codDestino=  agencia.getcodciudad();
        nomDestino= agencia.getNombre();
        if(origen){
            txtCodOrigen.setEnabled(false);
            txtCodOrigen.setText("No aplica");
            nomOrigen= nomDestino;
            agenciaOrigen=agencia;
        }
        resDIAN= readPreference(ConfigActivity.PREF_NUMRES);
        fecDIAN=readPreference(ConfigActivity.PREF_FECRES);
        iniDIAN= readPreference(ConfigActivity.PREF_INIRES);
        finDIAN= readPreference(ConfigActivity.PREF_FINRES);
        prefDIAN=readPreference(ConfigActivity.PREF_PREFRES);
        actEcon= readPreference(ConfigActivity.PREF_ACT_ECON);
    }

    public void leerInfoTarjeta() {
        AtomicBoolean cardpresent= new AtomicBoolean(false);
        try {

            if (device == null) {

                device=( (RFCardReaderDevice) POSTerminal.getInstance(getContext())
                        .getDevice("cloudpos.device.rfcardreader"));
            }

            sweetAlertDialog = mensaje.progreso(context,"Por favor acerque la tarjeta");
            sweetAlertDialog.show();
            if (!isOpened) {
                rfc.open(device);
                isOpened = true;
            }

            new Handler().postDelayed(() -> {
                if(sweetAlertDialog.isShowing() && !cardpresent.get())
                {
                    sweetAlertDialog.dismiss();
                }
            }, 5000);

            OperationListener listener = arg0 -> {
                if (arg0.getResultCode() == OperationResult.SUCCESS) {
                    rfCard=(((RFCardReaderOperationResult) arg0).getCard());
                    cardpresent.set(true);
                    respuesta=mifare.leerInfoTarjeta(datoTarjeta, rfCard );
                    if (!respuesta.equals("ITE"))
                    {
                        if(sweetAlertDialog.isShowing())
                        {
                            sweetAlertDialog.dismiss();
                            actionCallback.sendResponse(respuesta);
                        }
                    }
                    if(datoTarjeta.getUltHoraRec().length()>4){
                        horaAnterior= datoTarjeta.getUltHoraRec().substring(0,2)+":"+datoTarjeta.getUltHoraRec().substring(2,4)+":"+datoTarjeta.getUltHoraRec().substring(4,6);
                    }else{
                        horaAnterior= "00:00:00";
                    }actionCallback.sendResponse(respuesta);
                    mHandler.postDelayed(() -> {

                    },3000);

                } else {
                    if (sweetAlertDialog.isShowing())
                    {
                        sweetAlertDialog.dismiss();
                    }
                }
            };
            device.listenForCardPresent(listener, TimeConstants.SECOND * 5);

        } catch (DeviceException e) {
            try {
                if (sweetAlertDialog.isShowing())
                {
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(context,"Error:",e.toString());
                }
                device.close();
                isOpened=false;
            } catch (DeviceException deviceException) {
                deviceException.printStackTrace();
                try {
                    device.close();
                    isOpened=false;
                } catch (DeviceException exception) {
                    exception.printStackTrace();
                }

            }
            e.printStackTrace();
            //    sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    private Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            reloj();
            String  respuesta ="";
            respuesta = msg.obj.toString().replaceAll("[^\\p{Alpha}]", "");
            if(sweetAlertDialog.isShowing())
                sweetAlertDialog.dismiss();
            try{
                calcularFrec();
                if( respuesta.equals("ITE") )
                {
                    String nomEmpresa= data.getEmpresa(datoTarjeta.getCodEmpresa().trim()).getNombre();
                    datoTarjeta.setNomEmpresa(nomEmpresa);
                    tarjetaModel= data.getTarjeta(datoTarjeta.getPlacaPort());
                    empresa= data.getEmpresa(datoTarjeta.getCodEmpresa());
                    vehiculo= data.getVehiculoPlaca(datoTarjeta.getPlacaPort());
                    if (vehiculo.getPlaca() == null){
                        mensaje.MensajeAdvertencia(getContext(), Mensaje.MEN_INFO,"No se logró identificar la placa de la tarjeta");
                        return false;
                    }
                    // horaAnterior= datoTarjeta.getUltHoraRec();
                    /*if(!tarjetaModel.getTipo().equals("C")){
                        if(sweetAlertDialog.isShowing())
                            sweetAlertDialog.dismiss();
                        mensaje.MensajeAdvertencia(getContext(),Mensaje.MEN_ADV,"Tarjeta no valida para tiempos!");
                    }else{
                        guardarTiempo(device);
                        if(sweetAlertDialog.isShowing())
                            sweetAlertDialog.dismiss();
                        return true;
                    }*/

                    guardarTiempo(device);
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    return true;

                }else if (respuesta.equals("EIT"))
                {
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(getContext(),"Info","Error Intente Nuevamente");
                    return false;


                }else if(respuesta.equals("CLONADA")){
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(getContext(),Mensaje.MEN_ADV,"Tarjeta Clonada!");
                    return false;
                }else if(respuesta.equals("RSE")){
                    if(!origen) {
                        demora = calcularDemora(agenciaOrigen.getcodciudad(), horaAnterior);
                    }else{
                        noImprimirCobro=encontrarEmpNoCobro(empresa.getNombre());
                        demora="00:00:00";
                        SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss", new Locale("es", "CO"));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "CO"));
                        horaAnterior=  datoTarjeta.getUltHoraRec();
                    }
                    fecActual= sdfhN.format(new Date());
                    controlAnterior = data.getUltimoControlOrigen(agenciaOrigen.getcodciudad());
                    writePreference("Anterior",new Gson().toJson(controlAnterior));
                    nomPunto = data.getPuntoPuerto(readPreference(ConfigActivity.PREF_PUERTO)).getNombre();
                    cargarParImpr();
                    iniciaImpresionFactura();
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    return true;
                }else if(respuesta.equals("PAGADO")){
                    guardarFactura();
                    String totTar= readPreference(ConfigActivity.PREF_TOTALTARJETA);
                    int intTar= (totTar.equals(""))?0:Integer.parseInt(totTar);
                    int valor= Integer.parseInt(valorConcep)+intTar;
                    writePreference(ConfigActivity.PREF_TOTALTARJETA,String.valueOf(valor));
                    imprimirControl();
                    String demora = calculoHoras(facturaModel.getFacfecsalen(), facturaModel.getFacfecsalr(), frecuencia);
                    if(!origen){
                        txtCodOrigen.setText("");
                    }
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    return true;
                }else if(respuesta.equals("MENOS")){
                    if(sweetAlertDialog.isShowing())
                        sweetAlertDialog.dismiss();
                    sweetAlertDialog= mensaje.MensajeConfirmacionAdvertenciaConUnBoton(context,Mensaje.MEN_ADV,"No posee saldo suficiente");
                    sweetAlertDialog.setConfirmClickListener(sweetAlertDialog -> dialogPago());
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
    };

    private String calculoHoras(String horIni, String horfin, String horFrec) {

        int horaIni = 0, horaFin = 0, horaFrec = 0, hor = 0, mit = 0, seg = 0;
        String linea="";




        /* convertir hora inicial */
        horaIni = convertirHoras(horIni);



        /* convertir hora Final */
        horaFin = convertirHoras(horfin);



        /* convertir hora Frecuencia */
        horaFrec = convertirHoras(horFrec);


        hor = (horaFin - horaIni - horaFrec) / 3600;



        if (hor >= 0 && horaFrec != 0)
        {
            mit = (horaFin - horaIni - horaFrec) / 60 - (hor * 60);

            if (mit < 0)
                linea= "00:00:00";

            seg = (horaFin - horaIni - horaFrec) - (mit * 60) - (hor * 3600);

            if (seg < 0)
                linea = "00:00:00";


            linea= String.format(new Locale("es","CO") ,"%.2d:%.2d:%.2d", hor, mit, seg);

            if (hor > 0 || mit > 30){}
            //facturaModel.setFacban(1);
        }
        else
        {
            linea = "00:00:00";
        }


        return linea;
    }

    public void iniciaImpresionFactura() {
        if(!noImprimirCobro && origen) {
            dialogPago();
            dialogPago().show();
        }else{
            guardaControl(vehiculo.getPlaca(),fecActual,agenciaOrigen.getcodciudad(),horaAnterior,datoTarjeta.getUltHoraRec());
            guardarFactura();
            imprimirControl();
            if(!origen){
                txtCodOrigen.setText("");
            }

        }

    }


    public void imprimirFacturaEfe(){
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
            String usuario= readPreference(ConfigActivity.PREF_USUARIO_ACTIVO);
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
                                formatPiePagina();
                                printerDevice.printText(format, Constants.LINEA_DERECHOS);
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
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                imprimirCobroRuta();
                                formatPiePagina();
                                printerDevice.printText(format, "Derechos Reservados\n");
                                printerDevice.printText(format, "Consultores Tecnologicos\n");
                                printerDevice.printText(format, "www.consultorestecnologicos.com\n");
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

    public void guardarFactura(){

        String numFac = readPreference(ConfigActivity.PREF_NUMFAC);
        Date fecAct= new Date();

        if(noImprimirCobro){
            facturaModel.setValor(String.valueOf(0));
            //facturaModel.setNumFac(0);
            facturaModel.setForma("EFE");
            facturaModel.setValor("0");
        }else{
            facturaModel.setForma(formPago);
            facturaModel.setValor(valorConcep);
            facturaModel.setNumFac(Long.parseLong(numFac));
        }
        facturaModel.setAsociada(agencia.getCodAgenAso());
        facturaModel.setDestino(FacturaModel.FAC_COD_DESTINO);
        facturaModel.setFecha(fecActual);
        facturaModel.setPlaca(vehiculo.getPlaca());
        facturaModel.setSaldo("0");
        facturaModel.setEmpresa(empresa.getCodigo());
        facturaModel.setCodAgencia(agencia.getcodpunto());
        facturaModel.setIdTarjeta(datoTarjeta.getIdCard());
        //factBase= data.getFacturaBase(facturaModel.getOrigen());
        facturaModel.setPlanilla(FacturaModel.NUM_PLANILLA);
        facturaModel.setTurno(String.valueOf(readPreference(Loguin_Activity.PREF_TURNO)));
        facturaModel.setUsuario(readPreference(ConfigActivity.PREF_USUARIO_ACTIVO));
        facturaModel.setEnCadena("0");
        facturaModel.setFacfecsalen(datoTarjeta.getUltHoraRec());
        facturaModel.setFacfecsalr(fecActual);
        data.insertarFactura(facturaModel);
        if(!noImprimirCobro){
            String nuevoNumFac= String.valueOf(Integer.parseInt(numFac)+1);
            writePreference(ConfigActivity.PREF_NUMFAC,nuevoNumFac);
            String totalFac = (readPreference(ConfigActivity.PREF_TOTAL_CONTROL).equals(""))?"0":readPreference(ConfigActivity.PREF_TOTAL_CONTROL);
            int totalControles= Integer.parseInt(totalFac)+Integer.parseInt(valorConcep);
            writePreference(ConfigActivity.PREF_TOTAL_CONTROL,String.valueOf(totalControles));
        }

    }

    public AlertDialog dialogPago() {
        final AlertDialog alertDialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflar y establecer el layout para el dialogo
        // Inflar y establecer el layout para el dialogo
        // Pasar nulo como vista principal porque va en el diseño del diálogo
        View v = inflater.inflate(R.layout.dialog_pago, null);
        //builder.setView(inflater.inflate(R.layout.dialog_signin, null))
        Button btnEfectivo = (Button) v.findViewById(R.id.btnEfectivo2);
        Button btnTarjeta = (Button) v.findViewById(R.id.btnTarjeta2);
        Button btnCancelar = (Button) v.findViewById(R.id.btnCancel);
        builder.setView(v);
        alertDialog = builder.create();
        // Add action buttons
        btnEfectivo.setOnClickListener(
                v1 -> {
                    // 2884;
                    clickEfectivo= true;
                    formPago="EFE";
                    //Sebastián Rondón 23 de Marzo - 2023
                    //Si un vehículo vuelva a pasar en menos de 10 min
                    //La factura del control anterior se pasará a valor 0
                    //*****************************************************
                    revisarControlAnterior(vehiculo.getPlaca());
                    //*****************************************************
                    guardaControl(vehiculo.getPlaca(),fecActual,agenciaOrigen.getcodciudad(),horaAnterior,datoTarjeta.getUltHoraRec());
                    guardarFactura();
                    String totEfe= readPreference(ConfigActivity.PREF_TOTALEFECTIVO);
                    int intEfe= (totEfe.equals(""))?0:Integer.valueOf(totEfe);
                    int valor= Integer.valueOf(valorConcep)+intEfe;
                    writePreference(ConfigActivity.PREF_TOTALEFECTIVO,String.valueOf(valor));
                    imprimirControl();
                    //  imprimirFacturaEfe();
                    alertDialog.dismiss();
                    if(!origen){
                        txtCodOrigen.setText("");
                    }


                }
        );
        btnTarjeta.setOnClickListener(
                v12 -> {
                    clickTartjeta=true;
                    formPago= "TAR";
                    guardaControl(vehiculo.getPlaca(),fecActual,agenciaOrigen.getcodciudad(),horaAnterior,datoTarjeta.getUltHoraRec());
                    realizarPagoTarjeta(device,Integer.valueOf(valorConcep));
                    alertDialog.dismiss();
                }
        );

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,"Operación cancelada");
            }
        });
        return alertDialog;
    }


    public FrecuenciaModel buscarFrecuencia(ArrayList<FrecuenciaModel> frecuencias) throws ParseException {
        FrecuenciaModel frec = null;
        Boolean encontrado= false;
        Date fecAct= new Date();
        String fecFecha= sdfN.format(fecAct);
        if(frecuencias.size()==1){
            frec= frecuencias.get(0);
        }else{
            for(int i=0; i< frecuencias.size() && !encontrado;i++){
                FrecuenciaModel frecB= frecuencias.get(i);
                String horIni=fecFecha+" "+frecB.getHoraIni();
                String horFin=fecFecha+" "+frecB.getHoraFin();
                Date fechIni= sdfhN.parse(horIni);
                Date fechFin= sdfhN.parse(horFin);
                if(fecAct.before(fechFin) && fecAct.after(fechIni)){
                    encontrado= true;
                    frec= frecB;
                }

            }
        }
        return frec;
    }

    public String calcularDemora(String codOrigen,String horaMarca) {

        String demoraStr="00:00:00";
        try {
            // Calculo tiempo de ruta

            ArrayList<FrecuenciaModel> frec= data.getFrecuencia(agencia.getcodpunto(), codOrigen);
            frecuenciaModel = buscarFrecuencia(frec);
            Date fecActual = new Date();
            String horaTar = sdfN.format(fecActual)+" " + horaMarca;
            String horacAct = sdfN.format(fecActual)+" "+ datoTarjeta.getUltHoraRec();
            Date dHoraTar = sdfhN.parse(horaTar);
            Date dhoraAct = sdfhN.parse(horacAct);
            long tiempRuta = (dhoraAct != null ? dhoraAct.getTime() : 0) - (dHoraTar != null ? dHoraTar.getTime() :0);
            long resto;
            long totSegundos = tiempRuta / 1000;
            //Calculo tiempo de demora
            String tiemFrec = frecuenciaModel.getTiempFrec();
            if (tiemFrec == null) {
                return demoraStr;
            }
            int horFrec = Integer.parseInt(tiemFrec.substring(0, 2));
            int minFrec = Integer.parseInt(tiemFrec.substring(3, 5));
            int segFrec = Integer.parseInt(tiemFrec.substring(6, 8));
            long totSegFrec = horFrec* 3600L + minFrec*60L + segFrec;
            long dem= totSegundos - totSegFrec;
            // dem=1890;
            if(dem<=0){
                demoraStr="00:00:00";
            }else{
                long horasD = Math.abs(dem / 3600);
                resto = dem % 3600;
                //  resto = resto * 3600;
                long minutosD = Math.abs(resto / 60);
                resto = resto % 60;
                long segundosD = resto;
                if(horasD>0){
                    if(horasD>9){
                        demoraStr= horasD +":";
                    }else{
                        demoraStr= "0"+ horasD +":";
                    }
                }else {
                    demoraStr="00:";

                }
                if(minutosD>0){
                    if(minutosD>9){
                        demoraStr= demoraStr+String.valueOf(minutosD)+":";
                    }else{
                        demoraStr= demoraStr+"0"+String.valueOf(minutosD)+":";
                    }
                }else {
                    demoraStr="00:";

                }
                if(segundosD>0){
                    if(segundosD>9){
                        demoraStr= demoraStr+String.valueOf(segundosD);
                    }else{
                        demoraStr= demoraStr+"0"+String.valueOf(segundosD);
                    }
                }else {
                    demoraStr="00:";

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return demoraStr;

    }


    public void calcularFrec() throws ParseException {
        int entAct, entIni, entFin=0;
        String horaAct="";
        Date fecActual = new Date();
        horaAct = sdfh.format(fecActual);
        frecuencia= "00:00:00";
        entAct = convertirHoras(horaAct);
        ArrayList<FrecuenciaModel> frec= data.getFrecuencia(agencia.getcodpunto(), agencia.getcodciudad());
        if(frec.size()>0)
        {
            FrecuenciaModel tiempo = buscarFrecuencia(frec);
            entIni = convertirHoras(tiempo.getHoraIni());
            entFin = convertirHoras(tiempo.getHoraFin());
            if (entAct >= entIni && entAct <= entFin)
            {
                frecuencia = tiempo.getTiempFrec();
            }
        }

    }

    private int convertirHoras(@NonNull String horaAct) {
        int ent=0;
        String linea="";
        linea= String.format("%.2s",horaAct.substring(0,2));
        ent = Integer.parseInt(linea) * 3600;
        linea="";
        linea= String.format("%.2s",horaAct.substring(3,5));
        ent = ent + Integer.parseInt(linea) * 60;
        linea="";
        linea= String.format("%.2s",horaAct.substring(6,8));
        ent = ent + Integer.parseInt(linea);
        return ent;
    }


    public  void cargarParImpr(){
        prefFac = readPreference(ConfigActivity.PREF_PREFRES);
        numFac = readPreference(ConfigActivity.PREF_NUMFAC);
        concepControl = readPreference(ConfigActivity.PREF_COD_CONTROL);
        valorConcep = data.getConcepto(concepControl).getValTot();
        valorFormat = Utils.dollarFormat.format(Double.valueOf(valorConcep));

    }
    public void guardaControl(String placa,String fecha,String codAgencia, String horOrigen,String horAgencia ){
        String secGuardada= readPreference(ControlTiempoModel.SECUENCIA);
        if(secGuardada.equals("")){
            secGuardada="1";
            writePreference(ControlTiempoModel.SECUENCIA,secGuardada);
        }else{
            int secuenciaIncremento= Integer.parseInt(secGuardada)+1;
            String secuenciaNueva= String.valueOf(secuenciaIncremento);
            writePreference(ControlTiempoModel.SECUENCIA,secuenciaNueva);
            secGuardada = secuenciaNueva;
        }
        control= new ControlTiempoModel();
        control.setPlaca(placa);
        control.setFecha(fecha);
        control.setAgenciaOri(codAgencia);
        control.setSecuencia(secGuardada);
        control.setHoraOrigen(horOrigen);
        control.setHoraAgencia(horAgencia);

        if(origen){
            control.setFrecuencia("00:00:00");
            control.setDemora("00:00:00");
        }else {
            control.setDemora(demora);
            control.setFrecuencia(frecuenciaModel.getTiempFrec());
        }
        control.setEnCadena("0");
        ControlTiempoModel anterior = new Gson().fromJson(readPreference("Anterior"),ControlTiempoModel.class);
        if(anterior!=null){
            control.setCodempant(anterior.getCodempant());
            control.setPlacaAnt(anterior.getPlacaAnt());
            control.setFechaAnt(anterior.getFechaAnt());
            control.setHoraOrigenAnt(anterior.getHoraOrigenAnt());
            control.setHoraAgenciaAnt(anterior.getHoraAgenciaAnt());
            control.setDemoraAnt(anterior.getDemoraAnt());
            //control.setCoddes(anterior.getCoddes());
        }else{
            control.setCodempant(Integer.parseInt(empresa.getCodigo()));
            control.setPlacaAnt(placa);
            control.setFechaAnt(fecha);
            control.setHoraOrigenAnt(horOrigen);
            control.setHoraAgenciaAnt(horAgencia);
            control.setDemoraAnt(control.getDemora());
            //control.setCoddes(Integer.parseInt(codAgencia));
        }
        data.insertarControl(control);
    }

    public void limpiarVariables(){
        limpiarDatos();
        demora="00:00:00";
        nomOrigen="";
        nomDestino="";
        noImprimirCobro=false;
        clickEfectivo=false;
        clickTartjeta=false;
        clickCancel= false;
    }

    public void imprimirControlAnterior() throws DeviceException, ParseException {

        //sinVehAnter=(controlAnterior==null)?true:false;
        ControlTiempoModel anterior = new Gson().fromJson(readPreference("Anterior"),ControlTiempoModel.class);
        if(anterior==null){
            printerDevice.printText("No registra controles anteriores");
            printerDevice.printText("\n");
            printerDevice.printText("\n");
        }else{
            String dem= controlAnterior.getDemora();
            String empAnterior= controlAnterior.getPlaca();
            VehiculoModel vehiculoAnterior = data.getVehiculoPlaca(empAnterior);
            EmpresaModel empAnt= data.getEmpresa(vehiculoAnterior.getCodEmpresa());
            Date fecDate= sdfN.parse(controlAnterior.getFecha());
            String fecFormat= sdfImp.format(fecDate);
            printerDevice.printText(format,"EMPRESA: "+empAnt.getCodigo()+" - "+empAnt.getNombre());
            printerDevice.printText(format,"PLACA   :"+vehiculoAnterior.getPlaca()+" INTERNO:"+vehiculoAnterior.getNumInterno());
            printerDevice.printText(format,"Fecha  : " +fecFormat);
            printerDevice.printText(format,"Salida Origen   : "+controlAnterior.getHoraOrigen());
            printerDevice.printText(format,"Hora Registrada : "+controlAnterior.getHoraAgencia());
            if(!nomOrigen.equals(nomDestino))
            {
                printerDevice.printText(format,"Demora : "+controlAnterior.getDemora());
                //if(controlAnterior.)
            }


            printerDevice.printText("\n");
            printerDevice.printText("\n");


        }
        printerDevice.printText("- - - - - - - - - - - - - - - - ");
        printerDevice.printText("\n");
    }
    public void imprimirTiemControl(String fecImp) throws DeviceException {
        if(!origen){
            printerDevice.printText(format, "Hora Reg Origen: "+ horaAnterior+"\n");
            printerDevice.printText(format, "Hora Reg Control: "+ datoTarjeta.getUltHoraRec()+"\n");
            printerDevice.printText(format,"Demora  : "+demora+"\n");
        }else{
            printerDevice.printText(format, "Hora Reg Control: "+ datoTarjeta.getUltHoraRec()+"\n");
        }
    }

    public void imprimirCobroRuta() throws DeviceException {
        formatTitulo();
        printerDevice.printText(format,Constants.TIT_FAC+prefFac+" - "+numFac);
        formatTexto();
        printerDevice.printText("\n");
        printerDevice.printText("SITIO: "+nomPunto);
        printerDevice.printText(format,"| ITEM | CONCEPTO | VALOR |");
        printerDevice.printText(format,"|   1  | "+concepControl+"       | "+valorConcep+" |");
        String cadena = String.format("SUBTOTAL : %s\n",valorFormat);
        printerDevice.printText(format,cadena);
        cadena = String.format("IVA      : %s","$0\n");
        printerDevice.printText(format,cadena);
        cadena = String.format("TOTAL    : %s\n",valorFormat);
        printerDevice.printText(format,cadena);
        printerDevice.printText("- - - - - - - - - - - - - - - - ");
        cadena = String.format("Forma de pago  : %s\n",formPago);
        printerDevice.printText(format,cadena);
    }

    public boolean encontrarEmpNoCobro(String nomEmpresa){
        nomEmpresa= nomEmpresa.trim();
        boolean encontrado = false;
        String [] empNoCobro= readPreference(ConfigActivity.PREF_EMP_NO).toString().split(";");
        for(int i =0; i< empNoCobro.length && !encontrado; i++){
            encontrado=(empNoCobro[i].trim().equals(nomEmpresa));
        }
        return encontrado;
    }

    public void guardarTiempo(RFCardReaderDevice device){

        try {
            OperationListener listener = arg0 -> {

                if (arg0.getResultCode() == OperationResult.SUCCESS) {
                    rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                    respuesta = actualizarTiempo(rfCard);
                    actionCallback.sendResponse(respuesta);
                    mHandler.postDelayed(() -> {

                    },3000);

                } else {
                    mensaje.MensajeAdvertencia(context,Mensaje.MEN_INFO,"Error");
                }
            };
            device.listenForCardPresent(listener, TimeConstants.FOREVER);

            //   sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            //    sendFailedLog(mContext.getString(R.string.operation_failed));
        }


    }

    public void realizarPagoTarjeta(RFCardReaderDevice device,int pago){

        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {

                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                        respuesta = mifare.realizarPago(pago,rfCard);
                        actionCallback.sendResponse(respuesta);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        },3000);

                    } else {
                        mensaje.MensajeAdvertencia(context,Mensaje.MEN_INFO,"Error");
                    }
                }
            };
            device.listenForCardPresent(listener, TimeConstants.FOREVER);

            //   sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            //    sendFailedLog(mContext.getString(R.string.operation_failed));
        }


    }

    private String actualizarTiempo(Card rfCard)  {
        String retorno="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", new Locale("es","CO"));
            datoTarjeta.setUltFecRec( sdf.format(calendar.getTime()));
            datoTarjeta.setUltHoraRec(sdh.format(horaControl));
            facturaModel.setFacfecsalen(datoTarjeta.getUltHoraRec());
            facturaModel.setFacfecsalr(datoTarjeta.getUltFecRec());
            //mifare.validarClonacion(rfCard);
            String cad = Utils.encriptarDato(datoTarjeta.getUltFecRec());
            String aux = String.format("%16s",cad);
            byte[] arryData =  aux.getBytes();
            Log.d("ultFecRec",String.valueOf( arryData.length));
            //if(!agencia.getNombre().equals("CT SAN CARLOS"))
            if(!agencia.getNombre().equals("S. CARLOS"))
            {
                mifare.writeBlock(rfCard,arryData,8,0);
            }

            cad = Utils.encriptarDato(datoTarjeta.getUltHoraRec());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("ultHoraRec",String.valueOf( arryData.length));
            //if(!agencia.getNombre().equals("CT SAN CARLOS"))
            if(!agencia.getNombre().equals("S. CARLOS"))
            {
                mifare.writeBlock(rfCard,arryData,9,0);
            }

            retorno="RSE";
        }catch (Exception e)
        {
            e.printStackTrace();
            retorno="ERS";
        }

        return  retorno;


    }

    public void imprimirControl(){
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
            String usuario= readPreference(ConfigActivity.PREF_USUARIO_ACTIVO);
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
                                printerDevice.printText(format, "NIT: "+Constants.NIT_EMPRESA);
                                printerDevice.printText("\n");
                                formatPiePagina();
                               /* printerDevice.printText(format, Constants.LINEA_DERECHOS);
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
                                */
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
                                printerDevice.printText(format,"PLACA   :"+vehiculo.getPlaca()+" INTERNO:"+vehiculo.getNumInterno());
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss", new Locale("es","CO"));
                                String  FecImp  = sdf.format(calendar.getTime());
                                printerDevice.printText(format, "Fecha Act : "+ FecImp.substring(0,12)+"\n");
                                //imprimirTiemControl(FecImp);
                                if(!nomOrigen.equals(nomDestino))
                                {
                                    printerDevice.printText(format, "Hora Reg Origen: "+ horaAnterior+"\n");
                                }
                                printerDevice.printText(format, "Hora Reg Control: "+ datoTarjeta.getUltHoraRec()+"\n");
                                if(!nomOrigen.equals(nomDestino))
                                {
                                    printerDevice.printText(format,"Demora  : "+demora+"\n");
                                }

                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText("VEHICULO ANTERIOR");
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                imprimirControlAnterior();
                                if(origen && !noImprimirCobro) {
                                    imprimirFactura();
                                }
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

    public void imprimirFactura() throws DeviceException {
        formatPiePagina();
        printerDevice.printText(format, Constants.LINEA_DERECHOS);
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
        printerDevice.printText("- - - - - - - - - - - - - - - - ");
        imprimirCobroRuta();

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
                            imprimirControl();
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

    public void apagaReloj() {
        runReloj = false;
    }

    /**
     * Mantiene el hilo del reloj que se muestra en pantalla
     */
    public void reloj() {
        Thread hReloj = new Thread(() -> {
            while (runReloj) {
                handlerReloj.post(() -> {

                    Date fActual = new Date();
                    String fAcStr = formatFec.format(fActual);
                    txtFechaAct.setText(fAcStr.substring(0, 10));
                    txtHoraAct.setText(fAcStr.substring(11, 19));
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            }
        });
        hReloj.start();
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

    public void revisarControlAnterior(String placa){
        if(!placa.isEmpty()){
            //Buscamos el último control del vehículo
            ultControl = data.getUltControlPlaca(placa);
            if(ultControl != null){
                //Si se encontró un control vamos comparar sus fechas, para saber si está dentro de los últimos 10 min
                Calendar c = Calendar.getInstance();
                c.add(Calendar.MINUTE, -10);
                fecAnt = sdfhN.format(c.getTime());
                //Convertimos a fechas
                try{
                    Date fecha1 = sdfhN.parse(ultControl.getFecha());
                    Date fecha2 = sdfhN.parse(fecAnt);
                    if(fecha1.after(fecha2)) {
                        //Si es mayor a los últimos 10 min, procedemos a pasar su factura a 0
                        data.pasarFacturaCero(ultControl.getPlaca());

                        //Restamos al total en efectivo
                        String totEfe= readPreference(ConfigActivity.PREF_TOTALEFECTIVO);
                        int intEfe= (totEfe.equals(""))?0:Integer.valueOf(totEfe);
                        int valor= intEfe-Integer.valueOf(valorConcep) ;
                        writePreference(ConfigActivity.PREF_TOTALEFECTIVO,String.valueOf(valor));

                        //Restamos al total por concepto
                        String totCon= readPreference(ConfigActivity.PREF_TOTAL_CONTROL);
                        int intEfeCon = (totCon.equals(""))?0:Integer.valueOf(totCon);
                        int valorCon = intEfeCon-Integer.valueOf(valorConcep);
                        writePreference(ConfigActivity.PREF_TOTAL_CONTROL, String.valueOf(valorCon));
                    }
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }
        }
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
    @Override
    public void onDestroyView() {
        apagaReloj();
        super.onDestroyView();
        binding = null;
    }
}