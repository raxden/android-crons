package com.raxdenstudios.sample

import com.raxdenstudios.cron.model.Cron
import com.raxdenstudios.cron.service.CronProcedureService

/**
 * Created by agomez on 26/02/2016.
 */
class SampleCronProcedureService : CronProcedureService() {

    companion object {
        private val TAG = SampleCronProcedureService::class.java.simpleName
    }

    override fun onCronLaunched(cron: Cron) {

    }

}
