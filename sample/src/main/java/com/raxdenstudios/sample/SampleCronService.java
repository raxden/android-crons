package com.raxdenstudios.sample;

import android.util.Log;

import com.raxdenstudios.cron.service.CronService;
import com.raxdenstudios.cron.model.Cron;

/**
 * Created by agomez on 26/02/2016.
 */
public class SampleCronService extends CronService {

    private static final String TAG = SampleCronService.class.getSimpleName();

    @Override
    protected void onCronLaunched(Cron cron) {
        Log.d(TAG, "SUCCESS");
    }

}
