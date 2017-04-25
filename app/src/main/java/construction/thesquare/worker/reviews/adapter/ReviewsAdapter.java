package construction.thesquare.worker.reviews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by Evgheni on 11/11/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {

    private List<Review> data = new ArrayList<>();
    private ReviewsListener listener;
    private Context context;
    private Worker worker;


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
                .from(context).inflate(R.layout.item_review, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        final Review review = data.get(position);
      if (null != listener) {
            if(review.type.id == Review.REVIEW_TYPE_EMPLOYER ) {
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
            if (null != review.requestCompany && review.automatedRequest == false) {
                holder.company.setText(review.requestCompany);
                if (null != review.dateReviewRequested)
                    holder.date.setText("Date Requested: " + review.dateReviewRequested);
                holder.requestedby.setText(R.string.worker_reviews_requested_by_worker);
            }
            if (null != review.company) {
                if (review.automatedRequest == true && null != review.dateReviewRequested) {
                    holder.company.setText(review.company);
                    holder.date.setText("Date Requested: " + review.dateReviewRequested);
                    holder.requestedby.setText(R.string.worker_reviews_requested_by_square);
                }
            }
        }else if(review.type.id == Review.REVIEW_TYPE_EMPLOYER ) {
            holder.company.setText(review.company);
            holder.date.setText("Date Requested: " + review.dateReviewRequested);
            holder.requestedby.setText(R.string.worker_reviews_requested_by_employer);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_review_company) JosefinSansTextView company;
        @BindView(R.id.item_requested_by) JosefinSansTextView requestedby;
        @BindView(R.id.item_review_date) JosefinSansTextView date;
       public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
