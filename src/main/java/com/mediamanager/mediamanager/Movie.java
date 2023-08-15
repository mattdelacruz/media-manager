package com.mediamanager.mediamanager;

import java.util.ArrayList;

public class Movie {
    private final String name, desc, posterURL, year, motionPictureRating,
            duration
            , rating, IMDBRank;
    private final ArrayList<String> directors, writers, stars;
    public Movie(String name, String desc, String posterURL, String year,
                 String motionPictureRating, String duration, String rating,
                 String IMDBRank, ArrayList<String> directors,
                 ArrayList<String> writers,
                 ArrayList<String> stars) {
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

    public String getDescription() { return this.desc; }
    public String getPosterURL() {
        return this.posterURL;
    }

    public String getYear() { return this.year; }
    public String getMotionPictureRating() { return this.motionPictureRating; }
    public String getDuration() { return this.duration; }
    public String getRating() { return this.rating; }
    public String getIMDBRank() { return this.IMDBRank; }
    public ArrayList<String> getDirectors() { return this.directors; }
    public ArrayList<String> getStars() { return stars; }
    public ArrayList<String> getWriters() { return writers; }
}
