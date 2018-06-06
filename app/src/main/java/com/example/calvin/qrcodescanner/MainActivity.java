package com.example.calvin.qrcodescanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class MainActivity extends AppCompatActivity {
    public ImageView mUploadImage;
    Integer SELECT_FILE = 0;
    public static TextView tvresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvresult = findViewById(R.id.tvresult);
        Button btn = findViewById(R.id.btn);
        mUploadImage = findViewById(R.id.imageView);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }


    public void openWebsite(View view) {
        String url = tvresult.getText().toString();

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntent", "Cant handle this intent");
        }
    }

    public void uploadImage(View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        SelectImage();
    }


    public void SelectImage() {
        final CharSequence[] items = {"Gallery", "Cancel"};

        //show the alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);

                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null) {
            //read picked image data - its URI
            Uri pickedImage = data.getData();
            mUploadImage.setImageURI(pickedImage);
            Toast toast = Toast.makeText(this, R.string.toast_message, Toast.LENGTH_LONG);
            toast.show();

            //read picked image path using content resolver
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            //convert the imageview into bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            cursor.close();

            //read the bitmap
            BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

            Frame frame = new Frame.Builder()
                    .setBitmap(bitmap).build();
            SparseArray<Barcode> barsCode = detector.detect(frame);

            Barcode result = barsCode.valueAt(0);
            tvresult.setText(result.rawValue);
        } else if (requestCode == SELECT_FILE && resultCode != RESULT_OK) {
            android.support.v7.app.AlertDialog.Builder myAlertBuilder = new
                    android.support.v7.app.AlertDialog.Builder(MainActivity.this);

            myAlertBuilder.setTitle(R.string.alert_title);
            myAlertBuilder.setMessage(R.string.alert_message);

//            myAlertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    //
//                    Toast.makeText(getApplicationContext(), R.string.pressed_ok,
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            myAlertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Toast.makeText(getApplicationContext(),
//                            R.string.pressed_cancel, Toast.LENGTH_SHORT).show();
//                }
//            });

            myAlertBuilder.show();

        }
    }
}


