package com.example.videosimpleplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlayBackIconAdapter  extends RecyclerView.Adapter<PlayBackIconAdapter.ViewIconHolder> {

    private ArrayList<IconModel> iconModels;
    private Context context;

    public PlayBackIconAdapter(ArrayList<IconModel> iconModels, Context context) {
        this.iconModels = iconModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewIconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.icons_layout, parent, false);

        return new ViewIconHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewIconHolder holder, int position) {

        holder.icon.setImageResource(iconModels.get(position).getImageView());
        holder.iconName.setText(iconModels.get(position).getIconTitle());

    }

    @Override
    public int getItemCount() {
        return iconModels.size();
    }

    public class ViewIconHolder extends RecyclerView.ViewHolder {
        TextView iconName;
        ImageView icon;

        public ViewIconHolder(@NonNull View itemView) {
            super(itemView);
            iconName = itemView.findViewById(R.id.icon_title);
            icon = itemView.findViewById(R.id.player_icon);
        }
    }
}
