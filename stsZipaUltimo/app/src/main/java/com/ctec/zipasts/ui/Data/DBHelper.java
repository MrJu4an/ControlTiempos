package com.ctec.zipasts.ui.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ctec.zipasts.ui.Model.ConceptoModel;
import com.ctec.zipasts.ui.Model.ControlTiempoModel;
import com.ctec.zipasts.ui.Model.DestinoModel;
import com.ctec.zipasts.ui.Model.EmpresaModel;
import com.ctec.zipasts.ui.Model.FacturaModel;
import com.ctec.zipasts.ui.Model.FrecuenciaModel;
import com.ctec.zipasts.ui.Model.PuntoVentaModel;
import com.ctec.zipasts.ui.Model.ReciboModel;
import com.ctec.zipasts.ui.Model.TarjetaModel;
import com.ctec.zipasts.ui.Model.VehiculoModel;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "sts";
    public static final int DB_VERSION =3;

    /*Constructot de clase*/

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL("create table if not exists vehiculos(placa text primary key, numInterno text,codEmpresa text, estado text )");
            db.execSQL("create table if not exists empresas(codigo text primary key, nombre text,nit text)");
            db.execSQL("create table if not exists destinos(codigo text primary key, nombre text,estado text)");
            db.execSQL("create table if not exists tarjetas(placa text primary key, id text,estado text,tipo text)");
            db.execSQL("create table if not exists conceptos(codigo text primary key, nombre text,total text,poriva text, iva text)");
            db.execSQL("create table if not exists puntoventa(codPunto text primary key, codCiudad text,nombre text, puerto text,codAgenAso text)");
            db.execSQL("create table if not exists frecuencias(codOrigen text, codAgencia text,horaIni text, horaFin text,tiempFrec text)");
            db.execSQL("create table if not exists control(placa text, agenciaOri text,fecha text, secuencia text,horaOrigen text,horaAgencia text, demora text, frecuencia text,enCadena text,codempant text, placaAnt text, fechaAnt text, horaOrigenAnt text, horaAgenciaAnt text, demoraAnt text, coddes text)");
            db.execSQL("create table if not exists recibos(numRecibo text primary key, fechaHora text,placa text, IDTarjeta text,valRecibo text,tarSaldo text, enCadena text)");
            db.execSQL("create table if not exists facturas(numFac text, codAgencia text,fecha text, usuario text,valor text,forma text,destino text, empresa text,placa text,asociada text,turno text,idTarjeta text,saldo text, planilla text,enCadena text)");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(VehiculoModel.DELETE_TABLE);
            db.execSQL(EmpresaModel.DELETE_TABLE);
            db.execSQL(DestinoModel.DELETE_TABLE);
            db.execSQL(TarjetaModel.DELETE_TABLE);
            db.execSQL(ConceptoModel.DELETE_TABLE);
            db.execSQL(PuntoVentaModel.DELETE_TABLE);
            db.execSQL(FrecuenciaModel.DELETE_TABLE);
            db.execSQL(ControlTiempoModel.DELETE_TABLE);
            db.execSQL(ReciboModel.DELETE_TABLE);
            db.execSQL(FacturaModel.DELETE_TABLE);
            onCreate(db);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
