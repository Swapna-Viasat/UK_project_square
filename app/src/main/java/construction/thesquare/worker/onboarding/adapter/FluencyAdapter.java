package construction.thesquare.worker.onboarding.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.EnglishLevel;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/7/2016.
 */

public class FluencyAdapter extends RecyclerView.Adapter<FluencyAdapter.FluencyHolder> {

    public static final String TAG = "FluencyAdapter";

    private List<EnglishLevel> data = new ArrayList<>();

    public FluencyAdapter(List<EnglishLevel> levels) {
        this.data = levels;
    }

    @Override
    public FluencyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FluencyHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_fluency, parent, false));
    }

    @Override
    public void onBindViewHolder(final FluencyHolder holder, int position) {
        final EnglishLevel level = data.get(position);
        if (position == getItemCount() - 1) {
            holder.bottom.setVisibility(View.GONE);
        }
        if (position == 0) {
            holder.header.setVisibility(View.VISIBLE);
        } else {
            holder.header.setVisibility(View.GONE);
        }
        holder.title.setText(level.name);
        holder.radioButton.setChecked(level.selected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onFluency(level);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class FluencyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rb) RadioButton radioButton;
        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.english_header) JosefinSansTextView header;
        @BindView(R.id.bottom) View bottom;

        public FluencyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private FluencyListener listener;
    public void setListener(FluencyListener fluencyListener) {
        this.listener = fluencyListener;
    }

    public interface FluencyListener {
        void onFluency(EnglishLevel englishLevel);
    }
}
