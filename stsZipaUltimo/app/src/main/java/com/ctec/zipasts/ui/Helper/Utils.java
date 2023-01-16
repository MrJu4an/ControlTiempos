package com.ctec.zipasts.ui.Helper;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


public class Utils {

    //constastes Campos Tabla Pasajeros
    public static final String TABLA_PASAJEROS = "pasajeros";
    public static final String TABLA_ABORDAJE = "abordaje";
    public static final String CAMPO_NUMDOC = "NumDoc";
    public static final String CAMPO_NOMBRES = "Nombres";
    public static final String CAMPO_APELLIDOS = "Apellidos";
    public static final String CAMPO_GENERO = "Genero";
    public static final String CAMPO_CELULAR = "Celular";
    public static final String CAMPO_IMAGEN = "Imagen";
    public static final String CAMPO_ABORDADO = "Abordado";
    public static final String CAMPO_ID= "Id";
    public static final String CAMPO_NOMAPE = "NomApe";
    public static final String CAMPO_NUMBUS = "NumBus";
    public static final String CAMPO_PLACA = "Placa";
    public static final String DESCARGA_INICIAL = Global.g_DirecApi+"descargaInicial";
    public static final String VALIDA_USUARIO = "validaUsuario";
    public static final String DESCARGA_EMPRESAS= "descargaEmpresas";
    public static final String DESCARGA_DESTINOS= "descargaDestinos";
    public static final String DESCARGA_VEHICULOS= "descargaVehiculos";
    public static final String DESCARGA_TARJETAS= "descargaTarjetas";
    public static final String DESCARGA_CONCEPTOS= "descargaConceptos";
    public static final String DESCARGA_PUNTOS= "descargaPuntosVenta";
    public static final String DESCARGA_FRECUENCIAS= "descargaFrecuencias";
    public static final String CONSULTAR_RESOL= "consultarResolucion";
    public static final String CONSULTAR_CONSEC= "consultarConsecutivos";
    public static final String CONSULTAR_PARAM= "consultarParametros";
    public static final String CONSULTAR_EMP_NO_COBRO= "consultarEmpNoCobro";
    public static final String CONSULTAR_NUMAGENCIA= "consultarNumAgencia";
    public static final String CONSULTAR_CONTROLES="GetControles";
    public static final String EJECUTA_ISO= "ejecutaISO";
    //public static final String ISO_CONTROL= "SP_ISO209K2";
    public static final String ISO_CONTROL= "SP_ISO209";
    public static final String ISO_FACTURA= "SP_ISO202";
    public static final String CREAR_TABLA_ABORDAJE= "CREATE TABLE ABORDAJE (Id INTEGER, NumDoc INTEGER ,NomApe TEXT,NumBus INTEGER,Placa TEXT)";
    public static final String CREAR_TABLA_PASAJEROS= "CREATE TABLE " + TABLA_PASAJEROS + " (" + CAMPO_NUMDOC + " INTEGER,"+CAMPO_NOMBRES+
            " TEXT," + CAMPO_APELLIDOS + " TEXT, " + CAMPO_GENERO + " TEXT," + CAMPO_CELULAR + " INTEGER, " + CAMPO_IMAGEN + " blob,"+
            CAMPO_ABORDADO + " INTEGER)";

    public static final Locale usa = new Locale("en", "US");
    public static final Currency dollars = Currency.getInstance(usa);
    public static final NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(usa);


    public static boolean isOnline( Activity activity) {
        //Obtiene el servicio de conectividad que se encarga de las conexiones a internet.
        ConnectivityManager connectivityManager = ( ConnectivityManager ) activity.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo( );
        if( networkInfo != null && networkInfo.isConnected( ) )
            return true;
        return false;
    }



    public static String getMD5(String plaintext)  {

        int  j = 0, valor = 0;
        char []dato;
        String retorno;
        try {
            dato = plaintext.toCharArray();
            valor = 0;
            for (int i = 0; i < dato.length; i++) {

                valor  += (int) dato[i];
            }
            retorno =  ""+(5000 - valor + dato.length);
        }catch (Exception e){
            retorno = e.getMessage();
        }

        return retorno;

    }




    /**
     *
     * @param String cadena
     * @return String datoEncriptado
     */
    public static String encriptarDato(String cadena  )
    {
        char datoEncriptado[] = new char[16];
        int tamanioDato, aux, cont, random, randomCaract,i;
        char dato[];

        dato = cadena.toCharArray();
        random = (int) Math.random( ) % 10;
        for (i=0;i<=9;i++){
            random = (int) ((9 * Math.random( )) + 1);
        }
        tamanioDato = cadena.length();
        if (dato.length>0)
        {
            for( cont = 0; cont < dato.length; cont++)
            {
                tamanioDato -= 1;

                if( cont % 2 == 0 )
                    aux = ( ( int ) dato[ tamanioDato ] ) + random;
                else
                    aux = ( ( int ) dato[ tamanioDato ] ) - random;

                if( cont < dato.length - 1 )
                    datoEncriptado[ cont ] = ( char ) aux;
                else
                {
                    datoEncriptado[ cont ] = ( char ) aux;
                    randomCaract =  (random + 0x30);
                    datoEncriptado[ cont + 1 ] = ( char )randomCaract;
                }
            }
        }
        else
            datoEncriptado=dato;

        return (String.copyValueOf(datoEncriptado));
    }


    /**
     * Desencripta el dato que se recibe como parámetro
     * @param String cadena: Dato a desencriptar.
     * @return Dato desencriptado.
     */
    public static String desencriptarDato(String cadena  )
    {
        char datoDesencriptado[] = new char[16];
        int tamanioDato, numeroEsp, cont, aux;
        char datoEncript[] = new char [16];
        datoEncript = cadena.toCharArray();

        if( datoEncript.length > 0)
        {
            tamanioDato = ( datoEncript.length ) - 1;
            numeroEsp = ( ( int ) datoEncript[ tamanioDato ] ) - 48;

            for( cont = 0; cont < ( datoEncript.length ) - 1; cont++)
            {
                tamanioDato -= 1;

                if( ( datoEncript.length ) % 2 == 0 )
                {
                    if( cont % 2 == 0 )
                        aux = ( ( int ) datoEncript[ tamanioDato ] ) - numeroEsp;
                    else
                        aux = ( ( int ) datoEncript[ tamanioDato ] ) + numeroEsp;
                }

                else
                {
                    if( cont % 2 == 0 )
                        aux = ( ( int ) datoEncript[ tamanioDato ] ) + numeroEsp;
                    else
                        aux = ( ( int ) datoEncript[ tamanioDato ] ) - numeroEsp;
                }

                datoDesencriptado[ cont ] = ( char ) aux;
            }
        }
        else
            datoDesencriptado = datoEncript;

        return (String.copyValueOf(datoDesencriptado));
    }

    public static String bytesToString(byte[] b) {
        StringBuffer result = new StringBuffer("");
        int length = b.length;

        for(int i = 0; i < length; ++i) {
            char ch = (char)(b[i] & 255);
            if (ch == 0) {
                break;
            }

            result.append(ch);
        }

        return result.toString();
    }

    public static String screenMsgEsp( String mensaje)
    {
        String texto="";
        switch (mensaje)
        {
            case "NO":
                texto ="La transaccion No se pudo procesar Informacion InCorrecta";
                break;

            case "EXC":
                texto ="El Conductor no existe";
                break;

            case "NV":
                texto ="No se encontraron vehiculos para el conductor verifique!";
                break;

            case "VRA":
                texto ="El Vehiculo Digitado no Aprobo el Alistamiento";
                break;

            case "VI":
                texto ="El Vehiculo Digitado Esta Inactivo en el Sistema";
                break;

            case "LCV":
                texto ="La licencia de Conduccion esta Vencida";
                break;

            case "SSV":
                texto ="La Seguridad Social del Conductor esta Vencida";
                break;

            case "TO":
                texto ="El Vehiculo Presenta Tarjeta de Operacion Vencida";
                break;

            case "CCO":
                texto ="El Vehiculo Presenta Seg Responsabilidad  Civil y Contractual Vencida";
                break;

            case "EXCO":
                texto ="El Vehiculo Presenta Seg Responsabilidad  Civil ExContractual Vencida";
                break;

            case "RTCM":
                texto ="El Vehiculo Presenta Revision  Tecnomecanica Vencida";
                break;

            case "TDRIE":
                texto ="El Vehiculo Presenta Seguro Todo Riesgo Vencido";
                break;

            case "SO":
                texto ="El Vehiculo Presenta Seguro Obligatorio Vencido Vencido";
                break;

            case "ND":
                texto ="No se pudo validar despacho, colocar Sello";
                break;

            case "YRC":
                texto ="El vehiculo ya realizo control. de punta";
                break;
            case "TI":
                texto = "Tarjeta Inactiva";
                break;
            case "CI":
                texto = "Conductor Inactivo";
                break;

        }
        return texto;
    }

    public  static  String monthAlpha( int num_mes)
    {
        String texto="";
        switch (num_mes)
        {
            case 1:
                texto ="ENE";
                break;

            case 2:
                texto ="FEB";
                break;

            case 3:
                texto ="MAR";
                break;

            case 4:
                texto ="ABR";
                break;

            case 5:
                texto ="MAY";
                break;

            case 6:
                texto ="JUN";
                break;

            case 7:
                texto ="JUL";
                break;

            case 8:
                texto ="AGO";
                break;

            case 9:
                texto ="SEP";
                break;

            case 10:
                texto ="OCT";
                break;

            case 11:
                texto ="NOV";
                break;

            case 12:
                texto ="DIC";
                break;
            default:
                return "0";

        }
        return texto;
    }


    /**
     * pad to the left
     *
     * @param s   - original string
     * @param len - desired len
     * @param c   - padding char
     * @return padded string
     * @throws ISOException on error
     */
    public static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len)
            return null;
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0)
            d.append(c);
        d.append(s);
        return d.toString();
    }

    /**
     * Converts a hex string into a byte array
     *
     * @param s source string (with Hex representation)
     * @return byte array
     */
    public static byte[] hex2byte(String s) {
        if (s.length() % 2 == 0) {
            return hex2byte(s.getBytes(), 0, s.length() >> 1);
        } else {
            // Padding left zero to make it even size #Bug raised by tommy
            return hex2byte("0" + s);
        }
    }

    /**
     * @param b      source byte array
     * @param offset starting offset
     * @param len    number of bytes in destination (processes len*2)
     * @return byte[len]
     */
    public static byte[] hex2byte(byte[] b, int offset, int len) {
        byte[] d = new byte[len];
        for (int i = 0; i < len * 2; i++) {
            // Buginfo when i oddness then this line won't be work
            // but in the for judge i>0 & i++ so i absolutely won't be oddness
            int shift = ((i % 2 == 1) ? 0 : 4);
            d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
        }
        return d;
    }
}