package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;

/**
 * Created by Taras on 18.04.2017.
 */

public class MovieModel extends ViewModel {

    private MutableLiveData<List<Movie>> listMovieToday = new MutableLiveData<>();
    //TODO: create MutableData for movieScheduleMap...
    private Map<Integer, List<Show>> movieScheduleMap;
    private MutableLiveData<List<MovieSchedule>> dayScheduleList = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> listMovieSoon = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> listMovieMerged = new MutableLiveData<>();

    private MutableLiveData<LinkedHashMap<Integer,LinkedHashMap<String,List<Show>>>>
            movieIdScheduleForMovieActivity = new MutableLiveData<>();

    public void setMovieTodayList(List<Movie> listMovie) {
        this.listMovieToday.setValue(listMovie);
    }

    public void setMovieSoonList(List<Movie> listMovie) {
        this.listMovieSoon.setValue(listMovie);
    }

    public LiveData<List<Movie>> getMovieTodayList() {
        return listMovieToday;
    }

    public MutableLiveData<List<Movie>> getMovieSoonList() {
        return this.listMovieSoon;
    }

    public Map<Integer, List<Show>> getMovieScheduleMap() {
        return movieScheduleMap;
    }

    public void setMovieScheduleMap(Map<Integer, List<Show>> movieShowMap) {
        this.movieScheduleMap = movieShowMap;
    }

    public void setMovieIdScheduleForMovieActivity(LinkedHashMap<Integer,LinkedHashMap<String,List<Show>>> movieIdScheduleForMovieActivity) {
        this.movieIdScheduleForMovieActivity.setValue(movieIdScheduleForMovieActivity);
    }

    public LiveData<LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>>> getMovieIdScheduleForMovieActivity() {
        return movieIdScheduleForMovieActivity;
    }

    public LinkedHashMap<String, List<Show>> getMovieActivityScheduleForMovieId(int movieId){
        return movieIdScheduleForMovieActivity.getValue().get(movieId);
    }

    public void setDayScheduleList(List<MovieSchedule> dayScheduleList) {
        this.dayScheduleList.setValue(dayScheduleList);
    }

    public LiveData<List<MovieSchedule>> getDayScheduleList() {
        return dayScheduleList;
    }

    public MutableLiveData<List<Movie>> getMovieMergedList() {
        return listMovieMerged;
    }

    public void setListMovieMerged(List<Movie> listMovieMerged) {
        this.listMovieMerged.setValue(listMovieMerged);
    }

    public void mergeMovieSoonAndTodayList() {
        //in prioritized order
        List<Movie> prioritizedList = new ArrayList<>();
        prioritizedList.addAll(getMovieTodayList().getValue());
        for (Movie m : getMovieSoonList().getValue()){
            if (m.getMovieWithScheduleId() != 0){
                prioritizedList.add(m);
            }
        }
        Collections.sort(prioritizedList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return o2.getMovieId() - o1.getMovieId();
            }
        });

        setListMovieMerged(prioritizedList);
    }
}
