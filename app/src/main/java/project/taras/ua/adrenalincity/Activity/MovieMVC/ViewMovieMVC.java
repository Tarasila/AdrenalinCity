package project.taras.ua.adrenalincity.Activity.MovieMVC;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Seat;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 13.03.2017.
 */

public class ViewMovieMVC {

    /*implements FragmentCinema.OnFragmentCinemaListener*/

    private Activity context;
    private NestedScrollView scrollView;
    private RelativeLayout rlRoot;
    private CoordinatorLayout coordinatorLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsToolbar;
    private ImageView ivAppBar;
    final View v_back;

    private LinearLayout ll_container_buttons_desc;
    public Button b_info;
    public Button b_trailer;

    private RelativeLayout rlMovieDesc;
    //private CompoundVoteView compoundVoteView;
    private TextView tvProduction;
    private TextView tvGenre;
    private TextView tvTimeLength;
    private TextView tvStoryLine;
    private View vDivider;
    private TextView tvShowSchedule;

    private LinearLayout llShowDateAndHalllContainer;
    private RecyclerView rvShowDate;
    private LinearLayoutManager rvDateManager;
    private LinearLayoutManager rvTimeManager;
    private FrameLayout youTubeContainer;
    //private Spinner spTime;
    private RecyclerView rvTime;
    private FrameLayout flContainer;

    private FrameLayout flBookFacade;
    private FrameLayout flPaymentFacade;

    private static final String TAG_RED_HALL_FRAGMENT = "cinema_red_hall_fragment";
    private static final String TAG_BLUE_HALL_FRAGMENT = "cinema_blue_hall_fragment";
    private static final String TAG_SILVER_HALL_FRAGMENT = "cinema_silver_hall_fragment";

    private static final String TAG_HALL_FRAGMENT = "cinema_hall_fragment";
    FragmentCinema fragmentCinema;
    FragmentManager manager;

    private Movie movieInf;

    private OnViewMovieMVCListener controller;

    public ViewMovieMVC(Activity context, Movie movieInf, int moviePeriodType) {
        this.context = context;
        this.movieInf = movieInf;
        switch (moviePeriodType) {
            case Constants.TYPE_MOVIE_TODAY:
                init(Constants.TYPE_MOVIE_TODAY);
                break;
            case Constants.TYPE_MOVIE_SOON:
                init(Constants.TYPE_MOVIE_SOON);
                break;
        }
        v_back = collapsToolbar.findViewById(R.id.v_movie_title_background);
    }

    public void changeLoginIconColor() {
        ActionMenuItemView loginUser = (ActionMenuItemView) toolbar.findViewById(R.id.menu_user);

        scaleX = ObjectAnimator.ofFloat(loginUser, "scaleX", 1, 2f);
        scaleX.setDuration(500);
        scaleX.setRepeatCount(5);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleY = ObjectAnimator.ofFloat(loginUser, "scaleY", 1, 2f);
        scaleY.setDuration(500);
        scaleY.setRepeatCount(5);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleX.start();
        scaleY.start();
    }

    public interface OnViewMovieMVCListener {
        void getShowScheduleList();

        void requestAvailableSeats(int showPosInSpinner);

        void setAdapterToRvDate();

        void updateAdapterDate();
    }

    public void setOnViewMovieMVCListener(OnViewMovieMVCListener listener) {
        this.controller = listener;
    }

    public void setOnFragmentCinemaListener() {
        fragmentCinema.setOnFragmentCinemaListener((FragmentCinema.OnFragmentCinemaListener) controller);
    }

    public void init(final int moviePeriodType) {
        toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.removeView(toolbar.findViewById(R.id.logo_adrenalin));
        collapsToolbar = (CollapsingToolbarLayout) context.findViewById(R.id.collapsingToolbarLayout);

        switch (moviePeriodType) {
            case Constants.TYPE_MOVIE_TODAY:
                if (movieInf.getStartDate() != null) {
                    TextView tv = new TextView(context);
                    tv.setText(movieInf.getStartDate());
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(20);
                    tv.setPadding(20, 0, 20, 0);
                    tv.setBackgroundColor(Color.parseColor("#69FF0505"));

                    DisplayMetrics metrics = measureDisplay();

                    CollapsingToolbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(metrics.widthPixels / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(metrics.widthPixels / 2, 20, 0, 0);

                    tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    tv.setLayoutParams(params);
                    collapsToolbar.addView(tv);
                }

                //compoundVoteView = (CompoundVoteView) context.findViewById(R.id.compoundView);
                vDivider = context.findViewById(R.id.movie_v_divider);
                llShowDateAndHalllContainer = (LinearLayout) context.findViewById(R.id.movie_ll_container_show_date_and_cinema_hall);
                tvShowSchedule = (TextView) context.findViewById(R.id.movie_tv_show_schedule);
                rvShowDate = (RecyclerView) context.findViewById(R.id.movie_rv_show_date);
                rvTime = (RecyclerView) context.findViewById(R.id.movie_rv_show_time);
                flContainer = (FrameLayout) context.findViewById(R.id.container_cinema_seats);

                flBookFacade = (FrameLayout) context.findViewById(R.id.my_basket_fl_on_book_progress_container);
                flPaymentFacade = (FrameLayout) context.findViewById(R.id.my_basket_fl_on_payment_progress_container);
                break;
            case Constants.TYPE_MOVIE_SOON:
                TextView tv = new TextView(context);
                tv.setText(movieInf.getStartDate());
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                tv.setPadding(20, 0, 20, 0);
                tv.setBackgroundColor(Color.parseColor("#69FF0505"));

                DisplayMetrics metrics = measureDisplay();

                CollapsingToolbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(metrics.widthPixels / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(metrics.widthPixels / 2, 20, 0, 0);

                tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                tv.setLayoutParams(params);
                collapsToolbar.addView(tv);

                break;
        }
        collapsToolbar.setCollapsedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Collapsed);
        collapsToolbar.setExpandedTitleTextAppearance(R.style.TextAppearance_MyApp_Title_Expanded);
        collapsToolbar.setTitle(movieInf.getTitle());

        final TextView tvAgeRestriction = new TextView(context);

        collapsToolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View v_back = collapsToolbar.findViewById(R.id.v_movie_title_background);
                float back_height = measureDisplay().heightPixels * 0.1f;
                v_back.getLayoutParams().height = (int) back_height;
                v_back.setY(collapsToolbar.getBottom() - back_height);

                //tv_age_restriction
                tvAgeRestriction.setText(movieInf.getAgeRestriction());
                tvAgeRestriction.setTextColor(Color.WHITE);
                tvAgeRestriction.setTextSize(20);
                //tvAgeRestriction.setPadding(20, 0, 20, 0);

                DisplayMetrics metrics = measureDisplay();

                CollapsingToolbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //params.setMargins(metrics.widthPixels / 2, 20, 0, 0);

                tvAgeRestriction.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvAgeRestriction.setLayoutParams(params);
                tvAgeRestriction.setX(metrics.widthPixels - 80);
                //tvAgeRestriction.setY(metrics.heightPixels * 0.4f - 60);

                int y_age = (int) (collapsToolbar.getBottom() - back_height);

                tvAgeRestriction.setY((float) (y_age + toolbar.getHeight() * 0.25
                ));
                collapsToolbar.addView(tvAgeRestriction);

                collapsToolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        rlRoot = (RelativeLayout) context.findViewById(R.id.content_movie);
        scrollView = (NestedScrollView) context.findViewById(R.id.a_movie_nsv);
        coordinatorLayout = (CoordinatorLayout) context.findViewById(R.id.coordinator_layout_movie);
        appBarLayout = (AppBarLayout) context.findViewById(R.id.appBarLayout);
        rlMovieDesc = (RelativeLayout) context.findViewById(R.id.movie_rl_container_movie_description);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                } else if (Math.abs(verticalOffset) > appBarLayout.getHeight() / 2) {
                    tvAgeRestriction.setVisibility(View.INVISIBLE);
                    fade_out_title_back();
                } else if (Math.abs(verticalOffset) < appBarLayout.getHeight() / 3) {
                    tvAgeRestriction.setVisibility(View.VISIBLE);
                    fade_in_title_back();
                }
            }
        });

        rlMovieDesc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int tvProductionSize = measureDisplay().heightPixels / 50;
                int tvGenreTimeSize = measureDisplay().heightPixels / 100;

                tvGenre.setTextSize(tvGenreTimeSize);
                tvTimeLength.setTextSize(tvGenreTimeSize);
                tvProduction.setTextSize(tvProductionSize);

                tvProduction.getLayoutParams().width = measureDisplay().widthPixels / 2;
                tvGenre.getLayoutParams().width = measureDisplay().widthPixels / 2;

                rlMovieDesc.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        tvProduction = (TextView) context.findViewById(R.id.movie_tv_production);
        tvGenre = (TextView) context.findViewById(R.id.movie_tv_genre);
        tvStoryLine = (TextView) context.findViewById(R.id.movie_tv_story_line);
        tvTimeLength = (TextView) context.findViewById(R.id.movie_tv_time_length);

        youTubeContainer = (FrameLayout) context.findViewById(R.id.fl_container_for_youtube);

        ivAppBar = (ImageView) context.findViewById(R.id.iv_movie_poster);
        ivAppBar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (right == oldRight) {
                    //undisguiseAllViews(moviePeriodType);
                }
            }
        });
        Picasso.with(context)
                .load(movieInf.getImgUrl())
                .into(ivAppBar);

        initAppBarHeight();
        initViewParameters();
        initMovieData();

        manager = context.getFragmentManager();
        playerFragment = new YouTubePlayerFragment();
        transaction = manager.beginTransaction();
        //transaction.addToBackStack("trailer");
        transaction.add(R.id.fl_container_for_youtube, playerFragment, "trailer");
        transaction.commit();
        playerFragment.initialize("AIzaSyBbhd8HYY961_LLQnopymGg8ofmCeYMgG0", new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer
                        .cueVideo(movieInf.getTrailerUrl());
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });

        b_info = (Button) context.findViewById(R.id.movie_b_info);
        b_trailer = (Button) context.findViewById(R.id.movie_b_trailer);
        b_info.setOnClickListener(onClickListener);
        b_trailer.setOnClickListener(onClickListener);

        if (moviePeriodType == Constants.TYPE_MOVIE_SOON) {
            youTubeContainer.setVisibility(View.VISIBLE);
            trailerOpen = true;
            setOpenStatus(b_trailer, Constants.OPEN);
        }
    }

    boolean fade_in_title_back = false;

    private void fade_in_title_back(){
        if (!fade_in_title_back) {
            final Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(2000);
            fadeIn.setFillAfter(true);
            v_back.startAnimation(fadeIn);
            fade_in_title_back = true;
        }
    }

    private void fade_out_title_back(){
        if (fade_in_title_back) {
            final Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
            fadeOut.setDuration(2000);
            fadeOut.setFillAfter(true);
            v_back.startAnimation(fadeOut);
            fade_in_title_back = false;
        }
    }

    public void removeYouTubeFragment() {
        manager.popBackStack();
    }

    private void initAppBarHeight() {
        DisplayMetrics metrics = measureDisplay();
        int posterHeight = (int) (metrics.heightPixels * 0.4f);
        ViewGroup.LayoutParams corLayout = ivAppBar.getLayoutParams();
        corLayout.height = posterHeight;
    }

    private void initViewParameters() {
        DisplayMetrics metrics = measureDisplay();
        tvProduction.getLayoutParams().width = (int) (metrics.widthPixels * 0.5f);
    }

    public void initRecyclerViewDate() {
        rvDateManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvShowDate.setLayoutManager(rvDateManager);
        /**get empty adapter **/
        controller.setAdapterToRvDate();
        /** set list with shows to adapter in controller **/
        controller.updateAdapterDate();
    }

    public void initRecyclerViewTime() {
        rvTimeManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rvTime.setLayoutManager(rvTimeManager);
    }

    public void setRvTimeAdapter(AdapterRvTime adapter) {
        adapter.addRvRef(rvTime);
        rvTime.setAdapter(adapter);
    }

    private void initMovieData() {
        tvProduction.setText(movieInf.getProduction());
        tvGenre.setText(movieInf.getGenre());
        tvTimeLength.setText(movieInf.getDuration());
        tvStoryLine.setText(movieInf.getStoryLine());
    }

    public void setToolbarClickListener(View.OnClickListener toolbarClickListener) {
        toolbar.setNavigationOnClickListener(toolbarClickListener);
    }

    /**
     * gets called out from interface method getAdapter in controller
     **/
    public void setAdapterToRvDate(AdapterRvDate adapter) {
        adapter.addRvRef(rvShowDate);
        rvShowDate.setAdapter(adapter);
    }

    public void setOnRvDateLayoutChangeListener(View.OnLayoutChangeListener onRvDateChangeListener) {
        rvShowDate.addOnLayoutChangeListener(onRvDateChangeListener);
    }

    public void performDateClick(final int datePosition) {
        if (rvShowDate.findViewHolderForAdapterPosition(datePosition) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvShowDate.findViewHolderForAdapterPosition(datePosition).itemView.performClick();
                }
            }, 500);
        }
    }

    public void performTimeClick(final int timePosition) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("time_pos", "" + timePosition);
                rvTime.findViewHolderForAdapterPosition(timePosition).itemView.performClick();
            }
        }, 200);
    }

    /*public void setSpinnerAdapter(ArrayAdapter<String> adapterSpinner) {
        */
    /**
     * gets called from adapterClickListener in controller after show day has been selected
     **//*
        spTime.setAdapter(adapterSpinner);
    }

    public void setSpinnerClickListener(AdapterView.OnItemSelectedListener adapterClickListener) {
        spTime.setOnItemSelectedListener(adapterClickListener);
    }

    public void setInitialSpinnerPosition() {
        spTime.setSelection(0);
    }*/

    /*public void setCompoundViewVoteClickListener(View.OnClickListener clickListener){
        compoundVoteView.setVoteClickListener(clickListener);
    }*/

    /*public void setMovieRating(float rating){
        compoundVoteView.setMovieRatingToStarView(rating);
    }*/

    YouTubePlayerFragment playerFragment;
    // 'allowedToPressBackButton' gets called out of 'MovieActivity' when 'OnBackButton' is to be pressed
    public boolean allowedToPressBackButton = false;

    /**
     * when movie poster gets unrolled all views have to be shown
     */
    public void undisguiseAllViews(int moviePeriod) {

        //rlMovieDesc.setVisibility(View.VISIBLE);
        if (moviePeriod == Constants.TYPE_MOVIE_TODAY) {
            //compoundVoteView.setVisibility(View.VISIBLE);
            //((RelativeLayout.LayoutParams)vDivider.getLayoutParams()).addRule(RelativeLayout.BELOW, ll_container_buttons_desc.getId());

            allowedToPressBackButton = true;
            vDivider.setVisibility(View.VISIBLE);
            tvShowSchedule.setVisibility(View.VISIBLE);
            llShowDateAndHalllContainer.setVisibility(View.VISIBLE);
            rlMovieDesc.requestLayout();
        }
        //saveRlMovieDescHeight();
    }

    FragmentTransaction transaction;

    public void createOrUpdateCinemaHall(int hallType, int position) {
        Log.v("rev_create", "create");
        Fragment fRed = manager.findFragmentByTag(TAG_RED_HALL_FRAGMENT);
        Fragment fBlue = manager.findFragmentByTag(TAG_BLUE_HALL_FRAGMENT);
        Fragment fSilver = manager.findFragmentByTag(TAG_SILVER_HALL_FRAGMENT);

        //probably redundant
        boolean existRed = fRed != null;
        boolean existBlue = fBlue != null;
        boolean existSilver = fSilver != null;

        switch (hallType) {
            case Constants.RED_HALL:

                Log.v("rev_hall", "red");
                createFragmentCinemaRedHall();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.container_cinema_seats, fragmentCinema, TAG_RED_HALL_FRAGMENT);
                transaction.commit();
                flContainer.invalidate();
                getAvailableSeats(position);
                break;

            case Constants.BLUE_HALL:
                Log.v("rev_hall", "blue");
                createFragmentCinemaBlueHall();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.container_cinema_seats, fragmentCinema, TAG_BLUE_HALL_FRAGMENT);
                transaction.commit();
                flContainer.invalidate();
                getAvailableSeats(position);
                break;

            case Constants.SILVER_HALL:
                createFragmentCinemaSilverHall();
                transaction = manager.beginTransaction();
                transaction.replace(R.id.container_cinema_seats, fragmentCinema, TAG_SILVER_HALL_FRAGMENT);
                transaction.commit();
                flContainer.invalidate();
                getAvailableSeats(position);
                break;
        }
    }

    private void createFragmentCinemaRedHall() {
        Bundle bundleHallType = new Bundle();
        bundleHallType.putInt("hallType", Constants.RED_HALL);

        fragmentCinema = new FragmentCinema();
        fragmentCinema.setOnFragmentCinemaListener((FragmentCinema.OnFragmentCinemaListener) controller);
        fragmentCinema.setArguments(bundleHallType);
    }

    private void createFragmentCinemaBlueHall() {
        Bundle bundleHallType = new Bundle();
        bundleHallType.putInt("hallType", Constants.BLUE_HALL);

        fragmentCinema = new FragmentCinema();
        fragmentCinema.setOnFragmentCinemaListener((FragmentCinema.OnFragmentCinemaListener) controller);
        fragmentCinema.setArguments(bundleHallType);
    }

    private void createFragmentCinemaSilverHall() {
        Bundle bundleHallType = new Bundle();
        bundleHallType.putInt("hallType", Constants.SILVER_HALL);

        fragmentCinema = new FragmentCinema();
        fragmentCinema.setOnFragmentCinemaListener((FragmentCinema.OnFragmentCinemaListener) controller);
        fragmentCinema.setArguments(bundleHallType);
    }

    private void getAvailableSeats(int showPositionInSpinner) {
        controller.requestAvailableSeats(showPositionInSpinner);
    }

    public void passAvailableSeatsToFragmentCinema(List<Seat> listSeats) {
        fragmentCinema.passAvailableSeatsToCinemaLayout(listSeats);
        Log.v("rev_seats", "" + listSeats.size());
    }

    public void updateBasketCounterInFragmentCinema(int number) {
        //fragmentCinema.updateBasketCounter(number);
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        (context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public void updateRecyclerPreorder() {
        fragmentCinema.updateRvPreorder();
    }

    public void fullScrollToTheBottom(){
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public void hideScheduleDivider() {
        TextView tvSorry = new TextView(context);
        tvSorry.setText("Вибачте, на сьогодні сеансів немає");
        tvSorry.setTextColor(Color.WHITE);
        tvSorry.setTextSize(17);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, tvShowSchedule.getId());
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.bottomMargin = 50;
        tvSorry.setLayoutParams(params);
        rlRoot.addView(tvSorry);
        rlRoot.requestLayout();
    }

    ValueAnimator scaleX;
    ValueAnimator scaleY;

    public void displayBookBeingProcessed(int visibility) {
        scaleX = ObjectAnimator.ofFloat(flBookFacade, "scaleX", 1, 0.9f);
        scaleX.setDuration(500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleY = ObjectAnimator.ofFloat(flBookFacade, "scaleY", 1, 0.9f);
        scaleY.setDuration(500);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        switch (visibility) {
            case View.VISIBLE:
                flBookFacade.setVisibility(visibility);
                scaleX.start();
                scaleY.start();
                fragmentCinema.setContainerButtonsVisibility(View.INVISIBLE);
                fragmentCinema.setButtonLogInVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                flBookFacade.setVisibility(visibility);
                scaleX.cancel();
                scaleY.cancel();
                fragmentCinema.adapterTicketBasket.clearPreorderListUponSuccess();
                break;
        }
    }

    public void displayPaymentBeingProcessed(int visibility) {
        scaleX = ObjectAnimator.ofFloat(flPaymentFacade, "scaleX", 1, 0.9f);
        scaleX.setDuration(500);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleY = ObjectAnimator.ofFloat(flPaymentFacade, "scaleY", 1, 0.9f);
        scaleY.setDuration(500);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        switch (visibility) {
            case View.VISIBLE:
                flPaymentFacade.setVisibility(visibility);
                scaleX.start();
                scaleY.start();
                fragmentCinema.setContainerButtonsVisibility(View.INVISIBLE);
                fragmentCinema.setButtonLogInVisibility(View.INVISIBLE);
                break;
            case View.INVISIBLE:
                flPaymentFacade.setVisibility(visibility);
                scaleX.cancel();
                scaleY.cancel();
                fragmentCinema.adapterTicketBasket.clearPreorderListUponSuccess();
                //fragmentCinema.setContainerButtonsVisibility(View.VISIBLE);
                break;
        }
    }

    boolean infoOpen = false;
    boolean trailerOpen = false;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveRlMovieDescHeight();
            switch (v.getId()) {
                case R.id.movie_b_info:
                    if (!infoOpen) {
                        setOpenStatus(v, Constants.OPEN);
                        infoOpen = true;
                        trailerOpen = false;
                        youTubeContainer.setVisibility(View.INVISIBLE);
                        rlMovieDesc.setVisibility(View.VISIBLE);
                        //rlMovieDesc.getLayoutParams().height = rlMovieDescHeight;
                        rlMovieDesc.getLayoutParams().height = rlMovieDescHeight;
                        Log.v("inf_h", "" + rlMovieDescHeight);

                        //this means that we opened movie which has schedules
                        if (vDivider != null)
                            ((RelativeLayout.LayoutParams) vDivider.getLayoutParams()).addRule(RelativeLayout.BELOW, rlMovieDesc.getId());
                    } else {
                        setOpenStatus(v, Constants.CLOSE);
                        infoOpen = false;
                        youTubeContainer.setVisibility(View.INVISIBLE);
                        rlMovieDesc.setVisibility(View.INVISIBLE);
                        rlMovieDesc.getLayoutParams().height = 0;
                        //this means that we opened movie which has schedules
                        if (vDivider != null)
                            ((RelativeLayout.LayoutParams) vDivider.getLayoutParams()).addRule(RelativeLayout.BELOW, rlMovieDesc.getId());
                    }
                    rlMovieDesc.requestLayout();
                    break;
                case R.id.movie_b_trailer:
                    if (!trailerOpen) {
                        setOpenStatus(v, Constants.OPEN);
                        trailerOpen = true;
                        infoOpen = false;
                        rlMovieDesc.setVisibility(View.INVISIBLE);
                        youTubeContainer.getLayoutParams().height = 500;
                        youTubeContainer.setVisibility(View.VISIBLE);
                        rlMovieDesc.setVisibility(View.INVISIBLE);

                        //this means that we opened movie which has schedules
                        if (vDivider != null)
                            ((RelativeLayout.LayoutParams) vDivider.getLayoutParams()).addRule(RelativeLayout.BELOW, youTubeContainer.getId());
                    } else {
                        setOpenStatus(v, Constants.CLOSE);
                        trailerOpen = false;
                        youTubeContainer.getLayoutParams().height = 0;
                        youTubeContainer.setVisibility(View.INVISIBLE);

                        //this means that we opened movie which has schedules
                        if (vDivider != null)
                            ((RelativeLayout.LayoutParams) vDivider.getLayoutParams()).addRule(RelativeLayout.BELOW, youTubeContainer.getId());
                    }
                    rlRoot.requestLayout();
                    break;
            }
        }
    };

    private boolean firstTrailerOpenSoon = false;

    public void setOpenStatus(View view, int STATUS) {
        switch (STATUS) {
            case Constants.OPEN:
                if (view.getId() == b_info.getId()) {
                    if (infoOpen) {
                        b_info.getBackground().clearColorFilter();
                    } else {
                        b_info.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        b_trailer.getBackground().clearColorFilter();
                    }
                } else {
                    if (trailerOpen) {
                        b_info.getBackground().clearColorFilter();
                        b_trailer.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                    } else {
                        b_trailer.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        b_info.getBackground().clearColorFilter();
                    }
                }
                break;
            case Constants.CLOSE:
                if (view.getId() == b_info.getId()) {
                    b_info.getBackground().clearColorFilter();
                } else {
                    b_trailer.getBackground().clearColorFilter();
                }
                break;
        }
    }

    private int rlMovieDescHeight;

    public void saveRlMovieDescHeight() {
        this.rlMovieDescHeight = tvStoryLine.getHeight() + tvProduction.getHeight() + tvGenre.getHeight()
                + tvTimeLength.getHeight()
                + rlMovieDesc.getPaddingBottom();
    }
}
