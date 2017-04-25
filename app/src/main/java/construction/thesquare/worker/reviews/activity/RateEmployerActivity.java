package construction.thesquare.worker.reviews.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.reviews.Review;
import construction.thesquare.shared.reviews.ReviewUpdateResponse;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by swapna on 4/3/2017.
 */

public class RateEmployerActivity extends AppCompatActivity {

    public static final String TAG = "RateWorkerActivity";

    @BindView(R.id.review_details_name)
    JosefinSansTextView header;
    @BindView(R.id.rating_view_attitude)
    RatingView attitude;
    @BindView(R.id.rating_view_quality)
    RatingView quality;
    @BindView(R.id.rating_view_reliability)
    RatingView reliability;
    @BindView(R.id.rating_view_safety)
    RatingView safety;
    @BindView(R.id.again)
    JosefinSansTextView again;
    /*  @BindView(R.id.got_hired) JosefinSansTextView gotHired;
     */ @BindView(R.id.rate_your_worker)
    LinearLayout rateWorker;
    @BindView(R.id.hired_view)
    LinearLayout hiredView;
    private boolean hireAgain;
    private boolean gotHiredAgain;
    private Review review;
    public static final int HIRE_AGAIN_YES = 1;
    public static final int HIRE_AGAIN_NO = 2;
    public static final int GOT_HIRE_AGAIN_YES = 1;
    public static final int GOT_HIRE_AGAIN_NO = 2;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
  /*  @BindView(R.id.radio_group_got_hired) RadioGroup radioGroupGotHired;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_employer);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        attitude.makeStarsRed(); quality.makeStarsRed();
        reliability.makeStarsRed(); safety.makeStarsRed();
        if (null != getIntent().getExtras().getSerializable("data")) {
            this.review = (Review) getIntent().getExtras().getSerializable("data");
            populate(review);
        }
       radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_no:
                        //Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
                        hireAgain = false;
                        attitude.setValue(0);
                        quality.setValue(0);
                        reliability.setValue(0);
                        safety.setValue(0);
                        break;
                    case R.id.radio_yes:
                        //Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
                        hireAgain = true;
                        break;
                }
            }
        });
       /*  radioGroupGotHired.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_no_got_hired:
                        gotHiredAgain = false;
                        attitude.setValue(0);
                        quality.setValue(0);
                        reliability.setValue(0);
                        safety.setValue(0);
                        rateWorker.setVisibility(View.GONE);
                        break;
                    case R.id.radio_yes_got_hired:
                        gotHiredAgain = true;
                        rateWorker.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });*/
    }

    private void populate(Review review) {
        header.setText(String.format(getString(R.string.worker_rate_employer), review.company));
        again.setText(R.string.employer_reviews_work_again);
       // gotHired.setText(String.format(getString(R.string.worker_turned_up), review.workerSummary.name));
    }

    @OnClick(R.id.close)
    public void close() {
        finish();
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (null != review) {
            Review patchedReview = new Review();
            try {
                patchedReview.environment = attitude.getValue();
                patchedReview.team = quality.getValue();
                patchedReview.payers = reliability.getValue();
                patchedReview.induction = safety.getValue();
                patchedReview.wouldHireAgain = hireAgain;
             //   patchedReview.gotHired = gotHiredAgain;
            } catch (Exception e) {
                TextTools.log("dx", (null != e.getMessage()) ? e.getMessage() : "");
            }

            HttpRestServiceConsumer.getBaseApiClient().updateReview(review.id, patchedReview)
                    .enqueue(new Callback<ReviewUpdateResponse>() {
                        @Override
                        public void onResponse(Call<ReviewUpdateResponse> call,
                                               Response<ReviewUpdateResponse> response) {
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ReviewUpdateResponse> call, Throwable t) {
                            //
                        }
                    });
        }
    }
}
