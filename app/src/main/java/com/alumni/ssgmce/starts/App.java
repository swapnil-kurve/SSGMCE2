package com.alumni.ssgmce.starts;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by USer on 04-11-2016.
 */

public class App extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}