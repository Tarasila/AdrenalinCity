package project.taras.ua.adrenalincity.Activity.MovieMVC;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 29.06.2017.
 */

public class AdapterRvTime extends RecyclerView.Adapter<AdapterRvTime.ViewHolder> {

    private Context context;
    private List<String> listTime;
    private RecyclerView recyclerView;
    private IAdapterListener.IAdapterClickListener clickListener;

    boolean dataIsLoaded = false;

    public AdapterRvTime(Context context) {
        this.context = context;
    }

    public void addRvRef(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setOnTimeClickListener(IAdapterListener.IAdapterClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v("rv_click_time", "click");
            int childPosition = recyclerView.getChildLayoutPosition(v);
            clickListener.onTimeClicked(childPosition);

            TextView tv = (TextView) v.findViewById(R.id.cus_tv_time);

            TextView prevClicked = (TextView) recyclerView.findViewWithTag("red");
            if (prevClicked != null) {
                Log.v("prev_view", prevClicked.getTag().toString());
                removeRedHighlight(prevClicked);
            } else {
                Log.v("prev_view", "without tag");
                highlightWithRed(tv);
            }
            highlightWithRed(tv);
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_time_layout, null);
        view.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTime.setText(listTime.get(position));
    }

    volatile boolean alreadyClicked = false;

    public void setFirstClickStatus(boolean status) {
        this.alreadyClicked = status;
    }

    public void setDataIsLoadedStatus(boolean status) {
        this.dataIsLoaded = status;
    }

    @Override
    public int getItemCount() {
        if (dataIsLoaded) {
            Log.v("tag_count_", "data is loaded");
            if (!alreadyClicked) {
                Log.v("tag_count_", "not clicked yet");
                alreadyClicked = true;

                clickListener.onTimeAdapterIsReady();
            }
            return listTime.size();
        }
        return 0;
    }

    public void setData(List<String> timeList) {
        this.listTime = timeList;
        dataIsLoaded = true;
        TextView prevClicked = (TextView) recyclerView.findViewWithTag("red");
        if (prevClicked != null) {
            removeRedHighlight(prevClicked);
        }
        notifyDataSetChanged();
    }

    private void removeRedHighlight(TextView timeView) {
        timeView.setTextColor(Color.WHITE);
        timeView.setTag("white");
        timeView.invalidate();
    }

    private void highlightWithRed(TextView timeView) {
        timeView.setTextColor(Color.RED);
        timeView.setTag("red");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.cus_tv_time);
        }
    }

    public void performClick(final int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.findViewHolderForAdapterPosition(position).itemView.performClick();

            }
        }, 100);
    }
}
