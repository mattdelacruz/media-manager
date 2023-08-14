package com.mediamanager.mediamanager;

public class Movie {
    private String name, desc, posterURL, year, motionPictureRating, duration
            , rating, IMDBRank;
    private String[] directors, writers, stars;
    public Movie(String name, String desc, String posterURL, String year,
                 String motionPictureRating, String duration, String rating,
                 String IMDBRank, String[] directors, String[] writers,
                 String[] stars) {
        this.name = name;
        this.desc = desc;
        this.posterURL = posterURL;
        this.year = year;
        this.motionPictureRating = motionPictureRating;
        this.duration = duration;
        this.rating = rating;
        this.IMDBRank = IMDBRank;
        this.directors = directors;
        this.writers = writers;
        this.stars = stars;
    }

    public String getMovieName() {
        return this.name;
    }

    public String getDescription() {
        return this.desc;
    }

    public String getPosterURL() {
        return this.posterURL;
    }
}
