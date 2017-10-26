package project.taras.ua.adrenalincity.Activity.MyTicketsMVC;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 05.05.2017.
 */

public class TicketView extends RelativeLayout {

    private Context context;
    private Activity activity;
    private RelativeLayout rootView;
    private ImageView ivMoviePoster;
    private RelativeLayout rlInfArea;
    private TextView tv_movieTitle;
    private TextView tv_movieDate;
    private TextView tv_movieTime;
    private TextView tv_movieHall;
    private TextView tv_movieSeat;
    private TextView tv_moviePrice;

    private RelativeLayout rlBarCodeArea;
    private ImageView ivQrCode;
    private ImageView ivBarCode;

    private int displayWidth;
    private int displayHeight;

    public TicketView(Context context) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        init(context);
    }

    public TicketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.activity = (Activity) context;
        init(context);
    }

    private void init(Context context) {
        displayWidth = measureDisplay().widthPixels;
        displayHeight = measureDisplay().heightPixels;

        rootView = (RelativeLayout) inflate(context, R.layout.custom_ticket_view, this);
        ivMoviePoster = (ImageView) rootView.findViewById(R.id.my_ticket_iv);
        rlInfArea = (RelativeLayout) rootView.findViewById(R.id.my_ticket_inf_area);
        tv_movieTitle = (TextView) rootView.findViewById(R.id.my_ticket_movie_title);
        tv_movieDate = (TextView) rootView.findViewById(R.id.my_ticket_date);
        tv_movieTime = (TextView) rootView.findViewById(R.id.my_ticket_time);
        tv_movieHall = (TextView) rootView.findViewById(R.id.my_ticket_hall);
        tv_movieSeat = (TextView) rootView.findViewById(R.id.my_ticket_seat_inf);
        tv_moviePrice = (TextView) rootView.findViewById(R.id.my_ticket_price);

        rlBarCodeArea = (RelativeLayout) rootView.findViewById(R.id.my_ticket_barCode_area);
        ivQrCode = (ImageView) rootView.findViewById(R.id.my_ticket_iv_qr);
        ivBarCode = (ImageView) rootView.findViewById(R.id.my_ticket_iv_barCode);
    }

    int cardHeight;
    int cardWidth;
    int cardMargin;

    int moviePosterHeight;
    int ticketInfHeight;
    int ticketBarCodeHeight;

    int ivQrCodeHeight;
    int tvSize;
    int tvHeight;
    int tvWidth;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        cardHeight = (int) (displayHeight * 0.8f);
        cardWidth = (int) (displayWidth * 0.8f);

        cardMargin = (int) (displayWidth * 0.05);

        moviePosterHeight = (int) (cardHeight * 0.4f);
        ticketInfHeight = (int) (cardHeight * 0.4f);
        ticketBarCodeHeight = (int) (cardHeight * 0.2f);

        ivMoviePoster.measure(MeasureSpec.makeMeasureSpec(cardWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(moviePosterHeight, MeasureSpec.EXACTLY));
        rlInfArea.measure(MeasureSpec.makeMeasureSpec(cardWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(ticketInfHeight, MeasureSpec.EXACTLY));

        rlBarCodeArea.measure(MeasureSpec.makeMeasureSpec(cardWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(ticketBarCodeHeight, MeasureSpec.EXACTLY));

        ivQrCode.measure(MeasureSpec.makeMeasureSpec((int) (cardWidth * 0.5f), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int) (ticketInfHeight * 0.6f), MeasureSpec.EXACTLY));
        ivBarCode.measure(MeasureSpec.makeMeasureSpec(cardWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(ticketBarCodeHeight, MeasureSpec.EXACTLY));

        int tv_size = (int) tv_movieDate.getTextSize();
        Log.v("tv_size", "m "+tv_size);

        ivQrCodeHeight = ivQrCode.getMeasuredHeight();
        tvHeight = ivQrCodeHeight / 5;
        tvWidth = (int) (cardWidth * 0.5f);
        tvSize = (int) (tvHeight / 3);

        tv_movieDate.setTextSize(tvSize);
        tv_movieDate.measure(MeasureSpec.makeMeasureSpec(tvWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY));

        tv_movieTime.setTextSize(tvSize);
        tv_movieTime.measure(MeasureSpec.makeMeasureSpec(tvWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY));

        tv_movieHall.setTextSize(tvSize);
        tv_movieHall.measure(MeasureSpec.makeMeasureSpec(tvWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY));

        tv_movieSeat.setTextSize(tvSize);
        tv_movieSeat.measure(MeasureSpec.makeMeasureSpec(tvWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY));

        tv_moviePrice.setTextSize(tvSize);
        tv_moviePrice.measure(MeasureSpec.makeMeasureSpec(tvWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(tvHeight, MeasureSpec.EXACTLY));

        setMeasuredDimension(cardWidth, cardHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int infAreaBottom = moviePosterHeight + rlInfArea.getMeasuredHeight();
        int barCodeAreaBottom = moviePosterHeight + rlInfArea.getMeasuredHeight() + rlBarCodeArea.getMeasuredHeight();

        int ivQrX = (int) (cardWidth * 0.5f);
        int ivQrTop = (int) (rlInfArea.getMeasuredHeight() * 0.4f);
        int ivQrBottom = rlInfArea.getMeasuredHeight();

        int tvDateBottom = ivQrTop + tv_movieDate.getMeasuredHeight();
        int tvTimeTop = tvDateBottom;
        int tvTimeBottom = tvDateBottom + tv_movieTime.getMeasuredHeight();
        int tvHallTop = tvTimeBottom;
        int tvHallBottom = tvHallTop + tv_movieHall.getMeasuredHeight();
        int tvSeatTop = tvHallBottom;
        int tvSeatBottom = tvHallBottom + tv_movieSeat.getMeasuredHeight();
        int tvPriceTop = tvSeatBottom;
        int tvPriceBottom = tvPriceTop + tv_moviePrice.getMeasuredHeight();

        Log.v("tag_mytick", ""+infAreaBottom+" "+barCodeAreaBottom);

        ivMoviePoster.layout(0, 0, cardWidth, moviePosterHeight);
        rlInfArea.layout(0, moviePosterHeight, cardWidth, infAreaBottom);

        tv_movieDate.layout(cardMargin, ivQrTop, tvWidth, tvDateBottom);
        tv_movieTime.layout(cardMargin, tvTimeTop, tvWidth, tvTimeBottom);
        tv_movieHall.layout(cardMargin, tvHallTop, tvWidth, tvHallBottom);
        tv_movieSeat.layout(cardMargin, tvSeatTop, tvWidth, tvSeatBottom);
        tv_moviePrice.layout(cardMargin, tvPriceTop, tvWidth, tvPriceBottom);

        ivQrCode.layout(ivQrX, ivQrTop, cardWidth, ivQrBottom);

        rlBarCodeArea.layout(0, infAreaBottom, cardWidth, barCodeAreaBottom);

        ivBarCode.layout(0,0,cardWidth,rlBarCodeArea.getMeasuredHeight());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setImage(String imgUrl) {
        Picasso.with(context).load(imgUrl).into(ivMoviePoster);
        requestLayout();
    }

    public void setQrCode(Bitmap bQr) {
        ivQrCode.setImageBitmap(bQr);
        requestLayout();
    }

    public void setBarCode(Bitmap btmBarCode) {
        ivBarCode.setImageBitmap(btmBarCode);
        requestLayout();
    }

    public void setDate(String date, String time) {
        tv_movieDate.setText("Date: " + date.substring(0, 10));
        setTime(time);
    }

    public void setTime(String time) {
        tv_movieTime.setText("Time: " + time);
    }

    public void setHall(String hall) {
        tv_movieHall.setText("Hall: " + hall);
    }

    public void setRawAndPlace(String raw, String place) {
        tv_movieSeat.setText("Raw: " + raw + " " + "Seat: " + place);
    }

    public void setPrice(String price) {
        tv_moviePrice.setText("Price: " + price);
    }

    public void setMovieTitle(String movieTitle) {
        tv_movieTitle.setText(movieTitle);
    }

    public DisplayMetrics measureDisplay() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }
}
