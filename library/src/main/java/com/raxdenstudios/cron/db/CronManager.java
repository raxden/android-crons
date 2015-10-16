package com.raxdenstudios.cron.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.db.DBManager;
import com.raxdenstudios.db.task.DBTask;
import com.raxdenstudios.db.task.DeleteDBTask;
import com.raxdenstudios.db.task.SaveDBTask;
import com.raxdenstudios.db.task.SelectDBTask;

import java.util.ArrayList;
import java.util.List;

public class CronManager extends DBManager<Cron> {

    private static final String TAG = CronManager.class.getSimpleName();
	
	public CronManager(SQLiteOpenHelper oh) {
		super(oh);
	}

	@Override
	public void delete(final Cron cron, final DBDeleteCallbacks<Cron> callbacks) {
        new DeleteDBTask(getOpenHelper(), CronOpenHelper.CRON_TABLE_NAME, CronOpenHelper.CRON_ID+"=?", new String[] { Long.toString(cron.getId()) }, new DBTask.DBTaskCallbacks<Integer>() {

			@Override
			public void onBeginTransaction(SQLiteDatabase db) {}

			@Override
			public void onPreFinalizeTransaction(SQLiteDatabase db, Integer deletedRows) {}
			
			@Override
			public void onCompletion(Integer deletedRows) {	
				Log.d(TAG, "[onCompletion] cron deleted: " + cron.toString());
				if (callbacks != null) callbacks.dataDeleted(cron);
			}
        	
		}).execute();
	}

	@Override
	public void deleteAll(final DBDeleteAllCallbacks<Cron> callbacks) {
		SelectDBTask task = new SelectDBTask(getOpenHelper(), CronOpenHelper.CRON_TABLE_NAME, new DBTask.DBTaskCallbacks<Cursor>() {

			private List<Cron> crons = new ArrayList<Cron>();
			
			@Override
			public void onBeginTransaction(SQLiteDatabase db) {}

			@Override
			public void onPreFinalizeTransaction(SQLiteDatabase db, Cursor cursor) {
				if (cursor != null && cursor.moveToFirst()) {
					do {
						Cron cron = new Cron(cursor);
						crons.add(cron);
					} while (cursor.moveToNext());
				}
				DBManager.deleteAll(db, CronOpenHelper.CRON_TABLE_NAME);
			}

			@Override
			public void onCompletion(Cursor cursor) {
                for (Cron cron : crons) {
                    Log.d(TAG, "[onCompletion] cron deleted: "+cron.toString());
                }
				if (callbacks != null) callbacks.dataDeleted(crons);
			}				
			
		});
		task.setWritableDatabase(true);
		task.execute();
	}

	@Override
	public void find(final String id, final DBFindCallbacks<Cron> callbacks) {
		new SelectDBTask(getOpenHelper(), CronOpenHelper.CRON_TABLE_NAME, CronOpenHelper.CRON_ID+"=?", new String[] { id }, new DBTask.DBTaskCallbacks<Cursor>() {

			@Override
			public void onBeginTransaction(SQLiteDatabase db) {}

			@Override
			public void onPreFinalizeTransaction(SQLiteDatabase db, Cursor result) {}

			@Override
			public void onCompletion(Cursor cursor) {
				Cron cron = null;
				if (cursor != null && cursor.moveToFirst()) {
					cron = new Cron(cursor);
					Log.d(TAG, "[onCompletion] cron found: "+cron.toString());
				} else {
					Log.d(TAG, "[onCompletion] cron not found: "+id);
				}
				if (callbacks != null) callbacks.dataFound(cron);
			}				
			
		}).execute();
	}

	@Override
	public void findAll(final DBFindAllCallbacks<Cron> callbacks) {
		new SelectDBTask(getOpenHelper(), CronOpenHelper.CRON_TABLE_NAME, new DBTask.DBTaskCallbacks<Cursor>() {

			private List<Cron> crons = new ArrayList<Cron>();
			
			@Override
			public void onBeginTransaction(SQLiteDatabase db) {}

			@Override
			public void onPreFinalizeTransaction(SQLiteDatabase db, Cursor result) {}

			@Override
			public void onCompletion(Cursor cursor) {
				if (cursor != null && cursor.moveToFirst()) {
					do {
						Cron cron = new Cron(cursor);
						crons.add(cron);
					} while (cursor.moveToNext());
				}
                for (Cron cron : crons) {
					Log.d(TAG, "[onCompletion] cron found: "+cron.toString());
				}
				if (callbacks != null) callbacks.dataFound(crons);
			}				
			
		}).execute();
	}

	@Override
	public void save(final Cron cron, final DBSaveCallbacks<Cron> callbacks) {
		new SaveDBTask(getOpenHelper(), CronOpenHelper.CRON_TABLE_NAME, cron, CronOpenHelper.CRON_ID+"=?", new String[] { Long.toString(cron.getId()) }, new DBTask.DBTaskCallbacks<Object>() {

			@Override
			public void onBeginTransaction(SQLiteDatabase db) {}

			@Override
			public void onPreFinalizeTransaction(SQLiteDatabase db, Object result) {
				if (result instanceof Long) cron.setId((Long)result);
			}

			@Override
			public void onCompletion(Object result) {
				Log.d(TAG, "[onCompletion] cron saved: "+cron.toString());
				if (callbacks != null) callbacks.dataSaved(cron);
			}
			
		}).execute();
	}
	
}
