package com.barscan.barscan.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.barscan.firebaseidscanner.ScannedLicense;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StoreUser {

    private static final String TAG = StoreUser.class.getSimpleName();


    private static void pushData(ScannedLicense scannedId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String uid = mDatabase.child("customers").push().getKey();
        scannedId.setUuid(uid);
        mDatabase.child("customers").child(scannedId.getUuid()).setValue(scannedId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e(TAG, task.toString());
            }
        });
    }
}
