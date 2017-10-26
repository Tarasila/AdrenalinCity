package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Taras on 02.06.2017.
 */

public interface IAsync {

    interface ISelect {
        void doInBackground();
        void onPostExecute(OrderModel[] arrayOrderModel);
    }

    interface IInsert {
        void doInBackground();
        void onPostExecute(String res);
    }

}
