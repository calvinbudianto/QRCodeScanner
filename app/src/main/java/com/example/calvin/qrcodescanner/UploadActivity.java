package com.example.calvin.qrcodescanner;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class UploadActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    ImageView imgbitmap = MainActivity.getSelectedImageURI();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);  //Programmatically initialize the scanner view
        setContentView(mScannerView);                      //Set the scanner view as the content view

        ActivityCompat.requestPermissions(UploadActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }

    //convert ImageView to Bitmap
    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
    final Bitmap imgbitmap = drawable.getBitmap();


    //Scan Bitmap to integer
    public static String scanQRImage (Bitmap bitmap) {
        String contents = null;

        int[]intArray = new int [bitmap.getWidth()*bitmap.getHeight()];
        //copy pixel data from the bitmap into the "intArray" array
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try{
            Result result = reader.decode(binaryBitmap);
            contents = result.getText();
        }
        catch (Exception e){
            Log.e("QrTest", "Error decoding QR Image", e);
        }
        return contents;
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
        //Do something with the result here
        Log.v("TAG", rawResult.getText());   //Prints scan result
        Log.v("TAG", rawResult.getBarcodeFormat().toString());   //prints the scan format(qrcode, pdf, etc)
        //
        MainActivity.tvresult.setText(rawResult.getText());
        onBackPressed();

    }
}
