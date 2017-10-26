package project.taras.ua.adrenalincity.Activity.MovieMVC.ValueBarView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 15.03.2017.
 */

public class ValueBar extends View {

    private int maxVal = 100;
    private int currentCircleNumber = 10;

    private int orientation;
    private int HALL_TYPE;
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private int barWidth;
    private int circleRadius;
    private int spaceAfterBar;
    private int circleTextSize;
    private int maxValueTextSize;
    private int labelTextSize;
    private int labelTextColor;
    private int currentValueTextColor;
    private int circleTextColor;
    private int baseColor;
    private int fillColor;
    private String labelText;


    //objects used for drawing
    private Paint labelPaint;
    private Paint maxValuePaint;
    private Paint barBasePaint;
    private Paint barFillPaint;
    private Paint circlePaint;
    private Paint currentValuePaint;
    private Canvas canvas;
    private ThumbCircle circle;

    float barLength;
    float barCenter;
    float halfBarWidth;
    float top;
    float bottom;
    float left;
    float right;

    private boolean clickable = false;

    RelativeLayout.LayoutParams verticalBarParams;
    RelativeLayout.LayoutParams horizontalBarParams;

    private OnScrollBarChangeListener fragmentListener;

    public interface OnScrollBarChangeListener {
        void OnScrollBarChangeX(int xPosition);
        void OnScrollBarChangeY(int yPosition);
        void OnScrollBarActionUp(int x);
    }

    public void addOnScrollBarChangeListener(OnScrollBarChangeListener fragmentListener){
        this.fragmentListener = fragmentListener;
    }

    public void setLayoutParamsManually(RelativeLayout.LayoutParams verticalBarParams,
                                        RelativeLayout.LayoutParams horizontalBarParams) {
        this.verticalBarParams = verticalBarParams;
        this.horizontalBarParams = horizontalBarParams;
        switch (orientation) {
            case VERTICAL:
                setLayoutParams(verticalBarParams);
                break;
            case HORIZONTAL:
                setLayoutParams(horizontalBarParams);
                break;
        }

        int horValBar = horizontalBarParams.width;
        if (horValBar > 500) {
            updateBarAccordingToHallType(Constants.RED_HALL);
        }
        else if (horValBar > 300 && horValBar < 500){
            updateBarAccordingToHallType(Constants.BLUE_HALL);
        }
        else if (horValBar < 300){
            updateBarAccordingToHallType(Constants.SILVER_HALL);
        }
        requestLayout();
    }

    private void updateBarAccordingToHallType(int hallType) {
        switch (hallType) {
            case Constants.RED_HALL: {
                HALL_TYPE = Constants.RED_HALL;

                switch (orientation) {
                    case VERTICAL:
                        currentCircleNumber = 10;
                        circle = new ThumbCircle(0, circleRadius, circleRadius);
                        break;
                    case HORIZONTAL:
                        currentCircleNumber = 1;
                        circle = new ThumbCircle(circleRadius, 0, circleRadius);
                        break;
                }
                break;
            }
            case Constants.BLUE_HALL:
                HALL_TYPE = Constants.BLUE_HALL;

                switch (orientation) {
                    case VERTICAL:
                        currentCircleNumber = 11;
                        circle = new ThumbCircle(0, circleRadius, circleRadius);
                        break;
                    case HORIZONTAL:
                        currentCircleNumber = 1;
                        circle = new ThumbCircle(circleRadius, 0, circleRadius);
                        break;
                }
                break;
            case Constants.SILVER_HALL:
                HALL_TYPE = Constants.SILVER_HALL;

                switch (orientation) {
                    case VERTICAL:
                        currentCircleNumber = 5;
                        circle = new ThumbCircle(0, circleRadius, circleRadius);
                        break;
                    case HORIZONTAL:
                        currentCircleNumber = 1;
                        circle = new ThumbCircle(circleRadius, 0, circleRadius);
                        break;
                }
                break;
        }
    }

    public ValueBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ValueBar, 0, 0);
        barWidth = ta.getDimensionPixelSize(R.styleable.ValueBar_barWidth, 1);
        circleRadius = ta.getDimensionPixelSize(R.styleable.ValueBar_circleRadius, 0);
        spaceAfterBar = ta.getDimensionPixelSize(R.styleable.ValueBar_spaceAfterBar, 0);
        circleTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_circleTextSize, 0);
        maxValueTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_maxValueTextSize, 0);
        labelTextSize = ta.getDimensionPixelSize(R.styleable.ValueBar_labelTextSize, 0);
        labelTextColor = ta.getColor(R.styleable.ValueBar_labelTextColor, Color.BLACK);
        currentValueTextColor = ta.getColor(R.styleable.ValueBar_maxValueTextColor, Color.BLACK);
        circleTextColor = ta.getColor(R.styleable.ValueBar_circleTextColor, Color.BLACK);
        baseColor = ta.getColor(R.styleable.ValueBar_baseColor, Color.BLACK);
        fillColor = ta.getColor(R.styleable.ValueBar_fillColor, Color.BLACK);
        int t = ta.getDimensionPixelSize(R.styleable.ValueBar_testattr, 0);
        labelText = ta.getString(R.styleable.ValueBar_labelText);
        orientation = ta.getInt(R.styleable.ValueBar_orientation, 0);
        ta.recycle();

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextAlign(Paint.Align.LEFT);
        labelPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setColor(labelTextColor);

        maxValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxValuePaint.setTextSize(maxValueTextSize);
        maxValuePaint.setColor(currentValueTextColor);
        maxValuePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        maxValuePaint.setTextAlign(Paint.Align.RIGHT);

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setColor(baseColor);

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setColor(fillColor);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(fillColor);

        currentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentValuePaint.setTextSize(circleTextSize);
        currentValuePaint.setColor(circleTextColor);
        currentValuePaint.setTextAlign(Paint.Align.CENTER);

        switch (orientation) {
            case VERTICAL:
                currentCircleNumber = 10;
                circle = new ThumbCircle(0, circleRadius, circleRadius);
                break;
            case HORIZONTAL:
                currentCircleNumber = 1;
                circle = new ThumbCircle(circleRadius, 0, circleRadius);
                break;
        }
    }

    private void initItemCoordinats() {
        switch (orientation) {
            case VERTICAL:
                barLength = getHeight() - getPaddingTop() - getPaddingBottom();
                barCenter = getBarCenter();
                Log.v("TAG_CENTER_V", "" + barCenter);
                halfBarWidth = barWidth / 2;
                top = getPaddingTop();
                bottom = getPaddingBottom() + barLength;
                left = barCenter - halfBarWidth;
                right = barCenter + halfBarWidth;
                break;
            case HORIZONTAL:
                barLength = getWidth() - getPaddingRight() - getPaddingLeft();
                barCenter = getBarCenter();
                halfBarWidth = barWidth / 2;
                top = barCenter - halfBarWidth;
                bottom = barCenter + halfBarWidth;
                left = getPaddingLeft();
                right = getWidth() - getPaddingLeft() - getPaddingRight();
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int size = 0;
        switch (orientation) {
            case VERTICAL:
                size = getPaddingTop() + getPaddingBottom();
                break;
            case HORIZONTAL:
                size = getPaddingTop() + getPaddingBottom();
                break;
        }
        return resolveSizeAndState(size, heightMeasureSpec, 0);
    }

    private int measureWidth(int widthMeasureSpec) {
        int size = 0;
        switch (orientation) {
            case VERTICAL:
                size = getPaddingLeft() + getPaddingRight();
                size += Math.max(barWidth, circleRadius * 2);
                break;
            case HORIZONTAL:
                size = getPaddingLeft() + getPaddingRight();
                break;
        }
        return resolveSizeAndState(size, widthMeasureSpec, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        initItemCoordinats();
        drawRoundRect(canvas);
        drawCircle(canvas);
    }

    private float getBarCenter() {
        float barCenter = 0;
        switch (orientation) {
            case VERTICAL:
                //position the bar slightly below the middle of the drawable area
                barCenter = (getWidth() - getPaddingLeft() - getPaddingRight()) / 2; //this is the center
                break;
            case HORIZONTAL:
                barCenter = (getHeight() - getPaddingLeft() - getPaddingRight()) / 2;
                break;
        }
        return barCenter;
    }

    private void drawRoundRect(Canvas canvas) {
        //String maxValueString = String.valueOf(maxVal);
        //Rect maxValueRect = new Rect();
        //maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length(), maxValueRect);
        //float barLength = getWidth() - getPaddingRight() - getPaddingLeft() - circleRadius - maxValueRect.width() - spaceAfterBar;
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(rect, halfBarWidth, halfBarWidth, barBasePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickable) {
            switch (orientation) {
                case VERTICAL:
                    float y = event.getY();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            getParent().requestDisallowInterceptTouchEvent(true);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            fragmentListener.OnScrollBarChangeY((int) y);
                            invalidate();
                            return true;
                        case MotionEvent.ACTION_UP:
                            fragmentListener.OnScrollBarActionUp((int) y);
                            invalidate();
                            return true;
                    }
                    break;
                case HORIZONTAL:
                    float x = event.getX();
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            getParent().requestDisallowInterceptTouchEvent(true);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            fragmentListener.OnScrollBarChangeX((int) x);
                            invalidate();
                            return true;
                        case MotionEvent.ACTION_UP:
                            fragmentListener.OnScrollBarActionUp((int) x);
                            invalidate();
                            return true;
                    }
                    break;
            }
        }
        return true;
    }

    private void checkBounds(int coordinates, int circleRadius) {
        switch (orientation) {
            case VERTICAL:
                if (coordinates <= circleRadius) circle.y = circleRadius;
                else if (coordinates >= getHeight() - circleRadius)
                    circle.y = getHeight() - circleRadius;
                break;
            case HORIZONTAL:
                if (coordinates <= circleRadius) circle.x = circleRadius;
                else if (coordinates >= getWidth() - circleRadius)
                    circle.x = getWidth() - circleRadius;
                break;
        }

    }

    public ThumbCircle getCircle() {
        return circle;
    }

    public void setCurrentCircleNumber(int currentCircleNumber) {
        this.currentCircleNumber = currentCircleNumber;
    }

    public void drawCircle(Canvas canvas) {
        float centerOfBar;
        Rect bounds;
        String valueString;

        switch (orientation) {
            case VERTICAL:
                centerOfBar = barCenter;
                canvas.drawCircle(centerOfBar, circle.getY(), circleRadius, circlePaint);

                bounds = new Rect();
                valueString = String.valueOf(Math.round(currentCircleNumber));
                currentValuePaint.getTextBounds(valueString, 0, valueString.length(), bounds);
                canvas.drawText(valueString, centerOfBar, circle.getY() + bounds.height() / 2, currentValuePaint);
                break;
            case HORIZONTAL:
                centerOfBar = barCenter;
                canvas.drawCircle(circle.getX(), centerOfBar, circleRadius, circlePaint);

                bounds = new Rect();
                valueString = String.valueOf(Math.round(currentCircleNumber));
                currentValuePaint.getTextBounds(valueString, 0, valueString.length(), bounds);
                canvas.drawText(valueString, circle.getX(), centerOfBar + bounds.height() / 2, currentValuePaint);
                break;
        }
    }

    public void setMaxVal(int val) {
        this.maxVal = val;
        invalidate();
        requestLayout();
    }

    public void setValue(int newValue) {
        if (newValue < 0) {
            currentCircleNumber = 0;
        } else if (newValue > maxVal) {
            currentCircleNumber = maxVal;
        } else {
            currentCircleNumber = newValue;
        }
        invalidate();
    }

    public void setClickable(boolean b){
        this.clickable = b;
    }
}
