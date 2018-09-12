package com.asun.trendingtv;


public class Tvshow {

    private String Description;
    private String Image;
    private String Title;

    public Tvshow(){

    }

    public Tvshow(String title, String description, String image) {
        this.Title = title;
        this.Description = description;
        this.Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
