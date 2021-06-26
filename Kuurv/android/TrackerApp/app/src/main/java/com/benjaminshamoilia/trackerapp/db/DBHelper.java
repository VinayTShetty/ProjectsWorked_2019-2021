package com.benjaminshamoilia.trackerapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//import static androidx.constraintlayout.Constraints.TAG;

public class DBHelper extends SQLiteOpenHelper
{
    private static String DB_PATH = "/data/data/com.benjaminshamoilia.trackerapp/databases/";

    private final static String DB_NAME = "tracker.sqlite";
    private final static int DB_VERSION = 1;
    private final Context myContext;

    private static SQLiteDatabase mSqLiteDatabase;


    /*Table Name*/
    public static String Device_Table = "Device_Table";
    public static String UserAccount_Table = "UserAccount_Table";
    public static String User_Set_Info = "User_Set_Info";
    public static String User_latlong = "latlong";


    // Device_Table
    public static String user_id = "user_id";
    public static String ble_address = "ble_address";  //ble_address
    public static String device_name = "device_name";
    public static String correction_status = "correction_status";
    public static String device_type = "device_type";
    public static String latitude = "latitude";
    public static String longitude = "longitude";
    public static String photo_local_url = "photo_localURL";
    public static String tracker_device_alert = "tracker_device_alert";
    public static String marked_lost = "marked_lost";
    public static String is_active = "is_active";
    public static String contact_name = "contact_name";
    public static String contact_email = "contact_email";
    public static String contact_mobile = "contact_mobile";
    public static String status = "last_status";
    public static String created_time = "created_time";
    public static String updated_time = "updated_time";
    public static String id = "id";
    public static String server_id = "server_id";
    public static String tracker_locAdds = "address_tracker";


    public static String identifier = "identifier";
    public static String is_sync = "is_sync";
    public static String photo_server_url = "photo_serverURL";


    public static String DeviceFiledSilentMode = "is_silent_mode";
    public static String DeviceFiledBuzzerVolume = "buzzer_volume";
    public static String DeviceFiledSeperateAlert = "seperate_alert";
    public static String DeviceFiledRepeatAlert = "repeat_alert";


//    public static String DeviceFiledSelectedSound = "selected_sound";
//    public static String DeviceFiledDurationAlert = "duration_alert";
    public static String DeviceFiledBatteryStatus = "battery_status";

    // UserAccount_Table
    public static String account_name = "account_name";
    public static String user_email = "user_email";
    public static String user_pw = "user_pw";
    public static String fb_id = "fb_id";
    public static String twitter_id = "twitter_id";
    public static String is_social_login = "is_social_login";
    public static String device_token = "device_token";
    public static String user_unique_key = "user_unique_key";



    // User_Set_Info
    public static String User_Set_Info_user_id = "user_id";
    public static String name = "name";
    public static String email = "email";
    public static String mobile = "mobile";


    //latlongTable
    public static String UserLatitude = "latitude";
    public static String UserLongitude = "longitude";
    public static String LoginUserID = "User_id";


    private static DBHelper sInstance;


    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    /* Get Database Instance*/
    public static synchronized DBHelper getDBHelperInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /*Initialize database Path */
    public DBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        System.out.println("DATABASE-PATH=" + DB_PATH);
        this.myContext = context;
        //		mCommomMethod=new CommomMethod(myContext);
        //		myImageLoader=new MyImageLoader(myContext);
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    /*Create Database */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        System.out.println("dbExist" + dbExist);
        if (dbExist) {

        } else {
            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade : " + oldVersion + "-" + newVersion);
    }


    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        File databasePath = this.myContext.getDatabasePath(DB_PATH + DB_NAME);
        if (databasePath != null) {
            return databasePath.exists();
        } else {
            return false;
        }
//        SQLiteDatabase checkDB = null;
//        try {
//            String myPath = DB_PATH + DB_NAME;
//            checkDB = SQLiteDatabase.openDatabase(myPath, null,
//                    SQLiteDatabase.OPEN_READWRITE);
//
//            checkDB = getWritableDatabase();
//
//        } catch (SQLiteException e) {
//            // database does't exist yet.
//            e.printStackTrace();
//        }
//        if (checkDB != null) {
//            checkDB.close();
//        }
//        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        SQLiteDatabase database = this.getReadableDatabase();
        String outFileName = database.getPath();
        database.close();
        System.out.println("outFileName-" + outFileName);
//        String outFileName1 = DB_PATH + DB_NAME;
//        System.out.println("outFileName1-"+outFileName1);
        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    /* Open Database Connection*/
    public void openDatabase() throws SQLException {
        try {
            if (mSqLiteDatabase != null && mSqLiteDatabase.isOpen()) {
                mSqLiteDatabase.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mSqLiteDatabase != null && mSqLiteDatabase.isOpen()) {
                mSqLiteDatabase.close();
            }
        }

        // Open the database
        mSqLiteDatabase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
                SQLiteDatabase.OPEN_READWRITE);

    }

    /*Close Database connection*/
    public synchronized void close() {
        if (mSqLiteDatabase != null)
            mSqLiteDatabase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {

    }


    /**
     * Use this function to set the value of a particular column
     *
     * @param columnName       The column name whose value is to be changed
     * @param newColumnValue   The value to be replaced in the column
     * @param whereColumnName  The column name to be compared with the where clause
     * @param whereColumnValue The value to be compared in the where clause
     */
    void onUpdateSet(String columnName, String newColumnValue,
                     String[] whereColumnName, String[] whereColumnValue) {
        String expanded_ColumnNames = new String(whereColumnName[0]);
        String expanded_ColumnValues = new String(whereColumnValue[0]);
        for (int i = 1; i < whereColumnName.length; i++) {
            expanded_ColumnNames = expanded_ColumnNames + ","
                    + whereColumnName[i];
            expanded_ColumnValues = expanded_ColumnValues + ","
                    + whereColumnValue[i];
        }
        try {
            openDatabase();
            mSqLiteDatabase.execSQL("update recipe set \"" + columnName + "\" = \""
                    + newColumnValue + "\" where \"" + expanded_ColumnNames
                    + "\" = \"" + expanded_ColumnValues + "\"");
        } catch (Exception e) {
        }

    }

    /**
     * Query the given table, returning a Cursor over the result set.
     *
     * @param table         The table name to compile the query against.
     * @param columns       A list of which columns to return. Passing null will return
     *                      all columns, which is discouraged to prevent reading data from
     *                      storage that isn't going to be used.
     * @param selection     A filter declaring which rows to return, formatted as an SQL
     *                      WHERE clause (excluding the WHERE itself). Passing null will
     *                      return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be replaced by the
     *                      values from selectionArgs, in order that they appear in the
     *                      selection. The values will be bound as Strings.
     * @param groupBy       A filter declaring how to group rows, formatted as an SQL
     *                      GROUP BY clause (excluding the GROUP BY itself). Passing null
     *                      will cause the rows to not be grouped.
     * @param having        A filter declare which row groups to include in the cursor, if
     *                      row grouping is being used, formatted as an SQL HAVING clause
     *                      (excluding the HAVING itself). Passing null will cause all row
     *                      groups to be included, and is required when row grouping is
     *                      not being used.
     * @param orderBy       How to order the rows, formatted as an SQL ORDER BY clause
     *                      (excluding the ORDER BY itself). Passing null will use the
     *                      default sort order, which may be unordered.
     * @return A Cursor object, which is positioned before the first entry
     */
    public Cursor onQueryGetCursor(String table, String[] columns, String selection,
                                   String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor query = null;
        try {
            openDatabase();
            query = mSqLiteDatabase.query(table, columns, selection, selectionArgs, groupBy,
                    having, orderBy);
        } catch (Exception e) {
        }
        return query;
    }

    /**
     * Use this method to search a particular String in the provided field.
     *
     * @param columns     The array of columns to be returned
     * @param table       The table name
     * @param whereColumn The where clause specifying a particular columns
     * @param keyword     The keyword which is to be searched
     * @return The cursor containing the result of the query
     */
    Cursor onSearchGetCursor(String[] columns, String table,
                             String[] whereColumn, String keyword) {
        String expColumns = new String(columns[0]);
        Cursor rawquery = null;
        for (int i = 1; i < columns.length; i++)
            expColumns = expColumns + "," + columns[i];
        try {
            openDatabase();
            rawquery = mSqLiteDatabase.rawQuery("SELECT " + expColumns + " from " + table
                    + " where " + whereColumn[0] + " like \"%" + keyword
                    + "%\" or " + whereColumn[1] + " like \"%" + keyword
                    + "%\" or " + whereColumn[2] + " like \"%" + keyword
                    + "%\"", null);
        } catch (Exception e) {
        }
        return rawquery;
    }

    /*Fetch Record From Database*/
    public int getCountRecordByQuery(String countQuery) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cnt = cursor.getInt(0);
        }
        cursor.close();
//        System.out.println("countQueryUrl-" + countQuery + ":" + cnt);
        return cnt;
    }

    public Cursor Query(String sql) {
        Cursor c = null;
        try {
            c = mSqLiteDatabase.rawQuery(sql, null);
        } catch (Exception e) {
//            c.close();
        }
        return c;
    }

    public int getFirstRecordSqlQueryInt(String sql) {

        Cursor c = null;

        try {
            c = mSqLiteDatabase.rawQuery(sql, null);
        } catch (Exception e) {
        }
        c.moveToFirst();
        return c.getInt(0);
    }

//    public String getSingleRecordByQuery(String sql) {
//        System.out.println("url-" + sql);
//        Cursor c = null;
//        try {
//            c = db.rawQuery(sql, null);
//        } catch (Exception e) {
//        }
//        c.moveToFirst();
//        return c.getString(0);
//    }

  /*  public String getQueryResult(String query) {
//        System.out.println("query-" + query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        String result = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(0);
        }
        cursor.close();
//        System.out.println("result-" + result);
        return result;
    }*/

    public int getQueryIntResult(String query) {
//        System.out.println("query-" + query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int result = 0;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
        }
        cursor.close();
//        System.out.println("result-" + result);
        return result;
    }

    /**
     * update particular record in the database.
     *
     * @param table
     * @param whereClause
     * @param whereArgs
     */
    public void onDelete(String table, String whereClause, String[] whereArgs) {
        try {
            mSqLiteDatabase.delete(table, whereClause, whereArgs);
        } catch (Exception e) {
        }
    }

    /**
     * update particular record in the database.
     *
     * @param tableName
     * @param cValue
     * @param WhereField
     * @param complareValue
     */
    public void updateRecord(String tableName, ContentValues cValue, String WhereField, String[] complareValue) {
//        openDatabase();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
//              System.out.println("DBHelper >>>>> " +complareValue);


          /*  for (String s: complareValue) {
                //Do your stuff here
                System.out.println("DBHelper >>>>> " +s);
                System.out.println(s);
            }*/

            db.update(tableName, cValue, WhereField, complareValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert the record in the database.
     *
     * @param tableName
     * @param cValue
     */
    public int insertRecord(String tableName, ContentValues cValue) {
//        openDatabase();
        System.out.println("Record Inseted");
        System.out.println("Table Name ="+tableName  +"Values = "+cValue.describeContents());

        try {
         //   System.out.println("inside the Try block");
            SQLiteDatabase db = this.getWritableDatabase();
            return (int) db.insert(tableName, null, cValue);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Call Insert err...."+e.toString());
        }

        return -1;
    }

    public int getLastRecord(String countQuery) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getTableCount(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    /**
     * Insert the record in the database.
     *
     * @param tableName
     * @param cValue
     */
    public void insertUpdateRecord(String tableName, ContentValues cValue) {
        openDatabase();
        try {
            mSqLiteDatabase.replaceOrThrow(tableName, null, cValue);
        } catch (SQLException e) {
        }
    }


    public void exeQuery(String sql) {
        System.out.println("Vinay Test Querey executed");
        try {
            mSqLiteDatabase.execSQL(sql);
            System.out.println("Querey-->"+sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Vinay Test Querey exception occured");
        }

    }


    public DataHolder readCursor(Cursor mCursor) {

        // openDatabase();
        DataHolder _holder = null;

//		System.out.println("cursor read...."+mCursor.getCount());

        if (mCursor != null && mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            _holder = new DataHolder();

            while (!mCursor.isAfterLast()) {
                int count = mCursor.getColumnCount();
                _holder.CreateRow();
                for (int i = 0; i < count; i++) {
                    _holder.set_Lmap(mCursor.getColumnName(i), mCursor.getString(i));
                }
                _holder.AddRow();
                mCursor.moveToNext();
            }
        }
        return _holder;
    }

    public DataHolder read(String query) {

        // openDatabase();

//		System.out.println("query...san.."+query);

//        Cursor mCursor = Query(query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(query, null);
        DataHolder _holder = new DataHolder();
        try {
            if (mCursor != null && !mCursor.isClosed()) {
                if (mCursor.moveToFirst()) {
                    do {
                        _holder.CreateRow();
                        for (int i = 0; i < mCursor.getColumnCount(); i++) {
                            _holder.set_Lmap(mCursor.getColumnName(i), mCursor.getString(i));
                        }
                        _holder.AddRow();
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return _holder;
    }

    public DataHolder readData(String query) {

        // openDatabase();

//		System.out.println("query...san.."+query);

//        Cursor mCursor = Query(query);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(query, null);
        DataHolder _holder = new DataHolder();
        try {
            if (mCursor != null && !mCursor.isClosed()) {
                if (mCursor.moveToFirst()) {
                    do {
                        _holder.CreateRow();
                        for (int i = 0; i < mCursor.getColumnCount(); i++) {
                            _holder.set_Lmap(mCursor.getColumnName(i), mCursor.getString(i));
                        }
                        _holder.AddRow();
                    } while (mCursor.moveToNext());
                }
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return _holder;
    }


    //Vinay Code

    //print the contents of the table..
    //this method can be called as
    //      String result=  mActivity.mDbHelper.getTableAsString(mActivity.mDbHelper.getReadableDatabase(),"Device_Table");
    //      System.out.println(result);
    public static String getTableAsString(SQLiteDatabase db, String tableName) {
       // Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }



    // This code is to get the table columns details from the SQL ite...
    public static Map<String, String> getUser_Set_Info(SQLiteDatabase mSqLiteDatabase, String table_name, String dbfield, String userId)
    {
        Map<String,String> map= new HashMap<String, String>();
        SQLiteDatabase db = mSqLiteDatabase;
        Cursor cursor = db.rawQuery("Select * from " + table_name + " where " + dbfield + " = " +  '"'+userId+'"',null);
        if (cursor.moveToFirst())
        {
            do
            {
                String user_id = cursor.getString(cursor.getColumnIndex("user_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String email = cursor.getString(cursor.getColumnIndex("email"));
                String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
                System.out.println("DB Helper Class>>>>USer Id="+user_id);
                System.out.println("DB Helper Class>>>>name="+name);
                System.out.println("DB Helper Class>>>>email="+email);
                System.out.println("DB Helper Class>>>>mobile="+mobile);
                map.put("user_id", user_id);
                map.put("name", name);
                map.put("email", email);
                map.put("mobile", mobile);


            }while (cursor.moveToNext());
        }

        return map;

    }


    /**
     * This method is used to fetch the details of the SQL ite DB and return it to the Fragment_Home...
     * @return
     */
/*    public List<Vo_Device_Regstd_from_serv> getFavList(){
        String selectQuery = "SELECT  * FROM " + DBHelper.Device_Table;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Vo_Device_Regstd_from_serv> FavList = new ArrayList<Vo_Device_Regstd_from_serv>();
        if (cursor.moveToFirst()) {
            do {
                Vo_Device_Regstd_from_serv list = new Vo_Device_Regstd_from_serv();

                list.setBle_address(cursor.getString(2));
                list.setDevice_name(cursor.getString(3));
                list.setLatitude(cursor.getString(5));
                list.setLongitude(cursor.getString(6));
                list.setPhoto_localURL(cursor.getString(13));
                list.setCreated_time(cursor.getString(16));
                list.setUpdated_time(cursor.getString(17));
                list.setPhoto_serverURL(cursor.getString(14));

                FavList.add(list);
            } while (cursor.moveToNext());
        }
        return FavList;
    }*/




    //Vinay Code

/**
 * This method is used to Fetch the info from the Devie_Table Depending on BLE_address
 */
public static Map<String, String> getBLE_Set_Info(SQLiteDatabase mSqLiteDatabase, String table_name, String dbfield, String Ble_address)
{
    Map<String,String> map= new HashMap<String, String>();
    SQLiteDatabase db = mSqLiteDatabase;
    Cursor cursor = db.rawQuery("Select * from " + table_name + " where " + dbfield + " = " +  '"'+Ble_address+'"',null);
    System.out.println("Select * from " + table_name + " where " + dbfield + " = " +  '"'+Ble_address+'"');
    System.out.println("Querey Executed");
    if (cursor.moveToFirst())
    {
        do
        {
            String user_id = cursor.getString(cursor.getColumnIndex("user_id"));
            String ble_address = cursor.getString(cursor.getColumnIndex("ble_address"));
            String device_name = cursor.getString(cursor.getColumnIndex("device_name"));
            String device_type = cursor.getString(cursor.getColumnIndex("device_type"));
            String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
            String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
            String tracker_device_alert = cursor.getString(cursor.getColumnIndex("tracker_device_alert"));
            String marked_lost = cursor.getString(cursor.getColumnIndex("marked_lost"));
            String is_active = cursor.getString(cursor.getColumnIndex("is_active"));
            String contact_name = cursor.getString(cursor.getColumnIndex("contact_name"));
            String contact_email = cursor.getString(cursor.getColumnIndex("contact_email"));
            String contact_mobile = cursor.getString(cursor.getColumnIndex("contact_mobile"));
            String photo_localURL = cursor.getString(cursor.getColumnIndex("photo_localURL"));
            String photo_serverURL = cursor.getString(cursor.getColumnIndex("photo_serverURL"));
            String correction_status = cursor.getString(cursor.getColumnIndex("correction_status"));

            String created_time = cursor.getString(cursor.getColumnIndex("created_time"));
            String updated_time = cursor.getString(cursor.getColumnIndex("updated_time"));
            String server_id = cursor.getString(cursor.getColumnIndex("server_id"));

            String localVarSilentmode = cursor.getString(cursor.getColumnIndex("is_silent_mode"));
            String localVarBuzzerVolume = cursor.getString(cursor.getColumnIndex("buzzer_volume"));
            String localVarSeprationAlert = cursor.getString(cursor.getColumnIndex("seperate_alert"));
            String localVarRepeatAlert = cursor.getString(cursor.getColumnIndex("repeat_alert"));


            map.put("user_id", user_id);
            map.put("ble_address", ble_address);
            map.put("device_name", device_name);
            map.put("device_type", device_type);
            map.put("latitude", latitude);
            map.put("longitude", longitude);
            map.put("tracker_device_alert", tracker_device_alert);
            map.put("marked_lost", marked_lost);
            map.put("is_active", is_active);
            map.put("contact_name", contact_name);
            map.put("contact_email", contact_email);
            map.put("contact_mobile", contact_mobile);
            map.put("photo_localURL", photo_localURL);
            map.put("photo_serverURL", photo_serverURL);
            map.put("correction_status", correction_status);
            map.put("created_time", created_time);
            map.put("updated_time", updated_time);
            map.put("server_id", server_id);

            map.put("is_silent_mode", localVarSilentmode);
            map.put("buzzer_volume", localVarBuzzerVolume);
            map.put("seperate_alert", localVarSeprationAlert);
            map.put("repeat_alert", localVarRepeatAlert);

        }while (cursor.moveToNext());
    }

    return map;

}

    public boolean CheckRecordAvaliableinDB(String Tablename,String ColumnName,String BleAddress) {
        SQLiteDatabase db = mSqLiteDatabase;
        String Query = "Select * from " + Tablename + " where " + ColumnName + " = " + "'" + BleAddress + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }


    /**
     * Check Table is empty or not in SQL ite..
     *
     */

    public boolean tableIsEmpty(String TableName){

        SQLiteDatabase database = this.getReadableDatabase();
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(database,TableName);

        if (NoOfRows == 0){
            return true;
        }else {
            return false;
        }
    }


}
