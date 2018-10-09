package com.barscan.firebaseidscanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

    private static final String SHOW_DIALOG_EXTRA = "ShowDialog";

    private CameraView cameraView;

    private ProgressBar progress_spinner;

    private boolean showDialog;

    public static Intent getLaunchIntent(Context context) {
        return getLaunchIntent(context, false);
    }

    /**
     * @param context    context from which the intent is getting called
     * @param showDialog whether or not the user information should appear on a dialog after the scan
     * @return return launch intent for activity
     */
    public static Intent getLaunchIntent(Context context, boolean showDialog) {
        Intent intent = new Intent(context, CameraCaptureActivity.class);
        intent.putExtra(SHOW_DIALOG_EXTRA, showDialog);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        cameraView = findViewById(R.id.camera);
        progress_spinner = findViewById(R.id.progress_spinner);

        getExtras();
        setupBarcodeScanner();
    }

    private void getExtras() {
        if (getIntent().getExtras() != null) {
            showDialog = getIntent().getBooleanExtra(SHOW_DIALOG_EXTRA, false);
        }
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

        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        parseImageResults(barcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraCaptureActivity.this, "Image could not be parsed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                });
    }

    private void parseImageResults(List<FirebaseVisionBarcode> barcodes) {
        if (barcodes.isEmpty()) {
            Toast.makeText(this, "No barcodes detected", Toast.LENGTH_SHORT).show();
            progress_spinner.setVisibility(View.GONE);
            return;
        }
        boolean foundLicense = false;
        for (FirebaseVisionBarcode barcode : barcodes) {
            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            if (valueType == FirebaseVisionBarcode.TYPE_DRIVER_LICENSE) {
                FirebaseVisionBarcode.DriverLicense license = barcode.getDriverLicense();
                returnDriverLicense(license);
                foundLicense = true;
                break;
            }
        }

        if (!foundLicense) {
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

    private void returnDriverLicense(FirebaseVisionBarcode.DriverLicense driverLicense) {
        ScannedLicense scannedLicense = new ScannedLicense(driverLicense);

        progress_spinner.setVisibility(View.GONE);

        if (showDialog) {
            showDialog(scannedLicense);
        } else {
            killActivity(scannedLicense);
        }
    }

    private void showDialog(final ScannedLicense scannedLicense) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        String userInfo = "";
        if (scannedLicense.getAge() < 21) {
            userInfo = "Person Is not 21 \n";
        }
        userInfo += scannedLicense.getUserInfo();
        alertDialogBuilder.setTitle("User Information").setMessage(userInfo);
        alertDialogBuilder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        killActivity(scannedLicense);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        killActivity(scannedLicense);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void killActivity(ScannedLicense scannedLicense) {
        Intent data = new Intent();
        data.putExtra(LICENSE_PARAM, scannedLicense);
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
