package com.leofis.network.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    public final String KEY_GENERIC_ID = "GenericID";
    public final String KEY_INTERFACE_NAME = "InterfaceName";
    public final String KEY_INTERFACE_IP = "InterfaceIP";
    public final String KEY_MALICIOUS_PATTERN = "MaliciousPattern";
    public final String KEY_MALICIOUS_IP = "MaliciousIP";
    public final String COUNT = "Count";
    public final String STATE = "State";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "Nsa_Android";
    private static final String DATABASE_TABLE_ONE = "MaliciousIPCount";
    private static final String DATABASE_TABLE_TWO = "MaliciousPatternCount";
    private static final String DATABASE_TABLE_THREE = "InterfaceState";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_0NE =
            "create table MaliciousIPCount (GenericID text not null, "
                    + "InterfaceName text not null, InterfaceIP text not null, "
                    + "MaliciousIP text not null, Count integer, "
                    + "primary key (GenericID, InterfaceName,InterfaceIP,MaliciousIP));";

    private static final String CREATE_TABLE_TWO =
            "create table MaliciousPatternCount (GenericID text not null, "
                    + "InterfaceName text not null, InterfaceIP text not null, "
                    + "MaliciousPattern text not null, Count integer, "
                    + "primary key (GenericID, InterfaceName,InterfaceIP,MaliciousPattern));";

    private static final String CREATE_TABLE_THREE =
            "create table InterfaceState (GenericID text not null, "
                    + "InterfaceName text not null, State text, "
                    + "primary key (GenericID, InterfaceName));";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_0NE);
            db.execSQL(CREATE_TABLE_TWO);
            db.execSQL(CREATE_TABLE_THREE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            // on upgrade drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ONE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TWO);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_THREE);

            // create new tables
            onCreate(db);
        }
    }

    //---opens the database---
    public synchronized DatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public synchronized void close() {
        DBHelper.close();
    }

    //---insert the first count to the MaliciousIPCount table---
    public synchronized long insertIPCount(String genericID, String interfaceName, String interfaceIP, String maliciousIP, Integer count) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(KEY_INTERFACE_IP, interfaceIP);
        initialValues.put(KEY_MALICIOUS_IP, maliciousIP);
        initialValues.put(COUNT, count);
        return db.insert(DATABASE_TABLE_ONE, null, initialValues);
    }

    //---insert the first count to the MaliciousPatternCount table identical---
    public synchronized long insertPatternCount(String genericID, String interfaceName, String interfaceIP, String maliciousPattern, Integer count) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(KEY_INTERFACE_IP, interfaceIP);
        initialValues.put(KEY_MALICIOUS_PATTERN, maliciousPattern);
        initialValues.put(COUNT, count);
        return db.insert(DATABASE_TABLE_TWO, null, initialValues);
    }

    //---insert the first count to the InterfaceState table---
    public synchronized long insertInterfaceState(String genericID, String interfaceName, String state) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(STATE, state);
        return db.insert(DATABASE_TABLE_THREE, null, initialValues);
    }

    //---deletes a particular rows---
    public synchronized void deletePC(String genericID) {
        db.delete(DATABASE_TABLE_ONE, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
        db.delete(DATABASE_TABLE_TWO, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
        db.delete(DATABASE_TABLE_THREE, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
    }

    //---deletes the database---
    public synchronized void deleteDB() {
        db.execSQL("delete from " + DATABASE_TABLE_ONE);
        db.execSQL("delete from " + DATABASE_TABLE_TWO);
        db.execSQL("delete from " + DATABASE_TABLE_THREE);
    }

    //---retrieves all the IPTable---
    public synchronized Cursor getAllIPTable() {
        return db.query(DATABASE_TABLE_ONE, new String[]{
                        KEY_GENERIC_ID,
                        KEY_INTERFACE_NAME,
                        KEY_INTERFACE_IP,
                        KEY_MALICIOUS_IP,
                        COUNT},
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves all the PatternTable---
    public synchronized Cursor getAllPatternTable() {
        return db.query(DATABASE_TABLE_TWO, new String[]{
                        KEY_GENERIC_ID,
                        KEY_INTERFACE_NAME,
                        KEY_INTERFACE_IP,
                        KEY_MALICIOUS_IP,
                        COUNT},
                null,
                null,
                null,
                null,
                null);
    }

    //---retrieves all the InterfaceStateTable---
    public Cursor getAllInterfaceTable() {
        return db.query(DATABASE_TABLE_THREE, new String[]{
                        KEY_GENERIC_ID,
                        KEY_INTERFACE_NAME,
                        STATE},
                null,
                null,
                null,
                null,
                null);
    }


    //---retrieves all the StatisticsPerIPInterface---
    public Cursor getStatisticsPerIPInterface(String genericID, String interfaceName) throws SQLException {
        return db.query(DATABASE_TABLE_ONE, new String[]{
                        KEY_GENERIC_ID,
                        KEY_INTERFACE_NAME,
                        KEY_INTERFACE_IP,
                        KEY_MALICIOUS_IP,
                        COUNT},
                KEY_GENERIC_ID + "=? and " + KEY_INTERFACE_NAME + "=?",
                new String[]{genericID, interfaceName},
                null,
                null,
                null);
    }

    //---retrieves all the getStatisticsPerPatternInterface---
    public Cursor getStatisticsPerPatternInterface(String genericID, String interfaceName) throws SQLException {
        return db.query(DATABASE_TABLE_TWO, new String[]{
                        KEY_GENERIC_ID,
                        KEY_INTERFACE_NAME,
                        KEY_INTERFACE_IP,
                        KEY_MALICIOUS_PATTERN,
                        COUNT},
                KEY_GENERIC_ID + "=? and " + KEY_INTERFACE_NAME + "=?",
                new String[]{genericID, interfaceName},
                null,
                null,
                null);
    }

    //---retrieves a particular filed---
    public Cursor interfaceStatePerCo(String genericID) throws SQLException {
        Cursor mCursor =
                db.query(DATABASE_TABLE_THREE, new String[]{
                                KEY_GENERIC_ID,
                                KEY_INTERFACE_NAME,
                                STATE
                        },
                        KEY_GENERIC_ID + "=?",
                        new String[]{genericID},
                        null,
                        null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates the maliciousIPCount table---
    public boolean updateIPCount(String genericID, String interfaceName, String interfaceIP, String maliciousIP, Integer count) {
        ContentValues args = new ContentValues();
        args.put(COUNT, count);
        return db.update(DATABASE_TABLE_ONE, args,
                KEY_GENERIC_ID + "=?" + " AND " + KEY_INTERFACE_NAME + "=?" + " AND "
                        + KEY_INTERFACE_IP + "=?" + " AND " + KEY_MALICIOUS_IP + "=?",
                new String[]{genericID, interfaceName, interfaceIP, maliciousIP}) > 0;
    }

    //---updates the maliciousPatternCount table identical---
    public boolean updatePatternCount(String genericID, String interfaceName, String interfaceIP, String maliciousPattern, Integer count) {
        ContentValues args = new ContentValues();
        args.put(COUNT, count);
        return db.update(DATABASE_TABLE_TWO, args,
                KEY_GENERIC_ID + "=?" + " AND " + KEY_INTERFACE_NAME + "=?" + " AND "
                        + KEY_INTERFACE_IP + "=?" + " AND " + KEY_MALICIOUS_PATTERN + "=?",
                new String[]{genericID, interfaceName, interfaceIP, maliciousPattern}) > 0;
    }

    //---updates the InterfaceSate table identical---
    public boolean updateInterfaceState(String genericID, String interfaceName, String state) {
        ContentValues args = new ContentValues();
        args.put(STATE, state);
        return db.update(DATABASE_TABLE_THREE, args,
                KEY_GENERIC_ID + "=?" + " AND " + KEY_INTERFACE_NAME + "=?",
                new String[]{genericID, interfaceName}) > 0;
    }


}
