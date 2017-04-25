package construction.thesquare.employer.reviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Evgheni on 11/11/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private List<Review> data = new ArrayList<>();
    private ReviewsListener listener;
    private Context context;

    public interface ReviewsListener {
        void onViewDetails(Review review);
        void onCompleteReview(Review review);
    }

    public ReviewsAdapter(List<Review> reviews, Context context, ReviewsListener reviewsListener) {
        this.data = reviews;
        this.context = context;
        this.listener = reviewsListener;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReviewHolder(LayoutInflater
                .from(context).inflate(R.layout.item_review_employer, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        final Review review = data.get(position);
        if (null != listener) {
            if(review.type.id == Review.REVIEW_TYPE_WORKER ) {
                if (review.status.id == Review.CAT_PENDING) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onCompleteReview(review);
                        }
                    });
                } else if (review.status.id == Review.CAT_PUBLISHED) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onViewDetails(review);
                        }
                    });
                }
            }
        }
        if(review.type.id == Review.REVIEW_TYPE_WORKER ) {
            if (null != review.workerSummary.name) {
                holder.name.setText(review.workerSummary.name);
            }
            if (null != review.workerSummary.role) {
                holder.position.setText(review.workerSummary.role);
            }
            if (null != review.workerSummary.picture) {
                Picasso.with(context)
                        .load(review.workerSummary.picture)
                        .into(holder.avatar);
            } else {
                holder.avatar.setImageResource(R.drawable.bob);
            }

            if (null != review.dateReviewRequested) {
                holder.date.setText("Date Requested: " + review.dateReviewRequested);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.worker_account_avatar)
        CircleImageView avatar;
        @BindView(R.id.item_worker_name) JosefinSansTextView name;
        @BindView(R.id.item_review_date) JosefinSansTextView date;
        @BindView(R.id.item_role) JosefinSansTextView position;
        public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
