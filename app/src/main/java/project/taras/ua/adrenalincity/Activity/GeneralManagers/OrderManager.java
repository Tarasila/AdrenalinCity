package project.taras.ua.adrenalincity.Activity.GeneralManagers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import project.taras.ua.adrenalincity.Activity.HelperClasses.AppDatabase;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MovieMVC.IOrderManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieActivity;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.PlaceView;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.BasketActivity;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.FactoryAsync;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.IAsync;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.IBookPayment;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;
import project.taras.ua.adrenalincity.Activity.MyTicketsMVC.IMyTicket;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;
import ua.privatbank.paylibliqpay.ErrorCode;
import ua.privatbank.paylibliqpay.LiqPay;
import ua.privatbank.paylibliqpay.LiqPayCallBack;

/**
 * Created by Taras on 25.05.2017.
 */

public class OrderManager {

    private JSONObject jsonUser;
    private String userName;

    private Context context;
    private Activity activity;
    private static Map<String, Object> mapBook;
    private List<OrderModel> ordersToProceedWith = new ArrayList<>();

    private Movie movieInf;
    private Show selectedShow;

    private Pref pref;
    public FirebaseManager fbManager;
    private RequestManager requestManager;

    private AppDatabase appDb;
    private OrderModel order;

    public OrderManager(Context context) {
        Log.v("om_t", "simple");
        this.activity = (Activity) context;
        this.context = context;
        init(context);
    }

    public OrderManager(Context context, Movie movieInf, Show selectedShow) {
        Log.v("om_t", "complex");
        this.context = context;
        this.movieInf = movieInf;
        this.selectedShow = selectedShow;
        init(context);
    }

    private void init(Context context) {
        pref = Pref.getInstance(context);
        appDb = AppDatabase.getInstance(context);
        fbManager = new FirebaseManager(context);
        //fbManager = ((IMyTicket.PaymentBookValidationListener) context).getFirebaseManager();

        /** new changes **/
        requestManager = RequestManager.getInstance((Activity) context);

        //context passed to the 'requestManager' | 'fbManager' can be whether from MyBasket or Movie activity
        if (context.getClass().getSimpleName().equalsIgnoreCase("MovieActivity"))
            fbManager.setBookValidationListener((IMyTicket.PaymentBookValidationListener) context);

        requestManager.setOnMyBookPaymentListener((IBookPayment.IBookPaymentListener) context);
    }


    /**
     * 1
     **/
    public void createOrderFromSelectedSeats(List<PlaceView> seatsList) {
        OrderModel[] orderArray = new OrderModel[seatsList.size()];

        JSONObject jsonUser = pref.getCurrentUser();
        PlaceView seat;
        for (int i = 0; i < seatsList.size(); i++) {
            order = new OrderModel();
            try {
                seat = seatsList.get(i);

                order.setId(Integer.parseInt(generateOrderId() + i));
                order.setUserName(jsonUser.getString("username"));
                order.setUserDbId(jsonUser.getString("useridadrenalin"));
                order.setSeatId(String.valueOf(seat.getRealId()));
                order.setSeatRaw(String.valueOf(seat.getRaw()));
                order.setSeatPlace(String.valueOf(seat.getPlace()));
                order.setSeatPrice(String.valueOf(seat.getPrice()));
                order.setCtShowId(String.valueOf(selectedShow.getShowId()));
                order.setShowTime(String.valueOf(selectedShow.getTime()));
                order.setCalendarDay(String.valueOf(selectedShow.getCalendarDate()));
                Log.v("showday", "" + selectedShow.getCalendarDate());
                order.setMovieId(String.valueOf(selectedShow.getMovieId()));
                order.setDate(String.valueOf(selectedShow.getShowDate()));
                order.setDbShowId(String.valueOf(selectedShow.getDatabaseId()));
                order.setHall(String.valueOf(selectedShow.getHallId()));
                order.setMoviePoster(movieInf.getImgUrl());
                order.setMovieTitle(movieInf.getTitle());
                order.setStatus("notPaid");
                order.setIsConfirmed(1);

                orderArray[i] = order;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        FactoryAsync.createInsertExecutor(appDb.orderDao(), orderArray);
    }

    private boolean differentShowsException = false;

    public void validateBookAttempt(List<OrderModel> orders) {
        Log.v("order_b_val_click", "click");
        differentShowsException = false;
        OrderModel movieToCompareWith;
        for (int i = 0; i < orders.size(); i++) {
            movieToCompareWith = orders.get(i);
            if (differentShowsException)
                return;
            for (int j = i + 1; j < orders.size(); j++) {
                OrderModel movie = orders.get(j);
                if (movieToCompareWith.getMovieId().equalsIgnoreCase(movie.getMovieId())) {
                    if (!movieToCompareWith.getCtShowId().equalsIgnoreCase(movie.getCtShowId())) {
                        differentShowsException = true;
                        /**new changes */
                        ((MovieActivity) context).onBookRejected(Constants.DIFFERENT_SHOWS_ON_THE_SAME_MOVIE, 0);
                        //((BasketActivity) context).onBookRejected(Constants.DIFFERENT_SHOWS_ON_THE_SAME_MOVIE, 0);
                        return;
                    } else {
                        Log.v("order_b_val", "allowed");
                    }
                } else {
                    Log.v("order_b_val", "dif movies allowed");
                }
            }
        }

        if (ordersToProceedWith.size() != 0) {
            ordersToProceedWith.clear();
        }
        if (!differentShowsException) {
            for (OrderModel orderToValidate : orders) {
                if (!orderToValidate.getStatus().equalsIgnoreCase("booked")) {
                    ordersToProceedWith.add(orderToValidate);
                    Log.v("order_val", "this order is allowed - " + orderToValidate.getSeatPlace());
                } else {
                    //continue;
                    Log.e("order_val", "this order is already booked - " + orderToValidate.getSeatPlace());
                }
            }
            if (ordersToProceedWith.size() != 0) {
                fbManager.validateBookAttempt(ordersToProceedWith);
                Log.v("dfvd", "rf");
            } else {

                /** new changes */
                ((MovieActivity) context).onBookRejected(Constants.ALREADY_BOOKED_ORDER, 0);
                //((BasketActivity) context).onBookRejected(Constants.ALREADY_BOOKED_ORDER, 0);
            }
        }
    }

    public void bookTickets() throws InterruptedException {
        for (OrderModel order : ordersToProceedWith) {
            synchronized (OrderManager.this) {
                Log.v("tag_volley", String.valueOf(order.getSeatPlace()));
                sendBookConfirmationToServer(createBookMap(order));
                while (orderIsBeingProcessed) {
                    wait();
                }
            }
        }
    }

    //returns all orders passed validation process
    public List<OrderModel> getOrdersToProceedWith() {
        return this.ordersToProceedWith;
    }

    private void sendBookConfirmationToServer(Map<String, String> bookConfirmation) {
        orderIsBeingProcessed = true;
        requestManager.sendRequest(Constants.BOOK_SEAT, Constants.TYPE_BOOK_SEAT, bookConfirmation);
    }

    private Map<String, String> createBookMap(OrderModel order) {
        Map<String, String> mapBook = new HashMap<>();
        mapBook.put("title", order.getMovieTitle());
        mapBook.put("seats", order.getSeatId());
        mapBook.put("orderId", String.valueOf(order.getId()));
        mapBook.put("userId", order.getUserDbId());
        mapBook.put("userName", order.getUserName());
        mapBook.put("showId", order.getDbShowId());
        mapBook.put("showTime", order.getShowTime());
        mapBook.put("movieId", order.getMovieId());
        mapBook.put("date", order.getDate());
        mapBook.put("ctShowId", order.getCtShowId());
        mapBook.put("operation", "reserve");
        mapBook.put("isConfirmed", "1");
        mapBook.put("hall", order.getHall());
        mapBook.put("total", order.getSeatPrice());

        return mapBook;
    }

    public void payForTicketsWithLiqPay(final List<OrderModel> listOrders) {
        ordersToProceedWith = listOrders;

        String amountToPay = calculateAmountToPay(listOrders);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("version", "3");
        map.put("public_key", publicKey);
        map.put("action", "pay");
        //fill in real amount
        //map.put("amount", (String) mapOrder.get("totalCost"));
        map.put("amount", amountToPay);
        //map.put("amount", (String) mapOrder.get("totalCost"));
        map.put("currency", "UAH");
        map.put("description", "account top-up");
        //always has to be unique
        //map.put("order_id", (String) mapOrder.get("orderId"));
        map.put("order_id", String.valueOf(listOrders.get(0).getId()));
        map.put("language", "uk");
        //get rid of this field
        //map.put("sandbox", "1");
        map.put("server_url", "http://cinema.adrenalin.lutsk.ua/api/order/status");
        LiqPay.checkout(context, map, privateKey, new LiqPayCallBack() {
            @Override
            public void onResponseSuccess(String s) {
                Log.v("tag_liqpay", s);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            payForOrders(ordersToProceedWith);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onResponceError(ErrorCode errorCode) {
                Log.v("tag_liqpay", errorCode.toString());
                if (errorCode.toString().equalsIgnoreCase("checkout_canseled")) {
                    // create similar for MovieActivity
                    if (context.getClass().getSimpleName().equalsIgnoreCase("BasketActivity")) {
                        ((BasketActivity) context).onBackPressedFromLiqPay();
                    } else
                        ((MovieActivity) context).onBackPressedFromLiqPay();
                }
            }
        });
    }

    public void payForOrders(List<OrderModel> orders) throws InterruptedException {
        for (OrderModel order : orders) {
            synchronized (OrderManager.this) {
                sendPaymentConfirmationToServer(createPaymentConfirmation(order));
                while (orderIsBeingProcessed) {
                    wait();
                }
            }
        }
    }

    private void sendPaymentConfirmationToServer(Map<String, String> paymentConfirmation) {
        Log.v("tag_payment", "sendPaymentConfirmationToServer");
        orderIsBeingProcessed = true;
        requestManager.sendRequest(Constants.MAKE_ORDER, Constants.TYPE_MAKE_ORDER, paymentConfirmation);
    }

    private Map<String, String> createPaymentConfirmation(OrderModel currentOrder) {
        Map<String, String> map = new HashMap<>();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        try {
            //String userName = (String) mapOrder.get("userName");
            String userName = currentOrder.getUserName();
            String showId = currentOrder.getCtShowId();
            String showTime = currentOrder.getShowTime().replace(':', '-');

            jsonArray.put(showId);

            String seatId = currentOrder.getSeatId();

            jsonObject = new JSONObject();
            jsonObject.put(seatId, "-ОПЛАТА-"
                    + userName
                    + ";#"
                    + generatePassword(showTime));
            jsonArray.put(jsonObject);

            map.put("json", jsonArray.toString());
            Log.v("tag_order", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }

    private String calculateAmountToPay(List<OrderModel> boughtSeatsList) {
        int totalAmount = 0;
        for (OrderModel seat : boughtSeatsList) {
            totalAmount += Integer.parseInt(seat.getSeatPrice());
        }
        return String.valueOf(totalAmount);
    }

    private String generateOrderId() {
        Date orderTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hhmmss", Locale.getDefault());
        String orderId = dateFormat.format(orderTime);
        return orderId;
    }

    private String generatePassword(String firstPart) {
        StringBuilder password = new StringBuilder();
        password.append(firstPart);
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0 || i == 0)
                password.append("-");
            password.append(String.valueOf(random.nextInt(9)));
        }
        return password.toString();
    }

    public static Map<String, Object> getOrder() {
        return mapBook;
    }

    /************* ************/
    public boolean orderIsBeingProcessed = false;
    public int orderCounter = 0;
    List<PlaceView> orderList = new ArrayList<>();

    //TODO: if pay order was canceled clean orderList...

    public void addSeatToOrderList(PlaceView seat) {
        if (orderList.size() < 4 && !orderList.contains(seat)) {
            orderList.add(seat);
            seat.setBookedStatus(true);
            //seat.changeColorToBooked();
            //jjj
        }
        //else
        // you exceed your limit
    }

    public void removeOrderFromOrderList(PlaceView seat) {
        orderList.remove(seat);
    }

    public List<PlaceView> getOrderList() {
        return orderList;
    }

    public void createOrderFromFilledList(final IOrderManager iOrderManager) {
        final OrderModel[] orderArray = new OrderModel[orderList.size()];

        try {
            jsonUser = pref.getCurrentUser();
            userName = jsonUser.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PlaceView seat;
        for (int i = 0; i < orderList.size(); i++) {
            order = new OrderModel();
            try {
                seat = orderList.get(i);

                order.setId(seat.getRealId());
                order.setUserName(userName);
                order.setUserDbId(jsonUser.getString("useridadrenalin"));
                order.setSeatId(String.valueOf(seat.getRealId()));
                order.setSeatRaw(String.valueOf(seat.getRaw()));
                order.setSeatPlace(String.valueOf(seat.getPlace()));
                order.setSeatPrice(String.valueOf(seat.getPrice()));
                order.setCtShowId(String.valueOf(selectedShow.getShowId()));
                order.setShowTime(String.valueOf(selectedShow.getTime()));
                order.setMovieId(String.valueOf(selectedShow.getMovieId()));
                order.setCalendarDay(String.valueOf(selectedShow.getCalendarDate()));
                order.setDate(String.valueOf(selectedShow.getShowDate()));
                order.setDbShowId(String.valueOf(selectedShow.getDatabaseId()));
                order.setHall(String.valueOf(selectedShow.getHallId()));
                order.setMoviePoster(movieInf.getImgUrl());
                order.setMovieTitle(movieInf.getTitle());
                order.setStatus("notPaid");
                order.setIsConfirmed(1);
                order.setPurse(movieInf.getPurse());

                orderArray[i] = order;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        FactoryAsync.setSelectInterface(new IAsync.ISelect() {
            @Override
            public void doInBackground() {

            }

            @Override
            public void onPostExecute(OrderModel[] arrayOrderModel) {
                if (arrayOrderModel.length + orderArray.length < 4) {
                    Log.v("length_", "" + arrayOrderModel.length);
                    Log.v("length_", "" + orderArray.length);
                    FactoryAsync.createInsertExecutor(appDb.orderDao(), orderArray);

                    //orderArray - it's just created array of tickets waiting to be booked
                    iOrderManager.onOrderSuccessfullyAddedToSQL(orderArray);
                } else {
                    int exceededLimit = arrayOrderModel.length + orderArray.length - 3;
                    iOrderManager.onLimitExceeded(exceededLimit);
                }
            }
        });
        FactoryAsync.selectNotPaidOrders(userName, appDb.orderDao(), new Date(), "paid");
    }
}
