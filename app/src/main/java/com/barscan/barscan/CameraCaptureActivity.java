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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class CameraCaptureActivity extends AppCompatActivity {

    private static final String TAG = CameraCaptureActivity.class.getSimpleName();

    public static final String LICENSE_PARAM = "licenseParam";

    private CameraView cameraView;

    private ProgressBar progress_spinner;

    private DatabaseReference mDatabase;

    private ScannedLicense mockLicense = new ScannedLicense("Carl", "Burnham", 23, "04/24/1995", "male", "", "11/20/2017 08:08:08");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        cameraView = findViewById(R.id.camera);
        progress_spinner = findViewById(R.id.progress_spinner);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        pushMockData();
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
        Toast.makeText(this, "Processing Image", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "No barcodes detected", Toast.LENGTH_SHORT).show();
            progress_spinner.setVisibility(View.GONE);
            return;
        }
        boolean foundLicense = false;
        for (FirebaseVisionBarcode barcode: barcodes) {
            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case FirebaseVisionBarcode.TYPE_DRIVER_LICENSE:
                    FirebaseVisionBarcode.DriverLicense license = barcode.getDriverLicense();
                    returnDriverLicense(license);
                    Log.e(TAG, license.getFirstName());
                    foundLicense = true;
                    break;
                default:
                    //Toast.makeText(this, "No barcodes detected", Toast.LENGTH_SHORT);
                   // returnDriverLicense(null);

            }
        }
        if(!foundLicense) {
            progress_spinner.setVisibility(View.GONE);
            Toast.makeText(this, "No license detected", Toast.LENGTH_SHORT).show();
        }
    }


    public void scanBarcodeClicked(View view) {
        progress_spinner.setVisibility(View.VISIBLE);
        cameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage cameraKitImage) {
                processImage(cameraKitImage.getBitmap());
            }
        });
    }

    private void showDialog(ScannedLicense scannedLicense) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        String userInfo = "";
        if(scannedLicense.getAge() < 21) {
            userInfo = "Person Is not 21 \n";
        }
        userInfo += scannedLicense.getUserInfo();
        alertDialogBuilder.setTitle("User Information").setMessage(userInfo);
                alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                finish();
                            }
                        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void pushMockData() {
        for (int i = 0; i < 200; i++) {
            ScannedLicense scannedId = getMockLicense();
            pushData(scannedId);
        }
    }

    private void pushData(ScannedLicense scannedId) {
        String uid = mDatabase.child("customers").push().getKey();
        scannedId.setUuid(uid);
        mDatabase.child("customers").child(scannedId.getId()).setValue(scannedId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, task.toString());
            }
        });
    }

    private void returnDriverLicense(FirebaseVisionBarcode.DriverLicense driverLicense) {
        Intent data = new Intent();

        //ScannedLicense scannedId = mockLicense;
        ScannedLicense scannedLicense = new ScannedLicense(driverLicense);

       // data.putExtra(LICENSE_PARAM, scannedId);
        pushData(scannedLicense);

        progress_spinner.setVisibility(View.GONE);
        showDialog(scannedLicense);
    }

    private ScannedLicense getMockLicense() {
        Random random = new Random();
        String gender = random.nextBoolean() ? "male" : "female";
        return new ScannedLicense(RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(8), random.nextInt(40) + 18, "04/24/1995",
                gender, "", DateHelper.getRandomTime());
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
