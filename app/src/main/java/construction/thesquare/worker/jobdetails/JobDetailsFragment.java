package construction.thesquare.worker.jobdetails;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.worker.jobmatches.model.Application;
import construction.thesquare.worker.jobmatches.model.ApplicationStatus;
import construction.thesquare.worker.jobmatches.model.Job;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Vadim Goroshevsky
 */

public class JobDetailsFragment extends Fragment implements JobDetailsContract {

    @BindView(R.id.company_name)
    JosefinSansTextView companyName;
    @BindView(R.id.job_id)
    JosefinSansTextView jobId;
    @BindView(R.id.companyLogo)
    ImageView companyLogo;
    @BindView(R.id.job_role)
    TextView itemRole;
    @BindView(R.id.job_experience_years)
    TextView experienceYears;
    @BindView(R.id.job_item_payment_rate_per)
    TextView paymentRatePer;
    @BindView(R.id.job_item_payment_rate)
    TextView paymentRate;
    @BindView(R.id.job_item_start_date)
    TextView startDate;
    @BindView(R.id.job_item_work_place)
    TextView workPlace;
    @BindView(R.id.btnWorkerFirstStepNext)
    TextView ctaButton;
    @BindView(R.id.appliedHintView)
    TextView appliedHeaderView;
    @BindView(R.id.approvedHintView)
    View approvedHeaderView;
    @BindView(R.id.reportingToTextView)
    TextView reportingToTextView;
    @BindView(R.id.reportingToPhoneTextView)
    TextView reportingToPhoneTextView;
    @BindView(R.id.reportingToAddressTextView)
    TextView reportingToAddressTextView;
    @BindView(R.id.dateToArriveTextView)
    TextView dateToArriveTextView;
    @BindView(R.id.elseToNoteTextView)
    TextView elseToNoteTextView;
    @BindView(R.id.approvedHint)
    View approvedHintView;
    //
    @BindView(R.id.job_details_description)
    JosefinSansTextView description;
    @BindView(R.id.job_details_skills)
    JosefinSansTextView skills;
    @BindView(R.id.job_details_english_level)
    JosefinSansTextView englishLevel;
    @BindView(R.id.job_details_overtime)
    JosefinSansTextView overtime;
    @BindView(R.id.job_details_qualifications)
    JosefinSansTextView qualifications;
    @BindView(R.id.job_details_qualifications2)
    JosefinSansTextView qualifications2;
    @BindView(R.id.job_details_experience_types)
    JosefinSansTextView experienceTypes;
    @BindView(R.id.acceptOfferBtn)
    Button acceptOfferButton;
    @BindView(R.id.contactEmailView)
    TextView contactEmailTextView;
    @BindView(R.id.dateToArriveLabel)
    TextView dateToArriveLabel;
    @BindView(R.id.addressToArriveLabel)
    TextView addressToArriveLabel;
    @BindView(R.id.bookedHint)
    TextView bookedHint;

    private static final String TAG = "JobDetailsFragment";
    private static final String KEY_JOB = "KEY_JOB";

    private int currentJobId;
    private Job currentJob;
    private JobDetailsPresenter presenter;
    private MenuItem likeJobMenuItem, unlikeJobMenuItem;

    private SupportMapFragment mapFragment;
    private Dialog dialog;

    public JobDetailsFragment() {
    }

    public static JobDetailsFragment newInstance(int jobId) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_JOB, jobId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentJobId = getArguments().getInt(KEY_JOB);
        presenter = new JobDetailsPresenter(this);


        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_JOB_DETAILS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_job_details_worker, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment_worker);
    }

    private void populateData() {
        if (currentJob != null) {
            if (currentJob.company != null) {
                if (null != currentJob.company.name) {
                    companyName.setText(currentJob.company.name);
                }
                if (null != currentJob.company.logo) {
                    companyName.setVisibility(View.GONE);
                    companyLogo.setVisibility(View.VISIBLE);
                    Picasso.with(getActivity())
                            .load(currentJob.company.logo)
                            .fit().centerCrop()
                            .into(companyLogo);
                } else {
                    companyLogo.setVisibility(View.GONE);
                    companyName.setVisibility(View.VISIBLE);
                }
            }

            if (null != currentJob.locationName) {
                workPlace.setText(currentJob.locationName);
            }

            if (currentJob.role != null) {
                itemRole.setText(currentJob.role.name.toUpperCase());
            }

            experienceYears.setText(String.format(getString(R.string.item_match_format_experience),
                    currentJob.experience, getResources().getQuantityString(R.plurals.year_plural, currentJob.experience)));

            paymentRate.setVisibility(View.VISIBLE);
            paymentRate.setText(getString(R.string.pound_sterling) + String.valueOf(NumberFormat
                    .getInstance(Locale.UK).format(Double.valueOf(currentJob.budget))));

            if (null != currentJob.budgetType) {
                if (null != currentJob.budgetType.name) {
                    paymentRatePer.setText("Per " + currentJob.budgetType.name);
                }
                if (currentJob.budgetType.id == 4) {
                    paymentRatePer.setText("£POA");
                    paymentRate.setVisibility(View.GONE);
                }
            }

            jobId.setText("Job ref ID: " + currentJob.jobRef);

            try {

                if (currentJob.isConnect) {
                    if (!TextUtils.isEmpty(currentJob.startTime)) {
                        startDate.setText(String.format(getString(R.string.employer_jobs_app_deadline),
                                DateUtils.getFormattedJobDate(currentJob.startTime)));
                    }
                } else {
                    if (!TextUtils.isEmpty(currentJob.startTime)) {
                        startDate.setText(String.format(getString(R.string.item_match_format_starts),
                                DateUtils.getFormattedJobDate(currentJob.startTime)));
                    }
                }


                description.setText(currentJob.description);
                skills.setText(TextTools.toBulletList(currentJob.getSkillsList(), true));
                qualifications.setText(TextTools.toBulletList(currentJob.getRequirementsList(), true));
                qualifications2.setText(TextTools.toBulletList(currentJob.getQualificationsList(), true));
                experienceTypes.setText(TextTools.toBulletList(currentJob.getExperienceTypesList(), true));
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }

            String englishString = "Basic";
            switch (currentJob.englishLevel) {
                case 2:
                    englishString = "Fluent";
                    break;
                case 3:
                    englishString = "Native";
                    break;
            }
            englishLevel.setText(englishString);

            if (currentJob.payOvertime) {
                overtime.setText(String.format(getString(R.string.job_details_overtime_text),
                        (int) currentJob.payOvertimeValue));
            }

            if (currentJob.status != null) {
                if (TextUtils.equals(currentJob.status.name, "Old")) {
                    likeJobMenuItem.setVisible(false);
                    unlikeJobMenuItem.setVisible(false);
                }
            }
        }
    }

    private void setupApplicationData() {
        if (currentJob != null) {
            if (showCtaButton()) {
                ctaButton.setVisibility(View.VISIBLE);

                if (getCurrentAppStatus() == ApplicationStatus.STATUS_APPROVED) onBooked();
                else if (getCurrentAppStatus() == ApplicationStatus.STATUS_PENDING) {
                    if (getCurrentApplication() != null && getCurrentApplication().isOffer)
                        onOffered();
                    else onApplied();
                } else onApplicationNull();
            } else {
                ctaButton.setVisibility(View.GONE);
                acceptOfferButton.setVisibility(View.GONE);
            }

            appliedHeaderView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_PENDING ? View.VISIBLE : View.GONE);
            approvedHeaderView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_APPROVED ? View.VISIBLE : View.GONE);
            approvedHintView.setVisibility(getCurrentAppStatus()
                    == ApplicationStatus.STATUS_APPROVED ? View.VISIBLE : View.GONE);
        }
    }

    private boolean showCtaButton() {
        return (getCurrentAppStatus() == 0 ||
                getCurrentAppStatus() != ApplicationStatus.STATUS_CANCELLED
                        && getCurrentAppStatus() != ApplicationStatus.STATUS_DENIED
                        && getCurrentAppStatus() != ApplicationStatus.STATUS_END_CONTRACT);
    }

    private void onApplied() {
        ctaButton.setText(getString(R.string.employer_workers_cancel));
        ctaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
        acceptOfferButton.setVisibility(View.GONE);
    }

    private void onBooked() {
        acceptOfferButton.setVisibility(View.GONE);
        ctaButton.setText(getString(R.string.employer_workers_cancel));
        ctaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
        elseToNoteTextView.setText(currentJob.extraNotes);
        reportingToTextView.setText(currentJob.contactName);
        reportingToPhoneTextView.setText(currentJob.contactPhone);

        if (currentJob.isConnect) {
            bookedHint.setText("Boom! You're now connected with " + currentJob.contactName +
                    "\nAll the details are available below.\n" +
                    "\nGood luck.");

            if (!TextUtils.isEmpty(currentJob.connectEmail)) {
                contactEmailTextView.setVisibility(View.VISIBLE);
                contactEmailTextView.setText(currentJob.connectEmail);
            }
            dateToArriveTextView.setVisibility(View.GONE);
            dateToArriveLabel.setVisibility(View.GONE);
            addressToArriveLabel.setVisibility(View.GONE);
            reportingToAddressTextView.setVisibility(View.GONE);
        } else {
            bookedHint.setText(getString(R.string.job_details_success_message));
            dateToArriveTextView.setVisibility(View.VISIBLE);
            dateToArriveLabel.setVisibility(View.VISIBLE);
            addressToArriveLabel.setVisibility(View.VISIBLE);
            reportingToAddressTextView.setVisibility(View.VISIBLE);
            reportingToAddressTextView.setText(currentJob.address);
            contactEmailTextView.setVisibility(View.GONE);
            dateToArriveTextView.setText(DateUtils
                    .formatDateMonthDayAndTime(currentJob.startTime));
        }
    }

    private void onOffered() {
        if (currentJob.isConnect) {
            appliedHeaderView.setText("You've been offered the job! " +
                    "You can now accept or reject the connection with employer.");
            acceptOfferButton.setVisibility(View.VISIBLE);
            acceptOfferButton.setText("Accept connection");
            acceptOfferButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentApplication() != null) {
                        acceptOffer(getCurrentApplication().id);
                    }
                }
            });
            ctaButton.setText("Reject connection");
            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentApplication() != null) {
                        showDeclineOfferDialog(getCurrentApplication().id);
                    }
                }
            });
        } else {
            appliedHeaderView.setText("You've been offered the job! You can now accept or reject the offer.");
            acceptOfferButton.setVisibility(View.VISIBLE);
            acceptOfferButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentApplication() != null) {
                        acceptOffer(getCurrentApplication().id);
                    }
                }
            });
            ctaButton.setText("Reject offer");
            ctaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentApplication() != null) {
                        showDeclineOfferDialog(getCurrentApplication().id);
                    }
                }
            });
        }
    }

    private void onApplicationNull() {
        acceptOfferButton.setVisibility(View.GONE);
        ctaButton.setText(getString(R.string.apply));
        ctaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.applyToJob(currentJobId);
            }
        });
    }

    private void setupMenuIconsVisibility() {
        if (currentJob == null || likeJobMenuItem == null || unlikeJobMenuItem == null) return;

        if (currentJob.liked) {
            likeJobMenuItem.setVisible(false);
            unlikeJobMenuItem.setVisible(true);
        } else {
            likeJobMenuItem.setVisible(true);
            unlikeJobMenuItem.setVisible(false);
        }
    }

    @Nullable
    private Application getCurrentApplication() {
        try {
            if (currentJob.application != null
                    && !currentJob.application.isEmpty()) {
                return currentJob.application.get(0);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        return null;
    }

    private int getCurrentAppStatus() {
        int status = 0;
        if (getCurrentApplication() != null) status = getCurrentApplication().status.id;
        return status;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextTools.log(TAG, "onResume");
        presenter.fetchJob(currentJobId);
    }

    @Override
    public void onPause() {
        TextTools.log(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_worker_job_details, menu);
        super.onCreateOptionsMenu(menu, inflater);

        likeJobMenuItem = menu.findItem(R.id.job_like);
        unlikeJobMenuItem = menu.findItem(R.id.job_unlike);

        Drawable likeDrawable = menu.findItem(R.id.job_like).getIcon();
        likeDrawable = DrawableCompat.wrap(likeDrawable);
        DrawableCompat.setTint(likeDrawable, ContextCompat.getColor(getActivity(), R.color.redSquareColor));
        menu.findItem(R.id.job_like).setIcon(likeDrawable);
        //Not for this version
         /*Drawable shareDrawable = menu.findItem(R.id.job_share).getIcon();
        shareDrawable = DrawableCompat.wrap(shareDrawable);
        DrawableCompat.setTint(shareDrawable, ContextCompat.getColor(getActivity(), R.color.redSquareColor));
        menu.findItem(R.id.job_share).setIcon(shareDrawable);*/

        setupMenuIconsVisibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.job_like:
                presenter.onLikeJobClick();
                break;
            case R.id.job_unlike:
                presenter.onUnlikeJobClick();
                break;
            /*case R.id.job_share:
                presenter.onShareJobClick();
                break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelBooking() {
        DialogBuilder.showCancelBookingDialog(getContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showCancelBookingFeedbackDialog();
            }
        });
    }

    private void showCancelBookingFeedbackDialog() {
        DialogBuilder.showInputDialog(
                getContext(),
                R.string.job_feedback,
                R.string.job_feedback_hint,
                new DialogBuilder.OnTextInputDialogListener() {

                    @Override
                    public void onInputFinished(String input) {
                        if (!TextTools.trimIsEmpty(input))
                            presenter.cancelBooking(currentJob.application.get(0).id, input);
                        else
                            DialogBuilder.showStandardDialog(getContext(), "",
                                    getString(R.string.error_input_empty));
                    }
                });
    }

    @Override
    public void onJobFetched() {
        if (getActivity() == null || !isAdded()) return;
        currentJob = presenter.getCurrentJob();
        updateViews();
        if (null != mapFragment) {
            TextTools.log(TAG, "getting map async");
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    //
                    if (null != currentJob.location) {
                        TextTools.log(TAG, "current job location isn't null");

                        LatLng latLng = new LatLng(Double.valueOf(currentJob.location.latitude),
                                Double.valueOf(currentJob.location.longitude));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                    } else {
                        TextTools.log(TAG, "current job location is null");
                    }
                    //
                }
            });
        } else {
            TextTools.log(TAG, "map fragment null");
        }
    }

    private void updateViews() {
        setupMenuIconsVisibility();
        populateData();
        setupApplicationData();
    }

    @Override
    public void onJobApply(Application application) {
        if (getActivity() == null || !isAdded()) return;
        TextTools.log(TAG, "onJobApply");
        JobAppliedDialog.newInstance(currentJob).show(getFragmentManager(), "JobAppliedDialog");
    }

    @Override
    public void onBookingCanceled() {
        if (getActivity() == null || !isAdded()) return;
        getActivity().finish();
    }

    private void showDeclineOfferDialog(final int offerId) {
        dialog = DialogBuilder.showTwoOptionsStandardDialog(getContext(), "", "Are you sure you want to decline this job offer?",
                "YES", "NO", new DialogBuilder.OnClickTwoOptionsStandardDialog() {
                    @Override
                    public void onClickOptionOneStandardDialog(Context context) {
                        declineOffer(offerId);
                    }

                    @Override
                    public void onClickOptionTwoStandardDialog(Context context) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void acceptOffer(int id) {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .acceptOffer(id)
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call,
                                               Response<Object> response) {

                            DialogBuilder.cancelDialog(dialog);

                            if (response.isSuccessful()) {
                                presenter.fetchJob(currentJobId);
                            } else HandleErrors.parseError(getContext(), dialog, response);
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void declineOffer(int id) {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .declineOffer(id)
                    .enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call,
                                               Response<Object> response) {

                            DialogBuilder.cancelDialog(dialog);

                            if (response.isSuccessful()) {
                                presenter.fetchJob(currentJobId);
                            } else HandleErrors.parseError(getContext(), dialog, response);
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
}
