package com.digitalcitadel.ryan.geostash;

/**
 * Created by ryan on 4/23/16.
 */
public class Stash {
    private String description;
    private String latitude;
    private String longitude;
    private long id;


    public Stash()
    {
        setId(0);
        setLatitude("0");
        setLongitude("0");
        setDescription("");

    }
    public Stash(long id, String latitude, String longitude, String description)
    {
        this.id = id;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
