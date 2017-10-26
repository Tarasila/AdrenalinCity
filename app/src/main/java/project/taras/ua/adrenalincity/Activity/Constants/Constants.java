package project.taras.ua.adrenalincity.Activity.Constants;

import java.util.Map;

/**
 * Created by Taras on 10.03.2017.
 */

public class  Constants {

    public static final String MOVIE_TODAY = "http://cinema.adrenalin.lutsk.ua/api/shedule/getToday";
    public static final int TYPE_MOVIE_TODAY = 100;

    public static final String SCHEDULE_TODAY = "http://cinema.adrenalin.lutsk.ua/api/shedule/getWeek";
    public static final int TYPE_SCHEDULE_TODAY = 101;

    public static final String SHOW_AVAILABLE_SEATS_FOR_SHOW_ID_ = "http://cinema.adrenalin.lutsk.ua/api/film/getSeats/";
    public static final int TYPE_AVAILABLE_SEATS = 200;

    public static final String GET_SHOW_SCHEDULE_TO_MOVIE_ID_ = "http://cinema.adrenalin.lutsk.ua/api/film/getShowId/" ;
    public static final int TYPE_SHOW_SCHEDULE = 300;

    public static final String APPEND_SHOW_SCHEDULE_TO_MOVIE_ID_ = "http://cinema.adrenalin.lutsk.ua/api/film/getShowId/" ;
    public static final int TYPE_APPEND_SHOW_SCHEDULE = 301;

    public static final String CHECK_USER_IN_DATABASE_WITH_SN_ID_ = "http://cinema.adrenalin.lutsk.ua/api/user/get/" ;
    public static final int TYPE_CHECK_USER_WITH_SN_ID = 400;

    public static final String CREATE_NEW_USER = "http://cinema.adrenalin.lutsk.ua/api/user/create/" ;
    public static final int TYPE_CREATE_USER = 500;

    public static final String CHECK_USER_IN_DATABASE_WITH_EMAIL_ = "http://cinema.adrenalin.lutsk.ua/api/user/get/email/" ;
    public static final int TYPE_CHECK_USER_WITH_EMAIL = 600;

    public static final String BOOK_SEAT = "http://cinema.adrenalin.lutsk.ua/api/order/reserve" ;
    public static final int TYPE_BOOK_SEAT = 700;

    public static final String MOVIE_SOON = "http://cinema.adrenalin.lutsk.ua/api/shedule/getSoon";
    public static final int TYPE_MOVIE_SOON = 800;

    public static final String MAKE_ORDER = "http://cinema.adrenalin.lutsk.ua/api/order/makeorder";
    public static final int TYPE_MAKE_ORDER = 900;

    public static final String CANCEL_BOOK_SEAT = "http://cinema.adrenalin.lutsk.ua/api/order/destroy";
    public static final int TYPE_CANCEL_BOOK_SEAT = 902;

    public static final int TYPE_MOVIE_ALL = 901;

    public static final String CHECK_GOOGLE_USER_IN_DATABASE_WITH_EMAIL_ = "http://cinema.adrenalin.lutsk.ua/api/user/get/email/" ;
    public static final int TYPE_CHECK_GOOGLE_USER_WITH_EMAIL = 902;

    //SQLite constants
    public static final int QUERY_INSERT = 1000;
    public static final int QUERY_SELECT = 2000;

    public static final int BOOK_TICKETS = 3000;
    public static final int BUY_TICKETS = 4000;

    public static final int SEAT_AVAILABLE = 0;
    public static final int SEAT_BOOKED = 1;
    public static final int SEAT_SOLD = 10;
    public static final int SEAT_BLOCKED = -1;

    public static final int RED_HALL = 1;
    public static final int BLUE_HALL = 3;
    public static final int SILVER_HALL = 2;

    //Order codes
    public static final int EXCEEDING_ORDER_LIMIT = 1;
    public static final int ALREADY_BOOKED_ORDER = 2;
    public static final int DIFFERENT_SHOWS_ON_THE_SAME_MOVIE = 3;

    public static final String EMAIL_IS_NOT_UNIQUE = "The Email field must contain a unique value.";

    //RecyclerView types
    public static final int PLANE_RECYCLER = 0;
    public static final int SCHEDULE_RECYCLER = 1;
    public static final int ALL_RECYCLER = 2;

    //
    public static final int NOT_THE_LAST_MOVIE = 99;
    public static final int LAST_MOVIE = 100;

    public static final int RV_FRAGMENT_MOVIE_SOON = 150;
    public static final int RV_FRAGMENT_MOVIE_TODAY = 151;
    public static final int RV_ACTIVITY_MOVIE_TODAY = 152;
    public static final int RV_ACTIVITY_MOVIE_SOON = 153;
    public static final int RV_ACTIVITY_MOVIE_SCHEDULE = 154;
    public static final int RV_FRAGMENT_MOVIE_ALL = 155;

    //constants for appropriate context in request manager
    public static final int RM_MAIN_ACTIVITY = 160;
    public static final int RM_LOGIN_ACTIVITY = 159;

    public static final int MAIN_ACTIVITY = 179;
    public static final int MOVIE_TODAY_ACTIVITY = 180;
    public static final int MOVIE_ACTIVITY = 181;
    public static final int MY_BASKET_ACTIVITY = 182;
    public static final int SCHEDULE_ACTIVITY = 183;


    //how user is logged in
    public static final int FACEBOOK_LOG_IN = 190;
    public static final int GOOGLE_LOG_IN = 191;
    public static final int EMAIL_LOG_IN = 192;

    //create Request manager whether singleton, or not
    public static final int NOT_SINGLETON = 201;
    public static final int SINGLETON = 202;

    public static final int BUY_ORDER = 203;
    public static final int BOOK_ORDER = 204;

    public static final int OPEN = 205;
    public static final int CLOSE = 206;

    public static final int CLEAR_PREORDER_LIST = 207;
}
