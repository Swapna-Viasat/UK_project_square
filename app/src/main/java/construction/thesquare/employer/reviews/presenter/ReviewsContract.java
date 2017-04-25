package construction.thesquare.employer.reviews.presenter;

import java.util.List;

import construction.thesquare.shared.reviews.Review;

/**
 * Created by Evgheni on 11/11/2016.
 */

public interface ReviewsContract {
    interface View {
        void displayReviews(List<Review> data);
        void displayReview(Review review);
    }
    interface UserActionListener {
        void fetchReview(Review review);
        void fetchReviews(int tabId);
    }
}