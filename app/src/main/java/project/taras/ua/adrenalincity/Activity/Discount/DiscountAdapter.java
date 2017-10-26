package project.taras.ua.adrenalincity.Activity.Discount;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import project.taras.ua.adrenalincity.R;

/**
 * Created by Taras on 18.07.2017.
 */

public class DiscountAdapter extends RecyclerView.Adapter<DiscountAdapter.ViewHolder> {

    private Context context;
    private List<String> listDiscount = new ArrayList<>();

    public DiscountAdapter(Context context) {
        this.context = context;
    }

    public void setDiscountList(List<String> list) {
        this.listDiscount = list;
        notifyDataSetChanged();
    }

    @Override
    public DiscountAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_discount_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscountAdapter.ViewHolder holder, int position) {
        String imgUrl = listDiscount.get(position);
        Picasso.with(context).load(imgUrl).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return listDiscount.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_discount);
        }
    }

}
