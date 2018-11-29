package com.example.alien.course06task05;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 11;
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath;
    private Button mTakePhotoButton;
    private ImageView mPreviewImageView;
    private Button mRecognizeButton;
    private TextView mResultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTakePhotoButton = findViewById(R.id.btn_take_photo);
        mPreviewImageView = findViewById(R.id.iv_preview);
        mRecognizeButton = findViewById(R.id.btn_recognize);
        mResultTextView = findViewById(R.id.tv_result);
        requestPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mTakePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        mRecognizeButton.setOnClickListener(v -> recognize());
    }

    private void recognize() {
        try {
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(((BitmapDrawable) mPreviewImageView.getDrawable()).getBitmap());
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> result =
                    detector.processImage(image)
                            .addOnSuccessListener(firebaseVisionText ->
                                    mResultTextView.setText(firebaseVisionText.getText())
                            )
                            .addOnFailureListener(
                                    e -> {
                                        e.printStackTrace();
                                        Toast.makeText(this, R.string.recognition_error, Toast.LENGTH_SHORT).show();
                                    });

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTakePhotoButton.setOnClickListener(null);
        mRecognizeButton.setOnClickListener(null);
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            init();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permDialogTitle)
                    .setMessage(R.string.requestPermissionMessage)
                    .setPositiveButton(R.string.OkLabel, (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE))
                    .create()
                    .show();
        }
    }

    private void init() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permDialogTitle)
                        .setMessage(R.string.notGrantedPermMessage)
                        .setPositiveButton(R.string.OkLabel, (dialogInterface, i) -> finish())
                        .create()
                        .show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.alien.course06task05.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Picasso.get().load("file://" + mCurrentPhotoPath).into(mPreviewImageView);
        }
    }
}
