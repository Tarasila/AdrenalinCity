package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.app.Activity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.SchedulePageMVC.SchedulePageActivity;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.MovieTodayActivity;

/**
 * Created by Taras on 19.06.2017.
 */

public class DataManager {

    private RequestManager requestManager;
    private Activity context;
    private int ACTIVITY_NAME;

    public DataManager(Activity context, RequestManager requestManager, int activityName) {
        this.context = context;
        this.requestManager = requestManager;
        this.ACTIVITY_NAME = activityName;
        makeSynchronization();
    }

    private void makeSynchronization() {
        switch (ACTIVITY_NAME){
            case Constants.MAIN_ACTIVITY:
                ((MainActivity) context).synchronizeWith(this);
                break;
            case Constants.MOVIE_TODAY_ACTIVITY:
                break;
        }
        requestManager.synchronizeWith(this);
    }

    public boolean sendingRequest = false;
    private List<Movie> movieList;
    private Map<Integer, List<Show>> movieScheduleMap = new HashMap<>();
    private LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> movieIdWithDayAndHoursMap = new LinkedHashMap<>();

    int lastMovieId;

    public void appendScheduleToMovies(List<Movie> movieList) throws InterruptedException {
        this.movieList = movieList;
        lastMovieId = movieList.get(movieList.size() - 1).getMovieId();
        synchronized (DataManager.this) {

            for (int i = 0; i < movieList.size(); i++) {
                Movie movie = movieList.get(i);

                while (sendingRequest) {
                    wait();
                }
                sendingRequest = true;

                if (i != movieList.size() - 1){
                    requestManager.sendRequest(Constants.APPEND_SHOW_SCHEDULE_TO_MOVIE_ID_ + movie.getMovieId(),
                            Constants.TYPE_APPEND_SHOW_SCHEDULE, Constants.NOT_THE_LAST_MOVIE);
                }else {
                    requestManager.sendRequest(Constants.APPEND_SHOW_SCHEDULE_TO_MOVIE_ID_ + movie.getMovieId(),
                            Constants.TYPE_APPEND_SHOW_SCHEDULE, Constants.LAST_MOVIE);
                }
            }
        }
    }

    public void addNewSchedule(int movieId, List<Show> showList, int flag) {
        if (movieId != 0){
            movieScheduleMap.put(movieId, showList);
            prepareSchedulesForMovieActivity(movieId, showList);
        }else {
            movieScheduleMap.put(lastMovieId, null);
        }
        if (flag == Constants.LAST_MOVIE) {
            switch (ACTIVITY_NAME) {
                case Constants.MAIN_ACTIVITY:
                    ((MainActivity) context).onMovieScheduleMapForFilterReady(movieScheduleMap);
                    ((MainActivity) context).onMovieScheduleMapForMovieActivityReady(movieIdWithDayAndHoursMap);
                    break;
                case Constants.MOVIE_TODAY_ACTIVITY:
                    ((MovieTodayActivity) context).onMovieScheduleMapForFilterReady(movieScheduleMap);
                    ((MovieTodayActivity) context).onMovieScheduleMapForMovieActivityReady(movieIdWithDayAndHoursMap);
                    break;
                case Constants.SCHEDULE_ACTIVITY:
                    ((SchedulePageActivity) context).onMovieScheduleMapForFilterReady(movieScheduleMap);
                    ((SchedulePageActivity) context).onMovieScheduleMapForMovieActivityReady(movieIdWithDayAndHoursMap);
                    break;
            }
        }
    }

    public void prepareSchedulesForMovieActivity(int movieId, List<Show> showList) {
        LinkedHashMap<String, List<Show>> dayWithAllHoursMap = new LinkedHashMap<>();

        for (Show show : showList) {
            String keyDay = show.getDay();
            if (dayWithAllHoursMap.get(keyDay) != null) {
                List<Show> listWithShows = dayWithAllHoursMap.get(keyDay);
                listWithShows.add(show);
            } else {
                List<Show> newShowList = new ArrayList<>();
                newShowList.add(show);
                dayWithAllHoursMap.put(keyDay, newShowList);
            }
        }
        movieIdWithDayAndHoursMap.put(movieId, dayWithAllHoursMap);
    }
}
