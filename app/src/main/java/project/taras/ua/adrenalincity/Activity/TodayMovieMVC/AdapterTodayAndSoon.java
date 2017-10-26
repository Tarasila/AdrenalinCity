package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.IAdapterListener;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MainActivity;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieSchedule;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 10.03.2017.
 */

public class AdapterTodayAndSoon extends RecyclerView.Adapter<AdapterTodayAndSoon.ViewHolder> {

    private boolean dataIsLoaded = false;

    private int RECYCLER_TYPE = Constants.PLANE_RECYCLER;
    private int RECYCLER_FROM;

    private OnAdapterListener listener;
    private IAdapterListener.IAdapterMovieTodayAndSoon iListener;
    private int numberOfLoadedImages = 0;

    public void setRecyclerType(int type, int from) {
        RECYCLER_TYPE = type;
        RECYCLER_FROM = from;
        //recyclerView.setVisibility(View.INVISIBLE);
        notifyDataSetChanged();
    }

    public interface OnAdapterListener {
        void movieClicked(Movie movie, View refPosterTransition);
    }

    public void setOnAdapterListener(OnAdapterListener listener) {
        this.listener = listener;
    }

    public void setOnAdapterClickListener(IAdapterListener.IAdapterMovieTodayAndSoon listener) {
        this.iListener = listener;
    }

    private Context context;
    private List<Movie> listMovies;
    private List<MovieSchedule> listDaySchedule;
    private LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> lhpMovieWithAllShowToday;
    private RecyclerView recyclerView;

    private int displayWidth;
    private int displayHeight;

    //set RV from ViewMVC
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public AdapterTodayAndSoon(Context context, List<Movie> list) {
        this.context = context;
        this.listMovies = list;
        this.displayWidth = measureDisplay().widthPixels;
        this.displayHeight = measureDisplay().heightPixels;
    }

    public void setMovieListToAdapter(List<Movie> list) {
        this.listMovies = list;
        //Log.v("ins_movie", "list_day "+listMovies.size());
        notifyDataSetChanged();
    }

    public void setMovieScheduleList(List<MovieSchedule> list) {
        this.listDaySchedule = list;
        List<MovieSchedule> scheduleList = new ArrayList<>();
        Log.v("ins_movie", "list_day_sch " + listDaySchedule.size());

        for (MovieSchedule movieSchedule : listDaySchedule) {
            for (Movie movie : listMovies) {
                if (movieSchedule.getMovieId().equalsIgnoreCase(String.valueOf(movie.getMovieId()))) {
                    movieSchedule.setMovie(movie);
                    Log.v("ins_movie ", movie.getTitle() + " " + movieSchedule.getStartTime());
                    scheduleList.add(movieSchedule);
                }
            }
        }
        this.listDaySchedule = scheduleList;
        //notifyDataSetChanged();
    }

    public void setShowListTodayForAllMovies(LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> integerListMap) {
        this.lhpMovieWithAllShowToday = integerListMap;
        this.dataIsLoaded = true;
        notifyDataSetChanged();
    }

    public List<MovieSchedule> getListDaySchedule() {
        return listDaySchedule;
    }

    //ViewHolder holder;

    @Override
    public AdapterTodayAndSoon.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_movie, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder holder = new ViewHolder(view);
        holder.imgPlayTrailer.setOnClickListener(onYouTubeClickListener);
        //this.holder = holder;
        return holder;
    }

    private View clickedImageView;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickedImageView = v.findViewById(R.id.iv_movie_poster);
            int clickedPosition = recyclerView.getChildLayoutPosition(v);
            switch (RECYCLER_TYPE) {
                case Constants.PLANE_RECYCLER:
                    Movie movie = listMovies.get(clickedPosition);
                    Log.v("recycler_from", "" + RECYCLER_FROM);
                    iListener.onMovieClicked(movie, clickedImageView, null, RECYCLER_FROM);
                    break;
                case Constants.SCHEDULE_RECYCLER:
                    Log.v("recycler_from", "" + RECYCLER_FROM);
                    //holder.hideBlurryArea(recyclerView.findViewHolderForAdapterPosition(clickedPosition).itemView);
                    MovieSchedule movieSchedule = listDaySchedule.get(clickedPosition);
                    iListener.onMovieClicked(movieSchedule.getMovie(), clickedImageView, movieSchedule.getStartTime(), RECYCLER_FROM);
                    break;
                case Constants.ALL_RECYCLER:
                    Movie m = listMovies.get(clickedPosition);
                    Log.v("recycler_from", "" + RECYCLER_FROM);
                    iListener.onMovieClicked(m, clickedImageView, null, RECYCLER_FROM);
                    break;
            }
        }
    };

    public View.OnClickListener onYouTubeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int clickedPosition = recyclerView.getChildLayoutPosition((View) v.getParent().getParent().getParent());
            Movie movie = null;
            switch (RECYCLER_TYPE) {
                case Constants.PLANE_RECYCLER:
                    movie = listMovies.get(clickedPosition);
                    break;
                case Constants.SCHEDULE_RECYCLER:
                    movie = listDaySchedule.get(clickedPosition).getMovie();
                    break;
                case Constants.ALL_RECYCLER:
                    movie = listMovies.get(clickedPosition);
                    break;
            }
            iListener.onYouTubeTrailerClicked(movie, clickedPosition, recyclerView);
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (RECYCLER_TYPE) {
            case Constants.PLANE_RECYCLER:
                Movie currentMovie = listMovies.get(position);
                Log.v("m_id", "" + currentMovie.getMovieId() + " " + currentMovie.getTitle());
                holder.tTime.setText("");
                Picasso.with(context).load(currentMovie.getImgUrl()).into(holder.imgPosterMovie, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (position == listMovies.size() - 1) {
                            //the end
                            if (RECYCLER_FROM == Constants.RV_ACTIVITY_MOVIE_TODAY) {
                                iListener.onImgLoaded();
                                Log.v("on_img_loaded", "rv_a_movie_today_rv");
                            } else if (RECYCLER_FROM == Constants.RV_ACTIVITY_MOVIE_SOON) {
                                iListener.onImgLoaded();
                                Log.v("on_img_loaded", "rv_a_movie_today_rv");
                            }
                            //Otherwise we wait for schedule rv to be loaded
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                holder.tTitleMovie.setText(currentMovie.getTitle());

                switch (RECYCLER_FROM) {
                    /*case Constants.RV_FRAGMENT_MOVIE_TODAY:
                        Log.v("main_track", "rv_frag)");

                        TextView tvShowTimeMT;
                        for (int i = 0; i < holder.getTvListShowTime().size(); i++) {
                            tvShowTimeMT = holder.getTvListShowTime().get(i);
                            tvShowTimeMT.setText("");
                        }
                        break;*/


                    /*case Constants.RV_ACTIVITY_MOVIE_TODAY:
                        Log.v("main_track", "rv_frag)");

                        TextView tvShowTimeMT;
                        for (int i = 0; i < holder.getTvListShowTime().size(); i++) {
                            tvShowTimeMT = holder.getTvListShowTime().get(i);
                            tvShowTimeMT.setText("");
                        }
                        break;*/

                    case Constants.RV_ACTIVITY_MOVIE_SOON:
                        holder.tStartTime.setText(currentMovie.getStartDate());
                        break;

                    case Constants.RV_FRAGMENT_MOVIE_SOON:
                        holder.tStartTime.setText(currentMovie.getStartDate());
                        break;

                    /*case Constants.RV_FRAGMENT_MOVIE_TODAY:
                        if (dataIsLoaded) {
                            List<Show> listAllShowsToday = getTodayShowListForMovieId(currentMovie.getMovieId());
                            TextView tvShowTime;
                            if (listAllShowsToday != null) {
                                for (int i = 0; i < holder.getTvListShowTime().size(); i++) {
                                    if (i < listAllShowsToday.size()) {
                                        tvShowTime = holder.getTvListShowTime().get(i);
                                        tvShowTime.setText(listAllShowsToday.get(i).getTime());
                                    } else if (i > listAllShowsToday.size() - 1) {
                                        tvShowTime = holder.getTvListShowTime().get(i);
                                        tvShowTime.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                for (int i = 0; i < holder.getTvListShowTime().size(); i++) {
                                    tvShowTime = holder.getTvListShowTime().get(i);
                                    tvShowTime.setText("");
                                }
                            }
                        }
                        break;*/
                }
                break;

            case Constants.SCHEDULE_RECYCLER:

                MovieSchedule movieSchedule = listDaySchedule.get(position);
                final Movie movie = movieSchedule.getMovie();

                Picasso.with(context).load(movie.getImgUrl()).into(holder.imgPosterMovie, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (position == listDaySchedule.size() - 1) {
                            //the end
                            iListener.onImgLoaded();
                            Log.v("on_img_loaded", "schedule_rv");
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
                holder.tTime.setText(movieSchedule.getStartTime());
                holder.tTitleMovie.setText(movie.getTitle());
                for (TextView tv : holder.getTvListShowTime()) {
                    tv.setText("");
                }
                break;

            case Constants.ALL_RECYCLER:
                Movie this_movie = listMovies.get(position);
                Log.v("m_id", "" + this_movie.getMovieId() + " " + this_movie.getTitle());
                holder.tTime.setText("");
                Picasso.with(context).load(this_movie.getImgUrl()).into(holder.imgPosterMovie, new Callback() {
                    @Override
                    public void onSuccess() {
                        /*if (position == listMovies.size() - 1) {
                            //the end
                            if (RECYCLER_FROM == Constants.RV_ACTIVITY_MOVIE_TODAY) {
                                iListener.onImgLoaded();
                                Log.v("on_img_loaded", "rv_a_movie_today_rv");
                            }
                            //Otherwise we wait for schedule rv to be loaded
                        }*/
                    }

                    @Override
                    public void onError() {

                    }
                });
                holder.tTitleMovie.setText(this_movie.getTitle());
                break;
        }
    }


    @Override
    public int getItemCount() {
        Log.v("RV_TYPE", "" + RECYCLER_TYPE);
        switch (RECYCLER_TYPE) {
            case Constants.PLANE_RECYCLER:
                switch (RECYCLER_FROM) {
                    case Constants.RV_ACTIVITY_MOVIE_TODAY:
                        /*Log.v("RV_HEIGHT_ON_GET_ITEM", "" + "SOON");
                        int viewPagerHeight = (int) (displayHeight * 0.31f) * listMovies.size() + (dp2px(16) * listMovies.size());
                        iListener.onViewPagerHeightReady(viewPagerHeight);*/
                        return listMovies.size();
                    case Constants.RV_FRAGMENT_MOVIE_SOON:
                        int viewPagerHeight = (int) (displayHeight * 0.31f) * listMovies.size() + (dp2px(16) * listMovies.size());
                        if (((MainActivity) context).firstSoon) {
                            iListener.onViewPagerHeightReady(viewPagerHeight);
                            ((MainActivity) context).firstSoon = false;
                            Log.v("RV_HEIGHT_ON_GET_ITEM", "" + "SOON");
                        }
                        return listMovies.size();
                    case Constants.RV_ACTIVITY_MOVIE_SOON:
                        return listMovies.size();
                }

            case Constants.SCHEDULE_RECYCLER:
                if (listDaySchedule != null) {
                    //TODO:
                    if (context.getClass().getSimpleName().equalsIgnoreCase("MainActivity")) {
                        int h = (int) (displayHeight * 0.31f) * listDaySchedule.size() + (dp2px(16) * listDaySchedule.size());
                        if (((MainActivity) context).firstSchedule) {
                            iListener.onViewPagerHeightReady(h);
                            ((MainActivity) context).firstSchedule = false;
                            Log.v("RV_HEIGHT_ON_GET_ITEM", "SCHEDULE");
                        }
                    }
                    return listDaySchedule.size();
                } else
                    return 0;

            case Constants.ALL_RECYCLER:
                //TODO:
                int pagerHeight = (int) (displayHeight * 0.31f) * listMovies.size() + (dp2px(16) * listMovies.size());
                if (((MainActivity) context).firstAll) {
                    iListener.onViewPagerHeightReady(pagerHeight);
                    ((MainActivity) context).firstAll = false;
                    Log.v("RV_HEIGHT_ON_GET_ITEM", "" + "ALL");
                }
                return listMovies.size();
        }
        return 0;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public List<Show> getTodayShowListForMovieId(int movieId) {
        Log.v("all_show", "id " + movieId);
        Log.v("all_show", "size " + lhpMovieWithAllShowToday.size());
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("d", Locale.getDefault());
        String today = format.format(date);

        LinkedHashMap<String, List<Show>> allShowsToday = lhpMovieWithAllShowToday.get(movieId);

        if (allShowsToday != null) {
            return allShowsToday.get(today);
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardMovieView cardMovieView;
        private RelativeLayout rlCardView;
        private ImageView imgPosterMovie;
        private ImageView imgBlurry;
        private LinearLayout flBlurry;
        private ImageView imgPlayTrailer;
        private TextView tTime;
        private TextView tTitleMovie;
        private TextView tStartTime;

        private LinearLayout ll_container_top;
        private LinearLayout ll_container_bottom;

        /*private TextView tShowTime1;
        private TextView tShowTime2;
        private TextView tShowTime3;
        private TextView tShowTime4;
        private TextView tShowTime5;
        private TextView tShowTime6;*/

        private List<TextView> listOfTvShowTime = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            cardMovieView = (CardMovieView) itemView.findViewById(R.id.card_movie_view_view);
            rlCardView = (RelativeLayout) itemView.findViewById(R.id.rl_in_cardview);
            imgPosterMovie = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            imgBlurry = (ImageView) itemView.findViewById(R.id.iv_blurry);
            imgPlayTrailer = (ImageView) itemView.findViewById(R.id.iv_youtube_play_button);
            imgPlayTrailer.setOnClickListener(onYouTubeClickListener);
            tTime = (TextView) itemView.findViewById(R.id.tv_movie_time);
            tTitleMovie = (TextView) itemView.findViewById(R.id.tv_movie_title);
            tStartTime = (TextView) itemView.findViewById(R.id.tv_movie_start_time);

            //ll_container_top = (LinearLayout) itemView.findViewById(R.id.ll_container_top_show_time);
            //ll_container_bottom = (LinearLayout) itemView.findViewById(R.id.ll_container_bottom_show_time);

            /*tShowTime1 = (TextView) itemView.findViewById(R.id.tv_show_time_1);
            tShowTime2 = (TextView) itemView.findViewById(R.id.tv_show_time_2);
            tShowTime3 = (TextView) itemView.findViewById(R.id.tv_show_time_3);
            tShowTime4 = (TextView) itemView.findViewById(R.id.tv_show_time_4);
            tShowTime5 = (TextView) itemView.findViewById(R.id.tv_show_time_5);
            tShowTime6 = (TextView) itemView.findViewById(R.id.tv_show_time_6);

            listOfTvShowTime.add(tShowTime1);
            listOfTvShowTime.add(tShowTime2);
            listOfTvShowTime.add(tShowTime3);
            listOfTvShowTime.add(tShowTime4);
            listOfTvShowTime.add(tShowTime5);
            listOfTvShowTime.add(tShowTime6);*/

            measureDisplay(context);

            ViewTreeObserver observer = cardMovieView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Log.v("cardheight", "" + cardMovieView.getPaddingBottom());
                    int cardWidth = cardMovieView.getMeasuredWidth();
                    int cardHeight = (int) (displayHeight * 0.31f);

                    int playWidth = (int) (cardHeight * 0.6f);
                    int playHeight = (int) (cardHeight * 0.55f);

                    int descriptionArea = (int) (cardWidth / 2 - 30);

                    int tvWidth = descriptionArea;
                    int tvPadding = (int) (descriptionArea * 0.05f);

                    RelativeLayout.LayoutParams ivBlurryParams = (RelativeLayout.LayoutParams) imgBlurry.getLayoutParams();
                    ivBlurryParams.height = cardHeight;
                    ivBlurryParams.width = descriptionArea;
                    imgBlurry.setLayoutParams(ivBlurryParams);

                    int titleX = cardWidth - descriptionArea;

                    RelativeLayout.LayoutParams tvParams = (RelativeLayout.LayoutParams) tTitleMovie.getLayoutParams();
                    tvParams.width = tvWidth;
                    tTitleMovie.setLayoutParams(tvParams);
                    tTitleMovie.setTextColor(Color.WHITE);
                    tTitleMovie.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
                    tTitleMovie.setX(titleX);
                    tTitleMovie.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    int playCenterX = cardWidth - descriptionArea + (descriptionArea - playWidth) / 2;
                    int playCenterY = cardHeight / 2 - playHeight / 2;

                    RelativeLayout.LayoutParams ivPlayParams = (RelativeLayout.LayoutParams) imgPlayTrailer.getLayoutParams();
                    //ivPlayParams.addRule(RelativeLayout.BELOW, tTitleMovie.getId());
                    ivPlayParams.height = playHeight;
                    ivPlayParams.width = playWidth;
                    imgPlayTrailer.setX(playCenterX);
                    imgPlayTrailer.setY(playCenterY);
                    imgPlayTrailer.setLayoutParams(ivPlayParams);

                    RelativeLayout.LayoutParams tvTimeParams = (RelativeLayout.LayoutParams) tTime.getLayoutParams();
                    tvTimeParams.width = tvWidth;
                    tTime.setTextColor(Color.WHITE);
                    //tTime.setPadding(tvPadding, tvPadding, tvPadding, tvPadding);
                    tTime.setX(titleX);

                    Rect rect = new Rect();
                    tTime.getDrawingRect(rect);
                    int timeHeight = rect.height();

                    tTime.setY(cardHeight - timeHeight);
                    tTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tTime.setLayoutParams(tvTimeParams);

                    Rect start_time_rect = new Rect();
                    tStartTime.getDrawingRect(start_time_rect);

                    int xStartTime = titleX;
                    int yStartTime = cardHeight - start_time_rect.height() - start_time_rect.height() / 2;

                    RelativeLayout.LayoutParams start_time_params = (RelativeLayout.LayoutParams) tStartTime.getLayoutParams();
                    start_time_params.width = descriptionArea;

                    tStartTime.setTextColor(Color.WHITE);
                    tStartTime.setX(xStartTime);
                    tStartTime.setY(yStartTime);
                    tStartTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    tStartTime.setLayoutParams(start_time_params);

                    //Rect rectContainer = new Rect();
                    //tShowTime1.getDrawingRect(rectContainer);
                    //int containerHeight = rectContainer.height();

                    /*int contX = titleX + descriptionArea / 2 - ll_container_top.getMeasuredWidth() / 2;

                    ll_container_top.setX(contX);
                    ll_container_top.setY(cardHeight - containerHeight * 3);

                    ll_container_bottom.setX(contX);
                    ll_container_bottom.setY(cardHeight - containerHeight * 2);*/

                    CardView.LayoutParams params = (CardView.LayoutParams) cardMovieView.getLayoutParams();
                    params.height = cardHeight;
                    params.width = cardWidth;
                    cardMovieView.setLayoutParams(params);

                    cardMovieView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        private int displayHeight;
        private int displayWidth;

        public List<TextView> getTvListShowTime() {
            return listOfTvShowTime;
        }

        private void measureDisplay(Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            displayHeight = metrics.heightPixels;
            displayWidth = metrics.widthPixels;

            Log.v("TAG_WIDTH", String.valueOf(displayWidth));
            Log.v("TAG_HEIGHT", String.valueOf(displayHeight));
        }

        ValueAnimator scaleX;
        ValueAnimator scaleY;

        /*public void hideBlurryArea(View itemView) {
            scaleX = ObjectAnimator.ofFloat(itemView.findViewById(R.id.iv_blurry), "scaleX", 1, 0);
            scaleX.setDuration(100);
            scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleX.start();

            scaleY = ObjectAnimator.ofFloat(itemView.findViewById(R.id.iv_youtube_play_button), "scaleY", 1, 0);
            scaleY.setDuration(100);
            scaleY.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleY.start();
            *//*final Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
            fadeOut.setDuration(100);
            fadeOut.set
            fadeOut.setFillAfter(true);
            itemView.findViewById(R.id.tv_movie_title).startAnimation(fadeOut);
            itemView.findViewById(R.id.iv_blurry).startAnimation(fadeOut);
            itemView.findViewById(R.id.iv_youtube_play_button).startAnimation(fadeOut);*//*
        }*/
    }
}
