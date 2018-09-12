package com.asun.trendingtv;


public class Seasongettesette {
    private String season_title;
    private String no_of_episods;
    private String season_image;
    private String season_desc;

    public Seasongettesette(){

    }

    public Seasongettesette(String season_title, String no_of_episods, String season_image, String season_desc) {
        this.season_title = season_title;
        this.no_of_episods = no_of_episods;
        this.season_image = season_image;
        this.season_desc = season_desc;
    }

    public String getSeason_title() {
        return season_title;
    }

    public void setSeason_title(String season_title) {
        this.season_title = season_title;
    }

    public String getNo_of_episods() {
        return no_of_episods;
    }

    public void setNo_of_episods(String no_of_episods) {
        this.no_of_episods = no_of_episods;
    }

    public String getSeason_image() {
        return season_image;
    }

    public void setSeason_image(String season_image) {
        this.season_image = season_image;
    }

    public String getSeason_desc() {
        return season_desc;
    }

    public void setSeason_desc(String season_desc) {
        this.season_desc = season_desc;
    }

  }
