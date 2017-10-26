package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Taras on 02.06.2017.
 */

public class FactoryAsync {

    public static IAsync.ISelect listenerSelect;
    public static IAsync.IInsert listenerInsert;

    public static void setSelectInterface(IAsync.ISelect iListener) {
        listenerSelect = iListener;
    }

    public static void createSelectExecutor(OrderDao orderDao) {
        new AsyncTask<OrderDao, Void, OrderModel[]>() {
            @Override
            protected OrderModel[] doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                return orderDao.getAllOrders();
            }

            @Override
            protected void onPostExecute(OrderModel[] arrayOrderModel) {
                super.onPostExecute(arrayOrderModel);
                listenerSelect.onPostExecute(arrayOrderModel);
            }
        }.execute(orderDao);
    }

    public static void createInsertExecutor(final OrderDao orderDao, OrderModel[] orderModel) {
        new AsyncTask<OrderModel, Void, Void>() {
            @Override
            protected Void doInBackground(OrderModel... params) {
                orderDao.insertAll(params);
                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                super.onPostExecute(Void);
                //listenerInsert.onPostExecute("All orders have been added to basket");
            }
        }.execute(orderModel);
    }

    public static void selectNotPaidOrders(final String userName, OrderDao orderDao, final Date thisDate, final String payStatus) {
        Log.v("currentuser", userName);
        new AsyncTask<OrderDao, Void, OrderModel[]>() {
            @Override
            protected OrderModel[] doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                String date = format.format(thisDate);
                return orderDao.selectNotPaidOrders(userName, date, payStatus);
            }

            @Override
            protected void onPostExecute(OrderModel[] arrayOrderModel) {
                super.onPostExecute(arrayOrderModel);
                listenerSelect.onPostExecute(arrayOrderModel);
            }
        }.execute(orderDao);
    }

    public static void deleteOrder(OrderDao orderDao, final OrderModel orderModel) {
        new AsyncTask<OrderDao, Void, Void>() {
            @Override
            protected Void doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                int deletedRaw = orderDao.deleteOrder(orderModel);
                Log.v("tag_sql", "order - " + orderModel.getMovieTitle()
                        + "has been successfully deleted from SQLite. Deleted raw is - "
                        + deletedRaw);
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
            }
        }.execute(orderDao);
    }

    public static void deleteAllOrders(OrderDao dao) {
        new AsyncTask<OrderDao, Void, Void>() {
            @Override
            protected Void doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                orderDao.deleteAllOrders();
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
            }
        }.execute(dao);
    }

    public static void setOrdersStatus(OrderDao orderDao, final OrderModel[] orders) {
        new AsyncTask<OrderDao, Void, Void>() {
            @Override
            protected Void doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                orderDao.setOrdersStatus(orders);
                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                super.onPostExecute(v);
            }
        }.execute(orderDao);
    }

    public static void selectPaidAndRelevantOrders(OrderDao orderDao, final Date thisDate) {
        new AsyncTask<OrderDao, Void, OrderModel[]>() {
            @Override
            protected OrderModel[] doInBackground(OrderDao... params) {
                OrderDao orderDao = params[0];
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                /*Date subDate = thisDate - */
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(thisDate);
                calendar.add(Calendar.HOUR, -5);

                String date = format.format(calendar.getTime());
                Log.v("tag_subtime", date);

                return orderDao.selectPaidAndRelevantOrders("paid", date);
            }

            @Override
            protected void onPostExecute(OrderModel[] v) {
                super.onPostExecute(v);
                listenerSelect.onPostExecute(v);
            }
        }.execute(orderDao);
    }
}
