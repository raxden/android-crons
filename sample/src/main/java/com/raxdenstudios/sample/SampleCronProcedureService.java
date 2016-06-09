package com.raxdenstudios.sample;

import android.util.Log;

import com.raxdenstudios.cron.service.CronProcedureService;
import com.raxdenstudios.cron.model.Cron;

/**
 * Created by agomez on 26/02/2016.
 */
public class SampleCronProcedureService extends CronProcedureService {

    private static final String TAG = SampleCronProcedureService.class.getSimpleName();

    @Override
    protected void onCronLaunched(Cron cron) {
        Log.d(TAG, "SUCCESS");
    }

}
