package fr.intech.pesefit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Alexandre on 15/09/2016.
 */
public class UserDataManager
{
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserDataContract.UserDataEntry.TABLE_NAME + " ("
                    + UserDataContract.UserDataEntry._ID + " INTEGER PRIMARY KEY,"
                    + UserDataContract.UserDataEntry.COLUMN_NAME_DATE + INTEGER_TYPE + COMMA_SEP
                    + UserDataContract.UserDataEntry.COLUMN_NAME_DURATION + INTEGER_TYPE + COMMA_SEP
                    + UserDataContract.UserDataEntry.COLUMN_NAME_ACTIVITY + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " +  UserDataContract.UserDataEntry.TABLE_NAME;

    private UserDataDbHelper userDataDbHelper; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public UserDataManager(Context context)
    {
        userDataDbHelper = UserDataDbHelper.getInstance(context);
    }

    public void open()
    {
        // Gets the data repository in write/read mode
        db = userDataDbHelper.getWritableDatabase();
    }

    public void close()
    {
        //close BDD
        db.close();
    }

    /**
     * Add userData in table
     * @param userData
     * @return return id of new insertion or -1 on error
     */
    public long addUserData(UserData userData) {

        ContentValues values = new ContentValues();
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_DATE, userData.getDate());
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_DURATION, userData.getDuration());
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_ACTIVITY, userData.getActivity());

        return db.insert(UserDataContract.UserDataEntry.TABLE_NAME, null, values);
    }

    /**
     * Retrieve last insert userData
     * @return userData
     */
    public UserData getLastUserData() {

        UserData userData = new UserData();

        Cursor c = db.rawQuery(
                "SELECT * FROM "
                + UserDataContract.UserDataEntry.TABLE_NAME
                + " ORDER BY " + UserDataContract.UserDataEntry._ID  + " DESC LIMIT 1", null);


        if (c.moveToFirst()) {
            userData.setId(c.getInt(c.getColumnIndex(UserDataContract.UserDataEntry._ID)));
            userData.setDate(c.getLong(c.getColumnIndex(UserDataContract.UserDataEntry.COLUMN_NAME_DATE)));
            userData.setDuration(c.getLong(c.getColumnIndex(UserDataContract.UserDataEntry.COLUMN_NAME_DURATION)));
            userData.setActivity(c.getInt(c.getColumnIndex(UserDataContract.UserDataEntry.COLUMN_NAME_ACTIVITY)));
            c.close();
        }

        return userData;
    }

    /**
     * Update userData
     * @param userData
     * @return number of affected rows by the request
     */
    public int updateUserData(UserData userData) {

        ContentValues values = new ContentValues();
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_DATE, userData.getDate());
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_DURATION, userData.getDuration());
        values.put(UserDataContract.UserDataEntry.COLUMN_NAME_ACTIVITY, userData.getActivity());

        String selection  = UserDataContract.UserDataEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(userData.getId())};

        return db.update(UserDataContract.UserDataEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Return all user Data
     * @return all user Data
     */
    public Cursor getAllUserData() {
        return db.rawQuery("SELECT * FROM "+ UserDataContract.UserDataEntry.TABLE_NAME, null);
    }


    public static String getSqlCreateEntries() {
        return SQL_CREATE_ENTRIES;
    }

    public static String getSqlDeleteEntries() {
        return SQL_DELETE_ENTRIES;
    }



}
