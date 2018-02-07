package com.raxdenstudios.sample;

import com.raxdenstudios.cron.model.Cron;
import com.raxdenstudios.cron.service.CronProcedureService;

/**
 * Created by agomez on 26/02/2016.
 */
public class SampleCronProcedureService extends CronProcedureService {

    private static final String TAG = SampleCronProcedureService.class.getSimpleName();

    @Override
    protected void onCronLaunched(Cron cron) {

    }

}
