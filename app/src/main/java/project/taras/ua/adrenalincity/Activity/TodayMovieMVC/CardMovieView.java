package project.taras.ua.adrenalincity.Activity.TodayMovieMVC;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerView;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 10.03.2017.
 */

public class CardMovieView extends RelativeLayout {

    private Context context;
    private View rootView;
    private RelativeLayout rl;
    private ImageView ivMoviePoster;
    private TextView tvMovieTitle;
    private ImageView ivPlayButton;

    int posterHeight;
    int posterWidth;

    int cardHeight;
    int cardWidth;

    int widthDisplay;
    int heightDisplay;

    private int playButtonHeight;
    private int playButtonWidth;
    private int tvWidth;

    public CardMovieView(Context context) {
        super(context);
        init(context);
    }

    public CardMovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        rootView = inflate(context, R.layout.card_movie_view_layout, this);
        rl = (RelativeLayout) rootView.findViewById(R.id.rl_in_cardview);
        ivMoviePoster = (ImageView) rootView.findViewById(R.id.iv_movie_poster);
        tvMovieTitle = (TextView) rootView.findViewById(R.id.tv_movie_title);
        ivPlayButton = (ImageView) rootView.findViewById(R.id.iv_youtube_play_button);

        measureDisplay();
    }

    private void measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;
    }

    private void measureViews(final ImageView ivMoviePoster, final TextView tvMovieTitle) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);



       /* ViewTreeObserver observer = cardView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                cardHeight = cardView.getHeight();
                cardWidth = cardView.getWidth();

                int descriptionWidth = cardWidth - posterWidth;

                int textX = cardWidth - descriptionWidth;

                int textCenter = textX + (descriptionWidth - tvMovieTitle.getWidth()) / 2;

                tvMovieTitle.setX(textCenter);

                cardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/

    }

    int playButtomCenterX;
    int playButtomCenterY;

    int size;
    float textWidth;

    int descriptionArea;

    int tvHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.v("check_tag", "called");
        cardWidth = widthDisplay;
        cardHeight = (int) (heightDisplay * 0.33f);

       /* posterWidth = (int) (widthDisplay * 0.4f);
        posterHeight = (int) (heightDisplay * 0.33f);*/

        tvHeight = (int) (cardHeight * 0.15f);
        tvWidth = descriptionArea - 64;

        /*descriptionWidth = cardWidth - */


       /* posterHeight = (int) (heightDisplay * 0.33f);
        posterWidth = (int) (widthDisplay * 0.40f);

        ViewGroup.LayoutParams posterParams = ivMoviePoster.getLayoutParams();
        posterParams.height = posterHeight;
        posterParams.width = posterWidth;
        ivMoviePoster.setLayoutParams(posterParams);

        playButtonHeight = (int) (posterHeight * 0.5f);
        Log.v("onmeasure", "playHeight - "+playButtonHeight);
        playButtonWidth = (int) (posterWidth * 0.8f);

        ViewGroup.LayoutParams playBParams = ivPlayButton.getLayoutParams();
        playBParams.height = playButtonHeight;
        playBParams.width = playButtonWidth;
        ivPlayButton.setLayoutParams(playBParams);

        Log.v("measuresize", "size - "+ivPlayButton.getMeasuredWidth());

        Paint paint = tvMovieTitle.getPaint();
        textWidth = paint.measureText((String) tvMovieTitle.getText());

        int descriptionWidth = widthDisplay - ivMoviePoster.getRight() - 30;
        ViewGroup.LayoutParams tvParams = tvMovieTitle.getLayoutParams();
        tvParams.width = descriptionWidth;
        tvMovieTitle.setLayoutParams(tvParams);*/

        /*int widthSpec = resolveSizeAndState( cardWidth, widthMeasureSpec, 0);
        int heightSpec = resolveSizeAndState( cardHeight, heightMeasureSpec, 0);
        setMeasuredDimension(widthSpec, heightSpec);*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }
}
