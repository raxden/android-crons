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

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Cron cron = new Cron.Builder(23)
                .triggerAtTime(Calendar.getInstance().getTimeInMillis() + 20000)
                .interval(20000)
                .create();

        CronHandler handler = new CronHandler(this, mRealm);
        handler.start(cron, new CronHandler.StartCronCallbacks() {
            @Override
            public void onCronStarted(Cron cron) {
                Log.d(TAG, "[onCronStarted] "+ cron.toString());
            }

            @Override
            public void onCronError(String errorMessage) {
                Log.d(TAG, "[onCronError] "+errorMessage);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRealm != null) {
            mRealm.close();
        }
    }
}
