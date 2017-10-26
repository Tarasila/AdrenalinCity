package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;

/**
 * Created by Taras on 19.04.2017.
 */

public interface IAdapterListener {

    interface IAdapterMovieTodayAndSoon {
        void onMovieClicked(Movie movie, View refView, String showStartTime, int RECYCLER_FROM);
        void onYouTubeTrailerClicked(Movie movie, int position, RecyclerView clickFragmentMovieToday);
        void onImgLoaded();
        void onViewPagerHeightReady(int height);
    }

    interface IAdapterViewPager{
        void onMovieClicked(Movie movie, View refView, String showStartTime);
        void onImgLoaded();
    }
}
