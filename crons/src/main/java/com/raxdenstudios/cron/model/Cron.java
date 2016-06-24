package com.raxdenstudios.cron.model;

import android.app.AlarmManager;

import com.raxdenstudios.commons.util.DateFormatUtils;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;

import io.realm.CronRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { CronRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = { Cron.class })
public class Cron extends RealmObject {

	@PrimaryKey
	private long id;
	private int type;
	private long triggerAtTime;
	private long interval;
	private boolean status;

	public Cron() {}

	private Cron(Builder builder) {
		this.id = builder.id;
		this.triggerAtTime = builder.triggerAtTime;
		this.interval = builder.interval;
		this.type = builder.type;
		this.status = builder.status;
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
	public String toString() {
        String triggerAtTime = DateFormatUtils.dateToString(new Date(getTriggerAtTime()), "dd/MM/yy HH:mm:ss");
        String intervalInSeconds = ((float)(getInterval() / (1000))) + " seconds";
        String nextLaunchInSeconds = ((float)((getTriggerAtTime() - Calendar.getInstance().getTimeInMillis()) / (1000))) + " seconds";
        return "Cron[" + getId() + "][" + isStatus() + "] triggerAtTime " + triggerAtTime + " with interval " + intervalInSeconds + " next launch in " + nextLaunchInSeconds;
	}

	public static class Builder {

		private long id;
		private int type;
		private long triggerAtTime;
		private long interval;
		private boolean status;

		public Builder() {
			this(0);
		}

		public Builder (long id) {
			this(id, 0, 0);
		}

		public Builder (long id, long triggerAtTime, long interval) {
			this(id, triggerAtTime, interval, AlarmManager.RTC_WAKEUP);
		}

		public Builder (long id, long triggerAtTime, long interval, int type) {
			this(id, triggerAtTime, interval, type, true);
		}

		public Builder (long id, long triggerAtTime, long interval, int type, boolean status) {
			this.id = id;
			this.triggerAtTime = triggerAtTime;
			this.interval = interval;
			this.type = type;
			this.status = status;
		}

		public Builder id(long id) {
			this.id = id;
			return this;
		}

		public Builder type(int type) {
			this.type = type;
			return this;
		}

		public Builder triggerAtTime(long triggerAtTime) {
			this.triggerAtTime = triggerAtTime;
			return this;
		}

		public Builder interval(long interval) {
			this.interval = interval;
			return this;
		}

		public Builder status(boolean status) {
			this.status = status;
			return this;
		}

		public Cron create() {
			return new Cron(this);
		}

	}

}
