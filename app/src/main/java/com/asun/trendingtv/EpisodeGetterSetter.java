package com.asun.trendingtv;


public class EpisodeGetterSetter {
    private String episode_length;
    private String episode_name;
    private String episode_title;
    private String episode_url;


    private String episode_image;

    public EpisodeGetterSetter(){

    }

    public EpisodeGetterSetter(String episode_length, String episode_name, String episode_title, String episode_url,String episode_image) {
        this.episode_length = episode_length;
        this.episode_name = episode_name;
        this.episode_title = episode_title;
        this.episode_url = episode_url;
        this.episode_image=episode_image;
    }


    public String getEpisode_length() {
        return episode_length;
    }

    public void setEpisode_length(String episode_length) {
        this.episode_length = episode_length;
    }

    public String getEpisode_name() {
        return episodename;
    }

    public void setEpisode_name(String episode_name) {
        this.episode_name = episode_name;
    }

    public String getEpisode_title() {
        return episode_title;
    }

    public void setEpisode_title(String episode_title) {
        this.episode_title = episode_title;
    }

    public String getEpisode_url() {
        return episode_url;
    }

    public void setEpisode_url(String episode_url) {
        this.episode_url = episode_url;
    }
    public String getEpisode_image() {
        return episode_image;
    }

    public void setEpisode_image(String episode_image) {
        this.episode_image = episode_image;
    }


}
