package project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 13.03.2017.
 */

public class PlaceView extends View {

    private int price;
    private int status;

    Context context;
    Paint paintPlaceNumber;

    int raw = 0;
    int place = 0;
    int seatId;
    int realId;
    int xCoord;
    int yCoord;
    int hallType;

    int displayWidth;
    int displayHeight;

    boolean available = false;
    Canvas canvas;

    boolean emptyView = false;
    boolean scrollingStatus = false;
    boolean bookStatus = false;


    public PlaceView(Context context) {
        super(context);
        measureDisplay(context);
    }

    public PlaceView(Context context, int raw, int place) {
        super(context);
        this.raw = raw;
        this.place = place;
        this.context = context;
        init();
        measureDisplay(context);
    }

    public PlaceView(Context context, int raw, int place, boolean emptyView) {
        super(context);
        this.raw = raw;
        this.place = place;
        this.context = context;
        this.emptyView = emptyView;
        init();
        measureDisplay(context);
    }

    public PlaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        measureDisplay(context);
    }

    private void init() {
        paintPlaceNumber = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPlaceNumber.setColor(Color.BLACK);
        paintPlaceNumber.setStyle(Paint.Style.FILL);
        paintPlaceNumber.setTextAlign(Paint.Align.LEFT);
        paintPlaceNumber.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintPlaceNumber.setTextSize(20);
    }

    private void measureDisplay(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        displayHeight = metrics.heightPixels;
        displayWidth = metrics.widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        if (emptyView) {
            setBackgroundResource(R.drawable.empty_seat);
        } else if (!scrollingStatus && !bookStatus) {
            if (this.getStatus() == Constants.SEAT_BLOCKED) {
                setBackgroundResource(R.drawable.redarmchair);
            } else if (this.getStatus() == Constants.SEAT_AVAILABLE) {
                if (this.getRaw() == 11) {
                    setBackgroundResource(R.drawable.armchair_choosing_process);
                } else if (this.getRaw() == 10 && this.getHallType() == Constants.RED_HALL){
                    setBackgroundResource(R.drawable.armchair_choosing_process);
                }
                else {
                    setBackgroundResource(R.drawable.armchair);
                }
            } else if (this.getStatus() == Constants.SEAT_BOOKED) {
                setBackgroundResource(R.drawable.armchairbooked);
            } else if (this.getStatus() == Constants.SEAT_SOLD) {
                setBackgroundResource(R.drawable.redarmchair);
            } /*else if (this.getPrice() == 60){
                setBackgroundResource(R.drawable.armchair_choosing_process);
            }*/
        } else if (!scrollingStatus && bookStatus) {
            setBackgroundResource(R.drawable.armchairbooked);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightView = (int) (displayHeight * 0.028f);
        int widthView = (int) (displayWidth * 0.039f);

        int widthSpec = resolveSizeAndState(widthView, widthMeasureSpec, 0);
        int heightSpec = resolveSizeAndState(heightView, heightMeasureSpec, 0);

        setMeasuredDimension(widthSpec, heightSpec);
    }

    public int getRaw() {
        return raw;
    }

    public int getPlace() {
        return place;
    }

    public void changeColorAvailable() {
        Drawable background = getBackground();
        background.clearColorFilter();
        invalidate();
    }

    public void setBookedStatus(boolean b) {
        this.bookStatus = b;
        invalidate();
    }

    public void changeColorToBooked() {
        setBackgroundResource(R.drawable.armchairbooked);
        invalidate();
    }

    public void setScrollingStatus(boolean s) {
        this.scrollingStatus = s;
        invalidate();
    }

    public void changeColorWhileScrolling() {
        setBackgroundResource(R.drawable.armchair_choosing_process);
        invalidate();
    }

    public void clearColorFilterWhileScrolling() {
        /*Drawable background = getBackground();
        background.clearColorFilter();
        invalidate();*/
    }

    public void addColorFilterWhileScrolling() {
        Drawable background = getBackground();
        background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        invalidate();
    }

    public void setAvailable(boolean isAvailable) {
        this.available = isAvailable;
        invalidate();
    }

    public boolean isAvailable() {
        return this.available;
    }

    public boolean isEmpty() {
        return emptyView;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        invalidate();
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public void setRealId(int realId) {
        this.realId = realId;
    }

    public int getRealId() {
        return this.realId;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public void setHallType(int hallType) {
        this.hallType = hallType;
    }

    public int getHallType() {
        return hallType;
    }

    public void animatePlace() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.woble_seat);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                addColorFilterWhileScrolling();
                //Log.e("animcheck", "start - " + getRealId());
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearColorFilterWhileScrolling();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.startAnimation(animation);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

}

