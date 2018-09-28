package com.barscan.barscan;

import android.content.Intent;
import android.graphics.Bitmap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
    }


    public void scanBarcodeClicked(View view) {
        Intent intent = new Intent(this, CameraCaptureActivity.class);
        startActivityForResult(intent, TAKE_PHOTO_ACTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_ACTION) {
            if (resultCode == RESULT_OK) {
                ScannedLicense scannedLicense = data.getParcelableExtra(LICENSE_PARAM);
                processBarcode(scannedLicense);
            }
        }
    }

    private void processBarcode(ScannedLicense name) {

    }
}
