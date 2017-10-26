package project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taras on 21.03.2017.
 */

public class Show implements Parcelable{

    private int databaseId;
    private int showId;
    private int movieId;
    private int hallId;
    private String showDate;
    private String month;
    private String day;
    private String dayOfWeek;
    private String time;
    private String calendarDate;
    private List<Show> arrayShowWithTheSameDay = new ArrayList<>();

    public Show(){

    }

    public Show(Parcel in){
        super();
        readFromParcel(in);
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Show> getArrayShowWithTheSameDay() {
        return arrayShowWithTheSameDay;
    }

    public void setArrayShowWithTheSameDay(List<Show> arrayShowWithTheSameDay) {
        this.arrayShowWithTheSameDay = arrayShowWithTheSameDay;
    }

    public void addShowWithTheSameDay(Show show) {
        arrayShowWithTheSameDay.add(show);
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public void setCalendarDate(String calendarDate) {
        this.calendarDate = calendarDate;
    }

    public String getCalendarDate() {
        return calendarDate;
    }

    public static final Parcelable.Creator<Show> CREATOR = new Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel source) {
            return new Show(source);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(databaseId);
        dest.writeInt(showId);
        dest.writeInt(movieId);
        dest.writeInt(hallId);
        dest.writeString(showDate);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeString(dayOfWeek);
        dest.writeString(time);
        dest.writeString(calendarDate);
        dest.writeTypedList(arrayShowWithTheSameDay);
    }

    public void readFromParcel(Parcel in){
        databaseId = in.readInt();
        showId = in.readInt();
        movieId = in.readInt();
        hallId = in.readInt();
        showDate = in.readString();
        month = in.readString();
        day = in.readString();
        dayOfWeek = in.readString();
        time = in.readString();
        calendarDate = in.readString();
        in.readTypedList(arrayShowWithTheSameDay, Show.CREATOR);

    }


}
