package project.taras.ua.adrenalincity.Activity.GeneralManagers;

import android.content.Context;
import android.net.ConnectivityManager;


/**
 * Created by Taras on 24.10.2017.
 */

public class InternetConnectionManager {

    private Context context;

    public InternetConnectionManager(Context context){
        this.context = context;
    }

    public boolean isInternetAvailable(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
