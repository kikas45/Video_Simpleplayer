package com.example.videosimpleplayer;

import static com.example.videosimpleplayer.AllowAcessActivity.REQUEST_PERSIMISION_SETTING;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
    private ArrayList<String> allowFolderList = new ArrayList<>();
    RecyclerView recyclerView;
    VideoFolderAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED){
            Toast.makeText(getApplicationContext(), "Click on Permissions and allow storage ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("Package", getPackageName(), null);
            intent.setData(uri);

            startActivityForResult(intent, REQUEST_PERSIMISION_SETTING);

            showFoldeer();

       /*     try {
                startActivityForResult(intent, REQUEST_PERSIMISION_SETTING);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Unable to Access settings", Toast.LENGTH_SHORT).show();
            }*/


        }

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_folder);

        recyclerView = findViewById(R.id.folder_rv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                showFoldeer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showFoldeer() {

        mediaFiles = fetchMedia();
        adapter = new VideoFolderAdapter(mediaFiles, allowFolderList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<MediaFiles> fetchMedia() {
        ArrayList<MediaFiles> mediaFiles_arrays = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, null, null, null,null);

        if (cursor != null && cursor.moveToNext()) {

            do{
                /// note this arrangement must ber in same arrangement with the model class
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));

                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));

                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));

                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));

                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                @SuppressLint("Range") String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                MediaFiles mediaFiles_Stream = new MediaFiles(id, title, displayName,size, duration,path, dateAdded);

                int index = path.lastIndexOf("/");
                String substring = path.substring(0,index);
                if (!allowFolderList.contains(substring)){
                    allowFolderList.add(substring);
                }
                mediaFiles_arrays.add(mediaFiles_Stream);

            } while (cursor.moveToNext());
        }
        return mediaFiles_arrays;

        ///
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.rateUs:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="
                +getApplicationContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.refresh_folder:
                finish();
                startActivity(getIntent());
                break;
            case R.id.share_app:
                Intent  share_intent = new Intent();
                share_intent.putExtra(Intent.EXTRA_TEXT, "Check this app \n"+
                        "https://play.google.com/store/apps/details?id="
                +getApplicationContext().getPackageName());
                share_intent.setType("text/plain");
                startActivity(Intent.createChooser(share_intent, "share app via"));
                break;

        }
        return true;
    }
}