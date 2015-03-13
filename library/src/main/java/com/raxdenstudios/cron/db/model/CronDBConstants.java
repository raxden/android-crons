package com.raxdenstudios.cron.db.model;

public class CronDBConstants {

    public static final String CRON_TABLE_NAME = "com_raxdenstudios_cron";
    
    /* CRON_TABLE_NAME VALUES */
    public static final String CRON_ID = "cron_id";
    public static final String CRON_TRIGGER_AT_TIME = "cron_trigger_at_time";
    public static final String CRON_INTERVAL = "cron_interval";
    public static final String CRON_TYPE = "cron_type";
    public static final String CRON_STATUS = "cron_status";
    
    /* CRON TABLE -  User data */
    public static final String CRON_TABLE_CREATE = "CREATE TABLE " + CRON_TABLE_NAME + " (" +
    			CRON_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    			CRON_TRIGGER_AT_TIME + " INT, " +
    			CRON_INTERVAL + " INT, " +
    			CRON_TYPE + " INT, " +
    			CRON_STATUS + " INT " +
    			");";
    
}
