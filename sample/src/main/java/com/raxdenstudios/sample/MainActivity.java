package com.raxdenstudios.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Cron cron = new Cron.Builder(1)
                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 10000)
                .interval(10000)
                .create();

        final Cron cron2 = new Cron.Builder(2)
                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 30000)
                .interval(30000)
                .create();

        final Cron cron3 = new Cron.Builder(3)
                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 60000)
                .interval(60000)
                .create();

        CronHandler handler = new CronHandler(this);
        handler.start(cron, null);
//        handler.start(cron2, null);
//        handler.start(cron3, null);

//        handler.finishAll(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
