package project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.polidea.view.ZoomView;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 13.03.2017.
 */

public class CinemaLayout extends TableLayout {

    private float LAYOUT_HEIGHT;

    int displayWidth;
    int displayHeight;

    int hallType;

    public void setLayoutSize(float height) {
        this.LAYOUT_HEIGHT = height;
    }

    public void setHallType(int hallType) {
        this.hallType = hallType;
    }

    private OnCinemaLayoutListener listenerFragment;

    public interface OnCinemaLayoutListener {
        void OnSeatsRedHallCreated(Map<Integer, List<PlaceView>> mapRedHall);

        void onPlaceClicked(int xPos, int yPos);

        void onAvailableSeatsHaveBeenShown();
    }

    public void setOnCinemaLayoutListener(OnCinemaLayoutListener listenerFragment) {
        this.listenerFragment = listenerFragment;
    }

    private Context context;

    public CinemaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        measureDisplay(context);
    }

    public CinemaLayout(Context context) {
        super(context);
        this.context = context;
        measureDisplay(context);
    }

    public void animateAllRaw(int currentRaw) {
        switch (hallType) {
            case Constants.RED_HALL:
                TableRow rawToAnimate = (TableRow) getChildAt(currentRaw);
                rawToAnimate.startAnimation(AnimationUtils.loadAnimation(context, R.anim.woble_raw));
                break;
            case Constants.BLUE_HALL:
                TableRow rawToAnimateB = (TableRow) getChildAt(currentRaw);
                rawToAnimateB.startAnimation(AnimationUtils.loadAnimation(context, R.anim.woble_raw));
                break;
            case Constants.SILVER_HALL:
                TableRow rawToAnimateS = (TableRow) getChildAt(currentRaw);
                rawToAnimateS.startAnimation(AnimationUtils.loadAnimation(context, R.anim.woble_raw));
                break;
        }
    }

    PlaceView prevSeat;

    public void animateSeat(int lastRawPosition, PlaceView targetView) {
        TableRow raw = null;
        if (lastRawPosition == 0) {
            raw = (TableRow) getChildAt(0);
        } else {
            switch (hallType) {
                case Constants.RED_HALL:
                    raw = (TableRow) getChildAt(lastRawPosition);
                    break;
                case Constants.BLUE_HALL:
                    raw = (TableRow) getChildAt(lastRawPosition);
                    break;
                case Constants.SILVER_HALL:
                    raw = (TableRow) getChildAt(lastRawPosition);
                    break;
            }
        }
        int viewPosition = raw.indexOfChild(targetView);
        if (viewPosition != -1) {
            raw.getChildAt(viewPosition).startAnimation(AnimationUtils.loadAnimation(context, R.anim.woble_seat));
        }
    }

    public void changeColorSeatWhileScrolling(PlaceView lastSeat) {
        lastSeat.setScrollingStatus(true);
        lastSeat.changeColorWhileScrolling();
        if (prevSeat != null) {
            prevSeat.setScrollingStatus(false);
            prevSeat.clearColorFilterWhileScrolling();
        }
        prevSeat = lastSeat;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    PlaceView[][] arrayOfSeats = new PlaceView[10][10];
    TableRow tableRow;
    int idIndex = 1;
    int emptyCount = 0;

    private Map<Integer, List<PlaceView>> mapRedHall = new HashMap<>();
    private List<PlaceView> listOfSeats;

    public void createSilverCinemaHall(final Context context) {

        for (int r = 1; r < 6; r++) {

            listOfSeats = new ArrayList<>();
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            for (int p = 1; p < 11; p++) {
                PlaceView seat = null;
                if (r == 2 || r == 3 || r == 4) {
                    if (p == 9 || p == 10) {
                        seat = new PlaceView(context, r, p, true);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(0);
                        tableRow.addView(seat, p - 1);

                        emptyCount++;
                    } else {
                        seat = new PlaceView(context, 6 - r, p);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(idIndex);
                        tableRow.addView(seat, p - 1);
                        listOfSeats.add(seat);

                        idIndex++;
                    }
                } else if (r == 5) {
                    if (p == 8 || p == 9 || p == 10) {
                        seat = new PlaceView(context, r, p, true);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(0);
                        tableRow.addView(seat, p - 1);

                        emptyCount++;
                    } else {
                        seat = new PlaceView(context, 6 - r, p);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(idIndex);
                        tableRow.addView(seat, p - 1);
                        listOfSeats.add(seat);

                        idIndex++;
                    }
                } else {
                    seat = new PlaceView(context, 6 - r, p);
                    seat.setStatus(Constants.SEAT_AVAILABLE);
                    seat.setOnClickListener(onClickListener);
                    seat.setId(generateViewId());
                    seat.setSeatId(idIndex);
                    tableRow.addView(seat, p - 1);
                    listOfSeats.add(seat);

                    idIndex++;
                }
            }
            mapRedHall.put(r - 1, listOfSeats);
            tableRow.setId(generateViewId());
            addView(tableRow, r - 1);

            invalidate();
        }
    }

    ZoomView zoomView;

    public void createRedCinemaHall(final Context context) {

        zoomView = new ZoomView(context);
        zoomView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int r = 1; r < 11; r++) {

            listOfSeats = new ArrayList<>();
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int p = 1; p < 22; p++) {
                PlaceView seat = null;
                if (r == 1) {
                    if (p % 3 == 0) {
                        seat = new PlaceView(context, r, p, true);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(0);
                        tableRow.addView(seat, p - 1);

                        emptyCount++;

                    } else {
                        seat = new PlaceView(context, 11 - r, p - emptyCount);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(idIndex);
                        tableRow.addView(seat, p - 1);
                        listOfSeats.add(seat);

                        idIndex++;
                    }
                } else {
                    seat = new PlaceView(context, 11 - r, p);
                    seat.setStatus(Constants.SEAT_AVAILABLE);
                    seat.setOnClickListener(onClickListener);
                    seat.setId(generateViewId());
                    seat.setSeatId(idIndex);
                    tableRow.addView(seat, p - 1);
                    listOfSeats.add(seat);

                    idIndex++;
                }
            }
            mapRedHall.put(r - 1, listOfSeats);
            tableRow.setId(generateViewId());
            addView(tableRow, r - 1);

            invalidate();
        }
    }

    public void createBlueCinemaHall(final Context context) {
        for (int r = 1; r < 12; r++) {

            listOfSeats = new ArrayList<>();
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int p = 1; p < 17; p++) {
                PlaceView seat = null;
                if (r == 1) {
                    if (p == 1 || p == 16) {
                        seat = new PlaceView(context, r, p, true);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(0);
                        tableRow.addView(seat, p - 1);

                        emptyCount++;
                    } else {
                        seat = new PlaceView(context, 12 - r, p - emptyCount);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(idIndex);
                        tableRow.addView(seat, p - 1);
                        listOfSeats.add(seat);
                        Log.v("revisio", "r- " + seat.getRaw() + " s- " + seat.getPlace());

                        idIndex++;
                    }
                } else if (r == 2 || r == 3 || r == 4 || r == 5 || r == 6 || r == 7 || r == 8) {
                    if (p == 1 || p == 2 || p == 15 || p == 16) {
                        seat = new PlaceView(context, r, p, true);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(0);
                        tableRow.addView(seat, p - 1);

                        emptyCount++;
                    } else {
                        seat = new PlaceView(context, 12 - r, p - 2);
                        seat.setStatus(Constants.SEAT_AVAILABLE);
                        seat.setOnClickListener(onClickListener);
                        seat.setId(generateViewId());
                        seat.setSeatId(idIndex);
                        tableRow.addView(seat, p - 1);
                        listOfSeats.add(seat);
                        Log.v("revisio", "r- " + seat.getRaw() + " s- " + seat.getPlace());

                        idIndex++;
                    }

                } else {
                    seat = new PlaceView(context, 12 - r, p);
                    seat.setStatus(Constants.SEAT_AVAILABLE);
                    seat.setOnClickListener(onClickListener);
                    seat.setId(generateViewId());
                    seat.setSeatId(idIndex);
                    tableRow.addView(seat, p - 1);
                    listOfSeats.add(seat);
                    Log.v("revisio", "r- " + seat.getRaw() + " s- " + seat.getPlace());

                    idIndex++;
                }
            }
            mapRedHall.put(r - 1, listOfSeats);
            tableRow.setId(generateViewId());
            addView(tableRow, r - 1);
            invalidate();
        }
    }

    //this id is needed for both halls
    //
    int id = 1;

    public void showCurrentlyAvailableSeats(List<Seat> listSeats) {
        /**after Red hall is successfully drawn and get notified to show available seats
         * we pass seats information to FragmentCinema
         * to store it for operations requiring seat's raw and place number
         */
        assignRealIdToSeatFromServer(listSeats);

        TableRow row;

        switch (hallType) {
            case Constants.RED_HALL:

                for (int i = 0; i < 10; i++) {
                    row = (TableRow) getChildAt(i);
                    for (int j = 0; j < 21; j++) {

                        PlaceView viewSeat = (PlaceView) row.getChildAt(j);
                        int priceStatus = viewSeat.getPrice();
                        int status = viewSeat.getStatus();

                        if (priceStatus == Constants.SEAT_BLOCKED) {
                            viewSeat.setStatus(Constants.SEAT_BLOCKED);
                        } else
                            switch (status) {
                                case Constants.SEAT_AVAILABLE:
                                    viewSeat.setStatus(Constants.SEAT_AVAILABLE);
                                    break;
                                case Constants.SEAT_BOOKED:
                                    viewSeat.setStatus(Constants.SEAT_BOOKED);
                                    break;
                                case Constants.SEAT_SOLD:
                                    viewSeat.setStatus(Constants.SEAT_SOLD);
                                    break;
                            }
                    }
                }
                listenerFragment.onAvailableSeatsHaveBeenShown();
                break;

            case Constants.BLUE_HALL:

                for (int i = 0; i < 11; i++) {
                    row = (TableRow) getChildAt(i);
                    for (int j = 0; j < 16; j++) {

                        PlaceView viewSeat = (PlaceView) row.getChildAt(j);
                        int priceStatus = viewSeat.getPrice();
                        int status = viewSeat.getStatus();

                        if (priceStatus == Constants.SEAT_BLOCKED) {
                            viewSeat.setStatus(Constants.SEAT_BLOCKED);
                        } else
                            switch (status) {
                                case Constants.SEAT_AVAILABLE:
                                    viewSeat.setStatus(Constants.SEAT_AVAILABLE);
                                    break;
                                case Constants.SEAT_BOOKED:
                                    viewSeat.setStatus(Constants.SEAT_BOOKED);
                                    break;
                                case Constants.SEAT_SOLD:
                                    viewSeat.setStatus(Constants.SEAT_SOLD);
                                    break;
                            }
                    }
                }
                listenerFragment.onAvailableSeatsHaveBeenShown();
                break;

            case Constants.SILVER_HALL:
                for (int i = 0; i < 5; i++) {
                    row = (TableRow) getChildAt(i);
                    for (int j = 0; j < 10; j++) {
                        PlaceView viewSeat = (PlaceView) row.getChildAt(j);
                        int priceStatus = viewSeat.getPrice();
                        int status = viewSeat.getStatus();

                        if (priceStatus == Constants.SEAT_BLOCKED) {
                            viewSeat.setStatus(Constants.SEAT_BLOCKED);
                        } else
                            switch (status) {
                                case Constants.SEAT_AVAILABLE:
                                    viewSeat.setStatus(Constants.SEAT_AVAILABLE);
                                    break;
                                case Constants.SEAT_BOOKED:
                                    viewSeat.setStatus(Constants.SEAT_BOOKED);
                                    break;
                                case Constants.SEAT_SOLD:
                                    viewSeat.setStatus(Constants.SEAT_SOLD);
                                    break;
                            }
                    }
                }
                listenerFragment.onAvailableSeatsHaveBeenShown();
                break;
        }
        invalidate();
    }

    private void assignRealIdToSeatFromServer(final List<Seat> listSeats) {

        switch (hallType) {
            case Constants.RED_HALL:
                int indexList = listSeats.size() - 1;
                for (int i = 0; i < mapRedHall.size(); i++) {
                    List<PlaceView> rawList = mapRedHall.get(i);
                    int index = 0;
                    for (int y = rawList.size(); y > 0; y--) {
                        Seat seat = listSeats.get(indexList);
                        int realId = seat.getId();
                        int price = seat.getPrice();
                        int status = seat.getStatus();
                        rawList.get(index).setRealId(realId);
                        rawList.get(index).setPrice(price);
                        rawList.get(index).setStatus(status);
                        rawList.get(index).setHallType(Constants.RED_HALL);
                        index++;
                        indexList--;
                    }
                }
                listenerFragment.OnSeatsRedHallCreated(mapRedHall);
                break;

            case Constants.BLUE_HALL:

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        int indexListBlue = listSeats.size() - 1;
                        for (int i = 0; i < mapRedHall.size(); i++) {
                            List<PlaceView> rawList = mapRedHall.get(i);
                            int index = 0;
                            for (int y = rawList.size(); y > 0; y--) {
                                Seat seat = listSeats.get(indexListBlue);
                                int realId = seat.getId();
                                int price = seat.getPrice();
                                int status = seat.getStatus();
                                rawList.get(index).setRealId(realId);
                                rawList.get(index).setPrice(price);
                                rawList.get(index).setStatus(status);
                                rawList.get(index).setHallType(Constants.BLUE_HALL);
                                index++;
                                indexListBlue--;
                            }
                        }
                        listenerFragment.OnSeatsRedHallCreated(mapRedHall);
                    }
                });
                break;

            case Constants.SILVER_HALL:
                int indexListSilver = listSeats.size() - 1;
                for (int i = 0; i < mapRedHall.size(); i++) {
                    List<PlaceView> rawList = mapRedHall.get(i);
                    int index = 0;
                    for (int y = rawList.size(); y > 0; y--) {
                        Seat seat = listSeats.get(indexListSilver);
                        int realId = seat.getId();
                        int price = seat.getPrice();
                        int status = seat.getStatus();
                        rawList.get(index).setRealId(realId);
                        rawList.get(index).setPrice(price);
                        rawList.get(index).setStatus(status);
                        rawList.get(index).setHallType(Constants.SILVER_HALL);

                        index++;
                        indexListSilver--;
                    }
                }
                listenerFragment.OnSeatsRedHallCreated(mapRedHall);
                break;
        }
    }

    private boolean clickable = false;

    /**
     * is set upon 'onAvailableSeatsHaveBeenShown()' in 'FragmentCinema'
     **/
    public void setClickable(boolean b) {
        this.clickable = b;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (clickable) {
                PlaceView seat = (PlaceView) v;
                listenerFragment.onPlaceClicked(seat.getxCoord(), (int) ((TableRow) v.getParent()).getY());
            }
        }
    };

    public void showPlaceAt(int raw, int place) {
        PlaceView p = arrayOfSeats[raw][place];

    }

    private void measureDisplay(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        displayHeight = metrics.heightPixels;
        displayWidth = metrics.widthPixels;
    }

}
