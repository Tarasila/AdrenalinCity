package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Taras on 01.06.2017.
 */

@Dao
public interface OrderDao {

    @Query("SELECT * FROM OrderModel")
    OrderModel[] getAllOrders();

    @Query("SELECT * FROM OrderModel where id = :id")
    OrderModel getItemById(int id);

    @Query("DELETE FROM OrderModel")
    void deleteAllOrders();

    @Query("DELETE FROM OrderModel WHERE id = :id")
    void deleteOrderWithId(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(OrderModel... orders);

    @Query("SELECT * FROM OrderModel WHERE userName = :userName AND date > :date AND status = :paymentStatus")
    OrderModel[] selectNotPaidOrders(String userName, String date, String paymentStatus);

    @Delete
    int deleteOrder(OrderModel orderModel);

    @Update
    void setOrdersStatus(OrderModel... orders);

    @Query("SELECT * FROM OrderModel WHERE status = :paid AND date > :date")
    OrderModel[] selectPaidAndRelevantOrders(String paid, String date);
}
