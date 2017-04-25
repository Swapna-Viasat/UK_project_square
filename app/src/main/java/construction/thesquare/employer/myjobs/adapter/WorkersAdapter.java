package construction.thesquare.employer.myjobs.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Application;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gherg on 1/4/2017.
 */

public class WorkersAdapter extends RecyclerView.Adapter<WorkersAdapter.WorkerHolder> {

    public static final String TAG = "WorkersAdapter";

    private List<Worker> data = new ArrayList<>();
    private WorkersActionListener listener;
    private Context context;
    private int type;

    public WorkersAdapter(List<Worker> list,
                          Context context,
                          WorkersActionListener l,
                          int adapterType) {
        this.data = list;
        this.context = context;
        this.listener = l;
        this.type = adapterType;
    }

    public interface WorkersActionListener {
        void onInvite(Worker worker);

        void onBook(Worker worker);

        void onViewWorkerProfile(Worker worker);

        void onLikeWorkerClick(Worker worker);
    }

    @Override
    public WorkerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkerHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_worker_in_job_details, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkerHolder holder, int position) {
        final Worker worker = data.get(position);

        switch (type) {
            case Constants.ADAPTER_FOR_BOOK:
                holder.workerAction.setText("Offer Job");
                break;
            case Constants.ADAPTER_FOR_CONNECT:
                holder.workerAction.setText("Connect");
                break;
        }

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

        if (null != worker.matchedRole) {
            if (null != worker.matchedRole.name) {
                holder.workerOccupation.setText(worker.matchedRole.name);
            }
        }

        holder.workerRating.setRating((int) worker.rating);

        if (null != worker.picture) {
            holder.avatar.setImageDrawable(ContextCompat
                    .getDrawable(context, R.drawable.bob));
            Picasso.with(context)
                    .load(worker.picture)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .fit()
                    .centerCrop()
                    .into(holder.avatar);
        } else {
            holder.avatar.setImageDrawable(ContextCompat
                    .getDrawable(context, R.drawable.bob));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onViewWorkerProfile(worker);
            }
        });

        updateStatus(holder, worker);

        holder.availableNow.setVisibility(worker.now ? View.VISIBLE : View.GONE);

        setLiked(worker.liked, holder.likeImage);

        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onLikeWorkerClick(worker);
            }
        });
    }

    private void updateStatus(WorkerHolder workerHolder, final Worker worker) {
        if (null != worker.applications) {
            if (!worker.applications.isEmpty()) {
                workerHolder.workerAction.setVisibility(View.GONE);
                //
                workerHolder.workerLabel.setVisibility(View.VISIBLE);
                // now pick the right label
                final Application application = worker.applications.get(0);
                switch (application.status.id) {
                    case Application.STATUS_PENDING:
                        if (application.isOffer) {
                            workerHolder.workerLabel.setImageDrawable(ContextCompat
                                    .getDrawable(context, R.drawable.workers_offered));
                        } else {

                            workerHolder.workerAction.setVisibility(View.VISIBLE);
                            workerHolder.workerAction.setText("Book");
                            workerHolder.workerAction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (null != listener) listener.onBook(worker);
                                }
                            });

                            workerHolder.workerLabel.setImageDrawable(ContextCompat
                                    .getDrawable(context, R.drawable.workers_applied));
                        }
                        break;
                    case Application.STATUS_APPROVED:
                        if (type == Constants.ADAPTER_FOR_CONNECT) {
                            workerHolder.workerLabel.setImageDrawable(ContextCompat
                                    .getDrawable(context, R.drawable.worker_connected));
                        } else {
                            workerHolder.workerLabel.setImageDrawable(ContextCompat
                                    .getDrawable(context, R.drawable.workers_booked));
                        }
                        break;
                    case Application.STATUS_CANCELLED:
                        workerHolder.workerLabel.setImageDrawable(ContextCompat
                                .getDrawable(context, R.drawable.workers_declined));
                        break;
                    case Application.STATUS_DENIED:
                        workerHolder.workerLabel.setImageDrawable(ContextCompat
                                .getDrawable(context, R.drawable.workers_declined));
                        break;
                    case Application.STATUS_ENDED_CONTRACT:
                        workerHolder.workerLabel.setImageDrawable(ContextCompat
                                .getDrawable(context, R.drawable.workers_declined));
                        break;
                    default:
                        //
                        break;
                }
                //
            } else {
                workerHolder.workerAction.setVisibility(View.VISIBLE);
                workerHolder.workerAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != listener) {
                            listener.onInvite(worker);
                        }
                    }
                });
                workerHolder.workerLabel.setVisibility(View.GONE);
            }
        } else {
            workerHolder.workerAction.setVisibility(View.VISIBLE);
            workerHolder.workerLabel.setVisibility(View.GONE);
        }
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
        @BindView(R.id.worker_avatar)
        CircleImageView avatar;
        @BindView(R.id.likeImage)
        ImageView likeImage;

        public WorkerHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}