package com.raxdenstudios.cron.data;

import android.app.Activity;
import android.content.Context;

import com.raxdenstudios.cron.ApplicationTestCase;
import com.raxdenstudios.cron.model.Cron;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;

import io.realm.Realm;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

public class CronDAOUnitTest extends ApplicationTestCase {

    private static final String TAG = CronDAOUnitTest.class.getSimpleName();

    private Context mContext;
    private Realm mockRealm;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Before
    public void setUp() throws Exception {

        mContext = Robolectric.setupActivity(Activity.class);

        mockStatic(Realm.class);

        Realm mockRealm = PowerMockito.mock(Realm.class);
        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        this.mockRealm = mockRealm;
    }

    @Test
    public void testShouldBeAbleToCreateACronObject() {
        Cron cron = new Cron();
        when(mockRealm.createObject(Cron.class)).thenReturn(cron);

        Cron output = mockRealm.createObject(Cron.class);
        Assert.assertThat(output, is(cron));
    }

    @Test
    public void testCreateCron() {
        final Cron cronToCreate = new Cron.Builder().id(1).interval(10000).status(true).triggerAtTime(10000).create();
        final Cron cronCreated = new Cron.Builder().id(2).interval(10000).status(true).triggerAtTime(10000).create();

        doCallRealMethod().when(mockRealm).executeTransaction(Mockito.any(Realm.Transaction.class));
        when(mockRealm.copyToRealmOrUpdate(cronToCreate)).thenReturn(cronCreated);
        when(mockRealm.copyFromRealm(cronCreated)).thenReturn(cronCreated);

//        CronDAO dao = new CronDAOImpl(mContext, mockRealm);
//        dao.create(cronToCreate)
//                .subscribeOn(Schedulers.immediate())
//                .observeOn(Schedulers.immediate())
//                .subscribe(new Action1<Cron>() {
//                    @Override
//                    public void call(Cron output) {
//                        Assert.assertNotEquals(cronToCreate.getId(), output.getId());
//                        Assert.assertEquals(cronToCreate.getInterval(), output.getInterval());
//                        Assert.assertEquals(cronToCreate.getTriggerAtTime(), output.getTriggerAtTime());
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable t) {
//                        Assert.fail(t.getMessage());
//                    }
//                });
    }

}
