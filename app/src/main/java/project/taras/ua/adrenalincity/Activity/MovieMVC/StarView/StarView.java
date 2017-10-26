package project.taras.ua.adrenalincity.Activity.MovieMVC.StarView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 17.05.2017.
 */

public class StarView extends View {

    private float ratingPercent = 0f;
    private float ratingScore = 0f;
    private Bitmap filledStar;
    private Bitmap emptyStar;
    private Paint paintText;

    private Context context;

    private int displayHeight;
    private int displayWidth;

    public StarView(Context context) {
        super(context);
        init(context);
    }

    public StarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        this.displayWidth = measureDisplay().widthPixels;
        this.displayHeight = measureDisplay().heightPixels;

        initComponentsSize();

        filledStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.filled_star);
        filledStar = Bitmap.createScaledBitmap(filledStar, 100, 100, true);
        emptyStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_star);
        emptyStar = Bitmap.createScaledBitmap(emptyStar, 100, 100, true);

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setTextSize(textHeight);
        //paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.WHITE);
    }

    int starHeight;
    int starWidth;
    int textHeight;
    int viewHeight;
    int viewWidth;

    private void initComponentsSize(){
        starHeight = displayHeight / 10;
        starWidth = displayHeight / 13;

        textHeight = starHeight / 5;

        viewWidth = starWidth;
        viewHeight = starHeight + textHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpec = MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);

        setMeasuredDimension(widthSpec, heightSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawStar(canvas);
        drawRating(canvas);
    }

    private void drawStar(Canvas canvas){
        canvas.drawBitmap(emptyStar, 0f, 0f, null);
        canvas.save();
        //Log.v("measurheight", ""+getMeasuredHeight());
        //canvas.clipRect(0, (getMeasuredHeight() - 25) - (getMeasuredHeight() - 25) * ratingPercent, getWidth(), getMeasuredHeight() - 25);
        canvas.clipRect(0, starHeight - starHeight * ratingPercent, viewWidth, starHeight);
        canvas.drawBitmap(filledStar, 0f, 0f, null);
        canvas.restore();
    }

    private void drawRating(Canvas canvas){
        Rect bounds = new Rect();
        paintText.getTextBounds(rating_score, 0, rating_score.length(), bounds);
        int height = bounds.height();
        int width = bounds.width();
        Log.v("t_w", ""+width);

        canvas.drawText(rating_score, starWidth / 2 - (width / 2), viewHeight - textHeight, paintText);
        //canvas.drawText();
    }

    float perc;
    String rating_score = "";

    public void setRating(float percent){
        perc = percent / 5;
        this.ratingPercent = perc;
        this.ratingScore = percent;
        this.rating_score = String.valueOf(Math.floor(ratingScore * 100) / 100)+"/5";
        Log.i("perc_inf", ""+perc);
        invalidate();
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
