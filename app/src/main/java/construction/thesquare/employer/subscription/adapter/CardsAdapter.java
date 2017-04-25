package construction.thesquare.employer.subscription.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.subscription.model.CreditCard;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/29/2016.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CreditCardHolder> {

    private List<CreditCard> data = new ArrayList<>();
    private CardListener cardListener;

    public interface CardListener {
        void onCardPicked(CreditCard card);
        void onRemove(CreditCard card);
    }

    public CardsAdapter(CardListener listener, List<CreditCard> cards) {
        this.cardListener = listener;
        this.data = cards;
    }

    @Override
    public CreditCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CreditCardHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(CreditCardHolder holder, int position) {
        final CreditCard card = data.get(position);
        holder.name.setText(card.name);
        holder.number.setText("XXXX-XXXX-XXXX-" + card.last4);
        holder.expiration.setText(card.exp);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cardListener) {
                    cardListener.onRemove(card);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != cardListener) {
                    cardListener.onCardPicked(card);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CreditCardHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.card_name) JosefinSansTextView name;
        @BindView(R.id.card_number) JosefinSansTextView number;
        @BindView(R.id.card_expiration)
        JosefinSansTextView expiration;

        public CreditCardHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
