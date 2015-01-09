package com.leofis.network.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

    private final String KEY_GENERIC_ID = "GenericID";
    private final String KEY_INTERFACE_NAME = "InterfaceName";
    private final String KEY_INTERFACE_IP = "InterfaceIP";
    private final String KEY_MALICIOUS_PATTERN = "MaliciousPattern";
    private final String KEY_MALICIOUS_IP = "MaliciousIP";
    private final String COUNT = "Count";
    private final String STATE = "State";
    private final String TAG = "DBAdapter";

    private final String DATABASE_NAME = "Nsa_Android";
    private final String DATABASE_TABLE_ONE = "MaliciousIPCount";
    private final String DATABASE_TABLE_TWO = "MaliciousPatternCount";
    private final String DATABASE_TABLE_THREE = "InterfaceState";
    private final String DATABASE_TABLE_FOUR = "OfflineWork";
    private final int DATABASE_VERSION = 1;

    private final String CREATE_TABLE_0NE =
            "create table MaliciousIPCount (GenericID text not null, "
                    + "InterfaceName text not null, InterfaceIP text not null, "
                    + "MaliciousIP text not null, Count integer, "
                    + "primary key (GenericID, InterfaceName,InterfaceIP,MaliciousIP));";

    private final String CREATE_TABLE_TWO =
            "create table MaliciousPatternCount (GenericID text not null, "
                    + "InterfaceName text not null, InterfaceIP text not null, "
                    + "MaliciousPattern text not null, Count integer, "
                    + "primary key (GenericID, InterfaceName,InterfaceIP,MaliciousPattern));";

    private final String CREATE_TABLE_THREE =
            "create table InterfaceState (GenericID text not null, "
                    + "InterfaceName text not null, State text, "
                    + "primary key (GenericID, InterfaceName));";

    private final String CREATE_TABLE_FOUR =
            "create table OfflineWork (_id integer primary key autoincrement, "
                    + "RequestedJob text not null"
                    + ");";

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_0NE);
            db.execSQL(CREATE_TABLE_TWO);
            db.execSQL(CREATE_TABLE_THREE);
            db.execSQL(CREATE_TABLE_FOUR);
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
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_FOUR);
            // create new tables
            onCreate(db);
        }
    }

    //---opens the database---
    public DatabaseAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---insert the first count to the MaliciousIPCount table---
    public long insertIPCount(String genericID, String interfaceName, String interfaceIP, String maliciousIP, Integer count) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(KEY_INTERFACE_IP, interfaceIP);
        initialValues.put(KEY_MALICIOUS_IP, maliciousIP);
        initialValues.put(COUNT, count);
        return db.insert(DATABASE_TABLE_ONE, null, initialValues);
    }

    //---insert the first count to the MaliciousPatternCount table identical---
    public long insertPatternCount(String genericID, String interfaceName, String interfaceIP, String maliciousPattern, Integer count) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(KEY_INTERFACE_IP, interfaceIP);
        initialValues.put(KEY_MALICIOUS_PATTERN, maliciousPattern);
        initialValues.put(COUNT, count);
        return db.insert(DATABASE_TABLE_TWO, null, initialValues);
    }

    //---insert the first count to the InterfaceState table---
    public long insertInterfaceState(String genericID, String interfaceName, String state) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GENERIC_ID, genericID);
        initialValues.put(KEY_INTERFACE_NAME, interfaceName);
        initialValues.put(STATE, state);
        return db.insert(DATABASE_TABLE_THREE, null, initialValues);
    }

    //---insert the first count to the OfflineWork  table---
    public long insertOfflineWork(String requestedJob) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("RequestedJob", requestedJob);
        return db.insert(DATABASE_TABLE_FOUR, null, initialValues);
    }

    //---deletes a particular rows---
    public void deletePC(String genericID) {
        db.delete(DATABASE_TABLE_ONE, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
        db.delete(DATABASE_TABLE_TWO, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
        db.delete(DATABASE_TABLE_THREE, KEY_GENERIC_ID +
                "=?", new String[]{genericID});
    }

    //---deletes a particular rows OfflineWork---
    public void deleteJob(String requestedJob) {
        db.delete(DATABASE_TABLE_FOUR, "RequestedJob" +
                "=?", new String[]{requestedJob});
    }

    //---deletes the database---
    public void deleteDB() {
        db.execSQL("delete from " + DATABASE_TABLE_ONE);
        db.execSQL("delete from " + DATABASE_TABLE_TWO);
        db.execSQL("delete from " + DATABASE_TABLE_THREE);
        db.execSQL("delete from " + DATABASE_TABLE_FOUR);
    }

    //---retrieves all the IPTable---
    public Cursor getAllIPTable() {
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
    public Cursor getAllPatternTable() {
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

    //---retrieves all the OfflineWork---
    public Cursor getAllOfflineWork() {
        return db.query(DATABASE_TABLE_FOUR, new String[]{
                        "_id",
                        "RequestedJob"},
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
