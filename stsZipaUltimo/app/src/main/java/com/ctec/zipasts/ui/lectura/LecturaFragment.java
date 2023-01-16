package com.ctec.zipasts.ui.lectura;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
/*
import androidx.lifecycle.Observer;
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
import com.ctec.zipasts.databinding.FragmentLeerBinding;
import com.wizarpos.mvc.base.ActionCallback;
import com.ctec.zipasts.ui.Card.ActionCallbackImpl;
import com.ctec.zipasts.ui.Card.DatoTarjeta;
import com.ctec.zipasts.ui.Card.RFCardAction;
import com.ctec.zipasts.ui.Card.mifare;
import com.ctec.zipasts.ui.Data.Data;
import com.ctec.zipasts.ui.Helper.Mensaje;
import com.ctec.zipasts.ui.Helper.Utils;
import com.ctec.zipasts.ui.Model.VehiculoModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LecturaFragment extends Fragment {


   // private LecturaViewModel homeViewModel;
    private FragmentLeerBinding binding;
    private Button btnLeer;
    private TextView txtPlaca,txtCodInt,txtCodEmp,txtEstado,txtSaldo, txtNomEmp;
    private Mensaje mensaje = new Mensaje();
    private RFCardReaderDevice device;
    private Card rfCard;
    private RFCardAction rfc;
    private PrinterDevice printerDevice;
    private Calendar calendario = Calendar.getInstance();
    private DatoTarjeta datoTarjeta;
    private String respuesta;
    private Handler handler;
    private ActionCallback actionCallback;
    private Handler mHandler;
    private com.ctec.zipasts.ui.Card.mifare mifare;
    private Button btnImprimir;
    private Data data;
    private SweetAlertDialog sweetAlertDialog;
    private Format format;
    int bandera=0;
    String placa="";
    String saldo="";
    String codInt="";
    String codEmp="";
    String nomEmp="";
    String estado="";
    SimpleDateFormat formatFec = new SimpleDateFormat("dd/MMM/yyyy");


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
     /*   homeViewModel =
                new ViewModelProvider(this).get(LecturaViewModel.class);

      */

        binding = FragmentLeerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        /*
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */
        btnLeer = root.findViewById(R.id.btnLeerTarjeta);
        txtEstado = root.findViewById(R.id.txtEstado);
        txtSaldo = root.findViewById(R.id.txtSaldo);
        txtCodInt= root.findViewById(R.id.txtCodInterno);
        txtPlaca= root.findViewById(R.id.txtPlaca);
        btnImprimir= root.findViewById(R.id.btnImprimir);
        txtCodEmp= root.findViewById(R.id.txtCodEmp);
        txtNomEmp= root.findViewById(R.id.txtNomEmp);
        btnImprimir.setEnabled(false);
        data = new Data(getContext());

        btnLeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeerInfoTarjeta();
            }
        });
        mHandler = new Handler();
        handler = new Handler(handlerCallback);
        actionCallback = new ActionCallbackImpl(getContext(), handler);
        mifare = new mifare();
        datoTarjeta = new DatoTarjeta();
        rfc = new RFCardAction();
        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               placa=txtPlaca.getText().toString();
               saldo=txtSaldo.getText().toString();
               codInt=txtCodInt.getText().toString();
               estado=txtEstado.getText().toString();
               codEmp=txtCodEmp.getText().toString();
               nomEmp=txtNomEmp.getText().toString();
                limpiar();
                imprimirinfoTarjeta();
            }
        });

        return root;

    }

    public void limpiar(){
        txtPlaca.setText("______________");
        txtSaldo.setText("______________");
        txtCodInt.setText("______________");
        txtEstado.setText("______________");
        txtCodEmp.setText("______________");
        txtNomEmp.setText("______________");
    }

    public void limpiarDatos(){
        placa="";
        saldo="";
        codInt="";
        codEmp="";
        nomEmp="";
        estado="";
    }



    public void LeerInfoTarjeta() {

        try {

            if (device == null) {

                device=( (RFCardReaderDevice) POSTerminal.getInstance(getContext())
                        .getDevice("cloudpos.device.rfcardreader"));
            }

            rfc.open(device);

                OperationResult operationResult = device.waitForCardPresent(TimeConstants.FOREVER);
                if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                    rfCard = ((RFCardReaderOperationResult) operationResult).getCard();
                    respuesta=mifare.leerInfoTarjeta(datoTarjeta, rfCard );
                    actionCallback.sendResponse(respuesta);
                    mHandler.postDelayed(() -> {

                    },3000);

                } else {

                }
            } catch (DeviceException e) {
                e.printStackTrace();

            }
    }

    private Handler.Callback handlerCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            String  respuesta ="";
            respuesta = msg.obj.toString().replaceAll("[^\\p{Alpha}]", "");
            try{
                if( respuesta.equals("ITE") )
                {
                    txtPlaca.setText(datoTarjeta.getPlacaPort().trim());
                    txtCodInt.setText(datoTarjeta.getInternoPort().trim());
                    String nomEmpresa= data.getEmpresa(datoTarjeta.getCodEmpresa().trim()).getNombre();
                    txtCodEmp.setText(datoTarjeta.getCodEmpresa().trim());
                    txtNomEmp.setText(nomEmpresa);
                    datoTarjeta.setNomEmpresa(nomEmpresa);
                    txtEstado.setText(datoTarjeta.getEstCard().trim());
                    txtSaldo.setText(Utils.dollarFormat.format(Integer.valueOf(datoTarjeta.getSaldo().trim())));
                    btnImprimir.setEnabled(true);
                }else if (respuesta.equals("EIT"))
                {
                    mensaje.MensajeAdvertencia(getContext(),"Info","Error Intente Nuevamente");


                }else if(respuesta.equals("CLONADA")){
                    mensaje.MensajeAdvertencia(getContext(),Mensaje.MEN_ADV,"Tarjeta Clonada!");
                }


            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }
    };

    private void imprimirinfoTarjeta() {

        try {
            if (printerDevice==null) {
                printerDevice = (PrinterDevice) POSTerminal.getInstance(getContext()).getDevice(
                        "cloudpos.device.printer");
            }
            handlerp.post(myRunnable);
            printerDevice.open();
            handlerp.post(myRunnable);
            format = new Format();
            try {

                if (printerDevice.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                    handlerp.post(myRunnable);
                    mensaje.MensajeAdvertencia(getContext(),"Info","Impresora si papel");
                    closePrinter();
                    return;
                } else if (printerDevice.queryStatus() == PrinterDevice.STATUS_PAPER_EXIST) {
                    String fechaFormat = datoTarjeta.getFechaAsig().substring(0,2)+"/"+datoTarjeta.getFechaAsig().substring(2,4)+"/"+datoTarjeta.getFechaAsig().substring(4,8);
                    handlerp.post(myRunnable);
                    final Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            try {
                                String cadena;
                                format.setParameter("align", "center");
                                PrinterDeviceSpec printerDeviceSpec = (PrinterDeviceSpec) POSTerminal.getInstance(
                                        getContext()).getDeviceSpec("cloudpos.device.printer");

                                format = new Format();
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                formatTitulo();
                                printerDevice.printText("********************************");
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "INFORMACION");
                                printerDevice.printText("\n");
                                printerDevice.printText(format, "TARJETA");
                                printerDevice.printText("\n");
                                printerDevice.printText("********************************");
                                printerDevice.printText("\n");
                                printerDevice.printText("\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM/dd/yyyy HH:mm ", new Locale("es","CO"));
                                String  FecImp  = sdf.format(calendario.getTime());
                                printerDevice.printText(format, "Fecha Hora:"+ FecImp+"\n");
                                printerDevice.printText("- - - - - - - - - - - - - - - - ");
                                printerDevice.printText("\n");
                                format.clear();
                                formatTexto();
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                cadena= String.format("Placa          : %s",placa);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena= String.format("Numero Interno : %s",codInt);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena= String.format("Fecha Asignada : %s",fechaFormat);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena= String.format("Codigo Empresa : %s",codEmp);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena= String.format("Nombre Empresa : %s",nomEmp);
                                printerDevice.printText(format, cadena+" \n" );
                                cadena= String.format("Saldo Tarjeta  : %s",saldo);
                                printerDevice.printText(format, cadena+" \n" );
                                if (printerDevice.queryStatus() == 0)
                                    bandera = 1;
                                format.clear();
                                format.setParameter("align", "center");
                                format.setParameter("bold", "true");
                                format.setParameter("size", "small");
                                printerDevice.printText("- - - - - - - - - - - - - - - - \n");
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
                                     /*   sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(getContext(),"Info","Correcto");
                                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), Menu.class);
                                                startActivity(intent);
                                                //   finish();
                                                //return;
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
                                limpiarDatos();
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

    private void verificarImp() {

        if(bandera==0)
        {
            sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConUnBoton(getContext(),Mensaje.MEN_INFO,Mensaje.MEN_CONFIRMA);
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

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(getContext(), "Impresora sin papel", "Desea reimprimir la Ult?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            bandera=0;
                            imprimirinfoTarjeta();
                        } catch (Exception e) {
                            e.printStackTrace();
                            //startCounter = false;
                            //counter.cancel();
                            mensaje.MensajeAdvertencia(getContext(), "Advertencia", e.getMessage());
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
                            mensaje.MensajeAdvertencia(getContext(), Mensaje.MEN_ADV, e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                sweetAlertDialog.dismiss();
                mensaje.MensajeAdvertencia(getContext(), Mensaje.MEN_ADV, e.getMessage());
                sweetAlertDialog.dismiss();
                //startCounter = false;
                //counter.cancel();
            }
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


    private Handler handlerp = new Handler();

    private Runnable myRunnable = new Runnable() {
        public void run() {

        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}