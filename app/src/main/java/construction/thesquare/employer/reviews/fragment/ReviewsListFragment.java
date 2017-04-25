package construction.thesquare.employer.reviews.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.reviews.RateWorkerActivity;
import construction.thesquare.employer.reviews.ReviewDetailsActivity;
import construction.thesquare.employer.reviews.adapter.ReviewsAdapter;
import construction.thesquare.employer.reviews.presenter.ReviewsContract;
import construction.thesquare.employer.reviews.presenter.ReviewsPresenter;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/11/2016.
 */

public class ReviewsListFragment extends Fragment
        implements ReviewsAdapter.ReviewsListener,
        ReviewsContract.View {

    public static final String TAG = "ReviewsListFragment";
    private ReviewsContract.UserActionListener mUserActionListener;
    private List<Review> data = new ArrayList<>();
    private ReviewsAdapter adapter;
    private int employerId;
    private Employer employer;
    @BindView(R.id.worker_reviews_rv) RecyclerView recyclerView;
    @BindView(R.id.reviews_no_data) LinearLayout noData;
    @BindView(R.id.review_details_companies_reviews_value)
    JosefinSansTextView companyReviews;
    @BindView(R.id.review_details_companies_turned_up_value) JosefinSansTextView companiesTurnedUpReviews;
    @BindView(R.id.global)
    RatingView global;
    @BindView(R.id.rating_view_quality)
    RatingView environment;
    @BindView(R.id.rating_view_reliability) RatingView team;
    @BindView(R.id.rating_view_safety) RatingView payers;
    @BindView(R.id.aggregate_worker_review) LinearLayout aggregate;
    @BindView(R.id.rating_view_attitude)
    RatingView induction;

    public static ReviewsListFragment newInstance(int category) {
        ReviewsListFragment reviewsListFragment = new ReviewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("category", category);
        reviewsListFragment.setArguments(bundle);
        return reviewsListFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        employerId = SharedPreferencesManager.getInstance(getContext()).getEmployerId();
        mUserActionListener = new ReviewsPresenter(this, getContext());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reviews_employers_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewsAdapter(data, getContext(), this);
        adapter.registerAdapterDataObserver(observer);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserActionListener.fetchReviews(getArguments().getInt("category"));
    }



    @Override
    public void displayReviews(List<Review> reviews) {
        if (!data.isEmpty()) data.clear();
        if (getArguments().getInt("category") == Review.TAB_PENDING) {
            aggregate.setVisibility(View.GONE);
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PENDING && review.type.id == Review.REVIEW_TYPE_WORKER) {
                    data.add(review);
                }
            }
        } else if (getArguments().getInt("category") == Review.TAB_GIVEN) {
            aggregate.setVisibility(View.GONE);
            for (Review review : reviews) {
                if (review.status.id == Review.CAT_PUBLISHED
                        && review.type.id == Review.REVIEW_TYPE_WORKER) {
                    data.add(review);
                }
            }
        } else if (getArguments().getInt("category") == Review.TAB_RECEIVED) {
            fetchAggregateReviews();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayReview(Review review) {
        Intent intent = new Intent(getActivity(), ReviewDetailsActivity.class);
        Bundle data = new Bundle(); data.putSerializable("data", review);
        intent.putExtras(data); startActivity(intent);
    }

    @Override
    public void onViewDetails(Review review) {
        mUserActionListener.fetchReview(review);
    }

    @Override
    public void onCompleteReview(Review review) {
        Intent intent = new Intent(getActivity(), RateWorkerActivity.class);
        Bundle data = new Bundle(); data.putSerializable("data", review);
        intent.putExtras(data); startActivity(intent);
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (data.isEmpty()) noData.setVisibility(View.VISIBLE);
            else noData.setVisibility(View.GONE);
        }
    };

    public Employer fetchAggregateReviews() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getEmployerAggregateReview(employerId)
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            noData.setVisibility(View.GONE);
                            aggregate.setVisibility(View.VISIBLE);
                            employer = response.body().getResponse();
                            if (employer != null && employer.reviewData != null) {
                                companyReviews.setText(String.valueOf(employer.reviewData.reviewsCount));
                                // turnedUpReviews.setText(String.valueOf(employer.reviewData.showedToWorkTotal)+"%");
                                companiesTurnedUpReviews.setText(String.valueOf(employer.reviewData.wouldWorkTotal)+"%");
                                environment.makeStarsRed();
                                environment.setRating(employer.reviewData.environment);
                                team.makeStarsRed();
                                team.setRating(employer.reviewData.team);
                                payers.makeStarsRed();
                                payers.setRating(employer.reviewData.payers);
                                induction.makeStarsRed();
                                induction.setRating(employer.reviewData.induction);
                                global.makeStarsRed();
                                global.setRating(employer.reviewData.globalRating);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
        return employer;
    }
}