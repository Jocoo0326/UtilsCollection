package com.jocoo.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by Jocoo on 2016/10/20.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagement.getInstance().addActivity(this);
        preSetContentView();
        setContentView(getLayoutId());
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initData();
    }

    protected void preSetContentView() {
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void finish() {
        super.finish();

    }

    protected void startActivity(Class<?> activity) {
        startActivity(activity, null);
    }

    protected void startActivity(Class<?> activity, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> activity, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
}
