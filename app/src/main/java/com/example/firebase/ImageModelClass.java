package com.example.firebase;

public class ImageModelClass {
    private String imageUrl;
    private  String userid;


    public ImageModelClass(String imageUrl, String userid) {
        this.imageUrl = imageUrl;
        this.userid = userid;
    }

    public ImageModelClass() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
