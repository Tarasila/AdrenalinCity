package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

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
import project.taras.ua.adrenalincity.R;

public class MovieTodayActivity extends DrawerActivity implements RequestManager.OnRequestManagerListener{

    ViewMVC viewMVC;
    RequestManager requestManager;
    DataManager dataManager;
    MovieModel movieModel;

    AdapterTodayAndSoon adapterTodayAndSoon;

    @Override
    public void onMovieDownloaded(List<Movie> list, int moviePeriod) {
        Log.v("TAG_CALLBACK", list.get(1).getTitle());
        movieModel.setMovieTodayList(list);
    }

    private Bundle bundleMovieInf;
    private View refPosterTransition;

    private IAdapterListener.IAdapterMovieTodayAndSoon iClickListener = new IAdapterListener.IAdapterMovieTodayAndSoon() {
        @Override
        public void onMovieClicked(Movie movie, View refView, String showStartTime, int RECYCLER_FROM) {
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

    private void gotToMovieActivity(){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, refPosterTransition, "movie_poster");

        Intent intent = new Intent(MovieTodayActivity.this, MovieActivity.class);
        intent.putExtra("clicked_movie", bundleMovieInf);
        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void onMovieScheduleMapForFilterReady(Map<Integer, List<Show>> movieShowMap) {
        //redundant
    }

    @Override
    public void onMovieScheduleMapForMovieActivityReady(LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> movieShowMap) {
        movieModel.setMovieIdScheduleForMovieActivity(movieShowMap);
        //redundant
    }

    @Override
    public void onDayScheduleReady(List<MovieSchedule> list) {
        //redundant
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_movie_today);
        Log.v("TAG_TRACE", "on_create");
        viewMVC = new ViewMVC(this);
        movieModel = ViewModelProviders.of(this).get(MovieModel.class);

        requestManager = RequestManager.getInstance(this);
        //TODO: init request manager...
        requestManager.setOnRequestManagerListener(this);
        dataManager = new DataManager(this, requestManager, Constants.MOVIE_TODAY_ACTIVITY);

        adapterTodayAndSoon = new AdapterTodayAndSoon(this, new ArrayList<Movie>());
        adapterTodayAndSoon.setRecyclerType(Constants.PLANE_RECYCLER, Constants.RV_ACTIVITY_MOVIE_TODAY);
        adapterTodayAndSoon.setOnAdapterClickListener(iClickListener);
        viewMVC.setRVadapter(adapterTodayAndSoon);

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TAG_TRACE", "on_resume");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TAG_TRACE", "on_destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (viewMVC.containerIsAdded()){
            //remove this container
            viewMVC.removeTrailerContainer(1);
        }else {
            super.onBackPressed();
        }
    }
}
