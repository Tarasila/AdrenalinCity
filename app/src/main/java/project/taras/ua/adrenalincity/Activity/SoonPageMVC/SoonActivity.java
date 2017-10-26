package project.taras.ua.adrenalincity.Activity.SoonPageMVC;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
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

public class SoonActivity extends DrawerActivity implements RequestManager.OnRequestManagerListener {

    ViewMVC viewMVC;
    RequestManager requestManager;
    MovieModel movieModel;

    AdapterTodayAndSoon adapterTodayAndSoon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_soon);

        viewMVC = new ViewMVC(this);
        movieModel = ViewModelProviders.of(this).get(MovieModel.class);

        requestManager = RequestManager.getInstance(this);
        //TODO: init request manager...
        requestManager.setOnRequestManagerListener(this);


        adapterTodayAndSoon = new AdapterTodayAndSoon(this, new ArrayList<Movie>());
        //adapterTodayAndSoon.setRecyclerType(Constants.PLANE_RECYCLER, Constants.RV_ACTIVITY_MOVIE_TODAY);
        adapterTodayAndSoon.setRecyclerType(Constants.PLANE_RECYCLER, Constants.RV_ACTIVITY_MOVIE_SOON);
        adapterTodayAndSoon.setOnAdapterClickListener(iClickListener);
        viewMVC.setRVadapter(adapterTodayAndSoon);

        requestManager.sendRequest(Constants.MOVIE_SOON, Constants.TYPE_MOVIE_SOON);

        movieModel.getMovieSoonList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                adapterTodayAndSoon.setMovieListToAdapter(movieList);
            }
        });

    }

    @Override
    public void onMovieDownloaded(List<Movie> list, int moviePeriod) {
        switch (moviePeriod) {
            case Constants.TYPE_MOVIE_SOON:
                Movie m = list.get(1);
                Log.v("checkmovie", m.getTitle());

                movieModel.setMovieSoonList(list);

                break;
        }
    }

    private Bundle bundleMovieInf;
    private View refPosterTransition;

    private IAdapterListener.IAdapterMovieTodayAndSoon iClickListener = new IAdapterListener.IAdapterMovieTodayAndSoon() {
        @Override
        public void onMovieClicked(Movie movie, View refView, String showStartTime, int RECYCLER_FROM) {
            refPosterTransition = refView;

            bundleMovieInf = new Bundle();
            bundleMovieInf.putSerializable("movie", movie);
            bundleMovieInf.putInt("moviePeriod", Constants.TYPE_MOVIE_SOON);

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

    private void gotToMovieActivity(){
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, refPosterTransition, "movie_poster");

        Intent intent = new Intent(SoonActivity.this, MovieActivity.class);
        intent.putExtra("clicked_movie", bundleMovieInf);
        startActivity(intent, optionsCompat.toBundle());
    }

    @Override
    public void onMovieScheduleMapForFilterReady(Map<Integer, List<Show>> movieShowMap) {

    }

    @Override
    public void onMovieScheduleMapForMovieActivityReady(LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> movieShowMap) {

    }

    @Override
    public void onDayScheduleReady(List<MovieSchedule> list) {

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
