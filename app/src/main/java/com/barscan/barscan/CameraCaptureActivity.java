package com.barscan.barscan;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

public class CameraCaptureActivity extends AppCompatActivity {

    private static final String TAG = CameraCaptureActivity.class.getSimpleName();

    public static final String LICENSE_PARAM = "licenseParam";

    private CameraView cameraView;

    private ScannedLicense mockLicense = new ScannedLicense("Carl", "Burnham", 23, "04/24/1995", "male", "", "11/20/2017 08:08:08");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        cameraView = findViewById(R.id.camera);

        setupBarcodeScanner();
    }


    private void setupBarcodeScanner() {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                        .build();
    }

    private void processImage(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        parseImageResults(barcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                });
    }

    private void parseImageResults(List<FirebaseVisionBarcode> barcodes) {
        if(barcodes.isEmpty()) {
            Toast.makeText(this, "No barcodes detected", Toast.LENGTH_SHORT);
            returnDriverLicense(null);
            return;
        }
        for (FirebaseVisionBarcode barcode: barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case FirebaseVisionBarcode.TYPE_DRIVER_LICENSE:
                    FirebaseVisionBarcode.DriverLicense license = barcode.getDriverLicense();
                    //returnDriverLicense(license);
                    Log.e(TAG, license.getFirstName());
                default:
                    returnDriverLicense(null);

            }
        }
    }


    public void scanBarcodeClicked(View view) {
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                processImage(cameraKitImage.getBitmap());
            }
        });
    }

    private void showDialog(ScannedLicense scannedLicense) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        alertDialogBuilder.setTitle("User Information").setMessage("User Information");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void returnDriverLicense(FirebaseVisionBarcode.DriverLicense driverLicense) {
        Intent data = new Intent();

        data.putExtra(LICENSE_PARAM, mockLicense);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

}
