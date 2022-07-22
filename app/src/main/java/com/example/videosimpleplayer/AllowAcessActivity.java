package com.example.videosimpleplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.Permission;

public class AllowAcessActivity extends AppCompatActivity {

    Button allow_btn;
    public static final int STORAGE_PERMISSION = 1;
    public static final int REQUEST_PERSIMISION_SETTING = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_acess);

        allow_btn = findViewById(R.id.allow_acess);


        SharedPreferences preferences = getSharedPreferences("AllowAcess", MODE_PRIVATE);

        String value = preferences.getString("Allow", "");

        if (value.equals("Ok")){

            startActivity(new Intent(AllowAcessActivity.this, MainActivity.class));
            finish();

        }else{
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Allow", "Ok");
            editor.apply();

        }


        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                        .PERMISSION_GRANTED) {
                    startActivity(new Intent(AllowAcessActivity.this, MainActivity.class));
                    finish();
                } else {
                    ActivityCompat.requestPermissions(AllowAcessActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                }


            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String per = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRational = shouldShowRequestPermissionRationale(per);
                    if (!showRational) {
                        ///user clicked on never ask again
                        // now create alert dialog why he has to

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("App Permission")
                                .setMessage("To Access files, you need this permission to access storage"
                                        + "\n\n"
                                        + "Now following the step below"
                                        + "\n\n"
                                        + "Open settings from below button"
                                        + "\n"
                                        + "Click on Permission"
                                        + "\n"
                                        + "Allow access storage")
                                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("Package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, REQUEST_PERSIMISION_SETTING);

                                  /*    try {
                                            startActivityForResult(intent, REQUEST_PERSIMISION_SETTING);
                                        }catch (Exception e){
                                            Toast.makeText(getApplicationContext(), "Unable to Access settings", Toast.LENGTH_SHORT).show();
                                        }*/

                                    }
                                }).create().show();


                    }else {
                        ActivityCompat.requestPermissions(AllowAcessActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
                    }

                } else {
                    startActivity(new Intent(AllowAcessActivity.this, MainActivity.class));
                    finish();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            startActivity(new Intent(AllowAcessActivity.this, MainActivity.class));
            finish();
        }
    }
}