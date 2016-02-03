package edu.bc.luntc.phonehome;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by cameronlunt on 12/8/15.
 */
public class NotifierService extends IntentService{

    public NotifierService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
