package io.github.andyradionov.splashgallery.ui.base;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;

import io.github.andyradionov.splashgallery.R;

public abstract class BaseActivity extends MvpAppCompatActivity {

    protected void setActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.actionbar, null);
            TextView titleView = view.findViewById(R.id.toolbar_title);
            titleView.setText(title);
            actionBar.setCustomView(view);
        }
    }

    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            TextView titleView = actionBar.getCustomView().findViewById(R.id.toolbar_title);
            titleView.setText(title);
        }
    }
}
