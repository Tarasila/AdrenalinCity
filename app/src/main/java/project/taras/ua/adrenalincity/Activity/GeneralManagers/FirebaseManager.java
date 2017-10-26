package project.taras.ua.adrenalincity.Activity.GeneralManagers;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MovieMVC.IFirebaseListener;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;
import project.taras.ua.adrenalincity.Activity.MyTicketsMVC.IMyTicket;
import project.taras.ua.adrenalincity.Activity.HelperClasses.Pref;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;

/**
 * Created by Taras on 05.05.2017.
 */

public class FirebaseManager {

    private Pref pref;
    private String userName = null;
    private String date;

    private DatabaseReference refRoot;
    private DatabaseReference refTickets;
    private DatabaseReference refUserTickets;
    private DatabaseReference refUserBook;
    private DatabaseReference refMovies;

    private IMyTicket.FirebaseListener controller_listener;
    private IMyTicket.PaymentBookValidationListener my_payment_book_listener;
    private IFirebaseListener.IRatingListener controllerMovieActivity;

    private  float movieRating;
    private int numberOfOrders;
    private volatile String showId;

    public FirebaseManager(Context context) {
        initDay();
        pref = Pref.getInstance(context);
        if (pref.isUserLoggedIn())
            getCurrentUser();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refTickets = refRoot.child("ticket");
        if (pref.isUserLoggedIn()) {
            refUserTickets = refRoot.child("ticket").child(userName);
            refUserBook = refRoot.child("book").child(userName);
        }
        refMovies = refRoot.child("movies");

        // ?
        //setBookValidationListener((IMyTicket.BookValidationListener) context);
    }

    private void initDay(){
        Date bookDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        this.date = format.format(bookDate);
    }

    private void getCurrentUser() {
        JSONObject jsonUser = pref.getCurrentUser();
        try {
            userName = jsonUser.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setBookValidationListener(IMyTicket.PaymentBookValidationListener listener) {
        this.my_payment_book_listener = listener;
    }

    public void setFirebaseListener(IMyTicket.FirebaseListener listener) {
        this.controller_listener = listener;
    }

    public void setMovieRatingListener(IFirebaseListener.IRatingListener controllerMovieActivity) {
        this.controllerMovieActivity = controllerMovieActivity;
    }

    public void saveBookedTicket(List<OrderModel> bookedTicket) {
        for (OrderModel bookOrder : bookedTicket){
            OrderModel orderModel = bookedTicket.get(0);
            refUserBook.child(date).child(orderModel.getMovieId()).child(orderModel.getCtShowId()).push().setValue(bookOrder);
            Log.v("t_push", ""+bookOrder.getSeatPlace());
        }
    }

    public void savePaidOrders(List<OrderModel> orders) {
        //String movieTitle = bookedTicket.get("title");
        for (OrderModel order : orders){
            refUserTickets.push().setValue(order);
        }
        //notify controller on successful result to allow user to press back button
        /** new changes */
        my_payment_book_listener.onSuccessfulEticketsSaving();
    }

    public void getMyTickets() {
        refTickets.addChildEventListener(ticketListener);
        refTickets.addListenerForSingleValueEvent(singleValueEventListener);
    }

    OrderModel orderModel;

    public void validateBookAttempt(List<OrderModel> listOrders) {
        Log.v("fb_val", ""+listOrders.size());
        orderModel = listOrders.get(0);
        this.numberOfOrders = listOrders.size();
        this.showId = orderModel.getCtShowId();
        //this.showId = "130398";
        Log.v("fb_b_val", "showId " + listOrders.get(0).getCtShowId());
        //refUserBook.child(date).child(orderModel.getMovieId()).child(orderModel.getCtShowId()).addListenerForSingleValueEvent(bookCounterListener);
        refUserBook.child(date).child(orderModel.getMovieId()).addListenerForSingleValueEvent(singleShowIdListener);
    }

    private ValueEventListener singleShowIdListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            int showCount = (int) dataSnapshot.getChildrenCount();
            Log.v("fb_b_val", "showKey " + dataSnapshot.getKey());
            Log.v("fb_b_val", "showCount " + showCount);
            Log.v("fb_b_val", "showId " + showId);
            for (DataSnapshot c : dataSnapshot.getChildren()){
                Log.v("fb_b_val", "key " + c.getKey());
                Log.v("fb_b_val", "val " + c.getValue());
            }
            //already booked tickets for movie with certain show
            if (showCount != 0 && !dataSnapshot.hasChild(showId)){
                my_payment_book_listener.onBookRejected(Constants.DIFFERENT_SHOWS_ON_THE_SAME_MOVIE, 0);
                Log.v("fb_b_val", "not allowed");
            }
            //user hasn't booked any movies today
            else if (showCount == 0){
                /** new changes */
                //my_pa.onBookAllowed();
                my_payment_book_listener.onBookAllowed();
                Log.v("fb_b_val", "no date or movie.allowed");
            }
            else{
                Log.v("fb_b_val", "allowed!Congrats!");
                if (dataSnapshot.hasChild(showId)){
                    int ticketsCount = (int) dataSnapshot.child(showId).getChildrenCount();
                    Log.v("fb_b_val", "orders count " + ticketsCount);

                    if (ticketsCount + numberOfOrders < 4){
                        Log.v("fb_b_val", "overall tickets " + ticketsCount);
                        Log.v("fb_b_val", "overall orders " + numberOfOrders);
                        Log.v("fb_b_val", "the end allowed!Congrats! ");
                        /** new changes */
                        //my_basket_listener.onBookAllowed();
                        my_payment_book_listener.onBookAllowed();
                    }else {
                        int exceedingNumberOfOrders = Math.abs(3 - ticketsCount - numberOfOrders);
                        /** new changes */
                        //my_basket_listener.onBookRejected(Constants.EXCEEDING_ORDER_LIMIT, exceedingNumberOfOrders);
                        my_payment_book_listener.onBookRejected(Constants.EXCEEDING_ORDER_LIMIT, exceedingNumberOfOrders);
                    }
                }else{

                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    List<HashMap<String, String>> listTickets = new ArrayList<>();
    GenericTypeIndicator<List<OrderModel>> t = new GenericTypeIndicator<List<OrderModel>>() {};

    private final ChildEventListener ticketListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            for (DataSnapshot c : dataSnapshot.getChildren()) {
                //hashMap!!!
                Log.v("tag_fb", c.toString());
                OrderModel order = c.getValue(OrderModel.class);
                Log.v("tag_fb", "seatId - " + order.getSeatId());
                /*for (DataSnapshot snapshot : c.getChildren()) {


                    Log.v("tag_fb", ((OrderModel)snapshot.getValue()).getMoviePoster());
                    //listTickets.add((HashMap<String, String>) snapshot.getValue());

                }*/
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final ValueEventListener singleValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //controller_listener.onMyTicketsLoaded(listTickets);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final ValueEventListener bookCounterListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //compare order list with number from fb
            int ticketsCount = (int) dataSnapshot.getChildrenCount();
            Log.v("fb_book", ""+ticketsCount);
            if (ticketsCount + numberOfOrders < 4){
                /** new changes */
                //my_basket_listener.onBookAllowed();
                my_payment_book_listener.onBookAllowed();
            }else {
                int exceedingNumberOfOrders = Math.abs(3 - ticketsCount - numberOfOrders);
                /** new changes */
                //my_basket_listener.onBookRejected(Constants.EXCEEDING_ORDER_LIMIT, exceedingNumberOfOrders);
                my_payment_book_listener.onBookRejected(Constants.EXCEEDING_ORDER_LIMIT, exceedingNumberOfOrders);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void voteUpMovie(Movie movieInf) {
        m = movieInf;
        refMovies.child(movieInf.getTitle()).child("movie rating").child(userName).setValue("1");
    }
    Movie m;

    public void voteDownMovie(Movie movieInf) {
        m= movieInf;
        refMovies.child(movieInf.getTitle()).child("movie rating").child(userName).setValue("0");
    }

    public void getMovieRating(Movie movieInf){
        refMovies.child(movieInf.getTitle()).child("movie rating").addChildEventListener(ratingListener);
        refMovies.child(movieInf.getTitle()).child("movie rating").addListenerForSingleValueEvent(singleRatingValueEventListener);
    }

    int upCountUser;
    int downCountUser;

    int upCountPublic;
    int downCountPublic;

    int totalUpCount;
    int totalVotes;

    private final ChildEventListener ratingListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.v("vote_tag", dataSnapshot.toString());
            if (dataSnapshot.getKey().equalsIgnoreCase(userName)){
                if (dataSnapshot.getValue().toString().equalsIgnoreCase("1")) {
                    upCountUser++;
                }
                else {
                    downCountUser++;
                }
            }else {
                if (dataSnapshot.getValue().toString().equalsIgnoreCase("1")){
                    upCountPublic++;
                }
                else{
                    downCountPublic++;
                }
            }


            totalVotes++;
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            Log.v("vote_tag_change", dataSnapshot.toString());
                if (dataSnapshot.getValue().toString().equalsIgnoreCase("1")) {
                    upCountUser++;
                    downCountUser--;
                }
                else {
                    downCountUser++;
                    upCountUser--;
                }
            refMovies.child(m.getTitle()).child("movie rating").addListenerForSingleValueEvent(singleRatingValueEventListener);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final ValueEventListener singleRatingValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            totalUpCount = upCountPublic + upCountUser;
            movieRating = (float) totalUpCount / totalVotes * 5f;
            controllerMovieActivity.onMovieRatingCalculated(movieRating);
            Log.v("calculus", ""+movieRating);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
