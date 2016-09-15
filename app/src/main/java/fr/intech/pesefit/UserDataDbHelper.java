package fr.intech.pesefit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandre on 15/09/2016.
 */
public class UserDataDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "UserDataManager.db";
    private static final int DATABASE_VERSION = 1;
    //Singleton
    private static UserDataDbHelper sInstance;

    public static synchronized UserDataDbHelper getInstance(Context context) {
        if (sInstance == null) { sInstance = new UserDataDbHelper(context); }
        return sInstance;
    }

    private UserDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDataManager.getSqlCreateEntries()); // create table "userData"
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDataManager.getSqlDeleteEntries());
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
