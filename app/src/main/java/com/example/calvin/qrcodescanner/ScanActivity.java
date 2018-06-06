package com.example.calvin.qrcodescanner;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);  //Programmatically initialize the scanner view
        setContentView(mScannerView);                      //Set the scanner view as the content view

        ActivityCompat.requestPermissions(ScanActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);    //Register ourselves a handle for scan result
        mScannerView.startCamera();             //Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();              //Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Toast toast = Toast.makeText(this, R.string.toast_message2, Toast.LENGTH_LONG);
        toast.show();
        //Do something with the result here
        Log.v("TAG", rawResult.getText());   //Prints scan result
        Log.v("TAG", rawResult.getBarcodeFormat().toString());   //prints the scan format(qrcode, pdf, etc)
        //
        MainActivity.tvresult.setText(rawResult.getText());
        onBackPressed();
    }
}
