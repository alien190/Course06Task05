package com.example.alien.course06task05;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}
