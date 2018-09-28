package com.barscan.barscan;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaptureFragment extends Fragment {


    public CaptureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture, container, false);
    }


    public void scanBarcodeClicked(View view) {
        Intent intent = new Intent(getActivity(), CameraCaptureActivity.class);
        startActivity(intent);
    }

}
