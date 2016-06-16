package com.aswifter.material.common;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by erfli on 6/16/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected abstract void initView();
    protected abstract void initRepository();
}
