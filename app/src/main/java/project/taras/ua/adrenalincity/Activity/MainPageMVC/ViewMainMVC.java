package project.taras.ua.adrenalincity.Activity.MainPageMVC;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import java.util.ArrayList;
import java.util.List;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments.MovieAllFragment;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments.MovieScheduleFragment;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments.MovieSoonFragment;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.PagerFragments.ViewPagerAdapter;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.EContainerId;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 18.04.2017.
 */

public class ViewMainMVC {

    private static final int PREVIOUS_CONTAINER = 2;

    private Context context;
    private RelativeLayout rlRoot;
    private TextView tv_all_movies;
    private HorizontalViewPager infiniteCycleViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MovieSoonFragment fMovieToday;
    private MovieScheduleFragment fMovieSchedule;
    private MovieAllFragment fMovieAll;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private FrameLayout fl_hiding_layout;
    private Button b_refresh_inter_connect;

    private final RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    public ViewMainMVC(Context context) {
        this.context = context;
        measureDisplay(context);
        init();
    }

    private void init() {
        rlRoot = (RelativeLayout) ((Activity) context).findViewById(R.id.main_rl_root);
        fl_hiding_layout = ((Activity) context).findViewById(R.id.hidingLayout);
        b_refresh_inter_connect = ((Activity) context).findViewById(R.id.b_refresh_internet_connection);


        toolbar = (Toolbar) ((Activity) context).findViewById(R.id.toolbar);
        infiniteCycleViewPager = new HorizontalViewPager(context);
        tv_all_movies = (TextView) ((Activity) context).findViewById(R.id.tv_all_movie);
        infiniteCycleViewPager = (HorizontalViewPager) ((Activity) context).findViewById(R.id.coverflow);
        infiniteCycleViewPager.setScrollDuration(500);
        infiniteCycleViewPager.setMediumScaled(true);
        infiniteCycleViewPager.setMaxPageScale(0.95F);
        infiniteCycleViewPager.setMinPageScale(0.75F);
        infiniteCycleViewPager.setCenterPageScaleOffset(-80F);
        infiniteCycleViewPager.setMinPageScaleOffset(-80.0F);
        initViewPager();
        initTabLayout();

        final ViewTreeObserver observer = infiniteCycleViewPager.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelativeLayout.LayoutParams tv_all_m = (RelativeLayout.LayoutParams) tv_all_movies.getLayoutParams();
                tv_all_m.setMargins(0, (int) (displayHeight * 0.005f), 0, 0);
                tv_all_m.height = (int) (displayHeight * 0.025f);

                RelativeLayout.LayoutParams viewPager = (RelativeLayout.LayoutParams) infiniteCycleViewPager.getLayoutParams();
                viewPager.height = (int) (displayHeight * 0.50f);
                viewPager.width = (int) (displayWidth);

                RelativeLayout.LayoutParams tabParams = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
                tabParams.height = (int) (displayHeight * 0.055f);

                infiniteCycleViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        setComponentsForGoogleMap();

        manager = ((Activity) context).getFragmentManager();
    }

    private void initViewPager() {
        viewPager = (ViewPager) ((Activity) context).findViewById(R.id.vp_main_activity);
        fMovieToday = new MovieSoonFragment();
        fMovieSchedule = new MovieScheduleFragment();
        fMovieAll = new MovieAllFragment();
    }

    public void setOnPageChangeListenerToViewPager(ViewPager.OnPageChangeListener onPageChangeListenerToViewPager) {
        this.viewPager.addOnPageChangeListener(onPageChangeListenerToViewPager);
    }

    public void setViewPagerAdapter(ViewPagerAdapter fragmentViewPagerAdapter) {
        viewPager.setAdapter(fragmentViewPagerAdapter);
        fragmentViewPagerAdapter.add(fMovieSchedule, "Найближчі сеанси");
        fragmentViewPagerAdapter.add(fMovieAll, "Всі сеанси");
        fragmentViewPagerAdapter.add(fMovieToday, "Незабаром");
        fragmentViewPagerAdapter.notifyDataSetChanged();

        TextView tabSchedule = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
        tabSchedule.setText("НАЙБЛИЖЧІ");
        tabLayout.getTabAt(0).setCustomView(tabSchedule);

        TextView tabAll = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
        tabAll.setText("ВСІ");
        tabLayout.getTabAt(1).setCustomView(tabAll);

        TextView tabToday = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
        tabToday.setText("НЕЗАБАРОМ");
        tabLayout.getTabAt(2).setCustomView(tabToday);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) ((Activity) context).findViewById(R.id.tab_layout_main_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setAdapterToViewPager(AdapterViewPager adapterViewPager) {
        infiniteCycleViewPager.setAdapter(adapterViewPager);
    }

    private int displayHeight;
    private int displayWidth;

    private void measureDisplay(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        displayHeight = metrics.heightPixels;
        displayWidth = metrics.widthPixels;
    }

    public void addTrailerContainer(final Movie movie, int position, RecyclerView rv) {
        CardView child = (CardView) rv.getChildAt(position);
        RelativeLayout rl = (RelativeLayout) child.findViewById(R.id.rl_in_cardview);
        stackRl.add(rl);

        FrameLayout container = new FrameLayout(context);
        stackContainer.add(container);

        EContainerId[] containerId = EContainerId.values();
        int id = containerId[position].getLayoutResId();

        container.setId(id);
        container.setLayoutParams(containerParams);

        //remove previous container with fragment before adding the next one
        if (containerIsAdded())
            removeTrailerContainer(PREVIOUS_CONTAINER);

        playerFragment = new YouTubePlayerFragment();
        transaction = manager.beginTransaction();
        transaction.addToBackStack("tag");
        transaction.add(id, playerFragment, "tag");
        transaction.commit();

        playerFragment.initialize("AIzaSyBbhd8HYY961_LLQnopymGg8ofmCeYMgG0", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer
                        .cueVideo(movie.getTrailerUrl());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(1400);
                v.startAnimation(fadeIn);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });

        rl.addView(container);
        rotateYouTube(child);
        rotateRecyclerViewChild(child);
    }

    YouTubePlayerFragment playerFragment;
    List<RelativeLayout> stackRl = new ArrayList<>();
    List<FrameLayout> stackContainer = new ArrayList<>();

    ObjectAnimator translationX;
    ValueAnimator scaleY;
    ValueAnimator scaleX;

    public void rotateYouTube(CardView child) {
        final ImageView ivPlay = (ImageView) child.findViewById(R.id.iv_youtube_play_button);

        scaleX = ObjectAnimator.ofFloat(ivPlay, "scaleX", 1, 3.5f);
        scaleX.setDuration(1000);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleX.start();

        scaleY = ObjectAnimator.ofFloat(ivPlay, "scaleY", 1, 3.5f);
        scaleY.setDuration(1000);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.start();

        translationX = ObjectAnimator.ofFloat(ivPlay, "x", 200);
        translationX.setDuration(1000);
        translationX.setInterpolator(new AccelerateDecelerateInterpolator());
        translationX.start();

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
        fadeOut.setDuration(2000);
        fadeOut.setFillAfter(false);
        ivPlay.startAnimation(fadeOut);
    }

    public void rotateRecyclerViewChild(CardView child) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(child, "rotationY", 0, 360f);
        rotation.setDuration(1000);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        rotation.start();
    }

    public boolean containerIsAdded() {
        int backStackCount = manager.getBackStackEntryCount();
        return backStackCount != 0;
    }

    public void removeTrailerContainer(int posContainerInStack) {
        if (containerIsAdded()) {
            setBackYouTubeImg();

            manager.popBackStack();

            stackRl.get(stackRl.size() - posContainerInStack)
                    .removeView(stackContainer.get(stackContainer.size() - posContainerInStack));
        }
    }

    private void setBackYouTubeImg() {
        translationX.setDuration(0);
        translationX.reverse();

        scaleX.setDuration(0);
        scaleX.reverse();

        scaleY.setDuration(0);
        scaleY.reverse();
    }

    //for fragments
    public void setViewPagerHeight(final int height) {
        viewPager.getLayoutParams().height = height;
        viewPager.requestLayout();
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public void setComponentsForGoogleMap() {
        //pass it over to super class
        ((MainActivity)context).inflateMapLayout((MainActivity)context, rlRoot);
    }

    public void hideLoadingProgressBar() {
        if (b_refresh_inter_connect.getVisibility() == View.VISIBLE)
            b_refresh_inter_connect.setVisibility(View.INVISIBLE);
        fl_hiding_layout.setVisibility(View.GONE);
    }

    public void set_b_refresh_clickable(boolean status) {
        b_refresh_inter_connect.setClickable(status);
    }

    public void show_b_refresh_inter_connect() {
        b_refresh_inter_connect.setVisibility(View.VISIBLE);
    }

    public void set_on_b_internet_con_listener(View.OnClickListener click_listener){
        b_refresh_inter_connect.setOnClickListener(click_listener);
    }
}
