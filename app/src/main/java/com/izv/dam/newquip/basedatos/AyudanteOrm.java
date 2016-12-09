package com.izv.dam.newquip.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.izv.dam.newquip.pojo.Localizacion;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by dam on 7/12/16.
 */

public class AyudanteOrm extends OrmLiteSqliteOpenHelper {
    public static final int VERSION = 1;

    private Dao<Localizacion, Integer> simpleDao = null;
    private RuntimeExceptionDao<Localizacion, Integer> simpleRunTimeDao = null;

    public AyudanteOrm(Context context) {
        super(context, "localizacion", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Localizacion.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Localizacion.class, true);
            onCreate(database,connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Localizacion, Integer> getSimpleDao() throws SQLException{
        if (simpleDao == null){
            simpleDao = getDao(Localizacion.class);
        }
        return simpleDao;
    }

    public RuntimeExceptionDao<Localizacion, Integer> getSimpleRunTimeDao() {
        if (simpleRunTimeDao == null){
            simpleRunTimeDao = getRuntimeExceptionDao(Localizacion.class);
        }
        return simpleRunTimeDao;
    }

    @Override
    public void close(){
        super.close();
        simpleDao = null;
        simpleRunTimeDao = null;
    }
}
