package com.application.chetna_priya.exo_audio.entity;


import java.io.Serializable;

public class MetadataEntity implements Serializable {

    private String title;
    private String link;
    private String summary;
    private long duration;
    private String releaseDate;
    private String mediaId;

    public MetadataEntity(String title, String link, String summary, long duration, String releaseDate,
                          String media_id) {
        this.title = title;
        this.link = link;
        this.summary = summary;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.mediaId = media_id;
    }


    public String getMetadataTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getMetadataSummary() {
        return summary;
    }

    public long getMetadataDuration() {
        return duration;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMetadataMediaId() {
        return mediaId;
    }

}
