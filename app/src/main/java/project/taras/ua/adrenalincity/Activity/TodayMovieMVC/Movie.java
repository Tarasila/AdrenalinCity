package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Taras on 10.03.2017.
 */

public class Movie implements Serializable{

    private int movieId;
    //movie will have come out but already has a schedule
    private int movieWithScheduleId = 0;
    private String startDate;
    private String title;
    private String imgUrl;
    private String trailerUrl;
    private String director;
    private String production;
    private String genre;
    private String duration;
    private String storyLine;
    private String ageRestriction;
    private String purse;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStoryLine() {
        return storyLine;
    }

    public void setStoryLine(String storyLine) {
        this.storyLine = storyLine;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setMovieWithScheduleId(int id) {
        this.movieWithScheduleId = id;
    }

    public int getMovieWithScheduleId() {
        return movieWithScheduleId;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

}
