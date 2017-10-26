package project.taras.ua.adrenalincity.Activity.HelperClasses;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderDao;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.OrderModel;

/**
 * Created by Taras on 01.06.2017.
 */

@Database(entities = {OrderModel.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "order_db")
                    .build();
        }
        return instance;
    }

    public abstract OrderDao orderDao();

}
