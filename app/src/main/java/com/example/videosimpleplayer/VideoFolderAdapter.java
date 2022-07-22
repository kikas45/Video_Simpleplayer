package com.example.videosimpleplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {
    private ArrayList<MediaFiles> mediaFiles;
    private ArrayList<String> String_folderPath;
    private Context mContext;

    public VideoFolderAdapter(ArrayList<MediaFiles> mediaFiles, ArrayList<String> String_folderPath, Context mContext) {
        this.mediaFiles = mediaFiles;
        this.String_folderPath = String_folderPath;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.folder_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // this will return the last index of Storage Path
        // e.g /storgae/folder/video .. i.e the /video is the lastIndexOf
        int indexPath = String_folderPath.get(position).lastIndexOf("/");
        String nameOfFolder = String_folderPath.get(position).substring(indexPath+1);

        holder.folder_Name.setText(nameOfFolder);
        holder.folder_Path.setText(String_folderPath.get(position));
        holder.no_files.setText(noOfFiles(String_folderPath.get(position)) + " Videos");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(mContext, VideoFilesActivity.class );
                intent.putExtra("folderName", nameOfFolder);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return String_folderPath.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView folder_Name, folder_Path, no_files;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            folder_Name = itemView.findViewById(R.id.folder_name);
            folder_Path = itemView.findViewById(R.id.folder_path);
            no_files = itemView.findViewById(R.id.noFiles);
        }
    }
    int noOfFiles( String folder_name){
        int files_no = 0;
        for (MediaFiles mediaFiles: mediaFiles){
            if (mediaFiles.getPath().substring(0, mediaFiles.getPath().lastIndexOf("/"))
                    .endsWith(folder_name)){
                files_no++;
            }

        }
        return  files_no;

    }

}
