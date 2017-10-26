package project.taras.ua.adrenalincity.Activity.MovieMVC;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import pl.polidea.view.ZoomView;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.CinemaLayout;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.PlaceView;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.RootView;
import project.taras.ua.adrenalincity.Activity.MovieMVC.ValueBarView.ValueBar;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.AdapterTicketBasket;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.Swiping.SimpleTouchHelperCallback;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Seat;
import project.taras.ua.adrenalincity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCinema extends android.app.Fragment implements CinemaLayout.OnCinemaLayoutListener,
        ValueBar.OnScrollBarChangeListener {

    private int HALL_TYPE;
    private Handler handler = new Handler();

    private RootView rootView;
    private CinemaLayout cinemaLayout;
    private ValueBar verticalBar;
    private ValueBar horizontalBar;
    private Button bBook;
    private Button bBuy;
    private Button bLogIn;
    private TextView tvCounter;
    private RecyclerView rvBasket;

    private SimpleTouchHelperCallback touchHelperCallback;
    private ItemTouchHelper itemTouchHelper;

    public AdapterTicketBasket adapterTicketBasket;

    private PlaceView seatToBuy = null;

    private int lastRawPosition = 0;

    private OnFragmentCinemaListener controllerListener;
    private boolean firstSeatFound = false;

    public interface OnFragmentCinemaListener {
        void addSeatsToOrderList(PlaceView PlaceViewSeat);

        void createOrder(IOrderManager iOrderManager);

        void buyAllSeats(List<PlaceView> buyList);

        void createOrderFromSelectedSeats(List<PlaceView> buyList);

        void onCinemaHallCreated();

        void onBookButtonClicked();

        void onBuyButtonClicked();

        void onLogInButtonClicked();
    }

    public void setOnFragmentCinemaListener(OnFragmentCinemaListener listener) {
        this.controllerListener = listener;
    }

    public FragmentCinema() {
        // Required empty public constructor
    }

    int lastXposition;

    @Override
    public void OnScrollBarChangeX(int xPosition) {
        //we don't click, it's just scrolling
        onPlaceClicked = false;
        lastXposition = xPosition;
        List<PlaceView> raw = mapRedHall.get(lastRawPosition);

        int min = 50;
        int dif = 0;
        int nearestSeatNumber = 0;
        int nearestSeatCoordinate = 0;
        int seatX = 0;
        PlaceView targetView = null;

        for (int i = 0; i < raw.size(); i++) {
            seatX = raw.get(i).getxCoord();
            dif = Math.abs(seatX - xPosition);

            if (dif < min) {
                min = dif;
                targetView = raw.get(i);
                nearestSeatNumber = raw.get(i).getPlace();
                nearestSeatCoordinate = raw.get(i).getxCoord();
                seatToBuy = raw.get(i);
            }
        }
        if (nearestSeatCoordinate != 0) {
            cinemaLayout.animateSeat(lastRawPosition, targetView);
            horizontalBar.getCircle().moveCircleX(nearestSeatCoordinate);
            horizontalBar.setCurrentCircleNumber(nearestSeatNumber);
            horizontalBar.invalidate();

            selectedSeat = seatToBuy.getRealId();
        }
    }

    @Override
    public void OnScrollBarActionUp(int x) {
        cinemaLayout.changeColorSeatWhileScrolling(seatToBuy);
        //changeAddButtonColor(Color.GREEN);
        if (onPlaceClicked)
            controllerListener.addSeatsToOrderList(seatToBuy);
    }

    @Override
    public void OnScrollBarChangeY(int yPosition) {
        //we don't click, it's just scrolling
        onPlaceClicked = false;
        int min = 50;
        int dif;
        int nearestRawNumber = 0;
        int nearestRawCoordinate = 0;

        switch (HALL_TYPE) {
            case Constants.RED_HALL:
                for (int i = 0; i < mapRedHall.size(); i++) {
                    List<PlaceView> rawList = mapRedHall.get(i);
                    PlaceView firstSeat = rawList.get(0);

                    int ySeat = firstSeat.getyCoord();
                    dif = Math.abs(ySeat - yPosition);
                    if (dif < min) {
                        min = dif;
                        nearestRawNumber = firstSeat.getRaw();
                        nearestRawCoordinate = ySeat;
                        seatToBuy = firstSeat;
                        lastRawPosition = 10 - nearestRawNumber;
                    }
                }
                OnScrollBarChangeX(lastXposition);
                break;

            case Constants.BLUE_HALL:
                for (int i = 0; i < mapRedHall.size(); i++) {
                    List<PlaceView> rawList = mapRedHall.get(i);
                    PlaceView firstSeat = rawList.get(0);

                    int ySeat = firstSeat.getyCoord();
                    dif = Math.abs(ySeat - yPosition);
                    if (dif < min) {
                        min = dif;
                        nearestRawNumber = firstSeat.getRaw();
                        nearestRawCoordinate = ySeat;
                        seatToBuy = firstSeat;
                        lastRawPosition = 11 - nearestRawNumber;
                    }
                }
                OnScrollBarChangeX(lastXposition);
                break;

            case Constants.SILVER_HALL:
                for (int i = 0; i < mapRedHall.size(); i++) {
                    List<PlaceView> rawList = mapRedHall.get(i);
                    PlaceView firstSeat = rawList.get(0);

                    int ySeat = firstSeat.getyCoord();
                    dif = Math.abs(ySeat - yPosition);
                    if (dif < min) {
                        min = dif;
                        nearestRawNumber = firstSeat.getRaw();
                        nearestRawCoordinate = ySeat;
                        seatToBuy = firstSeat;
                        lastRawPosition = 5 - nearestRawNumber;
                    }
                }
                OnScrollBarChangeX(lastXposition);
                break;
        }
        if (nearestRawCoordinate != 0) {
            cinemaLayout.animateAllRaw(lastRawPosition);
            verticalBar.getCircle().moveCircleY(nearestRawCoordinate);
            verticalBar.setCurrentCircleNumber(nearestRawNumber);
            verticalBar.invalidate();

            selectedSeat = seatToBuy.getRealId();
        }
    }

    public void passAvailableSeatsToCinemaLayout(List<Seat> listSeats) {
        cinemaLayout.showCurrentlyAvailableSeats(listSeats);
    }

    @Override
    public void onAvailableSeatsHaveBeenShown() {
        //now user is allowed to scroll vertical and horizontal bars
        cinemaLayout.setClickable(true);
        verticalBar.setClickable(true);
        horizontalBar.setClickable(true);

        controllerListener.onCinemaHallCreated();
    }

    /**
     * to persist seats information
     **/
    private Map<Integer, List<PlaceView>> mapRedHall;

    @Override
    public void OnSeatsRedHallCreated(Map<Integer, List<PlaceView>> mapRedHall) {
        this.mapRedHall = mapRedHall;
        //initFirstSeatBeforeUserScroll();
    }

    private boolean onPlaceClicked = false;

    @Override
    public void onPlaceClicked(int xPos, int yPos) {
        OnScrollBarChangeY(yPos);
        OnScrollBarChangeX(xPos);
        onPlaceClicked = true;
        OnScrollBarActionUp(xPos);
    }

    private void initFirstSeatBeforeUserScroll() {
        seatToBuy = mapRedHall.get(0).get(0);
        OnScrollBarActionUp(seatToBuy.getxCoord());
    }

    private int selectedSeat;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        pref = Pref.getInstance(getActivity());
        super.onActivityCreated(savedInstanceState);
    }

    private LinearLayoutManager layoutManager;
    private ZoomView zoomView;

    NestedScrollView nsv;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundleHallType = getArguments();
        HALL_TYPE = bundleHallType.getInt("hallType");

        View view = inflater.inflate(R.layout.fragment_cinema_seats, container, false);
        view.setClickable(false);
        nsv = (NestedScrollView) (getActivity().findViewById(R.id.a_movie_nsv));

        cinemaLayout = (CinemaLayout) view.findViewById(R.id.cinema_layout_in_fragment);
        cinemaLayout.setOnCinemaLayoutListener(this);
        cinemaLayout.setLayoutSize(0.33f);

        switch (HALL_TYPE) {
            case Constants.RED_HALL: {
                //assign hall type here in order to animate raw properly

                cinemaLayout.setHallType(Constants.RED_HALL);
                drawRedHall();
                verticalBar = (ValueBar) view.findViewById(R.id.valueBar);
                verticalBar.setClickable(false);
                verticalBar.addOnScrollBarChangeListener(this);
                horizontalBar = (ValueBar) view.findViewById(R.id.valueBarHorizontal);
                horizontalBar.setClickable(false);
                horizontalBar.addOnScrollBarChangeListener(this);
                break;
            }
            case Constants.BLUE_HALL:
                //assign hall type here in order to animate raw properly

                cinemaLayout.setHallType(Constants.BLUE_HALL);
                drawBlueHall();
                verticalBar = (ValueBar) view.findViewById(R.id.valueBar);
                verticalBar.setClickable(false);
                verticalBar.addOnScrollBarChangeListener(this);
                horizontalBar = (ValueBar) view.findViewById(R.id.valueBarHorizontal);
                horizontalBar.setClickable(false);
                horizontalBar.addOnScrollBarChangeListener(this);
                break;

            case Constants.SILVER_HALL:
                //assign hall type here in order to animate raw properly

                cinemaLayout.setHallType(Constants.SILVER_HALL);
                drawSilverHall();
                verticalBar = (ValueBar) view.findViewById(R.id.valueBar);
                verticalBar.setClickable(false);
                verticalBar.addOnScrollBarChangeListener(this);
                horizontalBar = (ValueBar) view.findViewById(R.id.valueBarHorizontal);
                horizontalBar.setClickable(false);
                horizontalBar.addOnScrollBarChangeListener(this);
                break;
        }

        rootView = (RootView) view.findViewById(R.id.rl_fragment_root);

        zoomView = new ZoomView(getActivity()) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (MotionEvent.ACTION_DOWN == ev.getAction())
                    getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        };
        zoomView.setId(R.id.movie_zoom_view);
        ViewGroup.LayoutParams rlParams = cinemaLayout.getLayoutParams();
        zoomView.setLayoutParams(rlParams);

        rootView.removeView(cinemaLayout);
        zoomView.addView(cinemaLayout);
        rootView.addView(zoomView);

        rvBasket = (RecyclerView) view.findViewById(R.id.f_cinema_rv_ticket_basket);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapterTicketBasket = new AdapterTicketBasket(getActivity(), null, Constants.MOVIE_ACTIVITY);
        adapterTicketBasket.setRvRef(rvBasket);

        //align vertical bar according to cinemaLayout
        ViewTreeObserver observer = cinemaLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                //vertical params
                int cinemaLayoutHeight = cinemaLayout.getHeight();
                RelativeLayout.LayoutParams verticalBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, cinemaLayoutHeight);
                verticalBarParams.addRule(RelativeLayout.ALIGN_PARENT_START, zoomView.getId());
                verticalBarParams.addRule(RelativeLayout.ALIGN_LEFT, zoomView.getId());
                verticalBarParams.setMargins(0, 40, 0, 0);

                //horizontal params
                int cinemaLayoutWidth = cinemaLayout.getWidth();
                RelativeLayout.LayoutParams horizontalBarParams = new RelativeLayout.LayoutParams(cinemaLayoutWidth, 50);
                horizontalBarParams.addRule(RelativeLayout.BELOW, zoomView.getId());
                horizontalBarParams.addRule(RelativeLayout.ALIGN_END, zoomView.getId());

                verticalBar.setLayoutParamsManually(verticalBarParams, horizontalBarParams);
                horizontalBar.setLayoutParamsManually(verticalBarParams, horizontalBarParams);

                TableRow currentRaw;

                switch (HALL_TYPE) {
                    case Constants.RED_HALL:

                        for (int c = 0; c < cinemaLayout.getChildCount(); c++) {
                            currentRaw = (TableRow) cinemaLayout.getChildAt(c);
                            int rawTop = cinemaLayout.getChildAt(c).getTop();
                            int rawBottom = cinemaLayout.getChildAt(c).getBottom();
                            int rawCenter = rawTop + (rawBottom - rawTop) / 2;

                            // int rawSize = currentRaw.getChildCount();

                            for (int s = 0; s < currentRaw.getChildCount(); s++) {
                                int seatLeft = currentRaw.getChildAt(s).getLeft();
                                int seatRight = currentRaw.getChildAt(s).getRight();
                                int seatCenter = seatLeft + (seatRight - seatLeft) / 2;
                                PlaceView child = (PlaceView) currentRaw.getChildAt(s);
                                child.setxCoord(seatCenter);
                                //set Y coordinate only for the first seat in a raw
                                if (s == 0) child.setyCoord(rawCenter);
                            }
                        }
                        break;

                    case Constants.BLUE_HALL:
                        for (int c = 0; c < cinemaLayout.getChildCount(); c++) {
                            currentRaw = (TableRow) cinemaLayout.getChildAt(c);
                            int rawTop = cinemaLayout.getChildAt(c).getTop();
                            int rawBottom = cinemaLayout.getChildAt(c).getBottom();
                            int rawCenter = rawTop + (rawBottom - rawTop) / 2;

                            int rawSize = currentRaw.getChildCount();
                            PlaceView firstSeat;

                            firstSeatFound = false;
                            for (int s = 0; s < currentRaw.getChildCount(); s++) {
                                int seatLeft = currentRaw.getChildAt(s).getLeft();
                                int seatRight = currentRaw.getChildAt(s).getRight();
                                int seatCenter = seatLeft + (seatRight - seatLeft) / 2;
                                PlaceView child = (PlaceView) currentRaw.getChildAt(s);
                                child.setxCoord(seatCenter);

                                //set Y coordinate only for the first seat in a raw
                                if (!firstSeatFound) {
                                    if (child.getSeatId() != 0) {
                                        child.setyCoord(rawCenter);
                                        firstSeatFound = true;
                                    }
                                }
                            }
                        }
                        break;

                    case Constants.SILVER_HALL:

                        for (int c = 0; c < cinemaLayout.getChildCount(); c++) {
                            currentRaw = (TableRow) cinemaLayout.getChildAt(c);
                            int rawTop = cinemaLayout.getChildAt(c).getTop();
                            int rawBottom = cinemaLayout.getChildAt(c).getBottom();
                            int rawCenter = rawTop + (rawBottom - rawTop) / 2;

                            // int rawSize = currentRaw.getChildCount();

                            for (int s = 0; s < currentRaw.getChildCount(); s++) {
                                int seatLeft = currentRaw.getChildAt(s).getLeft();
                                int seatRight = currentRaw.getChildAt(s).getRight();
                                int seatCenter = seatLeft + (seatRight - seatLeft) / 2;
                                PlaceView child = (PlaceView) currentRaw.getChildAt(s);
                                child.setxCoord(seatCenter);
                                //set Y coordinate only for the first seat in a raw
                                if (s == 0) child.setyCoord(rawCenter);
                            }
                        }
                        break;
                }
                cinemaLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return view;
    }

    private void drawBlueHall() {
        cinemaLayout.createBlueCinemaHall(getActivity());
    }

    public void drawRedHall() {
        cinemaLayout.createRedCinemaHall(getActivity());
    }

    public void drawSilverHall() {
        cinemaLayout.createSilverCinemaHall(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        // initially getPreOrderList() is empty
        adapterTicketBasket.setSelectedSeats(((MovieActivity) getActivity()).getPreOrderList());
        rvBasket.setAdapter(adapterTicketBasket);
        rvBasket.setLayoutManager(layoutManager);

        touchHelperCallback = new SimpleTouchHelperCallback(adapterTicketBasket);
        itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvBasket);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Pref pref;
    RequestManager requestManager;

    //TODO: buttons are being developed..
    private View.OnClickListener bOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.f_cinema_b_buy:
                    controllerListener.onBuyButtonClicked();
                    break;

                case R.id.f_cinema_b_book_and_go_to_basket:
                    controllerListener.onBookButtonClicked();
                    break;

                case R.id.f_cinema_b_log_in:
                    controllerListener.onLogInButtonClicked();
                    break;
            }
        }
    };

    public void updateRvPreorder() {
        adapterTicketBasket.notifyDataSetChanged();
        /** new changes
         adapterTicketBasket.setSelectedSeats( ((MovieActivity) controllerListener).getPreOrderList()  );*/
    }

    private LinearLayout ll_container_buttons;

    public void undisguiseGoToBasketButton() {
        //Drawable drawableBasket = ContextCompat.getDrawable(getActivity(), R.drawable.bag);
        bBook = new Button(getActivity());
        bBook.setId(R.id.f_cinema_b_book_and_go_to_basket);

        bBook.setText("Забронювати");
        bBook.setTextSize(10);
        bBook.setOnClickListener(bOnClickListener);

        bBuy = new Button(getActivity());
        bBuy.setId(R.id.f_cinema_b_buy);

        bBuy.setText("Купити");
        bBuy.setTextSize(10);
        bBuy.setOnClickListener(bOnClickListener);

        ll_container_buttons = new LinearLayout(getActivity());
        ll_container_buttons.setId(R.id.f_cinema_ll_container_buttons);
        ll_container_buttons.addView(bBook);
        ll_container_buttons.addView(bBuy);

        RelativeLayout.LayoutParams ll_container_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_container_params.addRule(RelativeLayout.BELOW, R.id.f_cinema_rv_ticket_basket);
        ll_container_params.addRule(RelativeLayout.CENTER_IN_PARENT);

        ll_container_buttons.setLayoutParams(ll_container_params);

        rootView.addView(ll_container_buttons);

        rootView.requestLayout();
    }

    public void setContainerButtonsVisibility(int visibility) {
        switch (visibility) {
            case View.VISIBLE:
                if (bBook == null || bBuy == null)
                    undisguiseGoToBasketButton();
                ll_container_buttons.setVisibility(View.VISIBLE);
                break;
            case View.INVISIBLE:
                ll_container_buttons.setVisibility(View.INVISIBLE);
                //adapterTicketBasket.clearPreorderListUponSuccess();
                adapterTicketBasket.clearPreorderListUponSuccess();

                break;
        }
        rootView.requestLayout();
    }

    public void setButtonLogInVisibility(int visibility) {
        if (!pref.isUserLoggedIn()) {
            switch (visibility) {
                case View.VISIBLE:
                    if (bLogIn != null) {
                        bLogIn.setVisibility(View.VISIBLE);
                    } else {
                        createGoToLogInButton();
                    }
                    break;
                case View.INVISIBLE:
                    if (bLogIn != null)
                        bLogIn.setVisibility(View.INVISIBLE);
                    //adapterTicketBasket.clearPreorderListUponSuccess();
                    break;
            }
            rootView.requestLayout();
        }
    }

    public void createGoToLogInButton() {
        //Drawable drawableBasket = ContextCompat.getDrawable(getActivity(), R.drawable.bag);
        if (bLogIn == null) {
            bLogIn = new Button(getActivity());
            bLogIn.setId(R.id.f_cinema_b_log_in);

            bLogIn.setText("Увійти");
            bLogIn.setTextSize(10);
            bLogIn.setOnClickListener(bOnClickListener);

            RelativeLayout.LayoutParams b_login_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            b_login_params.addRule(RelativeLayout.BELOW, R.id.f_cinema_ll_container_buttons);
            b_login_params.addRule(RelativeLayout.CENTER_HORIZONTAL);

            bLogIn.setLayoutParams(b_login_params);

            rootView.addView(bLogIn);

            rootView.requestLayout();
        }
    }
}
