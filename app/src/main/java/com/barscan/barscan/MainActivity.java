package com.barscan.barscan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

import static com.barscan.barscan.CameraCaptureActivity.LICENSE_PARAM;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int TAKE_PHOTO_ACTION = 1;

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        setupTabs();
    }


    private void setupTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new CaptureFragment(), "Tab 1");
        adapter.addFragment(new CaptureFragment(), "Tab 2");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void processBarcode(ScannedLicense name) {

    }
}
