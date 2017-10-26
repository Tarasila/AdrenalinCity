package project.taras.ua.adrenalincity.Activity.SchedulePageMVC;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.DataManager;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.IAdapterListener;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieModel;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieSchedule;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.AdapterTodayAndSoon;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.ViewMVC;
import project.taras.ua.adrenalincity.R;

public class SchedulePageActivity extends DrawerActivity implements RequestManager.OnRequestManagerListener {

    ViewMVC viewMVC;
    DataManager dataManager;
    RequestManager requestManager;
    MovieModel movieModel;

    AdapterTodayAndSoon adapterTodayAndSoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_schedule_page);

        requestManager = RequestManager.getInstance(this);
        requestManager.setOnRequestManagerListener(this);

        viewMVC = new ViewMVC(this);
        movieModel = ViewModelProviders.of(this).get(MovieModel.class);

        adapterTodayAndSoon = new AdapterTodayAndSoon(this, new ArrayList<Movie>());
        //adapterTodayAndSoon.setRecyclerType(Constants.PLANE_RECYCLER, Constants.RV_ACTIVITY_MOVIE_TODAY);
        adapterTodayAndSoon.setOnAdapterClickListener(iClickListener);
        viewMVC.setRVadapter(adapterTodayAndSoon);


        dataManager = new DataManager(this, requestManager, Constants.SCHEDULE_ACTIVITY);

        requestManager.sendRequest(Constants.MOVIE_TODAY, Constants.TYPE_MOVIE_TODAY);

        movieModel.getMovieTodayList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieList) {
                adapterTodayAndSoon.setMovieListToAdapter(movieList);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dataManager.appendScheduleToMovies(movieList);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        movieModel.getDayScheduleList().observe(this, new Observer<List<MovieSchedule>>() {
            @Override
            public void onChanged(@Nullable List<MovieSchedule> movieSchedules) {
                adapterTodayAndSoon.setMovieScheduleList(movieSchedules);
                adapterTodayAndSoon.setRecyclerType(Constants.SCHEDULE_RECYCLER, Constants.RV_ACTIVITY_MOVIE_SCHEDULE);
                Log.v("ins_movie", "m_SCH "+movieSchedules.size());
            }
        });

        movieModel.getMovieIdScheduleForMovieActivity().observe(this, new Observer<LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>>>() {
            @Override
            public void onChanged(@Nullable LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> integerLinkedHashMapLinkedHashMap) {
                adapterTodayAndSoon.setShowListTodayForAllMovies(integerLinkedHashMapLinkedHashMap);
                Log.v("tag_count", ""+integerLinkedHashMapLinkedHashMap.size());
            }
        });
//////////////////////


    }

    @Override
    public void onMovieDownloaded(List<Movie> list, int moviePeriod) {
        switch (moviePeriod) {
            case Constants.TYPE_MOVIE_TODAY:
                movieModel.setMovieTodayList(list);
        }
    }

    @Override
    public void onMovieScheduleMapForFilterReady(Map<Integer, List<Show>> movieShowMap) {
        movieModel.setMovieScheduleMap(movieShowMap);
        //requestManager.sendRequest(Constants.MOVIE_SOON, Constants.TYPE_MOVIE_SOON);
        Log.v("the_end", "size - " + movieModel.getMovieScheduleMap().size());
    }

    @Override
    public void onMovieScheduleMapForMovieActivityReady(LinkedHashMap<Integer,
            LinkedHashMap<String, List<Show>>> movieShowMap) {
        movieModel.setMovieIdScheduleForMovieActivity(movieShowMap);
        requestManager.sendRequest(Constants.SCHEDULE_TODAY, Constants.TYPE_SCHEDULE_TODAY);
    }

    @Override
    public void onDayScheduleReady(List<MovieSchedule> list) {
        Log.v("thread_", "day_schedule " + list.size());
        movieModel.setDayScheduleList(list);
    }

    private Bundle bundleMovieInf;
    private View refPosterTransition;

    private IAdapterListener.IAdapterMovieTodayAndSoon iClickListener = new IAdapterListener.IAdapterMovieTodayAndSoon() {
        @Override
        public void onMovieClicked(Movie movie, View refView, String showStartTime, int RECYCLER_FROM) {
            /** the main purpose of 'showStartTime' is to perform click in Time RV
             // whenever user navigates from schedule fragment */
            refPosterTransition = refView;
            //Movie movie = movieModel.getMovieToday(position);

            //TODO:replace with MutableData
            LinkedHashMap<String, List<Show>> mapSchedule = movieModel.getMovieActivityScheduleForMovieId(movie.getMovieId());
            Gson gson = new Gson();
            String gsonString = gson.toJson(mapSchedule, LinkedHashMap.class);

            bundleMovieInf = new Bundle();
            bundleMovieInf.putSerializable("movie", movie);
            bundleMovieInf.putInt("moviePeriod", Constants.TYPE_MOVIE_TODAY);
            bundleMovieInf.putString("gsonLinkedHashMap", gsonString);
            if (showStartTime != null) {
                bundleMovieInf.putString("showStartTime", showStartTime);
            }

            gotToMovieActivity();

            Log.v("movietoday", "" + movie.getMovieId());

            if (viewMVC.containerIsAdded())
                viewMVC.removeTrailerContainer(1);
        }

        @Override
        public void onYouTubeTrailerClicked(Movie movie, int position, RecyclerView clickFragmentMovieToday) {
            viewMVC.addTrailerContainer(movie, position);
        }

        @Override
        public void onImgLoaded() {
            Log.v("callbackadapter", "all imgs have been loaded");
            viewMVC.hideLoadingPb();
        }

        @Override
        public void onViewPagerHeightReady(int height) {

        }
    };

   /* @Override
    public void onScheduleDownloaded(String gsonString) {

    }*/

    private void gotToMovieActivity() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, refPosterTransition, "movie_poster");

        Intent intent = new Intent(SchedulePageActivity.this, MovieActivity.class);
        intent.putExtra("clicked_movie", bundleMovieInf);
        startActivity(intent, optionsCompat.toBundle());
    }


    @Override
    public void onBackPressed() {
        if (viewMVC.containerIsAdded()) {
            //remove this container
            viewMVC.removeTrailerContainer(1);
        } else {
            super.onBackPressed();
        }
    }

}
