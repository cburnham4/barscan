package com.barscan.barscan;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CaptureFragment extends Fragment {

    private static final String TAG = CaptureFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private Button btn_scanCode;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private  FirebaseRecyclerOptions<ScannedLicense> options;
    private FirebaseRecyclerAdapter adapter;

    public CaptureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_capture, container, false);

        mRecyclerView = view.findViewById(R.id.my_recycler_view);
        btn_scanCode = view.findViewById(R.id.btn_scanCode);
        btn_scanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarcodeClicked();
            }
        });

        requestData();
        return view;
    }


    public void scanBarcodeClicked() {
        Intent intent = new Intent(getActivity(), CameraCaptureActivity.class);
        startActivity(intent);
    }

    private void requestData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query myTopPostsQuery = databaseReference.child("customers")
                .limitToLast(50);

        options =
                new FirebaseRecyclerOptions.Builder<ScannedLicense>()
                        .setQuery(myTopPostsQuery, ScannedLicense.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ScannedLicense, MyViewHolder>(options) {
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                TextView view = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.line_text, parent, false);

                return new MyViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(MyViewHolder holder, int position, ScannedLicense model) {
                // Bind the Chat object to the ChatHolder
                holder.mTextView.setText(model.getUserInfo());
            }
        };

        setupList(adapter);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public MyViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }


    private void setupList(FirebaseRecyclerAdapter adapter) {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null){
           adapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }
}
