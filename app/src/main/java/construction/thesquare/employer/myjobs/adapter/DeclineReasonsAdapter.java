package construction.thesquare.employer.myjobs.adapter;

import android.content.Context;
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
import construction.thesquare.shared.models.Reason;

/**
 * Created by gherg on 3/17/17.
 */

public class DeclineReasonsAdapter extends
        RecyclerView.Adapter<DeclineReasonsAdapter.ReasonHolder> {

    public static final String TAG = "DeclineReasonsAdapter";

    private List<Reason> data = new ArrayList<>();
    private Context context;
    private DeclineReasonListener listener;

    public void setListener(DeclineReasonListener declineReasonListener) {
        this.listener = declineReasonListener;
    }

    public interface DeclineReasonListener {
        void onReason(Reason reason);
    }

    public DeclineReasonsAdapter(Context context, List<Reason> reasons) {
        this.context = context;
        this.data = reasons;
    }

    @Override
    public ReasonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReasonHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_decline_reason, parent, false));
    }

    @Override
    public void onBindViewHolder(ReasonHolder holder, int position) {
        final Reason reason = data.get(position);
        if (null != reason.name) {
            holder.name.setText(reason.name);
        }
        if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onReason(reason);
                }
            });
        }
        holder.check.setChecked(reason.selected);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ReasonHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "ReasonHolder";

        @BindView(R.id.item_reason_name) TextView name;
        @BindView(R.id.item_reason_check) CheckBox check;

        public ReasonHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
