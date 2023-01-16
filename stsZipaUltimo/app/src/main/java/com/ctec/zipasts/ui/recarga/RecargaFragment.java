package com.ctec.zipasts.ui.recarga;

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
import com.ctec.zipasts.databinding.FragmentRecargaBinding;
import com.ctec.zipasts.ui.Card.ActionCallbackImpl;
import com.ctec.zipasts.ui.Card.DatoTarjeta;
import com.ctec.zipasts.ui.Card.RFCardAction;
import com.ctec.zipasts.ui.Card.mifare;
import com.ctec.zipasts.ui.Config.ConfigActivity;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Constants;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.ReciboModel;
import com.ctec.zipasts.ui.Model.TarjetaModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;
import com.wizarpos.mvc.base.ActionCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecargaFragment extends Fragment {

   // private RecargaViewModel galleryViewModel;
    private FragmentRecargaBinding binding;
    private Button btnLeer,btnRecargar;
    private TextView txtSaldoAct;
    private TextView txtValRecarga;
    private Mensaje mensaje;
    private SweetAlertDialog sweetAlertDialog;
    private Context context;
    private final static String MEN_CONFIRMA="Desea realizar la recarga por un valor de: ";
    private Card rfCard;
    private RFCardAction rfc = new RFCardAction();
    private PrinterDevice printerDevice;
    private Calendar calendario = Calendar.getInstance();
    private DatoTarjeta datoTarjeta = new DatoTarjeta();
    private String respuesta;
    private Handler handler;
    private ActionCallback actionCallback;
    private Handler mHandler;
    private com.ctec.zipasts.ui.Card.mifare mifare;
    private RFCardReaderDevice device;
    private String valrec="";
    private String resultado="";
    private int bandera = 0;
    private Handler handlerp;
    private Format format;
    private String numRec="0";
    String valorFormat="";
    private Data data;
    private EmpresaModel empresaModel;
    private VehiculoModel vehiculoModel;
    private TarjetaModel tarjetaModel;
    private static boolean isOpened=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
     /*   galleryViewModel =
                new ViewModelProvider(this).get(RecargaViewModel.class);*/

        binding = FragmentRecargaBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
     /*   galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

      */
        btnLeer = root.findViewById(R.id.btnLeerRecarga);
        btnRecargar= root.findViewById(R.id.btnRecargar);
        txtSaldoAct= root.findViewById(R.id.txtSaldoRec);
        txtValRecarga= root.findViewById(R.id.txtValorRecarga);
        context= getContext();
        mensaje= new Mensaje();
        sweetAlertDialog= new SweetAlertDialog(context);
        mHandler = new Handler();
        handler = new Handler(handlerCallback);
        actionCallback = new ActionCallbackImpl(getContext(), handler);
        mifare= new mifare();
        datoTarjeta= new DatoTarjeta();
        rfc= new RFCardAction();
        handlerp = new Handler();
        txtValRecarga.setEnabled(false);
        btnRecargar.setEnabled(false);
        txtValRecarga.setVisibility(View.INVISIBLE);
        data = new Data(getContext());
        vehiculoModel= new VehiculoModel();
        tarjetaModel= new TarjetaModel();
        empresaModel= new EmpresaModel();

        btnLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leerSaldoTarjeta();
            }
        });

        btnRecargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarRecarga();

            }
        });
        return root;
    }

    public void validarRecarga(){
        if( txtValRecarga.length()>0)
        {
            if (Integer.parseInt(txtValRecarga.getText().toString())<2300)
            {
                mensaje.MensajeAdvertencia(context,Mensaje.MEN_INFO,"Valor a recargar no puede ser menor a $ 2300");
                txtValRecarga.requestFocus();
                return;
            }else if (Integer.parseInt(txtValRecarga.getText().toString())> 50000)
            {
                mensaje.MensajeAdvertencia(context,"Info","Valor a recargar no puede ser mayor a $ 50.000");
                txtValRecarga.requestFocus();
                return;
            }

            valorFormat = Utils.dollarFormat.format(Double.valueOf(txtValRecarga.getText().toString()));
            sweetAlertDialog= mensaje.MensajeConfirmacionAdvertenciaConUnBoton(
                    context,Mensaje.MEN_PREG_CONFIRMA,MEN_CONFIRMA+valorFormat);
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    aceptar(device);


                }
            });
            sweetAlertDialog.show();

        }
    }

    public void aceptar(RFCardReaderDevice device){

        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {

                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                        valrec = txtValRecarga.getText().toString().trim();

                        resultado = recargarSaldo(rfCard,valrec);
                        actionCallback.sendResponse(resultado);
                        mHandler.postDelayed(() -> {

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

    private String recargarSaldo(Card rfCard, String valrec)  {
        String retorno="";
        try {
            datoTarjeta.setSaldoAnt(datoTarjeta.getSaldo());
            int valaux = Integer.parseInt(datoTarjeta.getSaldo()) + Integer.parseInt(valrec);
            datoTarjeta.setSaldo(String.format("%s", String.valueOf( valaux)));
            valaux = Integer.parseInt(datoTarjeta.getTotRecargas()) + Integer.parseInt(valrec);
            datoTarjeta.setTotRecargas(String.format("%s", String.valueOf( valaux)));
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            datoTarjeta.setUltFecRec( sdf.format(calendario.getTime()));
            datoTarjeta.setUltHoraRec(calendario.getTime().toString().substring(11,16));
            mifare.validarClonacion(rfCard);
            String cad = Utils.encriptarDato(datoTarjeta.getSaldo());
            String aux = String.format("%16s",cad);
            byte[] arryData =  aux.getBytes();
            Log.d("Saldo",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,7,0);
            cad = Utils.encriptarDato(datoTarjeta.getTotRecargas());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("TotRecargas",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,6,1);
            cad = Utils.encriptarDato(datoTarjeta.getUltFecRec());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("ultFecRec",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,8,0);
            cad = Utils.encriptarDato(datoTarjeta.getUltHoraRec());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("ultHoraRec",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,9,0);
            retorno="RSE";
        }catch (Exception e)
        {
            e.printStackTrace();
            retorno="ERS";
        }

        return  retorno;


    }

    public void leerSaldoTarjeta() {

        try {

            if (device == null) {
                device = (RFCardReaderDevice) POSTerminal.getInstance(context)
                        .getDevice("cloudpos.device.rfcardreader");
            }
            if (!isOpened)
            rfc.open(device);
            isOpened=true;
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                        String validar= validarTarjeta( rfCard );
                        actionCallback.sendResponse(validar);
                        mHandler.postDelayed(() -> {

                        },3000);

                    } else {
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

    private String validarTarjeta(Card rfCard) {

        Boolean valido;
        String respValida="";
        mifare mifare = new mifare();
        valido = mifare.validarClonacion(rfCard);
        if(!valido){
            respValida="CLONADA";
        }else {
            datoTarjeta.setPlacaPort(mifare.leerPlaca(rfCard));
            ArrayList<TarjetaModel> tarjetas= data.getTarjetas();
            for(TarjetaModel t : tarjetas){
                if(t.getPlaca().equals(datoTarjeta.getPlacaPort().trim())){
                    tarjetaModel = t;
                }
            }
            if (tarjetaModel.getPlaca()==null)
            {
                return  "NOEXISTE";
            }
            // tarjetaModel = data.getTarjeta(datoTarjeta.getPlacaPort());
            if(!tarjetaModel.getEstado().equals("A")){
                respValida= "INACTIVA";
            }else {
                if(mifare.leerTipoTarjeta(rfCard).equals("C")){
                    datoTarjeta.setInternoPort(mifare.leerInterno(rfCard));
                    datoTarjeta.setIdCard(mifare.leerIdTarjeta(rfCard));
                    datoTarjeta.setCodEmpresa(mifare.leerCodEmpresa(rfCard));
                    String nomEmpresa = data.getEmpresa(datoTarjeta.getCodEmpresa()).getNombre();
                    datoTarjeta.setNomEmpresa(nomEmpresa);
                    datoTarjeta.setTotRecargas(mifare.leerTotalRecargas(rfCard));
                    datoTarjeta.setSaldo(mifare.leerSaldo(rfCard));
                    datoTarjeta.setUltFecRec(mifare.leerUFRecarga(rfCard));
                    datoTarjeta.setUltHoraRec(mifare.leerUHRecarga(rfCard));
                    datoTarjeta.setTipoTarjeta(mifare.leerTipoTarjeta(rfCard));
                    respValida= "OK";
                }else{
                    respValida= "TIEMPOS";
                }

            }

        }
        return respValida;
    }

    public void guardarRecibo(String placa,String IDTarjeta, String valor){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", new Locale("es","CO"));
        Date fecActual = new Date();
        String fecFormat= sdf.format(fecActual);
        String numRec = readPreference(ConfigActivity.PREF_NUMREC);
        ReciboModel reciboModel = new ReciboModel();
        reciboModel.setNumRecibo(numRec);
        reciboModel.setPlaca(placa);
        reciboModel.setIDTarjeta(IDTarjeta);
        reciboModel.setValRecibo(String.valueOf(valor));
        reciboModel.setFechaHora(fecFormat);
        reciboModel.setTarSaldo(datoTarjeta.getSaldo());
        reciboModel.setEnCadena("0");
        data.insertarRecibo(reciboModel);
        String nuevoNumRec= String.valueOf(Integer.valueOf(numRec)+1);
        writePreference(ConfigActivity.PREF_NUMREC,nuevoNumRec);
    }

    private Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            String  respuesta ="";
            respuesta = msg.obj.toString().replaceAll("[^\\p{Alpha}]", "");
            try{
                if( respuesta.equals("OK") )
                {
                    txtSaldoAct.setText(Utils.dollarFormat.format(Integer.valueOf(datoTarjeta.getTotRecargas())));
                    btnLeer.setEnabled(false);
                    txtValRecarga.setVisibility(View.VISIBLE);
                    txtValRecarga.setEnabled(true);
                    btnRecargar.setEnabled(true);


                    rfc.close();
                }else if (respuesta.equals("RSE"))
                {

                    String auxEfe =  readPreference(ConfigActivity.PREF_TOTALEFECTIVO);
                    if (auxEfe.equals(""))
                    {
                        auxEfe="0";
                    }
                    int totalEfe = Integer.parseInt( auxEfe);
                    totalEfe = totalEfe + Integer.parseInt(valrec);
                    writePreference(ConfigActivity.PREF_TOTALEFECTIVO,String.valueOf(totalEfe));
                    String auxrecarga =  readPreference(ConfigActivity.PREF_TOTAL_RECARGAS);
                    if (auxrecarga.equals(""))
                    {
                        auxrecarga="0";
                    }
                    int totalRecargas= Integer.parseInt(auxrecarga)+ Integer.parseInt(valrec);
                    writePreference(ConfigActivity.PREF_TOTAL_RECARGAS,String.valueOf(totalRecargas));
                    guardarRecibo(tarjetaModel.getPlaca(),datoTarjeta.getIdCard(),valrec);
                    imprimirReciboRecarga();
                    limpiar();
                    // registrarRecarga();

                }else if(respuesta.equals("CLONADA")){
                    mensaje.MensajeAdvertencia(context,"Info","Tarjeta Clonada");
                    return false;
                }else if(respuesta.equals("INACTIVA")){
                    mensaje.MensajeAdvertencia(context,"Info","Tarjeta Inactiva");
                }else if(respuesta.equals("TIEMPOS")){
                    mensaje.MensajeAdvertencia(context,"Info","Tarjeta de tiempos, no valida para realizar recargas");
                }else if(respuesta.equals("NOEXISTE")){
                    mensaje.MensajeAdvertencia(context,"Info","El vehiculo no registra tarjeta activa, no es posible la  recarga");
                }

                else{
                    mensaje.MensajeAdvertencia(context,"Info","Error por favor intente Nuevamente");
                    return false;
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
    };

    private Runnable myRunnable = new Runnable() {
        public void run() {

        }
    };

    private void closePrinter() {
        try {
            printerDevice.close();
            isOpened=false;
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
                            imprimirReciboRecarga();
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

    public void imprimirReciboRecarga(){
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
            String nomPunto = readPreference(ConfigActivity.PREF_NOM_PUNTO);
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
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "Recarga de Tarjeta\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                //Verifica que exista papel
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                formatTexto();
                                printerDevice.printText(format,"RECIBO CAJA : "+numRec);
                                printerDevice.printText("\n");
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm", new Locale("es","CO"));
                                String  FecImp  = sdf.format(calendario.getTime());
                                printerDevice.printText(format, "Fecha Hora : "+ FecImp+"\n");
                                printerDevice.printText(format, "SUPERVISOR : "+usuario+"\n");
                                printerDevice.printText(format, "SITIO      : "+nomPunto+"\n");
                                formatTitulo();
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "DETALLE TARJETA"+"\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatTexto();
                                printerDevice.printText(format,"PLACA   :"+datoTarjeta.getPlacaPort());
                                printerDevice.printText(format,"INTERNO :"+datoTarjeta.getInternoPort());
                                printerDevice.printText("\n");
                                printerDevice.printText(format,"EMPRESA :"+datoTarjeta.getCodEmpresa()+" - "+ datoTarjeta.getNomEmpresa());
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                printerDevice.printText("|  ITEM  |  CONCEPTO  |  VALOR |");
                                printerDevice.printText("\n");
                                printerDevice.printText("|   1    |RECARGA TAR |" +valorFormat.substring(0,7)+" |");
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                formatTexto();
                                String cadena = String.format("SUBTOTAL : %s\n",valorFormat.substring(0,7));
                                printerDevice.printText(format,cadena);
                                cadena = String.format("IVA      : %s","$0\n");
                                printerDevice.printText(format,cadena);
                                cadena = String.format("TOTAL    : %s\n",valorFormat.substring(0,7));
                                printerDevice.printText(format,cadena);
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                cadena = String.format("Forma de pago  : %s\n","EFE");
                                printerDevice.printText(format,cadena);
                                cadena = String.format("Numero Tarjeta : %s\n",datoTarjeta.getIdCard());
                                printerDevice.printText(format,cadena);
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

    public void limpiar(){
        txtSaldoAct.setText("");
        txtValRecarga.setText("");
        btnLeer.setEnabled(true);
        btnRecargar.setEnabled(false);
        txtValRecarga.setVisibility(View.INVISIBLE);
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
            mensaje.MensajeAdvertencia(context,Mensaje.MEN_ADV,ex.getMessage() );
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}