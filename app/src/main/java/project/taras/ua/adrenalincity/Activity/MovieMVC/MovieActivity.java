package project.taras.ua.adrenalincity.Activity.MovieMVC;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.HelperClasses.AppDatabase;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.HelperClasses.DrawerActivity;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.FirebaseManager;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.OrderManager;
import project.taras.ua.adrenalincity.Activity.Login.LoginActivity;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.PlaceView;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.FactoryAsync;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.IBookPayment;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderDao;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;
import project.taras.ua.adrenalincity.Activity.MyTicketsMVC.IMyTicket;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Seat;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import project.taras.ua.adrenalincity.R;

public class MovieActivity extends DrawerActivity implements ViewMovieMVC.OnViewMovieMVCListener,
        RequestManager.OnRequestManagerControllerListener, FragmentCinema.OnFragmentCinemaListener, IFirebaseListener.IRatingListener,
        IBookPayment.IBookPaymentListener, IMyTicket.PaymentBookValidationListener {

    private JSONObject jsonCurrentUser;

    private int moviePeriod;
    private Movie movieInf;
    private String showStartTime;

    private ViewMovieMVC viewMVC;
    private RequestManager requestManagerMVC;
    private OrderManager orderManager;
    private AppDatabase appDb;

    private List<Show> dailyShowList;
    private List<String> spinnerTimeList = new ArrayList<>();
    private LinkedHashMap<String, List<Show>> linkedHashMapShow;
    private List<String> timeShowList;

    private Pref pref;
    private FirebaseManager fbManager;
    private AdapterRvDate adapterDate;
    private AdapterRvTime adapterTime;

    private boolean firstSpinnerClick = true;
    private ArrayAdapter<String> adapterSpinner;

    @Override
    public void getShowScheduleList() {
    }

    private Show selectedShow;

    /**
     * for now the main purpose of this method is to return show information
     * in order to book or buy ticket
     */
    public Show getCurrentShow() {
        return selectedShow;
    }

    //redundant
    @Override
    public void onMovieRatingCalculated(float movieRating) {
    }

    @Override
    public void setAdapterToRvDate() {
        viewMVC.setAdapterToRvDate(adapterDate);
    }

    @Override
    public void updateAdapterDate() {
        if (linkedHashMapShow != null)
            adapterDate.setData(linkedHashMapShow);
    }

    @Override
    public void requestAvailableSeats(int showPosInSpinner) {
        /**request clicking on spinner with show schedule within it**/
        selectedShow = dailyShowList.get(showPosInSpinner);
        int selectedShowId = selectedShow.getShowId();

        requestManagerMVC.sendRequest(Constants.SHOW_AVAILABLE_SEATS_FOR_SHOW_ID_ + selectedShowId,
                Constants.TYPE_AVAILABLE_SEATS);
    }

    @Override
    public void responseAvailableSeats(List<Seat> listSeats) {
        viewMVC.passAvailableSeatsToFragmentCinema(listSeats);
        orderManager = new OrderManager(this, movieInf, selectedShow);
    }

    @Override
    public void onCinemaHallCreated() {
        viewMVC.undisguiseAllViews(Constants.TYPE_MOVIE_TODAY);
    }

   /* *//**
     * redraw cinema hall after place has been booked
     *//*
    @Override
    public void onBookSuccess() {
        int selectedShowId = selectedShow.getShowId();
        Log.v("tag_book_success", "showid- " + selectedShowId);
        requestManagerMVC.sendRequest(Constants.SHOW_AVAILABLE_SEATS_FOR_SHOW_ID_ + selectedShowId,
                Constants.TYPE_AVAILABLE_SEATS);

        //fbManager.saveBookedTicket(mapOrderInf);
        //now we can visit personal cabinet to check to see our list of booked tickets
    }*/


    OrderDao dao;
   /* private IAsync.ISelect selectListener = new IAsync.ISelect() {
        @Override
        public void doInBackground() {

        }

        @Override
        public void onPostExecute(OrderModel[] arrayOrderModel) {
            Log.v("keeptrack", "called");
            viewMVC.fragmentCinema.adapterTicketBasket.setData(Collections.synchronizedList(new LinkedList(Arrays.asList(arrayOrderModel))));
            *//*if (adapterTicketBasket.getItemCount() == 0){
                adapterIsEmpty();
            }*//*
            //Log.v("tag_sql", String.valueOf(arrayOrderModel.length));
            //Log.v("tag_sql", String.valueOf(arrayOrderModel[0].getDate()));
        }
    };*/

    private List<PlaceView> preOrderList = new ArrayList<>();

    @Override
    public void addSeatsToOrderList(PlaceView seat) {
        switch (seat.getStatus()) {
            case Constants.SEAT_BLOCKED:
                Toast.makeText(this, "Дане місце заброньоване, будь ласка оберіть інше",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case Constants.SEAT_BOOKED:
                Toast.makeText(this, "Дане місце заброньоване, будь ласка оберіть інше",
                        Toast.LENGTH_LONG)
                        .show();
                break;
            case Constants.SEAT_AVAILABLE:
                addSeatToPreorderList(seat);
                orderManager.addSeatToOrderList(seat);
               /* if (pref.isUserLoggedIn()) {
                    addSeatToPreorderList(seat);
                    orderManager.addSeatToOrderList(seat);
                    //viewMVC.updateBasketCounterInFragmentCinema(orderManager.getOrderList().size());
                } else {
                    Toast.makeText(this, "Для бронювання та купівлі квитків необхідно зареєструватися", Toast.LENGTH_LONG).show();
                }*/

                break;
        }
        Log.v("tag_book", "" + seat.getPrice() + " " + seat.getStatus());
    }

    private void addSeatToPreorderList(PlaceView seat) {
        if (preOrderList.size() < 4 && !preOrderList.contains(seat)) {
            Log.v("TAG_ORDER", "add");
            preOrderList.add(seat);
            seat.setBookedStatus(true);
            //seat.changeColorToBooked();
            viewMVC.updateRecyclerPreorder();
            viewMVC.fullScrollToTheBottom();
        }
    }

    //gets called upon dismiss action out of adapterTicketBasket in FragmentCinema
    public void removeOrderFromOrderList(PlaceView seat) {
        orderManager.removeOrderFromOrderList(seat);
    }

    public void clearPreOrderList() {
        this.preOrderList.clear();
    }

    public List<PlaceView> getPreOrderList() {
        return preOrderList;
    }

    @Override
    public void buyAllSeats(List<PlaceView> buyList) {
        //orderManager.buyTickets(buyList);
    }

    @Override
    public void createOrderFromSelectedSeats(List<PlaceView> setsList) {

    }

    /**
     * test book&PrivatBank section
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_movie);

        /**new changes */
        //orderManager = new OrderManager(this);
        fbManager = new FirebaseManager(this);
        appDb = AppDatabase.getInstance(this);
        dao = appDb.orderDao();

        pref = Pref.getInstance(this);
        if (pref.isUserLoggedIn())
            jsonCurrentUser = pref.getCurrentUser();

        Bundle bundleMovieInf = getIntent().getExtras().getBundle("clicked_movie");
        movieInf = (Movie) bundleMovieInf.get("movie");
        moviePeriod = bundleMovieInf.getInt("moviePeriod");
        showStartTime = bundleMovieInf.getString("showStartTime");

        Log.v("TAG_FILMINS", "movieId " + movieInf.getMovieId());

        switch (moviePeriod) {
            case Constants.TYPE_MOVIE_TODAY:
                Log.v("click_on_m", "today");
                String gsonString = bundleMovieInf.getString("gsonLinkedHashMap");
                Gson gson = new Gson();
                Type entityType = new TypeToken<LinkedHashMap<String, List<Show>>>() {
                }.getType();
                linkedHashMapShow = gson.fromJson(gsonString, entityType);
                //Log.v("click_on_m", linkedHashMapShow.get());
                initViewMVCforMovieToday();
                break;
            case Constants.TYPE_MOVIE_SOON:
                initViewMVCforMovieSoon();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (moviePeriod) {
            case Constants.TYPE_MOVIE_TODAY:
                viewMVC.initRecyclerViewDate();
                viewMVC.initRecyclerViewTime();
                viewMVC.setRvTimeAdapter(adapterTime);
                break;
            case Constants.TYPE_MOVIE_SOON:
                break;
        }
    }

    private void initViewMVCforMovieToday() {
        adapterDate = new AdapterRvDate(this);
        adapterDate.setOnDateClickListener(adapterClickListener);
        adapterTime = new AdapterRvTime(this);
        adapterTime.setOnTimeClickListener(adapterClickListener);

        adapterSpinner = new ArrayAdapter<>(this, R.layout.custom_time_layout, spinnerTimeList);

        viewMVC = new ViewMovieMVC(this, movieInf, Constants.TYPE_MOVIE_TODAY);
        if (linkedHashMapShow == null) {
            viewMVC.hideScheduleDivider();
        }

        viewMVC.setOnViewMovieMVCListener(this);
        viewMVC.setToolbarClickListener(toolbarClickListener);
        /*if (pref.isUserLoggedIn())
            viewMVC.setCompoundViewVoteClickListener(bVoteClickListener);*/
        viewMVC.setOnRvDateLayoutChangeListener(onRvDateChangeListener);

        requestManagerMVC = RequestManager.getInstance(this);
        requestManagerMVC.setOnRequestManagerControllerListener(this);

        //fbManager = new FirebaseManager(this);
        //fbManager.setBookValidationListener(this);
        //fbManager.setMovieRatingListener(this);
        //fbManager.getMovieRating(movieInf);
    }

    private void initViewMVCforMovieSoon() {
        viewMVC = new ViewMovieMVC(this, movieInf, Constants.TYPE_MOVIE_SOON);
        viewMVC.setToolbarClickListener(toolbarClickListener);
    }

    private IAdapterListener.IAdapterClickListener adapterClickListener = new IAdapterListener.IAdapterClickListener() {
        @Override
        public void onDateClicked(String keyDay, int position) {
            Log.v("adapterclick", "" + position);
            Log.v("perform_click", keyDay);
            //create show time list
            clearPreOrderList();
            fillSpinnerTimeList(keyDay, position);
            adapterTime.setFirstClickStatus(false);
            adapterTime.setData(spinnerTimeList);
        }

        @Override
        public void onTimeClicked(int position) {
            Log.v("time_click", "selected " + position);
            clearPreOrderList();
            timePositionToClickOn = 0;
            //isn't appropriate way to open up this fragment
            /**based on .hallId from show object lets create
             * either FragmentCinema with redHall or another one **/
            Show selectedShow = dailyShowList.get(position);
            int hallId = selectedShow.getHallId();
            //Log.v("spinner_click", "show_id " + selectedShow.getShowId() + "hall_id " + selectedShow.getHallId());

            viewMVC.createOrUpdateCinemaHall(hallId, position);
        }

        @Override
        public void onTimeAdapterIsReady() {
            viewMVC.performTimeClick(timePositionToClickOn);
            showStartTime = null;
        }
    };

    private View.OnClickListener toolbarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    int timePositionToClickOn = 0;

    private void fillSpinnerTimeList(String keyDay, int position) {
        if (spinnerTimeList.size() > 0) {
            spinnerTimeList.clear();
        }
        dailyShowList = linkedHashMapShow.get(keyDay);
        for (Show s : dailyShowList) {
            Log.v("time_tag", s.getTime());
            /** define 'showStartTime' position to pass it over to 'performDateClick()' **/
            if (showStartTime != null) {
                if (s.getTime().equalsIgnoreCase(showStartTime)) {
                    Log.v("time_tag", s.getTime() + " == " + showStartTime);
                    timePositionToClickOn = dailyShowList.indexOf(s);
                    Log.v("time_tag", "timePositionToClickOn " + timePositionToClickOn);
                }
            }
            spinnerTimeList.add(s.getTime());
        }
    }

    boolean isReady = false;

    private View.OnLayoutChangeListener onRvDateChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            switch (moviePeriod) {
                case Constants.TYPE_MOVIE_TODAY:
                    break;
            }
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("d", Locale.getDefault());
            String today = format.format(date);

            if (right == oldRight && !isReady) {
                boolean todayIsFound = false;
                isReady = true;
                if (adapterDate.getKeyListOfDays() != null) {
                    for (String day : adapterDate.getKeyListOfDays()) {
                        if (today.equalsIgnoreCase(day) && !todayIsFound) {
                            viewMVC.performDateClick(0);
                            todayIsFound = true;
                        } else {
                            viewMVC.undisguiseAllViews(Constants.TYPE_MOVIE_TODAY);
                        }
                    }
                }
            }
        }
    };

    //adapter is in fragmentCinema
    public void adapterPreOrderIsSet() {
        viewMVC.fragmentCinema.undisguiseGoToBasketButton();
    }

    public void preOrderAdapterIsEmpty() {
        viewMVC.fragmentCinema.setContainerButtonsVisibility(View.INVISIBLE);
        viewMVC.fragmentCinema.setButtonLogInVisibility(View.INVISIBLE);
    }

    ////////////

    private int ORDER_ACTION;

    @Override
    public void onBuyButtonClicked() {
        this.ORDER_ACTION = Constants.BUY_ORDER;
        if (pref.isUserLoggedIn())
            createOrder(iOrderManager);
        else {
            viewMVC.changeLoginIconColor();
            viewMVC.fragmentCinema.setButtonLogInVisibility(View.VISIBLE);
            viewMVC.fullScrollToTheBottom();
            Toast.makeText(this, "Для бронювання та купівлі квитків необхідно зареєструватися", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBookButtonClicked() {
        this.ORDER_ACTION = Constants.BOOK_ORDER;
        if (pref.isUserLoggedIn())
            createOrder(iOrderManager);
        else {
            viewMVC.changeLoginIconColor();
            viewMVC.fragmentCinema.setButtonLogInVisibility(View.VISIBLE);
            viewMVC.fullScrollToTheBottom();
            Toast.makeText(this, "Для бронювання та купівлі квитків необхідно зареєструватися", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOrderDismiss(Map<String, String> map_book_to_dismiss) {
        //redundant
    }

    @Override
    public void onLogInButtonClicked() {
        Intent iLogIn = new Intent(getApplicationContext(), LoginActivity.class);
        //iLogIn.putExtra("FROM", "MovieActivity");
        iLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(iLogIn);
    }

    @Override
    public void createOrder(IOrderManager om) {
        // add all tickets together and create new order. This order is then stored in SQLite from which
        // we fetch order in MyBasket Activity
        orderManager.createOrderFromFilledList(om);
    }

    IOrderManager iOrderManager = new IOrderManager() {
        @Override
        public void onOrderSuccessfullyAddedToSQL(OrderModel[] modelArray) {
            viewMVC.fragmentCinema.adapterTicketBasket.setData(Collections.synchronizedList(new LinkedList(Arrays.asList(modelArray))));
        }

        @Override
        public void onLimitExceeded(int exceededNumberOfOrders) {
            //for(int i = 0; i < pre
            //viewMVC.fragmentCinema.adapterTicketBasket.onDismiss();
            Toast.makeText(MovieActivity.this, "Вибачте, ліміт бронювання для даного фільму перевищено, видаліть " + exceededNumberOfOrders, Toast.LENGTH_LONG)
                    .show();
        }
    };

    public void dataForBookIsSet() {
        switch (ORDER_ACTION) {
            case Constants.BOOK_ORDER:
                orderManager.validateBookAttempt(viewMVC.fragmentCinema.adapterTicketBasket.getData());
                break;
            case Constants.BUY_ORDER:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        orderManager.payForTicketsWithLiqPay(viewMVC.fragmentCinema.adapterTicketBasket.getData());
                    }
                }).start();
                break;
        }
    }

    /**
     * validation callbacks from fbManager
     * to be convinced that user doesn't exceed booking limit
     */

    @Override
    public void onBookAllowed() {
        //Toast.makeText(this, "You are allowed to book tickets", Toast.LENGTH_LONG).show();
        viewMVC.displayBookBeingProcessed(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    orderManager.bookTickets();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBookRejected(int code, int params) {
        switch (code) {
            case Constants.EXCEEDING_ORDER_LIMIT:
                viewMVC.fragmentCinema.adapterTicketBasket.clear_all_selected_seats();
                /*viewMVC.fragmentCinema.adapterTicketBasket.getData().clear();

                viewMVC.fragmentCinema.adapterTicketBasket.clear_all_selected_seats();
                *//*viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                msg.arg1 = Constants.CLEAR_PREORDER_LIST;
                msg.sendToTarget();*//*

                Log.v("TAG_ORDER", "listSelSeats size "+ preOrderList.size());
                //viewMVC.fragmentCinema.adapterTicketBasket.clear_all_selected_seats();


                //viewMVC.fragmentCinema.adapterTicketBasket.getData().clear();*/
                Toast.makeText(this, "Вибачте, ліміт бронювання перевииищено", Toast.LENGTH_LONG).show();
                break;
            //probably not gonna be called due to absence of book button in BasketActivity
            case Constants.ALREADY_BOOKED_ORDER:
                viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                Toast.makeText(this, "Order is already booked", Toast.LENGTH_LONG).show();
                break;
            case Constants.DIFFERENT_SHOWS_ON_THE_SAME_MOVIE:
                viewMVC.fragmentCinema.adapterTicketBasket.clear_all_selected_seats();
                //TODO: clean up seats status to set green color back
                Toast.makeText(this, "Вибачте, Ви не можете бронювати квитки на різні сеанси одного фільму", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @Override
    public void onSuccessfulOrderAction(int requestType) {
        synchronized (orderManager) {
            orderManager.orderCounter++;
            orderManager.orderIsBeingProcessed = false;
            orderManager.notify();

            //potential collapse

            if (orderManager.orderCounter == orderManager.getOrdersToProceedWith().size()) {
                switch (requestType) {
                    case Constants.BUY_TICKETS:
                        //send all paid orders to Firebase to make up tickets
                        savingEticketsToFirebase = true;
                        orderManager.fbManager.savePaidOrders(viewMVC.fragmentCinema.adapterTicketBasket.getData());

                        //clean up all paid orders from RecyclerView
                        Message msgBuy = handler.obtainMessage();
                        msgBuy.arg1 = Constants.BUY_TICKETS;
                        msgBuy.sendToTarget();
                        break;
                    case Constants.BOOK_TICKETS:
                        fbManager.saveBookedTicket(orderManager.getOrdersToProceedWith());

                        Message msgBook = handler.obtainMessage();
                        msgBook.arg1 = Constants.BOOK_TICKETS;
                        msgBook.sendToTarget();
                        break;
                }
            }
        }
    }

    private boolean savingEticketsToFirebase = false;

    @Override
    public void onSuccessfulEticketsSaving() {
        this.savingEticketsToFirebase = false;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constants.BUY_TICKETS:
                    viewMVC.fragmentCinema.adapterTicketBasket.markOrdersAsPaid();
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), viewMVC.fragmentCinema.adapterTicketBasket.getDataAsArray());

                    viewMVC.fragmentCinema.adapterTicketBasket.getData().clear();
                    viewMVC.fragmentCinema.adapterTicketBasket.notifyDataSetChanged();
                    viewMVC.displayPaymentBeingProcessed(View.INVISIBLE);
                    break;
                case Constants.BOOK_TICKETS:
                    /** new changes */
                    viewMVC.fragmentCinema.adapterTicketBasket.markOrdersAsBooked();
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), viewMVC.fragmentCinema.adapterTicketBasket.getDataAsArray());
                    viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (orderManager != null) {
            synchronized (orderManager) {
                if (orderManager.orderIsBeingProcessed) {
                    Toast.makeText(this, "Бронюю, будь ласка зачекайте", Toast.LENGTH_LONG).show();
                } else if (savingEticketsToFirebase) {
                    Toast.makeText(this, "Зберігаю електронні квитки, будь ласка зачекайте", Toast.LENGTH_LONG).show();
                } else {
                    //TODO: remove YouTube fragment along with cinema hall before going back to the Main activity
                    viewMVC.removeYouTubeFragment();
                    viewMVC.setOpenStatus(viewMVC.b_info, Constants.CLOSE);
                    viewMVC.setOpenStatus(viewMVC.b_trailer, Constants.CLOSE);
                    super.onBackPressed();
                }
            }
        } else {
            viewMVC.setOpenStatus(viewMVC.b_info, Constants.CLOSE);
            viewMVC.setOpenStatus(viewMVC.b_trailer, Constants.CLOSE);
            super.onBackPressed();
        }
    }

    public void onBackPressedFromLiqPay() {
        viewMVC.displayPaymentBeingProcessed(View.INVISIBLE);
    }

    private String getCurrentUser() {
        JSONObject jsonUser = pref.getCurrentUser();
        try {
            return jsonUser.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    /*///////////////

    private boolean savingEticketsToFirebase = false;

    public OrderManager orderManager;
    private FirebaseManager fbManager;
    private AppDatabase appDb;


    *//**
     * validation callbacks from fbManager
     * to be convinced that user doesn't exceed booking limit
     *//*
    @Override
    public void onBookAllowed() {
        Toast.makeText(this, "You are allowed to book tickets", Toast.LENGTH_LONG).show();
        //viewMVC.displayBookBeingProcessed(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    orderManager.bookTickets();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBookRejected(int code, int params) {

    }

    @Override
    public void onSuccessfulOrderAction(int requestType) {
        synchronized (orderManager) {
            orderManager.orderCounter++;
            orderManager.orderIsBeingProcessed = false;
            Log.v("tag_thread", "order completed!");
            Log.v("tag_thread", "order counter - " + orderManager.orderCounter);
            orderManager.notify();

            //potential collapse
            Log.v("payment_tag", "ordersToProccedWith.size() - " + orderManager.getOrdersToProceedWith().size());
            Log.v("payment_tag", "orderManager.orderCounter - " + orderManager.orderCounter);
            if (orderManager.orderCounter == orderManager.getOrdersToProceedWith().size()) {
                switch (requestType) {
                    case Constants.BUY_TICKETS:
                        //send all paid orders to Firebase to make up tickets
                        savingEticketsToFirebase = true;
                        *//** new changes *//*
                        fbManager.savePaidOrders(viewMVC.fragmentCinema.adapterTicketBasket.getData());
                        //fbManager.savePaidOrders(adapterTicketBasket.getData());

                        //clean up all paid orders from RecyclerView
                        Message msgBuy = handler.obtainMessage();
                        msgBuy.arg1 = Constants.BUY_TICKETS;
                        msgBuy.sendToTarget();

                        break;
                    case Constants.BOOK_TICKETS:
                        fbManager.saveBookedTicket(orderManager.getOrdersToProceedWith());

                        Message msgBook = handler.obtainMessage();
                        msgBook.arg1 = Constants.BOOK_TICKETS;
                        msgBook.sendToTarget();
                        break;
                }
            }
        }
    }

   /* Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constants.BUY_TICKETS:
                    adapterTicketBasket.markOrdersAsPaid();
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), adapterTicketBasket.getDataAsArray());

                    adapterTicketBasket.getData().clear();
                    adapterTicketBasket.notifyDataSetChanged();
                    viewMVC.displayPaymentBeingProcessed(View.INVISIBLE);
                    break;
                case Constants.BOOK_TICKETS:
                    *//**//** new changes *//**//*
                    //adapterTicketBasket.markOrdersAsBooked();
                    viewMVC.fragmentCinema.adapterTicketBasket.markOrdersAsBooked();
                    //FactoryAsync.setOrdersStatus(appDb.orderDao(), adapterTicketBasket.getDataAsArray());
                    FactoryAsync.setOrdersStatus(appDb.orderDao(), viewMVC.fragmentCinema.adapterTicketBasket.getDataAsArray());
                    //viewMVC.displayBookBeingProcessed(View.INVISIBLE);
                    break;
            }
        }
    };

    private String getCurrentUser() {
        JSONObject jsonUser = pref.getCurrentUser();
        try {
            return jsonUser.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*//*
*/
}
