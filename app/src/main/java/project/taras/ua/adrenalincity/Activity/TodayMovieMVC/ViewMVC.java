package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 10.03.2017.
 */
public class ViewMVC {

    private static final int PREVIOUS_CONTAINER = 2;
    private static final int THIS_CONTAINER = 1;

    private Activity context;

    private Toolbar toolbar;
    private RecyclerView rv;
    private LinearLayoutManager linearLayoutManager;

    private ProgressBar spinner;
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private final RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    public ViewMVC(Activity context) {
        this.context = context;
        init();
    }

    private void init() {
        toolbar = (Toolbar) context.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(toolbarClickListener);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        rv = (RecyclerView) context.findViewById(R.id.rv_movie_today);
        rv.setNestedScrollingEnabled(false);

        spinner = (ProgressBar) context.findViewById(R.id.spinner_movie_today);
        spinner.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        manager = context.getFragmentManager();
    }

    public void setRVadapter(final AdapterTodayAndSoon adapterTodayAndSoon) {
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            /*@Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);

                int visibleChildPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (visibleChildPosition != -1){
                    rv.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                }
            }*/
        };
        rv.setAdapter(adapterTodayAndSoon);
        adapterTodayAndSoon.setRecyclerView(rv);
        rv.setLayoutManager(linearLayoutManager);
    }

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    };

    public void hideLoadingPb() {
        rv.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    YouTubePlayerFragment playerFragment;
    List<RelativeLayout> stackRl = new ArrayList<>();
    List<FrameLayout> stackContainer = new ArrayList<>();

    ObjectAnimator translationX;
    ValueAnimator scaleY;
    ValueAnimator scaleX;

    public void addTrailerContainer(final Movie movie, int position) {
        CardView child = (CardView) rv.getChildAt(position);
        RelativeLayout rl = (RelativeLayout) child.findViewById(R.id.rl_in_cardview);
        stackRl.add(rl);

        FrameLayout container = new FrameLayout(context);
        stackContainer.add(container);

        EContainerId[] containerId = EContainerId.values();
        int id = containerId[position].getLayoutResId();

        container.setId(id);
        //container.setBackgroundColor(Color.BLACK);
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
        setBackYouTubeImg();

        manager.popBackStack();

        stackRl.get(stackRl.size() - posContainerInStack)
                .removeView(stackContainer.get(stackContainer.size() - posContainerInStack));
    }

    private void setBackYouTubeImg() {
        translationX.setDuration(0);
        translationX.reverse();

        scaleX.setDuration(0);
        scaleX.reverse();

        scaleY.setDuration(0);
        scaleY.reverse();
    }
}
