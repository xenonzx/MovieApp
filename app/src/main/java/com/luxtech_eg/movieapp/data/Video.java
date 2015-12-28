package com.luxtech_eg.movieapp.data;

/**
 * Created by ahmed on 26/12/15.
 */
public class Video {
    String site;
    String id;
    String type;
    String name;
    String key;

    public Video(String id,String site, String type, String name, String key) {
        this.site = site;
        this.id = id;
        this.type = type;
        this.name = name;
        this.key = key;
    }

    public String getSite() {

        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
