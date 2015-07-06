package com.raxdenstudios.cron.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CronOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    
    public static final String BD_NAME = "com_esthar_commons";

	/* CRON_TABLE_NAME */
	public static final String CRON_TABLE_NAME = "com_raxdenstudios_cron";

	/* CRON_TABLE_NAME VALUES */
	public static final String CRON_ID = "cron_id";
	public static final String CRON_TRIGGER_AT_TIME = "cron_trigger_at_time";
	public static final String CRON_INTERVAL = "cron_interval";
	public static final String CRON_TYPE = "cron_type";
	public static final String CRON_STATUS = "cron_status";

	/* CRON TABLE -  User data */
	public static final String CRON_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CRON_TABLE_NAME + " (" +
			CRON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			CRON_TRIGGER_AT_TIME + " INT, " +
			CRON_INTERVAL + " INT, " +
			CRON_TYPE + " INT, " +
			CRON_STATUS + " INT " +
			");";

	public CronOpenHelper(Context context) {
		super(context, BD_NAME, null, DATABASE_VERSION);
	}

	public CronOpenHelper(Context context, String datebaseName, CursorFactory cursorFactory, int databaseVersion) {
		super(context, datebaseName, cursorFactory, databaseVersion);
	}	
	
	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL(CRON_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+CRON_TABLE_NAME);
		onCreate(db);
	}

}
