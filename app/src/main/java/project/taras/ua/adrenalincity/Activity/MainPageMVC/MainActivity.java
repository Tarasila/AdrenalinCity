package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.InternetConnectionManager;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments.ViewPagerAdapter;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.AdapterTodayAndSoon;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.R;

public class MainActivity extends DrawerActivity implements RequestManager.OnRequestManagerListener {

    private ViewMainMVC viewMVC;
    private MovieModel movieModel;
    private AdapterViewPager adapterViewPager;
    private AdapterTodayAndSoon adapterTodayAndSoon;
    private RequestManager requestManager;
    private DataManager dataManager;
    private InternetConnectionManager connectionManager;

    private ViewPagerAdapter fragmentViewPagerAdapter;

    //to recreate previous view pager height
    private boolean onPause = false;

    private int displayHeight;

    @Override
    public void onMovieDownloaded(List<Movie> list, int moviePeriod) {
        switch (moviePeriod) {
            case Constants.TYPE_MOVIE_TODAY:
                movieModel.setMovieTodayList(list);

                requestManager.sendRequest(Constants.MOVIE_SOON, Constants.TYPE_MOVIE_SOON);
                break;

            /** TYPE_MOVIE_ALL **/
            case Constants.TYPE_MOVIE_SOON:
                movieModel.setMovieSoonList(list);
                movieModel.mergeMovieSoonAndTodayList();

                adapterViewPager = new AdapterViewPager(this);
                adapterViewPager.setOnAdapterListener(iAdapterViewPager);
                adapterViewPager.setData(movieModel.getMovieMergedList().getValue());
                viewMVC.setAdapterToViewPager(adapterViewPager);
                break;
        }
    }

    private Bundle bundleMovieInf;
    private View refPosterTransition;

    public IAdapterListener.IAdapterMovieTodayAndSoon iAdapterMovieTodayAndSoonListener = new IAdapterListener.IAdapterMovieTodayAndSoon() {
        @Override
        public void onMovieClicked(Movie movie, View refView, String showStartTime, int RECYCLER_FROM) {
            setMovieInf(movie, refView, showStartTime, RECYCLER_FROM);
            gotToMovieActivity();

            if (viewMVC.containerIsAdded())
                viewMVC.removeTrailerContainer(1);
        }

        @Override
        public void onYouTubeTrailerClicked(Movie movie, int position, RecyclerView rv) {
            viewMVC.addTrailerContainer(movie, position, rv);
        }

        @Override
        public void onImgLoaded() {
            viewMVC.hideLoadingProgressBar();
            internet_con_failed = false;
        }

        @Override
        public void onViewPagerHeightReady(int height) {
            viewMVC.setViewPagerHeight(height);
        }
    };

    private void setMovieInf(Movie movie, View refView, String showStartTime, int RECYCLER_FROM){
        /** the main purpose of 'showStartTime' is to perform click in Time RV
         // whenever user navigates from schedule fragment */
        refPosterTransition = refView;

        //TODO:replace with MutableData
        LinkedHashMap<String, List<Show>> mapSchedule = movieModel.getMovieActivityScheduleForMovieId(movie.getMovieId());
        Gson gson = new Gson();
        String gsonString = gson.toJson(mapSchedule, LinkedHashMap.class);

        bundleMovieInf = new Bundle();
        bundleMovieInf.putSerializable("movie", movie);
        if (RECYCLER_FROM == Constants.RV_FRAGMENT_MOVIE_SOON){
            bundleMovieInf.putInt("moviePeriod", Constants.TYPE_MOVIE_SOON);
        }else{
            bundleMovieInf.putInt("moviePeriod", Constants.TYPE_MOVIE_TODAY);
        }
        bundleMovieInf.putString("gsonLinkedHashMap", gsonString);
        if (showStartTime != null) {
            bundleMovieInf.putString("showStartTime", showStartTime);
        }
    }

    private IAdapterListener.IAdapterViewPager iAdapterViewPager = new IAdapterListener.IAdapterViewPager() {
        @Override
        public void onMovieClicked(Movie movie, View refView, String showStartDate) {
            setMovieInf(movie, refView, showStartDate, 0);
            viewMVC.removeTrailerContainer(1);
            gotToMovieActivity();
        }

        @Override
        public void onImgLoaded() {
        }
    };

    /**
     * after appendScheduleToMovies() from onMovieDownloaded has been called
     * we fetch all shows with respect to movies passing them to movieModel
     */
    @Override
    public void onMovieScheduleMapForFilterReady(Map<Integer, List<Show>> movieShowMap) {
        movieModel.setMovieScheduleMap(movieShowMap);
    }

    @Override
    public void onMovieScheduleMapForMovieActivityReady(LinkedHashMap<Integer,
            LinkedHashMap<String, List<Show>>> movieShowMap) {
        movieModel.setMovieIdScheduleForMovieActivity(movieShowMap);
        requestManager.sendRequest(Constants.SCHEDULE_TODAY, Constants.TYPE_SCHEDULE_TODAY);
    }

    /**
     * create list with time relevant shows and set it to adapter.
     * Adapter will loop through each one to check with movieId in already set MovieTodayList
     * and create a new list with MovieSchedule objects to show movies with respect to time
     */
    @Override
    public void onDayScheduleReady(List<MovieSchedule> list) {
        movieModel.setDayScheduleList(list);
    }

    private void gotToMovieActivity() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, refPosterTransition, "movie_poster");

        Intent intent = new Intent(MainActivity.this, MovieActivity.class);
        //intent.addFlags()
        intent.putExtra("clicked_movie", bundleMovieInf);
        startActivity(intent, optionsCompat.toBundle());
    }

    private boolean first_click = true;
    private boolean refresh_pressed = false;

    private View.OnClickListener b_on_internet_con_refresh_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(connectionManager.isInternetAvailable() && first_click){
                init_activity();
                first_click = false;
            }
            /*if (internet_con_failed) {
                init_activity();
            }*/
            //viewMVC.set_b_refresh_clickable(false);
            refresh_pressed = true;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

       /* requestManager = RequestManager.getInstance(this);
        requestManager.setOnRequestManagerListener(this);

        dataManager = new DataManager(this, requestManager, Constants.MAIN_ACTIVITY);

        viewMVC = new ViewMainMVC(this);
        displayHeight = viewMVC.measureDisplay().heightPixels;

        requestManager.sendRequest(Constants.MOVIE_TODAY, Constants.TYPE_MOVIE_TODAY);

        movieModel = ViewModelProviders.of(this).get(MovieModel.class);
        movieModel.getMovieTodayList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieList) {
            }
        });

        //movies in infiniteViewPager
        movieModel.getMovieMergedList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieList) {
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

        *//** working with fragments...**//*
        fragmentViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewMVC.setViewPagerAdapter(fragmentViewPagerAdapter);
        viewMVC.setOnPageChangeListenerToViewPager(onPageChangeListener);*/

        connectionManager = new InternetConnectionManager(this);
        viewMVC = new ViewMainMVC(this);
        viewMVC.set_on_b_internet_con_listener(b_on_internet_con_refresh_listener);

        if (!connectionManager.isInternetAvailable()) {
            viewMVC.show_b_refresh_inter_connect();
            Toast.makeText(this, "Будь ласка, перевірте з'єднання з інтернетом", Toast.LENGTH_LONG)
                    .show();
            internet_con_failed = true;
        }
        else
            init_activity();
    }

    private void init_activity(){
        requestManager = RequestManager.getInstance(this);
        requestManager.setOnRequestManagerListener(this);

        dataManager = new DataManager(this, requestManager, Constants.MAIN_ACTIVITY);

        //viewMVC = new ViewMainMVC(this);
        displayHeight = viewMVC.measureDisplay().heightPixels;

        requestManager.sendRequest(Constants.MOVIE_TODAY, Constants.TYPE_MOVIE_TODAY);

        movieModel = ViewModelProviders.of(this).get(MovieModel.class);
        movieModel.getMovieTodayList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieList) {
            }
        });

        //movies in infiniteViewPager
        movieModel.getMovieMergedList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable final List<Movie> movieList) {
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

        /** working with fragments...**/
        fragmentViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewMVC.setViewPagerAdapter(fragmentViewPagerAdapter);
        viewMVC.setOnPageChangeListenerToViewPager(onPageChangeListener);
    }

    public boolean firstSchedule = true;
    public boolean firstAll = true;
    public boolean firstSoon = true;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int viewPagerHeight = 0;
            switch (position) {
                case 0:
                    List<MovieSchedule> dayScheduleList = movieModel.getDayScheduleList().getValue();
                    viewPagerHeight = (int) (displayHeight * 0.31f) * dayScheduleList.size() + (dp2px(16) * dayScheduleList.size());
                    break;
                case 1:
                    List<Movie> movieAllList = movieModel.getMovieTodayList().getValue();
                    viewPagerHeight = (int) (displayHeight * 0.31f) * movieAllList.size() + (dp2px(16) * movieAllList.size());
                    break;
                case 2:
                    List<Movie> movieSoonList = movieModel.getMovieSoonList().getValue();
                    viewPagerHeight = (int) (displayHeight * 0.31f) * movieSoonList.size() + (dp2px(16) * movieSoonList.size());
                    break;
            }
            viewMVC.setViewPagerHeight(viewPagerHeight);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    boolean internet_con_failed = false;

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
        Log.v("TAG_LIFE", "ON PAUSE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("TAG_LIFE", "ON RESUME");
        /*if (internet_con_failed){
            ///init again
            init_activity();

            List<MovieSchedule> dayScheduleList = movieModel.getDayScheduleList().getValue();
            int viewPagerHeight = (int) (displayHeight * 0.31f) * dayScheduleList.size() + (32 * dayScheduleList.size());
            viewMVC.setViewPagerHeight(viewPagerHeight);

            //not proper way
            internet_con_failed = false;
        }*/

        if(onPause && refresh_pressed && !internet_con_failed) {
            List<MovieSchedule> dayScheduleList = movieModel.getDayScheduleList().getValue();
            int viewPagerHeight = (int) (displayHeight * 0.31f) * dayScheduleList.size() + (32 * dayScheduleList.size());
            viewMVC.setViewPagerHeight(viewPagerHeight);
        }

        /*if (!connectionManager.isInternetAvailable())
            Toast.makeText(this, "Будь ласка, перевірте з'єднання з інтернетом", Toast.LENGTH_LONG)
                    .show();
        else {
            if(movieModel != null) {
                List<MovieSchedule> dayScheduleList = movieModel.getDayScheduleList().getValue();
                int viewPagerHeight = (int) (displayHeight * 0.31f) * dayScheduleList.size() + (32 * dayScheduleList.size());
                viewMVC.setViewPagerHeight(viewPagerHeight);
            }
        }*/

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

    /**
     * for synchronization with different objects
     */
    public void synchronizeWith(DataManager dataManager) {
        this.dataManager = dataManager;
    }
}
