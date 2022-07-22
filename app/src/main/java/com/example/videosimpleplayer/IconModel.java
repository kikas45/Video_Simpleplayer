package com.example.videosimpleplayer;

public class IconModel {
    private int ImageView;
    private String IconTitle;

    public IconModel(int imageView, String iconTitle) {
        ImageView = imageView;
        IconTitle = iconTitle;
    }

    public int getImageView() {
        return ImageView;
    }

    public void setImageView(int imageView) {
        ImageView = imageView;
    }

    public String getIconTitle() {
        return IconTitle;
    }

    public void setIconTitle(String iconTitle) {
        IconTitle = iconTitle;
    }
}
