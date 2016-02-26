package com.raxdenstudios.cron.util;

import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by agomez on 26/02/2016.
 */
public class CronUtils {

    public static String dump(Cron cron) {
        StringBuffer dump = new StringBuffer();
        dump.append("\nCron		  	    : " + cron.toString());
        dump.append("\nId 		  	    : " + cron.getId());
        dump.append("\nTriggerAtTime 	: " + new Date(cron.getTriggerAtTime()).toString());
        dump.append("\nInterval 	  	: " + ((float)(cron.getInterval() / (1000))) + " seconds");
        dump.append("\nNext launch      : " + ((float)((cron.getTriggerAtTime() - Calendar.getInstance().getTimeInMillis()) / (1000))) + " seconds");
        dump.append("\nStatus           : " + cron.isStatus());
        return dump.toString();
    }

}
