package com.raxdenstudios.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.raxdenstudios.commons.util.ObjectUtils;
import com.raxdenstudios.cron.CronHandler;
import com.raxdenstudios.cron.model.Cron;

import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getInstance(this);

//        final Cron cron = new Cron.Builder(23)
//                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 20000)
//                .interval(20000)
//                .create();
//
//        final Cron cron2 = new Cron.Builder()
//                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 10000)
//                .interval(10000)
//                .create();
//
//        final Cron cron3 = new Cron.Builder()
//                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 30000)
//                .interval(30000)
//                .create();
//
        CronHandler handler = new CronHandler(this, mRealm);
//        handler.start(cron, null);
//        handler.start(cron2, null);
//        handler.start(cron3, null);

        handler.finishAll(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRealm != null) {
            mRealm.close();
        }
    }
}
