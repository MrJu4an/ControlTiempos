package com.ctec.zipasts.ui.Data;

import static java.lang.Integer.parseInt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class Data {

    private Context context;


    public Data(Context context) {
        try {
            this.context = context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// region CRUD_DESTINOS

    public void insertarDestino(DestinoModel destino) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = destino.valores();
        try {
            sqLiteDatabase.insert(DestinoModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarDestinos(List<DestinoModel> destinos) {
        try {
            for (DestinoModel destino : destinos) {
                insertarDestino(destino);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DestinoModel getDestino(String codigo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        DestinoModel destino = new DestinoModel();
        String[] whereArgs = new String[]{String.valueOf(codigo)};
        try {
            if (!codigo.isEmpty()) {
                Cursor fila = sqLiteDatabase.query(DestinoModel.NOMBRETABLA,DestinoModel.COL_TODAS,
                        DestinoModel.WHERE_CODIGO,whereArgs,null,null,null);
                // Cursor fila = sqLiteDatabase.rawQuery("select * from destinos where codigo =" + codigo, null);
                if (fila.moveToFirst()) {
                    destino.setCodigo((fila.getString(0)));
                    destino.setNombre(fila.getString(1));
                    destino.setEstado(fila.getString(2));
                    //   vehiculo.setEstado(fila.getString(3));
                    //  vehiculo.setCantPuestos(fila.getInt(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destino;
    }

    public void eliminarDestino(String codigo) {

        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String[] whereArgs = new String[]{String.valueOf(codigo)};
            sqLiteDatabase.delete(DestinoModel.NOMBRETABLA, EmpresaModel.WHERE_CODIGO, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarDestinos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(DestinoModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaDestinos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(DestinoModel.DELETE_TABLE);
            sqLiteDatabase.execSQL(DestinoModel.CREARTABLA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_EMPRESA

    public void insertarEmpresa(EmpresaModel empresa) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = empresa.valores();
        try {
            sqLiteDatabase.insert(EmpresaModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarEmpresas(List<EmpresaModel> empresas) {
        try {
            for (EmpresaModel empresa : empresas) {
                insertarEmpresa(empresa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EmpresaModel getEmpresa(String codigo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        EmpresaModel empresa = new EmpresaModel();
        String[] whereArgs = new String[]{String.valueOf(codigo)};
        try {
            if (!codigo.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from empresas where codigo =" + codigo, null);
                if (fila.moveToFirst()) {
                    empresa.setCodigo((fila.getString(0)));
                    empresa.setNombre(fila.getString(1));
                    empresa.setNit(fila.getString(2));
                    //   vehiculo.setEstado(fila.getString(3));
                    //  vehiculo.setCantPuestos(fila.getInt(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empresa;
    }

    public ArrayList<EmpresaModel> getEmpresas() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<EmpresaModel> empresas = new ArrayList<EmpresaModel>();
        try {
            EmpresaModel empresa1 = new EmpresaModel();
            empresa1.setCodigo("0");
            empresa1.setNombre("Seleccione");
            empresa1.setNit("1");
            empresas.add(empresa1);
            Cursor fila = sqLiteDatabase.rawQuery("select * from empresas", null);
            while (fila.moveToNext()) {
                EmpresaModel empresa = new EmpresaModel();
                empresa.setCodigo((fila.getString(0)));
                empresa.setNombre(fila.getString(1));
                empresa.setNit(fila.getString(2));
                empresas.add(empresa);
                //   vehiculo.setEstado(fila.getString(3));
                //  vehiculo.setCantPuestos(fila.getInt(4));

            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empresas;
    }

    public void eliminarEmpresa(String codigo) {

        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String[] whereArgs = new String[]{String.valueOf(codigo)};
            sqLiteDatabase.delete(EmpresaModel.NOMBRETABLA, EmpresaModel.WHERE_CODIGO, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarEmpresas() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(EmpresaModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaEmpresa() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(EmpresaModel.DELETE_TABLE);
            sqLiteDatabase.execSQL(VehiculoModel.CREARTABLA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_VEHICULO

    public void insertarVehiculo(VehiculoModel vehiculo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = vehiculo.valores();
        try {
            sqLiteDatabase.insert(VehiculoModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarVehiculos(List<VehiculoModel> vehiculos) {
        try {
            for (VehiculoModel vehiculo : vehiculos) {
                insertarVehiculo(vehiculo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VehiculoModel getVehiculo(String numInterno, String codEmpresa) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        VehiculoModel vehiculo = new VehiculoModel();
        String[] whereArgs = new String[]{String.valueOf(numInterno)};
        try {
            if (!numInterno.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from vehiculos where codEmpresa="+codEmpresa+" and numInterno =" + numInterno, null);
                if (fila.moveToFirst()) {


                    vehiculo.setPlaca(fila.getString(0));
                    vehiculo.setNumInterno(fila.getString(1));
                    vehiculo.setCodEmpresa((fila.getString(2)));
                    vehiculo.setEstado(fila.getString(3));
                    //   vehiculo.setEstado(fila.getString(3));
                    //  vehiculo.setCantPuestos(fila.getInt(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehiculo;
    }

    public ArrayList<VehiculoModel> getVehiculos() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<VehiculoModel> vehiculos = new ArrayList<VehiculoModel>();
        try {
//            if (!numInterno.isEmpty()) {
            Cursor fila = sqLiteDatabase.rawQuery("select * from vehiculos", null);
            while(fila.moveToNext()) {

                VehiculoModel vehiculo = new VehiculoModel();
                vehiculo.setPlaca(fila.getString(0));
                vehiculo.setNumInterno(fila.getString(1));
                vehiculo.setCodEmpresa((fila.getString(2)));
                vehiculo.setEstado(fila.getString(3));
                //   vehiculo.setEstado(fila.getString(3));
                //  vehiculo.setCantPuestos(fila.getInt(4));

                //     } else {
                vehiculos.add(vehiculo);

            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehiculos;
    }

    public VehiculoModel getVehiculoPlaca(String placa) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        VehiculoModel vehiculo = new VehiculoModel();
        String[] whereArgs = new String[]{String.valueOf(placa)};


        try {
            if (!placa.isEmpty()) {
                //   Cursor fila = sqLiteDatabase.rawQuery("select * from vehiculos where placa =" + placa, null);
                Cursor fila = sqLiteDatabase.query(VehiculoModel.NOMBRETABLA,VehiculoModel.COL_TODAS,
                        VehiculoModel.WHERE_PLACA,whereArgs,null,null,null);
                if (fila.moveToFirst()) {


                    vehiculo.setPlaca(fila.getString(0));
                    vehiculo.setNumInterno(fila.getString(1));
                    vehiculo.setCodEmpresa((fila.getString(2)));
                    vehiculo.setEstado(fila.getString(3));
                    //   vehiculo.setEstado(fila.getString(3));
                    //  vehiculo.setCantPuestos(fila.getInt(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehiculo;
    }

    public void eliminarVehiculo(String numInterno) {

        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String[] whereArgs = new String[]{String.valueOf(numInterno)};
            sqLiteDatabase.delete(VehiculoModel.NOMBRETABLA, VehiculoModel.WHERE_INTERNO, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarVehiculos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(VehiculoModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarCrearTablaVehiculos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(VehiculoModel.DELETE_TABLE);
            sqLiteDatabase.execSQL(VehiculoModel.CREARTABLA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_TARJETAS

    public void insertarTarjeta(TarjetaModel tarjeta) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = tarjeta.valores();
        try {
            sqLiteDatabase.insert(TarjetaModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarTarjetas(List<TarjetaModel> tarjetas) {
        try {

            for (TarjetaModel tarjeta : tarjetas) {
                SQLiteDatabase sqLiteDatabase;
                DBHelper sqLiteOpenHelper;
                sqLiteOpenHelper = new DBHelper(context);
                sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();
                ContentValues contentValues = tarjeta.valores();
                try {
                    sqLiteDatabase.insertOrThrow(TarjetaModel.NOMBRETABLA,null,contentValues);
                    sqLiteOpenHelper.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList <TarjetaModel> getTarjetas() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<TarjetaModel> tarjetas = new ArrayList<>();
        try {
            Cursor fila = sqLiteDatabase.rawQuery("select * from tarjetas", null);
            while (fila.moveToNext()) {

                TarjetaModel tarjeta= new TarjetaModel();
                tarjeta.setPlaca(fila.getString(0));
                tarjeta.setId(fila.getString(1));
                tarjeta.setEstado((fila.getString(2)));
                tarjeta.setTipo(fila.getString(3));
                //   vehiculo.setEstado(fila.getString(3));
                //  vehiculo.setCantPuestos(fila.getInt(4));
                tarjetas.add(tarjeta);
            }

            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tarjetas;
    }

    public TarjetaModel getTarjeta(String placa) {
        long cant = countTarjeta();
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getReadableDatabase();

        TarjetaModel tarjeta = new TarjetaModel();
        String[] whereArgs = new String[]{String.valueOf(placa)};
        try {

            if (!placa.isEmpty()) {
                Cursor fila = sqLiteDatabase.query(TarjetaModel.NOMBRETABLA,TarjetaModel.COL_TODAS,
                        TarjetaModel.WHERE_PLACA,whereArgs,null,null,null);
                //  Cursor fila = sqLiteDatabase.rawQuery("select * from tarjetas where placa =" + placa, null);

                if (fila.moveToFirst()) {


                    tarjeta.setPlaca(fila.getString(0));
                    tarjeta.setId(fila.getString(1));
                    tarjeta.setEstado((fila.getString(2)));
                    tarjeta.setTipo(fila.getString(3));
                    //   vehiculo.setEstado(fila.getString(3));
                    //  vehiculo.setCantPuestos(fila.getInt(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tarjeta;
    }

    public long countTarjeta() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        long registros=0;
        registros= DatabaseUtils.queryNumEntries(sqLiteDatabase,TarjetaModel.NOMBRETABLA);

        return  registros;

    }

    public void eliminarTarjeta(String placa) {

        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String[] whereArgs = new String[]{String.valueOf(placa)};
            sqLiteDatabase.delete(TarjetaModel.NOMBRETABLA, TarjetaModel.WHERE_PLACA, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTarjetas() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(TarjetaModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaTarjetas() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(TarjetaModel.DELETE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_CONCEPTOS

    public void insertarConcepto(ConceptoModel concepto) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = concepto.valores();
        try {
            sqLiteDatabase.insert(ConceptoModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarConceptos(List<ConceptoModel> conceptos) {
        try {
            for (ConceptoModel concepto : conceptos) {
                insertarConcepto(concepto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConceptoModel getConcepto(String codigo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ConceptoModel concepto = new ConceptoModel();
        String[] whereArgs = new String[]{String.valueOf(codigo)};
        try {
            if (!codigo.isEmpty()) {
                Cursor fila = sqLiteDatabase.query(ConceptoModel.NOMBRETABLA,ConceptoModel.COL_TODAS,
                        ConceptoModel.WHERE_CODIGO,whereArgs,null,null,null);
                // Cursor fila = sqLiteDatabase.rawQuery("select * from conceptos where codigo =" + codigo, null);
                if (fila.moveToFirst()) {

                    concepto.setCodigo(fila.getString(0));
                    concepto.setNombre(fila.getString(1));
                    concepto.setValTot((fila.getString(2)));
                    concepto.setPorcentajeIva(fila.getString(3));
                    concepto.setValIva(fila.getString(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return concepto;
    }

    public void eliminarConcepto(String concepto) {

        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            String[] whereArgs = new String[]{String.valueOf(concepto)};
            sqLiteDatabase.delete(ConceptoModel.NOMBRETABLA, ConceptoModel.WHERE_CODIGO, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarConceptos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(ConceptoModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaConceptos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(TarjetaModel.DELETE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_FRECUENCIAS

    public void insertarFrecuencia(FrecuenciaModel frecuencia) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = frecuencia.valores();
        try {
            sqLiteDatabase.insert(FrecuenciaModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarFrecuencias(List<FrecuenciaModel> frecuencias) {
        try {
            for (FrecuenciaModel frecuencia : frecuencias) {
                insertarFrecuencia(frecuencia);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<FrecuenciaModel> getFrecuencia(String codAgencia, String codOrigen) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<FrecuenciaModel> frecuencias = new ArrayList<FrecuenciaModel>();
        String[] whereArgs = new String[]{String.valueOf(codAgencia)};
        try {
            if (!codAgencia.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from frecuencias where codAgencia =" + codAgencia + " and codOrigen=" + codOrigen, null);
                while (fila.moveToNext()) {
                    FrecuenciaModel frecuencia = new FrecuenciaModel();
                    frecuencia.setCodOrigen(fila.getString(0));
                    frecuencia.setCodAgencia(fila.getString(1));
                    frecuencia.setHoraIni((fila.getString(2)));
                    frecuencia.setHoraFin((fila.getString(3)));
                    frecuencia.setTiempFrec((fila.getString(4)));
                    frecuencias.add(frecuencia);
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frecuencias;
    }

    public ArrayList<FrecuenciaModel> getFrecuencias(String codAgencia) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<FrecuenciaModel> frecuenciAgencia = new ArrayList<FrecuenciaModel>();
        String[] whereArgs = new String[]{String.valueOf(codAgencia)};
        try {
            if (!codAgencia.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from frecuencias where codAgencia =" + codAgencia, null);
                int count = 0;
                if (fila.moveToFirst()) {
                    FrecuenciaModel frecuencia = new FrecuenciaModel();
                    frecuencia.setCodOrigen(fila.getString(0));
                    frecuencia.setCodAgencia(fila.getString(1));
                    frecuencia.setHoraIni((fila.getString(2)));
                    frecuencia.setHoraFin((fila.getString(3)));
                    frecuencia.setTiempFrec((fila.getString(4)));
                    count++;

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return frecuenciAgencia;
    }


    public void eliminarFrecuencia() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(FrecuenciaModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaFrecuencias() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(FrecuenciaModel.DELETE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_PUNTOVENTA

    public void insertarPunto(PuntoVentaModel punto) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = punto.valores();
        try {
            sqLiteDatabase.insert(PuntoVentaModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarPuntos(List<PuntoVentaModel> puntos) {
        try {
            for (PuntoVentaModel punto : puntos) {
                insertarPunto(punto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PuntoVentaModel getPuntoCodigo(String codigo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        PuntoVentaModel punto = new PuntoVentaModel();
        String[] whereArgs = new String[]{String.valueOf(codigo)};
        try {
            if (!codigo.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from puntoventa where codPunto =" + codigo, null);
                if (fila.moveToFirst()) {

                    punto.setcodpunto(fila.getString(0));
                    punto.setcodciudad(fila.getString(1));
                    punto.setNombre((fila.getString(2)));
                    punto.setPuerto(fila.getString(3));
                    punto.setCodAgenAso(fila.getString(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return punto;
    }

    public PuntoVentaModel getPuntoCodCiudad(String codCiudad) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        PuntoVentaModel punto = new PuntoVentaModel();
        String[] whereArgs = new String[]{String.valueOf(codCiudad)};
        try {
            if (!codCiudad.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from puntoventa where codCiudad =" + codCiudad, null);
                if (fila.moveToFirst()) {

                    punto.setcodpunto(fila.getString(0));
                    punto.setcodciudad(fila.getString(1));
                    punto.setNombre((fila.getString(2)));
                    punto.setPuerto(fila.getString(3));
                    punto.setCodAgenAso(fila.getString(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
            punto=null;
        }
        return punto;
    }

    public PuntoVentaModel getPuntoPuerto(String puerto) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        PuntoVentaModel punto = new PuntoVentaModel();
        String[] whereArgs = new String[]{String.valueOf(puerto)};
        try {
            if (!puerto.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from puntoventa where puerto =" + puerto, null);
                if (fila.moveToFirst()) {

                    punto.setcodpunto(fila.getString(0));
                    punto.setcodciudad(fila.getString(1));
                    punto.setNombre((fila.getString(2)));
                    punto.setPuerto(fila.getString(3));
                    punto.setCodAgenAso(fila.getString(4));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return punto;
    }

    public ArrayList <PuntoVentaModel> getPuntos() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList <PuntoVentaModel> puntos = new ArrayList<>();
        // String[] whereArgs = new String[]{String.valueOf(puerto)};
        try {
            //   if (!puerto.isEmpty()) {
            Cursor fila = sqLiteDatabase.rawQuery("select * from puntoventa", null);
            while (fila.moveToNext()) {
                PuntoVentaModel punto = new PuntoVentaModel();
                punto.setcodpunto(fila.getString(0));
                punto.setcodciudad(fila.getString(1));
                punto.setNombre((fila.getString(2)));
                punto.setPuerto(fila.getString(3));
                punto.setCodAgenAso(fila.getString(4));
                puntos.add(punto);
                //  } else {
                Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
            }
            // }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return puntos;
    }


    public void eliminarPuntos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(PuntoVentaModel.DELETE_ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarTablaPuntos() {
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(PuntoVentaModel.DELETE_TABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_RECIBO

    public void insertarRecibo(ReciboModel recibo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = recibo.valores();
        try {
            sqLiteDatabase.insert(ReciboModel.NOMBRETABLA, null, contentValues);
            sqLiteOpenHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertarRecibos(List<ReciboModel> recibos) {
        try {
            for (ReciboModel recibo : recibos) {
                insertarRecibo(recibo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReciboModel getRecibo(String numRecibo) {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ReciboModel recibo = new ReciboModel();
        try {
            if (!numRecibo.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from recibos where numRecibo =" + numRecibo, null);
                if (fila.moveToFirst()) {

                    recibo.setNumRecibo(fila.getString(0));
                    recibo.setFechaHora(fila.getString(1));
                    recibo.setPlaca((fila.getString(2)));
                    recibo.setIDTarjeta(fila.getString(3));
                    recibo.setValRecibo(fila.getString(4));
                    recibo.setEnCadena(fila.getString(5));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recibo;
    }

    public ArrayList<ReciboModel> getRecibos() {
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<ReciboModel> recibos = new ArrayList<ReciboModel>();
        try {
            Cursor fila = sqLiteDatabase.rawQuery("select * from recibos", null);
            while (fila.moveToNext()) {
                ReciboModel recibo = new ReciboModel();
                recibo.setNumRecibo(fila.getString(0));
                recibo.setFechaHora(fila.getString(1));
                recibo.setPlaca((fila.getString(2)));
                recibo.setIDTarjeta(fila.getString(3));
                recibo.setValRecibo(fila.getString(4));
                recibo.setTarSaldo(fila.getString(5));
                recibo.setEnCadena(fila.getString(6));
                recibos.add(recibo);
            }

            sqLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recibos;
    }

    public void confirmarEnCadenaRecibo(String numRec){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ControlTiempoModel control = new ControlTiempoModel();
        try{
            if (!numRec.isEmpty()) {
                sqLiteDatabase.execSQL("UPDATE recibos SET enCadena= '1' where numRecibo ="+ numRec);

            } else {
                Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
            }

            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public void eliminarRecibos(){
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(ReciboModel .DELETE_ALL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eliminarTablaRecibos(){
        try{
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(ReciboModel.DELETE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_CONTROLTIEMPO

    public void insertarControl(ControlTiempoModel control){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = control.valores();
        try{
            sqLiteDatabase.insert(ControlTiempoModel.NOMBRETABLA, null,contentValues);
            sqLiteOpenHelper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertarControles(List<ControlTiempoModel> controles){
        try{
            for (ControlTiempoModel control: controles){
                insertarControl(control);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<ControlTiempoModel> getControles(){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<ControlTiempoModel> controles = new ArrayList<ControlTiempoModel>();
        try{
            Cursor fila = sqLiteDatabase.rawQuery("select * from control", null);
            while (fila.moveToNext()) {
                ControlTiempoModel control= new ControlTiempoModel();
                control.setPlaca(fila.getString(0));
                control.setAgenciaOri(fila.getString(1));
                control.setFecha((fila.getString(2)));
                control.setSecuencia(fila.getString(3));
                control.setHoraOrigen(fila.getString(4));
                control.setHoraAgencia(fila.getString(5));
                control.setDemora(fila.getString(6));
                control.setFrecuencia(fila.getString(7));
                control.setEnCadena(fila.getString(8));
                controles.add(control);
            }


            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return controles;
    }

    public ArrayList<ControlTiempoModel>  getControlesPlaca(String placa){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        String[] whereArgs = new String[]{String.valueOf(placa)};
        ArrayList<ControlTiempoModel> controles = new ArrayList<ControlTiempoModel>();
        try{
            Cursor fila = sqLiteDatabase.query(ControlTiempoModel.NOMBRETABLA,ControlTiempoModel.COL_TODAS,
                    ControlTiempoModel.WHERE_PLACA,whereArgs,null,null,null);
            // Cursor fila = sqLiteDatabase.rawQuery("select * from control where placa="+placa, null);
            while (fila.moveToNext()) {
                ControlTiempoModel control= new ControlTiempoModel();
                control.setPlaca(fila.getString(0));
                control.setAgenciaOri(fila.getString(1));
                control.setFecha((fila.getString(2)));
                control.setSecuencia(fila.getString(3));
                control.setHoraOrigen(fila.getString(4));
                control.setHoraAgencia(fila.getString(5));
                control.setDemora(fila.getString(6));
                control.setFrecuencia(fila.getString(7));
                control.setEnCadena(fila.getString(8));
                controles.add(control);
            }


            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return controles;
    }

    public ControlTiempoModel getControlPlaca(String placa){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ControlTiempoModel control = new ControlTiempoModel();
        try{
            if (!placa.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from control where placa ="+ placa, null);
                if (fila.moveToFirst()) {

                    control.setPlaca(fila.getString(0));
                    control.setAgenciaOri(fila.getString(1));
                    control.setFecha((fila.getString(2)));
                    control.setSecuencia(fila.getString(3));
                    control.setHoraOrigen(fila.getString(4));
                    control.setHoraAgencia(fila.getString(5));
                    control.setDemora(fila.getString(6));
                    control.setFrecuencia(fila.getString(7));
                    control.setEnCadena(fila.getString(8));

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return control;
    }

    public ControlTiempoModel getUltControlPlaca(String placa){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(placa)};
        ControlTiempoModel control = new ControlTiempoModel();
        int busq=0;
        boolean ent=false;
        try{
            if(!placa.isEmpty()){
                Cursor fila = sqLiteDatabase.query(ControlTiempoModel.NOMBRETABLA,ControlTiempoModel.COL_TODAS,
                        ControlTiempoModel.WHERE_PLACA,whereArgs,null,null,null);
                while (fila.moveToNext()) {
                    ent=true;
                    int temp= parseInt(fila.getString(3));
                    if(temp>busq){
                        busq=temp;
                        control.setPlaca(fila.getString(0));
                        control.setAgenciaOri(fila.getString(1));
                        control.setFecha((fila.getString(2)));
                        control.setSecuencia(fila.getString(3));
                        control.setHoraOrigen(fila.getString(4));
                        control.setHoraAgencia(fila.getString(5));
                        control.setDemora(fila.getString(6));
                        control.setFrecuencia(fila.getString(7));
                        control.setEnCadena(fila.getString(8));
                        control.setCodempant(fila.getInt(9));
                        control.setPlacaAnt(fila.getString(10));
                        control.setHoraOrigenAnt(fila.getString(11));
                        control.setHoraAgenciaAnt(fila.getString(12));
                        control.setDemoraAnt(fila.getString(13));
                        //control.setCoddes(fila.getInt(14));
                    }
                }
                sqLiteDatabase.close();
                if(!ent){
                    control = null;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            control = null;
        }
        return control;
    }

    public void confirmarEnCadenaControl(String secuencia){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ControlTiempoModel control = new ControlTiempoModel();
        try{
            if (!secuencia.isEmpty()) {
                sqLiteDatabase.execSQL("UPDATE control SET enCadena= '1' where secuencia ="+ secuencia);

            } else {
                Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
            }

            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public ControlTiempoModel getUltimoControlOrigen(String codOrigen){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ControlTiempoModel control = new ControlTiempoModel();
        int busq=0;
        boolean ent=false;
        try{
            if (!codOrigen.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from control where agenciaOri ="+ codOrigen, null);
                while (fila.moveToNext()) {
                    ent=true;
                    int temp= parseInt(fila.getString(3));
                    if(temp>busq){
                        busq=temp;
                        control.setPlaca(fila.getString(0));
                        control.setAgenciaOri(fila.getString(1));
                        control.setFecha((fila.getString(2)));
                        control.setSecuencia(fila.getString(3));
                        control.setHoraOrigen(fila.getString(4));
                        control.setHoraAgencia(fila.getString(5));
                        control.setDemora(fila.getString(6));
                        control.setFrecuencia(fila.getString(7));
                        control.setEnCadena(fila.getString(8));
                        control.setCodempant(fila.getInt(9));
                        control.setPlacaAnt(fila.getString(10));
                        control.setHoraOrigenAnt(fila.getString(11));
                        control.setHoraAgenciaAnt(fila.getString(12));
                        control.setDemoraAnt(fila.getString(13));
                        //control.setCoddes(fila.getInt(14));
                    }
                }
            }
            sqLiteDatabase.close();
            if(!ent){
                control=null;
            }
        }catch (Exception e){
            e.printStackTrace();
            control= null;
        }
        return control;
    }


    public void eliminarControles(){
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(ControlTiempoModel .DELETE_ALL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eliminarTablaControles(){
        try{
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(ControlTiempoModel.DELETE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

//endregion

    // region CRUD_FACTURA

    public void insertarFactura(FacturaModel factura){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = factura.valores();
        try{
            sqLiteDatabase.insert(FacturaModel.NOMBRETABLA, null,contentValues);
            sqLiteOpenHelper.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertarFacturas(List<FacturaModel> facturas){
        try{
            for (FacturaModel factura: facturas){
                insertarFactura(factura);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<FacturaModel> getFacturas(){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<FacturaModel> facturas = new ArrayList<FacturaModel>();
        try{

            Cursor fila = sqLiteDatabase.rawQuery("select * from facturas", null);
            while (fila.moveToNext()) {
                FacturaModel fact= new FacturaModel();
                fact.setNumFac(parseInt(fila.getString(0)));
                fact.setCodAgencia(fila.getString(1));
                fact.setFecha((fila.getString(2)));
                fact.setUsuario(fila.getString(3));
                fact.setValor(fila.getString(4));
                fact.setForma(fila.getString(5));
                fact.setDestino(fila.getString(6));
                fact.setEmpresa(fila.getString(7));
                fact.setPlaca(fila.getString(8));
                fact.setAsociada(fila.getString(9));
                fact.setTurno(fila.getString(10));
                fact.setIdTarjeta(fila.getString(11));
                fact.setSaldo(fila.getString(12));
                fact.setPlanilla(fila.getString(13));
                fact.setEnCadena(fila.getString(14));
                facturas.add(fact);

            }


            fila.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return facturas;
    }

    public ArrayList<FacturaModel> getFacturasCierre(){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ArrayList<FacturaModel> facturas = new ArrayList<FacturaModel>();
        try{

            Cursor fila = sqLiteDatabase.rawQuery("select * from facturas where valor <> '0' ", null);
            while (fila.moveToNext()) {
                FacturaModel fact= new FacturaModel();
                fact.setNumFac(parseInt(fila.getString(0)));
                fact.setCodAgencia(fila.getString(1));
                fact.setFecha((fila.getString(2)));
                fact.setUsuario(fila.getString(3));
                fact.setValor(fila.getString(4));
                fact.setForma(fila.getString(5));
                fact.setDestino(fila.getString(6));
                fact.setEmpresa(fila.getString(7));
                fact.setPlaca(fila.getString(8));
                fact.setAsociada(fila.getString(9));
                fact.setTurno(fila.getString(10));
                fact.setIdTarjeta(fila.getString(11));
                fact.setSaldo(fila.getString(12));
                fact.setPlanilla(fila.getString(13));
                fact.setEnCadena(fila.getString(14));
                facturas.add(fact);

            }


            fila.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return facturas;
    }

    public FacturaModel getFactura(String numFac){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        FacturaModel factura = new FacturaModel();
        try{
            if (!numFac.isEmpty()) {
                Cursor fila = sqLiteDatabase.rawQuery("select * from facturas where numFac ="+ numFac, null);
                if (fila.moveToFirst()) {
                    /*
                    factura(fila.getString(0));
                    factura.setCodciudad(fila.getString(1));
                    factura.setNombre((fila.getString(2)));
                    factura.setPuerto(fila.getString(3));
                    factura.setCodAgenAso(fila.getString(4));

                     */

                } else {
                    Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                }
            }
            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return factura;
    }

    public void confirmarEnCadenaFactura(String numFac){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();

        ControlTiempoModel control = new ControlTiempoModel();
        try{
            if (!numFac.isEmpty()) {
                sqLiteDatabase.execSQL("UPDATE facturas SET enCadena= '1' where numFac ="+ numFac);

            } else {
                Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
            }

            sqLiteDatabase.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void pasarFacturaCero(String placa){
        SQLiteDatabase sqLiteDatabase;
        DBHelper sqLiteOpenHelper;
        sqLiteOpenHelper = new DBHelper(context);
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("valor", 0);
        String[] whereArgs = new String[]{String.valueOf(placa)};
        try{
            if(!placa.isEmpty()){

                //sqLiteDatabase.update(FacturaModel.NOMBRETABLA, cv, "placa=?", whereArgs );
                sqLiteDatabase.execSQL("UPDATE facturas SET valor= '0' where placa='"
                        + placa +"' and numFac = (SELECT MAX(numFac) FROM facturas WHERE placa='"+ placa +"')");
            } else {
                Toast.makeText(context, "No se pudo pasar la factura anterior a 0", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eliminarFacturas(){
        try {
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(FacturaModel .DELETE_ALL);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void eliminarTablaFacturas(){
        try{
            SQLiteDatabase sqLiteDatabase;
            DBHelper sqLiteOpenHelper;
            sqLiteOpenHelper = new DBHelper(context);
            sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(FacturaModel.DELETE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



//endregion


}