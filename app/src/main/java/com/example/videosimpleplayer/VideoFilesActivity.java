package com.example.videosimpleplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;

public class VideoFilesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String MY_PREF = "my_pref";
    RecyclerView recyclerView;
    private ArrayList<MediaFiles> video_Array_List = new ArrayList<>();
    static  VideoFilesAdapter video_Files_Adapter;
    String folder_Name;
    String sortOrder;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);
        folder_Name = getIntent().getStringExtra("folderName"); // note folder name needs to be gotten before setting on tool bar
        getSupportActionBar().setTitle(folder_Name); // sets the name on toolbar
        recyclerView = findViewById(R.id.videos_recycler);
        swipeRefreshLayout = findViewById(R.id.swipe_file_videos);
        show_Video_Files();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                show_Video_Files();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void show_Video_Files() {
        video_Array_List = fecth_Media(folder_Name);
        video_Files_Adapter = new VideoFilesAdapter(this, video_Array_List);
        recyclerView.setAdapter(video_Files_Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false ));
        video_Files_Adapter.notifyDataSetChanged();


    }

    private ArrayList<MediaFiles> fecth_Media(String folder_Name) {

        SharedPreferences preferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "abcd");

        /// sortimg the file
        if (sort_value.equals("sortName")){
            sortOrder = MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
        }else  if (sort_value.equals("sortSize")){
            sortOrder = MediaStore.MediaColumns.SIZE+ " DESC";
        }else if (sort_value.equals("sortDate")){
            sortOrder = MediaStore.MediaColumns.DATE_ADDED+ " DESC";
        }else{
            sortOrder = MediaStore.Video.Media.DURATION+ " DESC";
        }

        ArrayList<MediaFiles> vido_files = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selection_Arg= new String[]{"%"+folder_Name+"%"};
        Cursor cursor = getContentResolver().query(uri, null,
                selection, selection_Arg,sortOrder);

        if (cursor!=null && cursor.moveToNext()){

            do{
                /// note this arrangement must ber in same arrangement with the model class
                @SuppressLint("Range")
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range")
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range")
                String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range")
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range")
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range")
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range")
                String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                MediaFiles mediaFiles_Stream = new MediaFiles(id, title, displayName,size, duration,path, dateAdded);

                vido_files.add(mediaFiles_Stream);


            } while(cursor.moveToNext());

        }

        return vido_files;
    }

    //// implement the search method
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
        for (MediaFiles media : video_Array_List){
            if (media.getTitle().toLowerCase().contains(inputs)){
            mediaFiles.add(media);
         }
        }
        // note we make the video file adapter static
        VideoFilesActivity.video_Files_Adapter.updatevideoFiles(mediaFiles);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_video);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences dPreferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = dPreferences.edit();

        int id = item.getItemId();
        switch (id){
            case R.id.refresh_files:
                finish();
                startActivity(getIntent());
                break;

            case R.id.sort_by:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Sort by");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        editor.apply();
                        finish();
                        startActivity(getIntent());
                        dialog.dismiss();



                    }
                });
                String[] items = {"Name (A to Z)", "Sized ( Big to Small ", "Date (New to Old", "Length (Long to Short", "NEW"};

                alertDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        switch (which){
                            case 0:

                                editor.putString("sort", "sortName");
                                break;

                            case 1:
                                editor.putString("sort", "sortSize");
                                break;

                            case 2:
                                editor.putString("sort", "sortDate");
                                break;

                            case 3:
                                editor.putString("sort", "sortLength");
                                break;
                        }
                    }
                });
                alertDialog.create().show();
               break;

        }
        return super.onOptionsItemSelected(item);
    }
}