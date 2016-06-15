package com.aswifter.material;

import android.app.Application;

import com.aswifter.material.common.AppClient;

/**
 * Created by erfli on 6/14/16.
 */
public class BookApplication extends Application{
    static {
        AppClient.initAppClient();
    }
}
