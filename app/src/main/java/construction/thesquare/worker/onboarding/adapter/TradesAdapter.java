package construction.thesquare.worker.onboarding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Trade;

/**
 * Created by gherg on 12/6/2016.
 */

public class TradesAdapter extends RecyclerView.Adapter<TradesAdapter.TradeHolder> {

    public static final String TAG = "TradesAdapter";

    private List<Trade> data = new ArrayList<>();
    private TradesListener listener;

    public TradesAdapter(List<Trade> trades) {
        this.data = trades;
    }

    @Override
    public TradeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TradeHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_trade, parent, false));
    }

    @Override
    public void onBindViewHolder(final TradeHolder holder, final int position) {
        final Trade trade = data.get(position);
        holder.title.setText(trade.name);
        holder.check.setChecked(trade.selected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onTradeClick(trade);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class TradeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.check) CheckBox check;

        public TradeHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setListener(TradesListener tradesListener) {
        this.listener = tradesListener;
    }

    public interface TradesListener {
        void onTradeClick(Trade trade);
    }
}
