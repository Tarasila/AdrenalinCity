package project.taras.ua.adrenalincity.Activity.MovieMVC;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import project.taras.ua.adrenalincity.Activity.MovieMVC.MovieView.Show;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 11.05.2017.
 */

public class AdapterRvDate extends RecyclerView.Adapter<AdapterRvDate.ViewHolder> {

    private Context context;
    private RecyclerView recyclerView;
    private LinkedHashMap<String, List<Show>> dataHashMap = new LinkedHashMap<>();
    private List<String> keyListOfDays;
    private IAdapterListener.IAdapterClickListener clickListener;

    public AdapterRvDate(Activity context) {
        this.context = context;
    }

    public void addRvRef(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setOnDateClickListener(IAdapterListener.IAdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_date_layout, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("rv_click_date", "click");
            int childPosition = recyclerView.getChildLayoutPosition(v);
            String keyDay = keyListOfDays.get(childPosition);
            clickListener.onDateClicked(keyDay, childPosition);
            TextView tv = (TextView) v.findViewById(R.id.cus_tv_date_digit);

            TextView prevClicked = (TextView) recyclerView.findViewWithTag("red");
            if (prevClicked != null) {
                Log.v("prevtv", prevClicked.getText().toString());
                prevClicked.setTextColor(Color.WHITE);
                prevClicked.setTag("white");
                prevClicked.invalidate();
            }else {

                tv.setTextColor(Color.RED);
                tv.setTag("red");
            }

            tv.setTextColor(Color.RED);
            tv.setTag("red");
        }
    };

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = keyListOfDays.get(position);
        Show randomShow = dataHashMap.get(key).get(0);
        holder.tvDateDigit.setText(randomShow.getDay());
        holder.tvDateText.setText(randomShow.getDayOfWeek());
        holder.tvDateMonth.setText(randomShow.getMonth());
    }

    @Override
    public int getItemCount() {
        return dataHashMap.size();
    }

    public void setData(LinkedHashMap<String, List<Show>> hashMapShow) {
        this.dataHashMap = hashMapShow;
        keyListOfDays = new ArrayList<>();
        for (Map.Entry<String, List<Show>> m : dataHashMap.entrySet()) {
            keyListOfDays.add(m.getKey());
        }
        //notifyItemInserted(hashMapShow.size() - 1);
        notifyDataSetChanged();
    }

    public List<String> getKeyListOfDays(){
        return keyListOfDays;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateDigit;
        TextView tvDateText;
        TextView tvDateMonth;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDateDigit = (TextView) itemView.findViewById(R.id.cus_tv_date_digit);
            tvDateText = (TextView) itemView.findViewById(R.id.cus_tv_date_text);
            tvDateMonth = (TextView) itemView.findViewById(R.id.cus_tv_date_month);
        }
    }

    public void performClick(final int position){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();

            }
        },100);
    }
}
