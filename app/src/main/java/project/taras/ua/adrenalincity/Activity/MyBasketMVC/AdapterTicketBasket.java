package project.taras.ua.adrenalincity.Activity.MyBasketMVC;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.Constants.Constants;
import project.taras.ua.adrenalincity.Activity.GeneralManagers.RequestManager;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieActivity;
import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.PlaceView;
import project.taras.ua.adrenalincity.Activity.MyBasketMVC.Swiping.ISwipe;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 29.05.2017.
 */

public class AdapterTicketBasket extends RecyclerView.Adapter<AdapterTicketBasket.ViewHolder>
        implements ISwipe {

    private int ACTIVITY_TYPE;

    private OrderDao dao;

    private IBookPayment.IClickListener listener;
    private IBookPayment.IBookPaymentListener i_book_listener;

    private Context context;
    private RecyclerView rv;
    private List<OrderModel> orders;
    private List<PlaceView> listSelectedSeats;

    public AdapterTicketBasket(Context context, OrderDao dao, int ACTIVITY_TYPE) {
        this.ACTIVITY_TYPE = ACTIVITY_TYPE;
        this.context = context;
        this.dao = dao;
    }

    public void clear_all_selected_seats(){
        Log.v("TAG_ORDER", "listSelSeats size "+ listSelectedSeats.size());
        for (int position = 0; position < listSelectedSeats.size(); position++){
            listSelectedSeats.get(position).setBookedStatus(false);
            listSelectedSeats.get(position).setScrollingStatus(false);
            ((MovieActivity) context)
                    .removeOrderFromOrderList(listSelectedSeats.get(position));
        }
        listSelectedSeats.clear();
        if (getItemCount() == 0) {
            Log.v("check_empty", "size is zero");
            ((MovieActivity) context).preOrderAdapterIsEmpty();
        }
        notifyDataSetChanged();
    }

    @Override
    public void onDismiss(int position) {
        switch (ACTIVITY_TYPE) {
            case Constants.MOVIE_ACTIVITY:
                ((MovieActivity) context)
                        .removeOrderFromOrderList(listSelectedSeats.get(position));
                listSelectedSeats.get(position).setBookedStatus(false);
                Log.v("check_empty", "remove " + position);
                listSelectedSeats.get(position).setScrollingStatus(false);
                listSelectedSeats.remove(position);

                if (getItemCount() == 0) {
                    Log.v("check_empty", "size is zero");
                    ((MovieActivity) context).preOrderAdapterIsEmpty();
                }
                notifyDataSetChanged();

                break;
            case Constants.MY_BASKET_ACTIVITY:
                Log.v("tag_sql", "pos" + position);

                //remove from adrenalin database
                List<OrderModel> list_orders = getData();

                OrderModel order_to_dismiss = orders.get(position);
                String seat_to_dismiss = order_to_dismiss.getSeatId();
                String ctShowId_to_dismiss = order_to_dismiss.getCtShowId();

                Map<String, String> map_book_to_dismiss = new HashMap<String, String>();
                map_book_to_dismiss.put("ctShowId", ctShowId_to_dismiss);
                map_book_to_dismiss.put("seat", String.valueOf(seat_to_dismiss));

                Log.v("TAG_ORDER_DELETE", "CT_SHOW_ID - " + ctShowId_to_dismiss);
                Log.v("TAG_ORDER_DELETE", "CT_SEAT_ID - " + seat_to_dismiss);

                ((BasketActivity) context).onOrderDismiss(map_book_to_dismiss);

                FactoryAsync.deleteOrder(dao, orders.get(position));
                orders.remove(position);

                if (getItemCount() == 0)
                    ((BasketActivity) context).adapterIsEmpty();
                notifyDataSetChanged();
                break;
        }
    }

    @Override
    public AdapterTicketBasket.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case Constants.BUY_TICKETS:
                view = LayoutInflater.from(context).inflate(R.layout.custom_order_card, parent, false);
                break;
            case Constants.BOOK_TICKETS:
                view = LayoutInflater.from(context).inflate(R.layout.custom_order_card_booked, parent, false);
                break;
            case Constants.MOVIE_ACTIVITY:
                view = LayoutInflater.from(context).inflate(R.layout.custom_selecting_card, parent, false);
                Log.v("pre_order", "on create");
                break;
        }
        //view.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(view);

        if (viewType == Constants.MOVIE_ACTIVITY)
            viewHolder.b_cancel.setOnClickListener(onClickListener);

        return viewHolder;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onDismiss((Integer) v.getTag());
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (ACTIVITY_TYPE) {
            case Constants.MOVIE_ACTIVITY:
                PlaceView seat = listSelectedSeats.get(position);
                Log.v("pre_order", "" + seat.getPlace());
                holder.tvSeatRaw.setText("Ряд: " + String.valueOf(seat.getRaw()));
                holder.tvSeatPlace.setText("Місце: " + String.valueOf(seat.getPlace()));
                holder.tvSeatPrice.setText("Ціна: " + String.valueOf(seat.getPrice()) + " грн");
                holder.b_cancel.setTag(position);
                break;
            case Constants.MY_BASKET_ACTIVITY:
                OrderModel order = orders.get(position);
                holder.tvMovieTitle.setText(order.getMovieTitle());
                holder.tvSeatRaw.setText("Ряд\n" + String.valueOf(order.getSeatRaw()));
                holder.tvSeatPlace.setText("Місце\n" + String.valueOf(order.getSeatPlace()));
                holder.tvSeatPrice.setText("Ціна\n" + String.valueOf(order.getSeatPrice()));
                holder.tvDate.setText("Дата\n" + String.valueOf(order.getCalendarDay()));
                holder.tvTime.setText("Час\n" + String.valueOf(order.getShowTime()));

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (ACTIVITY_TYPE) {
            case Constants.MOVIE_ACTIVITY:
                return Constants.MOVIE_ACTIVITY;
            case Constants.MY_BASKET_ACTIVITY:
                OrderModel order = orders.get(position);
                if (order.getStatus().equalsIgnoreCase("booked")) {
                    return Constants.BOOK_TICKETS;
                } else {
                    return Constants.BUY_TICKETS;
                }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        switch (ACTIVITY_TYPE) {
            case Constants.MOVIE_ACTIVITY:
                Log.v("pre_order_size_!=null", "not null " + listSelectedSeats.size());
                if (listSelectedSeats.size() != 0) {
                    notifyControllerOnDataIsSet();
                    Log.v("pre_order_size", "not null " + listSelectedSeats.size());
                    return listSelectedSeats.size();
                } else if (listSelectedSeats.size() == 0) {
                    firstInit = true;
                    return 0;
                } else {
                    Log.v("pre_order_size", "movie_activity 0");
                    return 0;
                }
            case Constants.MY_BASKET_ACTIVITY:
                return orders == null ? 0 : orders.size();
        }
        Log.v("pre_order_size", "last return 0");
        return 0;
    }

    boolean firstInit = true;

    private void notifyControllerOnDataIsSet() {
        if (firstInit) {
            ((MovieActivity) context).adapterPreOrderIsSet();
            firstInit = false;
        }
    }

    public void setData(List<OrderModel> orderedSeats) {
        Log.v("data_is_set", "" + orderedSeats.get(0).getStatus());
        this.orders = orderedSeats;
        switch (ACTIVITY_TYPE) {
            case Constants.MOVIE_ACTIVITY:
                /** today changes */
                ((MovieActivity) context).dataForBookIsSet();
                break;
        }
    }

    public void setSelectedSeats(List<PlaceView> selectedSeats) {
        this.listSelectedSeats = selectedSeats;
        notifyDataSetChanged();
    }

    public List<OrderModel> getData() {
        return this.orders;
    }

    public List<PlaceView> getListSelectedSeats() {
        return this.listSelectedSeats;
    }

    public void setClickListener(IBookPayment.IClickListener clickListener) {
        this.listener = clickListener;
    }

    public void setRvRef(RecyclerView rvTicketBasket) {
        this.rv = rvTicketBasket;
    }

    public void removeOrder(OrderModel order) {
        orders.remove(order);
    }

    public void clearPreorderListUponSuccess() {
        listSelectedSeats.clear();
        notifyDataSetChanged();
    }

    public void markOrdersAsPaid() {
        for (OrderModel order : this.orders) {
            order.setStatus("paid");
        }
    }

    public void markOrdersAsBooked() {
        for (OrderModel order : this.orders) {
            order.setStatus("booked");
        }
        notifyDataSetChanged();
    }

    public OrderModel[] getDataAsArray() {
        OrderModel[] orders = new OrderModel[this.orders.size()];
        for (int i = 0; i < this.orders.size(); i++) {
            orders[i] = this.orders.get(i);
        }
        return orders;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle;
        TextView tvSeatRaw;
        TextView tvSeatPlace;
        TextView tvSeatPrice;
        TextView tvHall;
        TextView tvDate;
        TextView tvTime;
        Button b_cancel;

        public ViewHolder(View itemView) {
            super(itemView);
            switch (ACTIVITY_TYPE) {
                case Constants.MY_BASKET_ACTIVITY:
                    tvMovieTitle = (TextView) itemView.findViewById(R.id.ticket_basket_tv_movie_title);
                    tvSeatRaw = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_raw);
                    tvSeatPlace = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_place);
                    tvSeatPrice = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_price);
                    tvHall = (TextView) itemView.findViewById(R.id.ticket_basket_tv_hall);
                    tvDate = (TextView) itemView.findViewById(R.id.ticket_basket_tv_date);
                    tvTime = (TextView) itemView.findViewById(R.id.ticket_basket_tv_time);
                    break;
                case Constants.MOVIE_ACTIVITY:
                    tvSeatRaw = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_raw);
                    tvSeatPlace = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_place);
                    tvSeatPrice = (TextView) itemView.findViewById(R.id.ticket_basket_tv_seat_price);
                    b_cancel = (Button) itemView.findViewById(R.id.ticket_basket_b_cancel_order);
                    //b_cancel.setOnClickListener(onClickListener);
                    break;
            }

        }
    }
}
