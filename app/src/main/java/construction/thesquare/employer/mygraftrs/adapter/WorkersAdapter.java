package construction.thesquare.employer.mygraftrs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.mygraftrs.model.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;

/**
 * Created by Evgheni on 10/21/2016.
 */

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkerHolder> {

    private List<Worker> data = new ArrayList<>();
    private WorkersActionListener listener;
    private Context context;

    public WorkersAdapter(List<Worker> list, Context context, WorkersActionListener l) {
        this.data = list;
        this.context = context;
        this.listener = l;
    }

    public interface WorkersActionListener {
        void onQuickInvite(Worker worker);

        void onCancelBooking(Worker worker);

        void onEndContract(Worker worker);

        void onViewDetails(Worker worker);

        void onLikeWorkerClick(Worker worker);
    }

    @Override
    public WorkerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkerHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_worker, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkerHolder holder, int position) {
        final Worker worker = data.get(position);
        holder.workerAction.setVisibility(View.GONE);
        holder.workerRating.makeStarsRed();
        if (worker.firstName != null) {
            if (worker.lastName != null) {
                holder.workerName.setText(worker.firstName + " " + worker.lastName);
            } else {
                holder.workerName.setText(worker.firstName);
            }
        } else {
            if (worker.lastName != null) holder.workerName.setText(worker.lastName);
        }
        if (!CollectionUtils.isEmpty(worker.roles)) {
            if (worker.roles.get(0) != null) {
                if (worker.roles.get(0).name != null)
                    holder.workerOccupation.setText(worker.roles.get(0).name);
            }
        }
        holder.workerRating.setRating(worker.rating);
        if (worker.availableNow) holder.availableNow.setVisibility(View.VISIBLE);
        else holder.availableNow.setVisibility(View.GONE);

        setLiked(worker.liked, holder.likeImage);

        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onLikeWorkerClick(worker);
            }
        });

        if (worker.picture != null) Picasso.with(holder.itemView.getContext())
                .load(worker.picture)
                .error(R.drawable.bob)
                .placeholder(R.drawable.bob)
                .into(holder.workerAvatar);

//        switch (worker.status.id) {
//            case Worker.STATUS_APPLIED:
//                holder.workerAction.setText("");
//                holder.workerLabel.setImageResource(R.drawable.workers_applied);
//                break;
//            case Worker.STATUS_BOOKED:
//                holder.workerAction.setText(context.getString(R.string.employer_workers_cancel));
//                holder.workerLabel.setImageResource(R.drawable.workers_booked);
//                break;
//            case Worker.STATUS_DECLINED:
//                holder.workerAction.setText(context.getString(R.string.employer_workers_invite));
//                holder.workerLabel.setImageResource(R.drawable.workers_declined);
//                break;
//            case Worker.STATUS_OFFERED:
//                holder.workerAction.setText(context.getString(R.string.employer_workers_end));
//                holder.workerLabel.setImageResource(R.drawable.workers_offered);
//                break;
//            case Worker.STATUS_PREVIOUS:
//                holder.workerLabel.setVisibility(View.GONE);
//                holder.workerAction.setText(context.getString(R.string.employer_workers_invite));
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewDetails(worker);
            }
        });
//        holder.workerAction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onEndContract(worker);
//                switch (((TextView) v).getText().toString()) {
//                    case "Cancel Booking":
//                        listener.onCancelBooking(worker);
//                        break;
//                    case "Quick Invite":
//                        listener.onQuickInvite(worker);
//                        break;
//                    case "End Contract":
//                        listener.onEndContract(worker);
//                        break;
//                }
//            }
//        });

    }

    private void setLiked(boolean liked, ImageView imageView) {
        imageView.setImageResource(liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class WorkerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.worker_rating)
        RatingView workerRating;
        @BindView(R.id.worker_name)
        JosefinSansTextView workerName;
        @BindView(R.id.worker_occupation)
        JosefinSansTextView workerOccupation;
        @BindView(R.id.worker_label)
        ImageView workerLabel;
        @BindView(R.id.worker_action_button)
        TextView workerAction;
        @BindView(R.id.worker_additional_info)
        View availableNow;
        @BindView(R.id.likeImage)
        ImageView likeImage;
        @BindView(R.id.worker_avatar)
        ImageView workerAvatar;

        public WorkerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
