package construction.thesquare.worker.reviews.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.reviews.ReviewsResponse;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Evgheni on 11/14/2016.
 */

public class ReviewRequestActivity extends AppCompatActivity {

    public static final String TAG = "ReviewRequestActivity";
    @BindView(R.id.get_first)
    JosefinSansEditText firstName;
    @BindView(R.id.get_last)
    JosefinSansEditText lastName;
    @BindView(R.id.get_company)
    JosefinSansEditText company;
    @BindView(R.id.get_email)
    JosefinSansEditText email;
    @BindView(R.id.get_mobile)
    JosefinSansEditText mobile;
    @BindView(R.id.err)
    JosefinSansTextView error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_review);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.close, R.id.cancel, R.id.request})
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.request:
                if (validate()) {
                    createReviewRequest();
                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_reference_request);
                    dialog.setCancelable(false); dialog.show();
                    dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            finish();

                        }
                    });
                    break;
                }
                break;
            case R.id.close:
                finish();
                break;
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(firstName.getText().toString())) {
            firstName.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            lastName.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(company.getText().toString())) {
            company.setError(getString(R.string.worker_request_field));
            return false;
        }
        if (TextUtils.isEmpty(mobile.getText().toString()) &&
                TextUtils.isEmpty(email.getText().toString())) {
            error.setVisibility(View.VISIBLE);
            return false;
        } else {
            error.setVisibility(View.GONE);
        }
        return true;
    }

    public void createReviewRequest() {
        Review review = new Review();

        review.requestEmail = email.getText().toString();
        review.requestFirstName = firstName.getText().toString();
        review.requestLastName = lastName.getText().toString();
        review.requestMobile = mobile.getText().toString();
        review.requestCompany = company.getText().toString();

        HttpRestServiceConsumer.getBaseApiClient().requestReview(review)
                .enqueue(new Callback<ReviewsResponse>() {
                    @Override
                    public void onResponse(Call<ReviewsResponse> call,
                                           Response<ReviewsResponse> response) {
                        //
                        if (response.isSuccessful()) {
                            TextTools.log(TAG,"succesfully created ");
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewsResponse> call, Throwable t) {

                    }
                });
    }

}