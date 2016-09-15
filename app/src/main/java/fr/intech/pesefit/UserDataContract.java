package fr.intech.pesefit;

import android.provider.BaseColumns;

/**
 * Created by Alexandre on 15/09/2016.
 */
public final class UserDataContract {

    private UserDataContract(){}

    public static class UserDataEntry implements BaseColumns{
        public static final String TABLE_NAME = "userData";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_ACTIVITY = "activity";

    }

}
