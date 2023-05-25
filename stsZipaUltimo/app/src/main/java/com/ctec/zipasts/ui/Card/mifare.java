package com.ctec.zipasts.ui.Card;

import static com.ctec.zipasts.ui.Helper.Utils.bytesToString;
import static com.ctec.zipasts.ui.Helper.Utils.desencriptarDato;
import static com.ctec.zipasts.ui.Helper.Utils.encriptarDato;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.card.Card;
import com.cloudpos.card.MifareCard;
import com.ctec.zipasts.ui.Helper.Utils;

import org.bouncycastle.util.encoders.Hex;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;


public class mifare {


    public Boolean validarClonacion(Card rfCard)
    {
        boolean valido = false;
        String dato="";

        try {
            valido = verifyKeyA(rfCard,2);
            String datoaux = readBlock(rfCard,2,2);
            dato = datoaux.substring(0,8);
            byte[] serial = Hex.decode(String.valueOf(dato.toCharArray()));
            if(Arrays.equals(serial, rfCard.getID()))
            {
                dato ="TC";
            }

            switch (dato) {
                case "ED":
                    valido = false;
                    break;
                case "E":
                    valido = false;
                    break;
                case "TC":
                    valido = true;
                    break;
            }

        }catch (DeviceException e)
        {
            e.printStackTrace();
        }

        return valido;

    }

    private boolean verifyKeyA(Card rfCard, int sector) {
        boolean valido = false;
        byte[] key = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((MifareCard) rfCard).verifyKeyA(sector, key);
            if (verifyResult) {
                valido = true;
            } else {

            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        return valido;
    }

    private String readBlock( Card rfCard, int sector, int bloque ) {

        String dato="";
        try {
            byte[] result = ((MifareCard) rfCard).readBlock(sector, bloque);
            String block = bytesToString(result);
            Log.d("Dato Tarjeta", block);
            dato = desencriptarDato(block);
            Log.d("Dato desencriptado",dato);
        } catch (DeviceException e) {
            e.printStackTrace();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return  dato;

    }

    public void writeBlock( Card rfCard, byte[] arryData,  int sector, int bloque ) {

        boolean valido = false;
        try {

            Log.d("Q2", Arrays.toString(arryData)+" Sec:" +String.valueOf( sector)+"Blo:"+String.valueOf(bloque));
            valido= verifyKeyA(rfCard,sector);
            ((MifareCard) rfCard).writeBlock(sector, bloque, arryData);
            Log.d("Q2", "----ESCRIBIO---");

        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }


    public String leerEstado(Card rfCard) {
        boolean valido = false;
        String dato="";
        try {
            valido = verifyKeyA(rfCard,1);
            if (valido)
            {
                dato = readBlock(rfCard,1,0);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Alpha}]", "");

    }

    public String leerIdTarjeta(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,1);
            if (valido)
            {
                dato = readBlock(rfCard,1,2);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    public String leerPlaca(Card rfCard) {
        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,3);
            if (valido)
            {
                dato = readBlock(rfCard,3,0);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Alnum}]", "");
    }

    public  String realizarPago(Integer valor,Card rfCard) {

        Calendar calendario = Calendar.getInstance();
        mifare mifare = new mifare();
        DatoTarjeta datoTarjeta = new DatoTarjeta();
        String dato="",saldo="",retorno="";
        boolean valido = false;
        try {

            valido = verifyKeyA(rfCard,7);
            String saldoaux = readBlock(rfCard,7,0);
            saldo = saldoaux.replaceAll("[^\\p{Digit}]", "");
            if (Integer.parseInt(saldo)< valor)
            {
                return retorno="MENOS";
            }
            datoTarjeta.setSaldo(desencriptarDato(saldo).replaceAll("[^\\p{Digit}]", ""));
            valido = verifyKeyA(rfCard,6);
            datoTarjeta.setTotDescargas( readBlock(rfCard,6,2));
            valido = verifyKeyA(rfCard,9);
            datoTarjeta.setUltHoraDesc(readBlock(rfCard,9,1));
            valido = verifyKeyA(rfCard,8);
            datoTarjeta.setUltFecDesc(readBlock(rfCard,8,1));
            saldoaux = String.valueOf( Integer.valueOf(saldo) -valor);
            String cad = encriptarDato(saldoaux);
            String aux = String.format("%16s",cad);
            byte[] arryData =  aux.getBytes();
            Log.d("Saldo",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,7,0);
            int dataaux = Integer.parseInt( datoTarjeta.getTotDescargas().replaceAll("[^\\p{Digit}]",""));
            cad = String.valueOf(dataaux+ valor);
            cad = encriptarDato(cad);
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("TotDesca",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,6,2);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            datoTarjeta.setUltFecDesc( sdf.format(calendario.getTime()));
            datoTarjeta.setUltHoraDesc(calendario.getTime().toString().substring(11,16));
            cad = encriptarDato(datoTarjeta.getUltFecDesc());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("ultFecDes",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,8,1);
            cad = encriptarDato(datoTarjeta.getUltHoraDesc());
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("ultHoraDEs",String.valueOf( arryData.length));
            mifare.writeBlock(rfCard,arryData,9,1);
            retorno ="PAGADO";
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return retorno;
    }

    public String GrabarRuta(Integer numero, Integer codRuta, String codAgencia, String placa, String numInterno, Integer codEmpresa, Card rfCard)
    {
        String retorno="";
        mifare mifare= new mifare();
        try {
            validarClonacion(rfCard);
            String cad = encriptarDato(String.valueOf( numero));
            String aux = String.format("%16s",cad);
            byte[] arryData =  aux.getBytes();
            Log.d("Despacho dese",String.valueOf( numero));
            Log.d("Despacho enc",cad);
            mifare.writeBlock(rfCard,arryData,10,0);
            cad = encriptarDato(String.valueOf( codRuta));
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("CodRuta dese",String.valueOf( codRuta));
            Log.d("CodRuta enc",cad);
            mifare.writeBlock(rfCard,arryData,10,1);
            cad = encriptarDato(codAgencia);
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("codAgencia dese", codAgencia);
            Log.d("codAgencia enc",cad);
            mifare.writeBlock(rfCard,arryData,10,2);
            cad = encriptarDato(placa);
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("placa dese", placa);
            Log.d("placa enc",cad);
            mifare.writeBlock(rfCard,arryData,4,2);

            cad = encriptarDato(numInterno);
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("numInterno dese", numInterno);
            Log.d("numInterno enc",cad);
            mifare.writeBlock(rfCard,arryData,5,1);

            cad = encriptarDato(String.valueOf( codEmpresa));
            aux = String.format("%16s",cad);
            arryData =  aux.getBytes();
            Log.d("codEmpresa dese", String.valueOf(codEmpresa));
            Log.d("codEmpresa enc",cad);
            mifare.writeBlock(rfCard,arryData,4,0);
            retorno="GRE";


        }catch (Exception de)
        {
            de.printStackTrace();
            retorno="EGR";
        }

        return  retorno;
    }

    public String leerTipoTarjeta(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,5);
            dato = readBlock(rfCard,5,0);

        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Alpha}]", "");

    }

    public String leerCodEmpresa(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,4);
            if (valido)
            {
                dato = readBlock(rfCard,4,0);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", "");

    }

    public String leerEmpresa(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,4);
            dato = readBlock(rfCard,4,1);
        }catch ( Exception e)
        {
            valido= false;
        }
        String datoConvertido= dato.replaceAll("[^\\p{Alnum}]", "");
        dato= Utils.desencriptarDato(datoConvertido);
        return dato;
    }

    public String leerTotalRecargas(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,6);
            if (valido)
            {
                dato = readBlock(rfCard,6,1);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }



    public String leerSaldo(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,7);
            if (valido)
            {
                dato = readBlock(rfCard,7,0);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    public String leerUFRecarga(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,8);
            dato = readBlock(rfCard,8,0);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Alpha}]", "");
    }

    public String leerUHRecarga(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,9);
            dato = readBlock(rfCard,9,0);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Alnum}]", "");
    }

    public static String byteArray2String(byte[] arryByte){
        String strOut = new String();
        for (int i = 0; i < arryByte.length; i++)
            strOut += String.format("%02X ", arryByte[i]);
        return strOut;
    }

    public String leerParadero(DatoTarjeta datoTarjeta, Card rfCard) {
        String respuesta="";
        boolean valido = false;

        try {

            valido= validarClonacion(rfCard);
            datoTarjeta.setNumDespacho( leerNumdes(rfCard));
            datoTarjeta.setCodRuta(leerCodRuta(rfCard));
            datoTarjeta.setCodParadero(leercodParadero(rfCard));
            datoTarjeta.setCedCond(leerPlaca(rfCard));
            datoTarjeta.setCodEmpresa(leerCodEmpresa(rfCard));
            datoTarjeta.setNomEmpresa(leerEmpresa(rfCard));
            datoTarjeta.setPlacaPort(leerOtro(rfCard));
            datoTarjeta.setInternoPort(leerInterno(rfCard));
            datoTarjeta.setNombreConductor(leerNumInterno(rfCard));
            datoTarjeta.setSaldo(leerSaldo(rfCard));
            respuesta ="CPE";

        }catch ( Exception e)
        {
            respuesta ="ECP";
        }

        return respuesta;
    }

    private String leerNumInterno(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,3);
            if (valido)
            {
                dato = readBlock(rfCard,3,1);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", " ");
    }

    public String leerInterno(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,3);
            dato = readBlock(rfCard,3,1);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String leerOtro(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,4);
            dato = readBlock(rfCard,4,2);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Alpha}]", "");
    }

    private String leercodParadero(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,10);
            dato = readBlock(rfCard,10,2);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String leerCodRuta(Card rfCard) {

        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,10);
            dato = readBlock(rfCard,10,1);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String leerNumdes(Card rfCard) {
        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,10);
            dato = readBlock(rfCard,10,0);
        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String buscarCampos(Card rfCard) {
        String dato="";
        boolean valido = false;
        try{
            for(int i=0; i<17;i++){
                for(int j=0; j<4;j++){
                    valido = verifyKeyA(rfCard,i);
                    dato = readBlock(rfCard,i,j);
                    dato.replaceAll("[^\\p{Alnum}]", "");
                }

            }

        }catch ( Exception e)
        {
            valido= false;
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String leerFechaAsig(Card rfCard) {
        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,3);
            if (valido)
            {
                dato = readBlock(rfCard,3,2);
            }else{
                dato="err";
            }
        }catch ( Exception e)
        {
            dato="err";
        }

        return dato.replaceAll("[^\\p{Digit}]", "");
    }

    private String leerOtros(Card rfCard) {
        String dato="";
        boolean valido = false;
        try{
            valido = verifyKeyA(rfCard,11);
            dato = readBlock(rfCard,11,0);
        }catch ( Exception e)
        {
            valido= false;
        }

        dato.replaceAll("[^\\p{Digit}]", "");
        String dato2 = readBlock(rfCard,11,1);
        dato2.replace("[^\\p{Digit}]", "");
        return dato2;
    }

    public String leerInfoTarjeta(DatoTarjeta datoTarjeta, Card rfCard) {

        String respuesta="";
        boolean valido = false;

        try {

            valido= validarClonacion(rfCard);
            if(valido){
                respuesta ="ITE";
            }else{
                respuesta ="CLONADA";
                return respuesta;
            }
            String info_tar= leerOtros(rfCard);
            respuesta= leerIdTarjeta(rfCard);
            if (!respuesta.equals("err"))
            {
                datoTarjeta.setIdCard(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el id de la tarjeta";
            }
            respuesta="";
            respuesta= leerTipoTarjeta(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setTipoTarjeta(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el tipo de tarjeta";
            }

            respuesta="";
            respuesta= leerPlaca(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setPlacaPort(respuesta);
            }else{
                return  respuesta + " No se pudo identificar la placa de la tarjeta";
            }
            respuesta="";
            respuesta= leerNumInterno(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setInternoPort(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el número interno de la tarjeta";
            }
            respuesta="";
            respuesta= leerCodEmpresa(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setCodEmpresa(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el código de la empresa de la tarjeta";
            }
            respuesta="";
            respuesta= leerEstado(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setEstCard(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el estado de la tarjeta";
            }

            respuesta="";
            respuesta= leerTotalRecargas(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setTotRecargas(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el total de recargas de la tarjeta";
            }

            respuesta="";
            respuesta= leerSaldo(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setSaldo(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el saldo de la tarjeta";
            }
            respuesta="";
            respuesta= leerFechaAsig(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setFechaAsig(respuesta);
            }else{
                return  respuesta + " No se pudo identificar la fecha asignada de la tarjeta";
            }

            respuesta="";
            respuesta= leerUHRecarga(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setUltHoraRec(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el UHH de la tarjeta";
            }
            respuesta="";
            respuesta= leerUFRecarga(rfCard);
            if (!respuesta.equals("err")) {
                datoTarjeta.setUltFecRec(respuesta);
            }else{
                return  respuesta + " No se pudo identificar el UF de la tarjeta";
            }
            respuesta="ITE";
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 300);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            /*
            datoTarjeta.setSaldo(leerSaldo(rfCard));
            datoTarjeta.setIdCard(leerIdTarjeta(rfCard));
            datoTarjeta.setCodParadero(leercodParadero(rfCard));
            datoTarjeta.setCodRuta(leerCodRuta(rfCard));
            datoTarjeta.setTipoTarjeta(leerOtro(rfCard));
            datoTarjeta.setNumDespacho(leerNumdes(rfCard));
            datoTarjeta.setTipoTarjeta(leerInterno(rfCard));
            datoTarjeta.setNomEmpresa(leerEmpresa(rfCard));
            leerUFRecarga(rfCard);
            leerUHRecarga(rfCard);
            */


        }catch ( Exception e)
        {
            respuesta ="EIT";
        }
        return respuesta;
    }

}

