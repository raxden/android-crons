package com.raxdenstudios.cron.db;

import com.raxdenstudios.cron.db.model.CronDBConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CronOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    
    public static final String BD_NAME = "com_esthar_commons";
           
	public CronOpenHelper(Context context) {
		super(context, BD_NAME, null, DATABASE_VERSION);
	}

	public CronOpenHelper(Context context, String datebaseName, CursorFactory cursorFactory, int databaseVersion) {
		super(context, datebaseName, cursorFactory, databaseVersion);
	}	
	
	@Override
	public void onCreate(SQLiteDatabase db) {	
		db.execSQL(CronDBConstants.CRON_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+CronDBConstants.CRON_TABLE_NAME);
		onCreate(db);
	}

}
