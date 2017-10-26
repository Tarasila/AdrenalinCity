package project.taras.ua.adrenalincity.Activity.GeneralManagers;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.DataManager;
import project.taras.ua.adrenalincity.Activity.MainPageMVC.MovieSchedule;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.IBookPayment;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Seat;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.Activity.TodayMovieMVC.Movie;

/**
 * Created by Taras on 10.03.2017.
 */

public class RequestManager {

    private OnLoginActivityListener loginListener;
    private IBookPayment.IBookPaymentListener myBookPayment_listener;

    public interface OnLoginActivityListener {
        void onAccessGranted();
        void onUserNotFound();
        void checkPassword(String usersPassword);
        void appendUserDbId(String userDbId);
    }

    public void setOnLoginActivityListener(OnLoginActivityListener listener) {
        this.loginListener = listener;
    }

    private OnRequestManagerControllerListener controllerListener;

    public interface OnRequestManagerControllerListener {
        void responseAvailableSeats(List<Seat> listSeats);
    }

    public void setOnRequestManagerControllerListener(OnRequestManagerControllerListener controllerListener) {
        this.controllerListener = controllerListener;
    }

    private OnRequestManagerListener listener;

    public interface OnRequestManagerListener {
        void onMovieDownloaded(List<Movie> list, int moviePeriod);
        void onMovieScheduleMapForFilterReady(Map<Integer, List<Show>> movieShowMap);
        void onMovieScheduleMapForMovieActivityReady(LinkedHashMap<Integer, LinkedHashMap<String, List<Show>>> movieShowMap);
        void onDayScheduleReady(List<MovieSchedule> list);
    }

    public void setOnRequestManagerListener(OnRequestManagerListener listener) {
        this.listener = listener;
    }

    public void setOnMyBookPaymentListener(IBookPayment.IBookPaymentListener listener) {
        this.myBookPayment_listener = listener;
    }

    private static RequestManager instance;

    private static int REQUEST_TYPE;
    private static int FLAG;

    //private Activity context;
    private StringRequest stringRequest;
    private RequestQueue requestQueue;

    private DataManager dataManagerListener;
    private Context context;

    public void synchronizeWith(DataManager dataManagerListener) {
        this.dataManagerListener = dataManagerListener;
    }

    public RequestManager(Activity context, int type) {
        if (type == Constants.NOT_SINGLETON)
            this.context = context;
        init(context);
    }

    public static RequestManager getInstance(Activity context) {
        if (instance == null) {
            instance = new RequestManager(context, Constants.SINGLETON);
        }
        return instance;
    }

    private void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);

       /* //TODO: clean it up...or fix it
        switch (contextType){
            case Constants.RM_MAIN_ACTIVITY:
                //movieModel = ViewModelProviders.of( (MainActivity) context ).get(MovieModel.class);
                break;
        }*/

    }

    public void sendRequest(final String requestURL, int reqType, final Map<String, String> params) {
        REQUEST_TYPE = reqType;
        switch (REQUEST_TYPE) {

            case Constants.TYPE_CREATE_USER:
                stringRequest = new StringRequest(Request.Method.POST, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                        //return null;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        byte[] byteString = "android:softwest".getBytes();
                        String base64String = Base64.encodeToString(byteString, Base64.DEFAULT);
                        Map<String, String> userCredentials = new HashMap<String, String>();
                        userCredentials.put("Authorization", "Basic " + base64String);
                        return userCredentials;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_BOOK_SEAT:
                stringRequest = new StringRequest(Request.Method.POST, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        byte[] byteString = "android:softwest".getBytes();
                        String base64String = Base64.encodeToString(byteString, Base64.DEFAULT);
                        Map<String, String> userCredentials = new HashMap<String, String>();
                        userCredentials.put("Authorization", "Basic " + base64String);
                        return userCredentials;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_CANCEL_BOOK_SEAT:
                stringRequest = new StringRequest(Request.Method.POST, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        byte[] byteString = "android:softwest".getBytes();
                        String base64String = Base64.encodeToString(byteString, Base64.DEFAULT);
                        Map<String, String> userCredentials = new HashMap<String, String>();
                        userCredentials.put("Authorization", "Basic " + base64String);
                        return userCredentials;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_MAKE_ORDER:
                stringRequest = new StringRequest(Request.Method.POST, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        byte[] byteString = "android:softwest".getBytes();
                        String base64String = Base64.encodeToString(byteString, Base64.DEFAULT);
                        Map<String, String> userCredentials = new HashMap<String, String>();
                        userCredentials.put("Authorization", "Basic " + base64String);
                        return userCredentials;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;
        }
        requestQueue.add(stringRequest);
    }

    public void sendRequest(final String requestURL, int reqType, int flag) {
        REQUEST_TYPE = reqType;
        FLAG = flag;
        switch (REQUEST_TYPE) {
            case Constants.TYPE_APPEND_SHOW_SCHEDULE:
                synchronized (dataManagerListener) {
                    dataManagerListener.sendingRequest = true;
                }
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_CHECK_USER_WITH_EMAIL:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;
        }
        requestQueue.add(stringRequest);
    }

    public void sendRequest(final String requestURL, int reqType) {
        REQUEST_TYPE = reqType;
        switch (REQUEST_TYPE) {
            case Constants.TYPE_MOVIE_TODAY:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_SCHEDULE_TODAY:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_MOVIE_SOON:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_SHOW_SCHEDULE:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_AVAILABLE_SEATS:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_CHECK_USER_WITH_SN_ID:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;

            case Constants.TYPE_CHECK_USER_WITH_EMAIL:
                stringRequest = new StringRequest(Request.Method.GET, requestURL, responseListener, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return null;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        return super.parseNetworkResponse(response);
                    }
                };
                break;
        }
        requestQueue.add(stringRequest);
    }

    private Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            switch (REQUEST_TYPE) {
                case Constants.TYPE_MOVIE_TODAY:
                    Movie movie = null;
                    List<Movie> listMovie = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject currentFilm = jsonArray.getJSONObject(i);

                            int movieId = currentFilm.getInt("filmId");
                            String startDate = currentFilm.getString("startDate");
                            String endDate = currentFilm.getString("endDate");
                            String title = (String) currentFilm.get("title_uk");
                            String production = currentFilm.getString("countries_uk");
                            String director = currentFilm.getString("director_uk");
                            String genre = currentFilm.getString("genre_uk");
                            String storyLine = currentFilm.getString("story_uk");
                            String ageRestriction = currentFilm.getString("limitations");
                            String duration = currentFilm.getString("duration");
                            String imgURL = "http://cinema.adrenalin.lutsk.ua/images/film/" + currentFilm.get("image");
                            String trailerHtml = currentFilm.getString("trailerCode");
                            String purse = currentFilm.getString("vibor");
                            String rawTrailerUrl;
                            String trailerUrl;

                            Document document = Jsoup.parse(trailerHtml);
                            Element element = document.select("iframe").first();
                            if (element != null) {
                                rawTrailerUrl = element.absUrl("src");
                                String selectPattern = "embed/";
                                int firstIndex = rawTrailerUrl.indexOf("embed/");
                                int lastIndex = firstIndex + selectPattern.length();
                                trailerUrl = rawTrailerUrl.substring(lastIndex, rawTrailerUrl.length());
                            } else {
                                trailerUrl = "";
                            }

                            //time restriction to rule out movies whose schedule are up
                            Date today = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dateOfShow = format.parse(endDate);

                            if (!title.equalsIgnoreCase("Анімаційна феєрія")) {
                                movie = new Movie();
                                movie.setMovieId(movieId);
                                movie.setTitle(title);
                                movie.setDirector(director);
                                movie.setProduction(production);
                                movie.setGenre(genre);
                                movie.setStoryLine(storyLine);
                                movie.setDuration(duration);
                                movie.setImgUrl(imgURL);
                                movie.setTrailerUrl(trailerUrl);
                                movie.setAgeRestriction(ageRestriction);
                                movie.setPurse(purse);

                                listMovie.add(movie);
                            }
                        }
                        listener.onMovieDownloaded(listMovie, Constants.TYPE_MOVIE_TODAY);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_SCHEDULE_TODAY:
                    try {
                        List<MovieSchedule> dayList = new ArrayList<>();
                        MovieSchedule movieSchedule;
                        JSONArray jsonArray = new JSONArray(response);

                        Calendar calendarStart = Calendar.getInstance();
                        Calendar calendarNow = Calendar.getInstance();
                        Date today = new Date();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String movieId = jsonObject.getString("filmId");
                            String startTime = jsonObject.getString("startTime").substring(0, 5);

                            calendarStart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0, 2)));
                            calendarStart.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3, 5)));

                            String currentTime = dateFormat.format(today);

                            calendarNow.set(Calendar.HOUR_OF_DAY, Integer.parseInt(currentTime.substring(0, 2)));
                            calendarNow.set(Calendar.MINUTE, Integer.parseInt(currentTime.substring(3, 5)));

                            Date dateCurrent = calendarNow.getTime();
                            Date dateStart = calendarStart.getTime();

                            if (dateCurrent.before(dateStart)) {
                                movieSchedule = new MovieSchedule();
                                movieSchedule.setMovieId(movieId);
                                movieSchedule.setStartTime(startTime);

                                dayList.add(movieSchedule);
                            }
                        }
                        listener.onDayScheduleReady(dayList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_MOVIE_SOON:
                    Movie movieSoon;
                    List<Movie> listMovieSoon = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject currentFilm = jsonArray.getJSONObject(i);

                            int movieId = currentFilm.getInt("id");

                            //movie will have come out but already has a schedule
                            String movieWithScheduleId = currentFilm.getString("cinemaTicketId");
                            String startDate = currentFilm.getString("startDate");
                            String title = (String) currentFilm.get("title_uk");
                            String production = currentFilm.getString("countries_uk");
                            String director = currentFilm.getString("director_uk");
                            String genre = currentFilm.getString("genre_uk");
                            String storyLine = currentFilm.getString("story_uk");
                            String duration = currentFilm.getString("duration");
                            String imgURL = "http://cinema.adrenalin.lutsk.ua/images/film/" + currentFilm.get("image");
                            String trailerHtml = currentFilm.getString("trailerCode");
                            String rawTrailerUrl;
                            String trailerUrl;

                            Document document = Jsoup.parse(trailerHtml);
                            Element element = document.select("iframe").first();
                            if (element != null) {
                                rawTrailerUrl = element.absUrl("src");
                                String selectPattern = "embed/";
                                int firstIndex = rawTrailerUrl.indexOf("embed/");
                                int lastIndex = firstIndex + selectPattern.length();
                                trailerUrl = rawTrailerUrl.substring(lastIndex, rawTrailerUrl.length());
                            } else {
                                trailerUrl = "";
                            }

                            movieSoon = new Movie();
                            movieSoon.setMovieId(movieId);
                            if (!movieWithScheduleId.equalsIgnoreCase("null")) {
                                movieSoon.setMovieWithScheduleId(Integer.parseInt(movieWithScheduleId));
                            }

                            movieSoon.setStartDate(startDate);
                            movieSoon.setTitle(title);
                            movieSoon.setDirector(director);
                            movieSoon.setProduction(production);
                            movieSoon.setGenre(genre);
                            movieSoon.setStoryLine(storyLine);
                            movieSoon.setDuration(duration);
                            movieSoon.setImgUrl(imgURL);
                            movieSoon.setTrailerUrl(trailerUrl);

                            listMovieSoon.add(i, movieSoon);
                        }
                        listener.onMovieDownloaded(listMovieSoon, Constants.TYPE_MOVIE_SOON);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_APPEND_SHOW_SCHEDULE:
                    ArrayList<Show> showListAppend = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<String> keyList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length() - 1; i++) {
                            JSONObject jsonShowObject = jsonArray.getJSONObject(i);
                            Iterator iterator = jsonShowObject.keys();

                            while (iterator.hasNext()) {
                                keyList.add(i, (String) iterator.next());
                            }

                            String jsonObjectShowId = keyList.get(i);

                            JSONObject showInfJsonObject = jsonShowObject.getJSONObject(jsonObjectShowId);

                            String showDate = showInfJsonObject.getString("showDate");

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date dateOfShow = format.parse(showDate);

                            format.applyPattern("yyyy-MM-dd");
                            String calendarDate = format.format(dateOfShow);
                            format.applyPattern("d");
                            String day = format.format(dateOfShow);
                            format.applyPattern("EEEE");
                            String dayOfWeek = format.format(dateOfShow);
                            //format.applyPattern("MMM");
                            format.applyPattern("MM");
                            String month = format.format(dateOfShow);
                            format.applyPattern("HH:mm");
                            String time = format.format(dateOfShow);

                            Date today = new Date();

                            if (today.before(dateOfShow)) {
                                int databaseId = showInfJsonObject.getInt("id");
                                int showId = showInfJsonObject.getInt("showId");
                                int filmId = showInfJsonObject.getInt("filmId");
                                int hallId = showInfJsonObject.getInt("hallId");

                                Show show = new Show();

                                show.setDatabaseId(databaseId);
                                show.setShowId(showId);
                                show.setMovieId(filmId);
                                show.setHallId(hallId);
                                show.setCalendarDate(calendarDate);
                                show.setShowDate(showDate);
                                show.setMonth(month);
                                show.setDay(day);
                                show.setDayOfWeek(dayOfWeek);
                                show.setTime(time);

                                showListAppend.add(show);
                            }
                        }
                        synchronized (dataManagerListener) {
                            if (showListAppend.size() != 0) {
                                switch (FLAG) {
                                    case Constants.NOT_THE_LAST_MOVIE:
                                        dataManagerListener.addNewSchedule(showListAppend.get(0).getMovieId(),
                                                showListAppend,
                                                Constants.NOT_THE_LAST_MOVIE);
                                        break;
                                    case Constants.LAST_MOVIE:
                                        dataManagerListener.addNewSchedule(showListAppend.get(0).getMovieId(),
                                                showListAppend,
                                                Constants.LAST_MOVIE);
                                        break;
                                }
                                //if last movie happens to have no shows
                            } else if (FLAG == Constants.LAST_MOVIE && showListAppend.size() == 0) {
                                dataManagerListener.addNewSchedule(0,
                                        null,
                                        Constants.LAST_MOVIE);
                            }
                            dataManagerListener.sendingRequest = false;
                            dataManagerListener.notify();
                        }
                    } catch (JSONException e) {
                        Log.e("jsontracker", e.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_AVAILABLE_SEATS:
                    ArrayList<Seat> listSeats = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<String> keyList = new ArrayList<>();
                        //jsonArray.length() may cause an json exception. Need to be checked later on
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonSeatObject = jsonArray.getJSONObject(i);
                            Iterator iterator = jsonSeatObject.keys();

                            while (iterator.hasNext()) {
                                keyList.add(i, (String) iterator.next());
                            }

                            String jsonObjectSeatId = keyList.get(i);

                            JSONObject seatInfJsonObject = jsonSeatObject.getJSONObject(jsonObjectSeatId);

                            Seat seat = new Seat();

                            int id = Integer.parseInt((String) seatInfJsonObject.get("seat_id"));
                            int price = (int) Float.parseFloat((String) seatInfJsonObject.get("price"));
                            int status;

                            if (((String) seatInfJsonObject.get("status")).equalsIgnoreCase("")) {
                                status = Constants.SEAT_AVAILABLE;
                            } else {
                                status = Integer.parseInt((String) seatInfJsonObject.get("status"));
                            }

                            seat.setId(id);
                            seat.setPrice(price);
                            seat.setStatus(status);

                            listSeats.add(seat);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    controllerListener.responseAvailableSeats(listSeats);
                    break;
                case Constants.TYPE_CHECK_USER_WITH_SN_ID:
                    try {
                        Object object = new JSONTokener(response).nextValue();
                        if (object instanceof JSONObject) {
                            loginListener.onUserNotFound();
                        } else {
                            JSONArray arrayResponse = new JSONArray(response);
                            JSONObject jsonUserInf = arrayResponse.getJSONObject(0);

                            String userDbId = jsonUserInf.getString("id");
                            //append id from Adrenalin server to current user object
                            loginListener.appendUserDbId(userDbId);
                            loginListener.onAccessGranted();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_CREATE_USER:
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("id")) {
                            String userDbId = jsonResponse.getString("id");
                            //append id from Adrenalin server to current user object
                            loginListener.appendUserDbId(userDbId);
                            loginListener.onAccessGranted();
                        } else if (jsonResponse.has("email")) {
                            Toast.makeText(context, "Користувач з такою адресою вже зареєстрований, будь ласка використайте іншу",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_CHECK_USER_WITH_EMAIL:
                    try {
                        Object object = new JSONTokener(response).nextValue();
                        if (object instanceof JSONObject) {
                            JSONObject jsonResponse = new JSONObject(response);

                            if (jsonResponse.has("id")) {
                                loginListener.onAccessGranted();
                            } else if (jsonResponse.has("email")) {
                                Toast.makeText(context, "Користувач з такою адресою вже зареєстрований, будь ласка використайте іншу", Toast.LENGTH_LONG).show();
                            } else if (jsonResponse.has("status")) {

                                if (jsonResponse.get("status").toString().equalsIgnoreCase("error data empty!")) {
                                    //Google user hasn't been found therefore create a new one
                                    if (FLAG == Constants.TYPE_CHECK_GOOGLE_USER_WITH_EMAIL) {
                                        loginListener.onUserNotFound();
                                    } else {
                                        Toast.makeText(context, "Невірний емейл, або пароль", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        } else {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String id = jsonObject.getString("id");
                            String sha1Password = jsonObject.getString("password");

                            loginListener.appendUserDbId(jsonObject.getString("id"));
                            loginListener.checkPassword(sha1Password);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_BOOK_SEAT:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (myBookPayment_listener) {
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                    myBookPayment_listener.onSuccessfulOrderAction(Constants.BOOK_TICKETS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    break;

                case Constants.TYPE_CANCEL_BOOK_SEAT:
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        Log.v("TAG_ORDER_DELETE", "RESPONSE " + jsonResponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case Constants.TYPE_MAKE_ORDER:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (myBookPayment_listener) {
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                    myBookPayment_listener.onSuccessfulOrderAction(Constants.BUY_TICKETS);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    break;
            }
        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse response = error.networkResponse;
            if (error instanceof ServerError && response != null) {
                try {
                    String res = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    // Now you can use any deserializer to make sense of data
                    JSONObject obj = new JSONObject(res);
                } catch (UnsupportedEncodingException e1) {
                    // Couldn't properly decode data to string
                    e1.printStackTrace();
                } catch (JSONException e2) {
                    // returned data is not JSONObject?
                    e2.printStackTrace();
                }
            }
        }
    };

}
