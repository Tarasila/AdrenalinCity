package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Taras on 31.05.2017.
 */
@Entity
public class OrderModel {

    @PrimaryKey
    private int id;
    private String userName;
    private String userDbId;
    private String seatId;
    private String seatRaw;
    private String seatPlace;
    private String seatPrice;
    private String ctShowId;
    private String dbShowId;
    private String showId;
    private String showTime;
    private String movieId;
    private String date;
    private String calendarDay;
    private String databaseId;
    String hall;
    String moviePoster;
    String movieTitle;
    String totalCost;
    private String status;
    private int isConfirmed;
    private String purse;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDbId() {
        return userDbId;
    }

    public void setUserDbId(String userDbId) {
        this.userDbId = userDbId;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getSeatRaw() {
        return seatRaw;
    }

    public void setSeatRaw(String seatRaw) {
        this.seatRaw = seatRaw;
    }

    public String getSeatPlace() {
        return seatPlace;
    }

    public void setSeatPlace(String seatPlace) {
        this.seatPlace = seatPlace;
    }

    public String getSeatPrice() {
        return seatPrice;
    }

    public void setSeatPrice(String seatPrice) {
        this.seatPrice = seatPrice;
    }

    public String getCtShowId() {
        return ctShowId;
    }

    public void setCtShowId(String ctShowId) {
        this.ctShowId = ctShowId;
    }

    public String getDbShowId() {
        return dbShowId;
    }

    public void setDbShowId(String dbShowId) {
        this.dbShowId = dbShowId;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(String calendarDay) {
        this.calendarDay = calendarDay;
    }

    public String getPurse() {
        return purse;
    }

    public void setPurse(String purse) {
        this.purse = purse;
    }

}
