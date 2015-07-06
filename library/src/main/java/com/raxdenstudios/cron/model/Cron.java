package com.raxdenstudios.cron.model;

import android.app.AlarmManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.raxdenstudios.commons.util.ObjectUtils;
import com.raxdenstudios.cron.db.CronOpenHelper;
import com.raxdenstudios.db.DatabaseParcelable;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cron implements Serializable, Parcelable, DatabaseParcelable {

	private long id;
	private int type;
	private long triggerAtTime;
	private long interval;
	private boolean status;
	
	public Cron (Cursor cursor) {
		this.id = cursor.getLong(cursor.getColumnIndex(CronOpenHelper.CRON_ID));
		this.type = cursor.getInt(cursor.getColumnIndex(CronOpenHelper.CRON_TYPE));
		this.triggerAtTime = cursor.getLong(cursor.getColumnIndex(CronOpenHelper.CRON_TRIGGER_AT_TIME));
		this.interval = cursor.getLong(cursor.getColumnIndex(CronOpenHelper.CRON_INTERVAL));
		this.status = cursor.getInt(cursor.getColumnIndex(CronOpenHelper.CRON_STATUS)) == 1;
	}

	public Cron (Parcel in) {
		id = in.readLong();
		type = in.readInt();
		triggerAtTime = in.readLong();
		interval = in.readLong();
		status = in.readInt() == 1;
	}
	
	public Cron (long id) {
		this(id, 0, 0);
	}
	
	public Cron (long id, long triggerAtTime, long interval) {
		this(id, triggerAtTime, interval, AlarmManager.RTC_WAKEUP);
	}
	
	public Cron (long id, long triggerAtTime, long interval, int type) {
		this(id, triggerAtTime, interval, type, true);
	}
	
	public Cron (long id, long triggerAtTime, long interval, int type, boolean status) {
		this.id = id;
		this.triggerAtTime = triggerAtTime;
		this.interval = interval;
		this.type = type;
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTriggerAtTime() {
		return triggerAtTime;
	}

	public void setTriggerAtTime(long triggerAtTime) {
		this.triggerAtTime = triggerAtTime;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public ContentValues readContentValues() {
		ContentValues values = new ContentValues();
		if (id > 0) values.put(CronOpenHelper.CRON_ID, id);
		values.put(CronOpenHelper.CRON_TRIGGER_AT_TIME, triggerAtTime);
		values.put(CronOpenHelper.CRON_INTERVAL, interval);
		values.put(CronOpenHelper.CRON_TYPE, type);
		values.put(CronOpenHelper.CRON_STATUS, status ? 1 : 0);
		return values;
	}
		
	public String toString() {
		return ObjectUtils.dump(this);
	}

	public static final Parcelable.Creator<Cron> CREATOR = new Parcelable.Creator<Cron>() {
		public Cron createFromParcel(Parcel in) {
			return new Cron(in);
		}

		public Cron[] newArray(int size) {
			return new Cron[size];
		}
	};	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(type);
		dest.writeLong(triggerAtTime);
		dest.writeLong(interval);
		dest.writeInt(status ? 1 : 0);
	}

}
