package com.example.videosimpleplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.Video_FileV_iewHolder> {

    private ArrayList<MediaFiles> video_list;
    private Context vContext;
    BottomSheetDialog bottomSheetDialog;

    // sometimes, context must be passed first to avoid error on main activity
    public VideoFilesAdapter(Context vContext, ArrayList<MediaFiles> videoArrayList) {
        this.video_list = videoArrayList;
        this.vContext = vContext;
    }


    @NonNull
    @Override
    public Video_FileV_iewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(vContext).inflate(R.layout.video_file_items, parent, false);

        return new Video_FileV_iewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Video_FileV_iewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.video_name.setText(video_list.get(position).getDisplayName());
        String size = video_list.get(position).getSize();
        holder.video_sieze.setText(android.text.format.Formatter.formatFileSize(vContext,
                Long.parseLong(size)));

        double miliSeconds = Double.parseDouble(video_list.get(position).getDuration());
        holder.videoDuration.setText(timeConversion((long) miliSeconds));

        Glide.with(vContext).load(new File(video_list.get(position).getPath()))
                .into(holder.thumbnail);

        holder.video_menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "menu more", Toast.LENGTH_SHORT).show();
                bottomSheetDialog = new BottomSheetDialog(vContext, R.style.BottomSheetTheme);
                View bsView = LayoutInflater.from(vContext).inflate(R.layout.video_bs_layout,null);
                view.findViewById(R.id.bottom_sheet);

                //now setting uo the play
                bsView.findViewById(R.id.bas_play).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.itemView.performClick();
                        bottomSheetDialog.dismiss();
                    }
                });
                //setting on click listener to rename
                bsView.findViewById(R.id.bas_rename).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(vContext);
                        alertdialog.setTitle("Rename to");

                        EditText editText = new EditText(vContext);
                        String path = video_list.get(position).getPath();
                        final  File file = new File(path);
                        String videoName = file.getName();
                        videoName = videoName.substring(0, videoName.lastIndexOf(""));
                        editText.setText(videoName);
                        alertdialog.setView(editText);
                        editText.requestFocus();

                        alertdialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (TextUtils.isEmpty(editText.getText().toString())){
                                    Toast.makeText(vContext, "cant not rename file", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String onlyPath = file.getParentFile().getAbsolutePath();
                                String ext = file.getAbsolutePath();
                                ext = ext.substring(ext.lastIndexOf(""));
                                /// for instance, let say we have Media/Video/abc.mp4,
                                // only the complete path of the video file wil be changed

                                String newPath = onlyPath+ "/" + editText.getText().toString();
                                File newFile = new File(newPath);
                                boolean rename = file.renameTo(newFile);
                                //now check if the video is renamed or not
                                if (rename){
                                    ContentResolver resolver = vContext.getApplicationContext().getContentResolver();
                                    resolver.delete(MediaStore.Files.getContentUri("external"),
                                           MediaStore.MediaColumns.DATA+"=?", new String[]{
                                                   file.getAbsolutePath()} );

                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    intent.setData(Uri.fromFile(newFile));
                                    vContext.getApplicationContext().sendBroadcast(intent);

                                    notifyDataSetChanged();

                                    Toast.makeText(vContext, "Video Re-named", Toast.LENGTH_SHORT).show();

                                    /// we need to sleep the system
                                    //so as to rename the file instantly
                                    SystemClock.sleep(200);
                                    ((Activity) vContext).recreate();
                                } else {
                                    Toast.makeText(vContext, "process Re- name Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        // below this Ok positive button
                        // we have create an negative button

                        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });

                        /// completing the re-name process
                        alertdialog.create().show();
                        bottomSheetDialog.dismiss();
                    }
                });

                bsView.findViewById(R.id.bas_Share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = Uri.parse(video_list.get(position).getPath());
                        Intent shareIntent = new Intent(Intent.ACTION_SENDTO);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        vContext.startActivity(Intent.createChooser(shareIntent, "Share video via"));
                        bottomSheetDialog.dismiss();


                    }
                });

                bsView.findViewById(R.id.bas_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(vContext);
                      alertDialog.setTitle("Delete");
                      alertDialog.setMessage("Do you want to delete this video");
                      alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {

                              Uri contentUri = ContentUris
                                      .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                              Long.parseLong(video_list.get(position).getId()));

                              File file = new File(video_list.get(position).getPath());
                              boolean delete = file.delete();
                              if (delete){

                                  vContext.getContentResolver().delete(contentUri, null, null);
                                  video_list.remove(position);
                                  notifyItemRemoved(position);
                                  notifyItemChanged(position,video_list.size());
                                  Toast.makeText(vContext, "video Deleted", Toast.LENGTH_SHORT).show();

                              } else {
                                  Toast.makeText(vContext, "Can not be  Deleted", Toast.LENGTH_SHORT).show();
                              }
                          }
                      });

                      alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int i) {
                              dialog.dismiss();
                          }
                      });
                      alertDialog.show();
                      bottomSheetDialog.dismiss();

                    }
                });

                bsView.findViewById(R.id.bas_properties).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alertdialog = new AlertDialog.Builder(vContext);
                        alertdialog.setTitle("Properties");

                        // let get all the properties for video files

                        //starting with path of video files
                        String one = "File "+video_list.get(position).getClass();
                        String path = video_list.get(position).getPath();
                        int indexOfpath = path.lastIndexOf("/");
                        String two = "Path: " + path.substring(0, indexOfpath);

                        // getting the size of video files

                        String three = "Size: " + android.text.format.Formatter
                                .formatFileSize(vContext, Long.parseLong(video_list.get(position).getSize()));

                        String four = "Length: " +timeConversion((long) miliSeconds);
                        String nameWithFormat = video_list.get(position).getDisplayName();

                        int index = nameWithFormat.lastIndexOf(".");
                        String format = nameWithFormat.substring(index + 1);

                        String five = "Format: " + format;

                        // now the 6th property of video file
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(video_list.get(position).getPath());
                        String height = mediaMetadataRetriever.extractMetadata(mediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                        String width = mediaMetadataRetriever.extractMetadata(mediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                        String six = "Resolution: " + width + "x" + height;


                        alertdialog.setMessage(one + "\n\n" + two + "\n\n" + three + "\n\n" + four + "\n\n" + five + "\n\n" + six + "\n\n");
                        alertdialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });

                        alertdialog.show();
                        bottomSheetDialog.dismiss();
                    }
                });





                bottomSheetDialog.setContentView(bsView);
                bottomSheetDialog.show();
                //

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(vContext, VideoPlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("video_title", video_list.get(position).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("video_Arraylist", video_list);
                intent.putExtras(bundle);
                vContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return video_list.size();
    }

    public class Video_FileV_iewHolder extends RecyclerView.ViewHolder {
        TextView video_name, video_sieze, videoDuration;
        ImageView thumbnail, video_menu_more;

        public Video_FileV_iewHolder(@NonNull View itemView) {
            super(itemView);
            video_name = itemView.findViewById(R.id.video_name);
            videoDuration = itemView.findViewById(R.id.video_duration);
            video_sieze = itemView.findViewById(R.id.video_sieze);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            video_menu_more = itemView.findViewById(R.id.video_menu_more);

        }
    }

    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int scs = duration % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }

    /// implementing the search method
    void updatevideoFiles(ArrayList<MediaFiles> files ){

        video_list = new ArrayList<>();
        video_list.addAll(files);
        notifyDataSetChanged();

    }
}
