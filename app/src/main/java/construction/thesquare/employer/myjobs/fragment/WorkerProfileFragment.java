package construction.thesquare.employer.myjobs.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.FlavorSettings;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.LikeWorkerConnector;
import construction.thesquare.shared.applications.model.Feedback;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.response.QuickInviteResponse;
import construction.thesquare.shared.models.Application;
import construction.thesquare.shared.models.Company;
import construction.thesquare.shared.models.ExperienceType;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.models.Language;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Skill;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.RatingView;
import construction.thesquare.shared.view.widget.StrikeJosefinSansTextView;
import construction.thesquare.worker.jobmatches.model.ApplicationStatus;
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkerProfileFragment extends Fragment
        implements LikeWorkerConnector.Callback {

    @BindView(R.id.worker_view_profile_avatar) CircleImageView avatarImage;
    @BindView(R.id.worker_view_profile_name) TextView nameView;
    @BindView(R.id.worker_view_profile_position) TextView positionView;
    @BindView(R.id.worker_view_profile_experience) TextView experienceYearsView;
    @BindView(R.id.worker_view_profile_rating) RatingView ratingView;
    @BindView(R.id.worker_profile_bio_text) TextView bioView;
    @BindView(R.id.worker_details_bullet_list_experience) LinearLayout experienceView;
    @BindView(R.id.worker_details_bullet_list_requirements) LinearLayout requirementsView;
    @BindView(R.id.worker_details_bullet_list_skills) LinearLayout skillsView;
    @BindView(R.id.worker_details_bullet_list_companies) TextView companiesView;
    @BindView(R.id.worker_details_preferred_location) TextView locationView;
    @BindView(R.id.mapView) MapView mapView;
    @BindViews({R.id.cscs1, R.id.cscs2, R.id.cscs3, R.id.cscs4,
            R.id.cscs5, R.id.cscs6, R.id.cscs7, R.id.cscs8})
    List<TextView> cscsNumbers;
    @BindView(R.id.workerImage) ImageView cscsImage;
    @BindView(R.id.cscs_status) TextView cscsStatus;
    @BindView(R.id.cscsContent) View cscsContent;
    @BindViews({R.id.nis1, R.id.nis2, R.id.nis3, R.id.nis4,
            R.id.nis5, R.id.nis6, R.id.nis7, R.id.nis8,
            R.id.nis9})
    List<TextView> nisNumbers;
    @BindView(R.id.worker_profile_nationality_value) TextView nationalityView;
    @BindView(R.id.worker_profile_birthday_value) TextView dateOfBirthView;
    @BindView(R.id.worker_profile_languages_value) TextView languagesView;
    @BindView(R.id.worker_profile_passport_value) ImageView passportImage;
    @BindView(R.id.cscs_expires_value) TextView cscsExpirationView;
    @BindView(R.id.cscsRecordsLayout) LinearLayout cscsRecordsLayout;
    @BindView(R.id.worker_profile_email) TextView workerEmail;
    @BindView(R.id.worker_profile_phone) TextView workerPhone;
    @BindView(R.id.worker_profile_english_value) TextView englishLevel;
    @BindView(R.id.worker_details_bullet_list_experience_type) LinearLayout experienceTypesView;
    @BindView(R.id.book) JosefinSansTextView book;
    @BindView(R.id.decline) JosefinSansTextView decline;
    @BindView(R.id.withdraw) JosefinSansTextView withdraw;
    @BindView(R.id.bookedBanner) TextView bookedBanner;
    @BindView(R.id.offered_hint_view) ViewGroup offeredHint;
    @BindView(R.id.offered_hint_text) TextView offeredHintText;
    @BindView(R.id.contactWorkerLayout) View contactWorkerLayout;
    @BindView(R.id.nis_status) TextView nisStatus;
    @BindView(R.id.nisNumberLayout) View nisNumberLayout;
    @BindView(R.id.date_of_birth_status) TextView dateOfBirthStatusView;
    @BindView(R.id.passport_status) TextView passportStatus;
    @BindView(R.id.likeImage) ImageView likeImage;
    @BindView(R.id.workerBioLayout) View workerBioLayout;

    private static final String KEY_WORKER_ID = "KEY_WORKER_ID";

    private static final int VERIFICATION_NONE = 1;     // Verification hasn't been requested yet.
    private static final int VERIFICATION_FAILED = 2;   // Infrastructural issues: cannot verify cards (e.g. failed to connect to citb website).
    private static final int VERIFICATION_INVALID = 3;  // Supplied card details have been confirmed as invalid.
    private static final int VERIFICATION_VALID = 4;    // Supplied card details are valid.

    private Worker worker;
    private GoogleMap googleMap;
    private int workerId;
    private int jobId;
    private Job job;
    private boolean booked;
    private LikeWorkerConnector likeWorkerConnector;

    public static WorkerProfileFragment newInstance(int workerId,
                                                    int jobId) {
        WorkerProfileFragment fragment = new WorkerProfileFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_WORKER_ID, workerId);
        args.putInt(Constants.KEY_JOB_ID, jobId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workerId = getArguments().getInt(KEY_WORKER_ID, 0);
        jobId = getArguments().getInt(Constants.KEY_JOB_ID, 0);
        likeWorkerConnector = new LikeWorkerConnector(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_worker_profile, container, false);
        ButterKnife.bind(this, v);
        mapView.onCreate(savedInstanceState);
        return v;
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();

        fetchWorker();
    }

    private void initComponents() {
        if (worker != null) {

            try {
                fillWorkerImage();
                fillWorkerName();
                fillWorkerPosition();
                fillCompanies();
                fillWorkerBio();
                fillLocationName();
                initMap();
                fillDateOfBirth();
                fillNiNumber();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }

            if (worker.nationality != null) {
                nationalityView.setText(worker.nationality.name);
            }
            if (null != worker.englishLevel) {
                if (null != worker.englishLevel.name) {
                    englishLevel.setText(worker.englishLevel.name);
                }
            }

            if (!CollectionUtils.isEmpty(worker.languages)) {
                List<String> languageNames = new ArrayList<>();
                for (Language l : worker.languages) languageNames.add(l.name);
                languagesView.setText(TextUtils.join(", ", languageNames));
            }

            try {
                fillPassportImage();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }

            try {
                likeImage.setImageResource(worker.liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    private void fillPassportImage() {
        if (!TextUtils.isEmpty(worker.passportUpload)) {
            if (!TextUtils.equals(worker.passportUpload, "******")) {
                passportImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(worker.passportUpload)
                        .fit().centerCrop().into(passportImage);
                passportStatus.setVisibility(View.GONE);
            } else {
                //provided
                passportStatus.setVisibility(View.VISIBLE);
                passportStatus.setText(getString(R.string.worker_details_provided));
                passportImage.setVisibility(View.GONE);
            }
        } else {
            //not provided
            passportStatus.setVisibility(View.VISIBLE);
            passportStatus.setText(getString(R.string.worker_details_not_provided));
            passportImage.setVisibility(View.GONE);
        }
    }

    private void fillDateOfBirth() {
        if (!TextUtils.isEmpty(worker.dateOfBirth)) {
            if (!TextUtils.equals(worker.dateOfBirth, "******")) {
                dateOfBirthView.setVisibility(View.VISIBLE);
                dateOfBirthView.setText(DateUtils.getParsedBirthDate(worker.dateOfBirth));
                dateOfBirthStatusView.setVisibility(View.GONE);
            } else {
                //provided
                dateOfBirthStatusView.setVisibility(View.VISIBLE);
                dateOfBirthStatusView.setText(getString(R.string.worker_details_provided));
                dateOfBirthView.setVisibility(View.GONE);
            }
        } else {
            //not provided
            dateOfBirthStatusView.setVisibility(View.VISIBLE);
            dateOfBirthStatusView.setText(getString(R.string.worker_details_not_provided));
            dateOfBirthView.setVisibility(View.GONE);
        }
    }

    private void fillExperienceTypes() {
        experienceTypesView.removeAllViews();
        if (!CollectionUtils.isEmpty(job.experienceTypes)) {
            if (!CollectionUtils.isEmpty(worker.experienceTypes)) {

                List<String> matchedItems = new ArrayList<>();
                List<String> workerItems = new ArrayList<>();
                List<String> unmatchedItems = new ArrayList<>();

                List<String> jobExperienceTypes = new ArrayList<>();
                List<String> workerExperienceTypes = new ArrayList<>();

                for (ExperienceType jobExperienceType : job.experienceTypes) {
                    jobExperienceTypes.add(jobExperienceType.name);
                }

                for (ExperienceType workerExperienceType : worker.experienceTypes) {
                    workerExperienceTypes.add(workerExperienceType.name);
                }

                for (String jobExpType : jobExperienceTypes) {
                    if (workerExperienceTypes.contains(jobExpType)) matchedItems.add(jobExpType);
                    else unmatchedItems.add(jobExpType);
                }

                for (String workerExpType : workerExperienceTypes) {
                    if (!matchedItems.contains(workerExpType)) workerItems.add(workerExpType);
                }

                for (String matchedItem : matchedItems) {
                    populateWithMatchedItem(experienceTypesView, matchedItem);
                }

                for (String workerItem : workerItems) {
                    populateWithMatchedItem(experienceTypesView, workerItem);
                }

                for (String unmatchedItem : unmatchedItems) {
                    populateWithUnmatchedItem(experienceTypesView, unmatchedItem);
                }
            }
        }
    }

    private void fillNiNumber() {
        if (worker != null && !TextUtils.isEmpty(worker.niNumber)) {

            if (!TextUtils.equals(worker.niNumber, "******")) {
                nisNumberLayout.setVisibility(View.VISIBLE);
                nisStatus.setVisibility(View.GONE);

                final char ni[] = worker.niNumber.toCharArray();
                ButterKnife.Setter<TextView, Boolean> ENABLED = new ButterKnife.Setter<TextView, Boolean>() {
                    @Override
                    public void set(TextView view, Boolean value, int index) {

                        switch (view.getId()) {
                            case R.id.nis1:
                                nisNumbers.get(0).setText(Character.toString(ni[0]));
                            case R.id.nis2:
                                nisNumbers.get(1).setText(Character.toString(ni[1]));
                            case R.id.nis3:
                                nisNumbers.get(2).setText(Character.toString(ni[2]));
                            case R.id.nis4:
                                nisNumbers.get(3).setText(Character.toString(ni[3]));
                            case R.id.nis5:
                                nisNumbers.get(4).setText(Character.toString(ni[4]));
                            case R.id.nis6:
                                nisNumbers.get(5).setText(Character.toString(ni[5]));
                            case R.id.nis7:
                                nisNumbers.get(6).setText(Character.toString(ni[6]));
                            case R.id.nis8:
                                nisNumbers.get(7).setText(Character.toString(ni[7]));
                            case R.id.nis9:
                                nisNumbers.get(8).setText(Character.toString(ni[8]));
                        }
                    }
                };
                ButterKnife.apply(nisNumbers, ENABLED, true);
            } else {
                //provided
                nisStatus.setText(getString(R.string.worker_details_provided));
                nisNumberLayout.setVisibility(View.GONE);
                nisStatus.setVisibility(View.VISIBLE);
            }
        } else {
            //not provided
            nisStatus.setText(getString(R.string.worker_details_not_provided));
            nisNumberLayout.setVisibility(View.GONE);
            nisStatus.setVisibility(View.VISIBLE);
        }
    }

    private void fillWorkerImage() {
        if (!TextUtils.isEmpty(worker.picture)) {
            Picasso.with(getContext()).load(worker.picture).fit().centerCrop().into(avatarImage);
        } else {
            avatarImage.setImageResource(R.drawable.bob);
        }
    }

    private void fillWorkerName() {
        StringBuilder nameString = new StringBuilder();
        if (!TextUtils.isEmpty(worker.firstName)) nameString.append(worker.firstName);
        if (!TextUtils.isEmpty(worker.lastName)) nameString.append(" ").append(worker.lastName);
        nameView.setText(nameString.toString().toUpperCase(Locale.UK));
    }

    private void fillWorkerPosition() {
        String role = "";
        if (worker.matchedRole != null) role = worker.matchedRole.name;
        positionView.setText(role);

        String positionString = getString(R.string.role_year_experience, worker.yearsExperience,
                getResources().getQuantityString(R.plurals.year_plural, worker.yearsExperience));
        experienceYearsView.setText(positionString.toUpperCase(Locale.UK));
        ratingView.setRating((int) worker.rating);
    }

    private void fillExperienceAndQualifications() {
        experienceView.removeAllViews();
        requirementsView.removeAllViews();
        if (!CollectionUtils.isEmpty(job.qualifications)) {
            if (!CollectionUtils.isEmpty(worker.qualifications)) {

                List<String> matchedQualifications = new ArrayList<>();
                List<String> workerUnmatchedQualifications = new ArrayList<>();
                List<String> unmatchedQualifications = new ArrayList<>();

                List<String> matchedRequirements = new ArrayList<>();
                List<String> workerUnmatchedRequirements = new ArrayList<>();
                List<String> unmatchedRequirements = new ArrayList<>();

                List<String> jobQualifications = new ArrayList<>();
                List<String> workerQualifications = new ArrayList<>();
                List<String> jobRequirements = new ArrayList<>();
                List<String> workerRequirements = new ArrayList<>();

                for (Qualification jobQualification : job.qualifications) {
                    if (jobQualification.onExperience) jobRequirements.add(jobQualification.name);
                    else
                        jobQualifications.add(jobQualification.name);
                }

                for (Qualification workerQualification : worker.qualifications) {
                    if (workerQualification.onExperience)
                        workerRequirements.add(workerQualification.name);
                    else
                        workerQualifications.add(workerQualification.name);
                }

                for (String jobQualification : jobQualifications) {
                    if (workerQualifications.contains(jobQualification))
                        matchedQualifications.add(jobQualification);
                    else unmatchedQualifications.add(jobQualification);
                }

                for (String workerQualification : workerQualifications) {
                    if (!matchedQualifications.contains(workerQualification))
                        workerUnmatchedQualifications.add(workerQualification);
                }

                for (String matchedItem : matchedQualifications) {
                    populateWithMatchedItem(experienceView, matchedItem);
                }

                for (String workerItem : workerUnmatchedQualifications) {
                    populateWithMatchedItem(experienceView, workerItem);
                }

                for (String unmatchedItem : unmatchedQualifications) {
                    populateWithUnmatchedItem(experienceView, unmatchedItem);
                }

                for (String jobRequirement : jobRequirements) {
                    if (workerRequirements.contains(jobRequirement))
                        matchedRequirements.add(jobRequirement);
                    else unmatchedRequirements.add(jobRequirement);
                }

                for (String workerRequirement : workerRequirements) {
                    if (!matchedRequirements.contains(workerRequirement))
                        workerUnmatchedRequirements.add(workerRequirement);
                }

                for (String matchedItem : matchedRequirements) {
                    populateWithMatchedItem(requirementsView, matchedItem);
                }

                for (String workerItem : workerUnmatchedRequirements) {
                    populateWithMatchedItem(requirementsView, workerItem);
                }

                for (String unmatchedItem : unmatchedRequirements) {
                    populateWithUnmatchedItem(requirementsView, unmatchedItem);
                }
            }
        }
    }

    private void fillSkills() {
        skillsView.removeAllViews();
        if (!CollectionUtils.isEmpty(job.skills)) {
            if (!CollectionUtils.isEmpty(worker.skills)) {
                List<String> matchedItems = new ArrayList<>();
                List<String> workerItems = new ArrayList<>();
                List<String> unmatchedItems = new ArrayList<>();

                List<String> jobSkills = new ArrayList<>();
                List<String> workerSkills = new ArrayList<>();

                for (Skill jobSkill : job.skills) {
                    jobSkills.add(jobSkill.name);
                }

                for (Skill workerSkill : worker.skills) {
                    workerSkills.add(workerSkill.name);
                }

                for (String jobSkill : jobSkills) {
                    if (workerSkills.contains(jobSkill)) matchedItems.add(jobSkill);
                    else unmatchedItems.add(jobSkill);
                }

                for (String workerSkill : workerSkills) {
                    if (!matchedItems.contains(workerSkill)) workerItems.add(workerSkill);
                }

                for (String matchedSkill : matchedItems) {
                    populateWithMatchedItem(skillsView, matchedSkill);
                }

                for (String workerItem : workerItems) {
                    populateWithMatchedItem(skillsView, workerItem);
                }

                for (String unmatchedItem : unmatchedItems) {
                    populateWithUnmatchedItem(skillsView, unmatchedItem);
                }
            }
        }
    }

    private void fillCompanies() {
        String text = "";
        if (!CollectionUtils.isEmpty(worker.companies)) {
            for (Company preference : worker.companies) {
                text += "• " + preference.name + "\n";
            }
        }
        companiesView.setText(text);
    }

    private void fillWorkerBio() {
        if (!TextUtils.isEmpty(worker.bio)) {
            workerBioLayout.setVisibility(View.VISIBLE);
            bioView.setText(worker.bio);
        } else workerBioLayout.setVisibility(View.GONE);
    }

    private void initMap() {
        if (googleMap == null)
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.getUiSettings().setAllGesturesEnabled(false);
                    googleMap.getUiSettings().setCompassEnabled(false);
                    googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    drawMarker();
                }
            });
        else drawMarker();
    }

    private void fillLocationName() {
        if (worker != null) {
            if (null != worker.zip) {
                locationView
                        .setText(getString(R.string.employer_view_worker_commute_time,
                                worker.commuteTime, worker.zip.toUpperCase()));
            }
        }
    }

    private void drawMarker() {
        if (worker != null && worker.location != null) {
            if (null != googleMap) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(worker.location.getLatitude(),
                                worker.location.getLongitude())));
                googleMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(worker.location.getLatitude(),
                                worker.location.getLongitude()), 12f));
            }
        }
    }

    private void fetchJob() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJob(jobId)
                .enqueue(new Callback<ResponseObject<Job>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Job>> call,
                                           Response<ResponseObject<Job>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            job = response.body().getResponse();
                            try {
                                populateJobData();
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                            fetchCscsDetails(workerId);
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchWorker(workerId, jobId)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            worker = response.body().getResponse();
                            try {
                                initComponents();
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                            fetchJob();
                        } else
                            HandleErrors.parseError(getContext(), dialog, response);
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateJobData() {
        if (job != null) {
            if (worker != null) {
                Application currentApplication = null;
                if (null != worker.applications) {
                    if (!CollectionUtils.isEmpty(worker.applications)) {
                        for (Application application : worker.applications) {
                            if (application.jobId == jobId) {
                                currentApplication = application;
                            }
                        }
                    }
                }

                if (currentApplication != null) {
                    if (currentApplication.status != null)
                        if (currentApplication.status.id == ApplicationStatus.STATUS_PENDING) {
                            if (!currentApplication.isOffer) {
                                onApplied(currentApplication.id);
                            } else {
                                String start = "";
                                try {
                                    start = DateUtils.magicDate(job.start.split("T")[0]);
                                } catch (Exception e) {
                                    CrashLogHelper.logException(e);
                                }
                                onOffered((null != worker.firstName) ?
                                                worker.firstName : "",
                                        (null != job.role) ? job.role.name : "", start,
                                        currentApplication.id);
                            }
                        } else if (currentApplication.status.id == ApplicationStatus.STATUS_APPROVED) {
                            //
                            onBooked();
                            //
                        } else if (currentApplication.status.id == ApplicationStatus.STATUS_CANCELLED ||
                                currentApplication.status.id == ApplicationStatus.STATUS_DENIED ||
                                currentApplication.status.id == ApplicationStatus.STATUS_END_CONTRACT) {
                            //
                            book.setVisibility(View.GONE);
                            //
                        }
                } else {
                    onApplicationNull();
                }

                try {
                    fillExperienceAndQualifications();
                    fillSkills();
                    fillExperienceTypes();
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            }
        }
    }

    private void onApplied(final int applicationId) {
        decline.setVisibility(View.VISIBLE);
        book.setVisibility(View.VISIBLE);
        book.setText("BOOK");
        if (null != job) {
            if (job.isConnect) {
                book.setText("CONNECT");
            }
        }
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,
                                WorkerDeclineFragment.newInstance(applicationId))
                        .addToBackStack("")
                        .commit();


            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
                HttpRestServiceConsumer.getBaseApiClient()
                        .acceptApplication(applicationId)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                //
                                if (response.isSuccessful()) {
                                    //
                                    DialogBuilder.cancelDialog(dialog);
                                    book.setVisibility(View.GONE);
                                    decline.setVisibility(View.GONE);
                                    // Toast.makeText(getContext(), "Booked!", Toast.LENGTH_LONG).show();
                                    fetchWorker();
                                    //
                                    final Dialog dialog1 = new Dialog(getContext());
                                    dialog1.setCancelable(false);
                                    dialog1.setContentView(R.layout.dialog_worker_booked);
                                    if (null != worker) {
                                        if (null != worker.firstName) {
                                            String confirmBooked =
                                                    String.format(
                                                            getString(R.string.employer_worker_booked),
                                                            worker.firstName);
                                            String confirmBookedMore =
                                                    String.format(
                                                            getString(R.string.employer_worker_booked_more),
                                                            worker.firstName);
                                            String confirmConnected =
                                                    String.format(
                                                            getString(R.string.connect_worker_connected),
                                                            worker.firstName);
                                            String confirmConnectedMore =
                                                    String.format(
                                                            getString(R.string.connect_worker_connected_more),
                                                            worker.firstName);
                                            ((TextView) dialog1
                                                    .findViewById(R.id.dialog_worker_booked_title))
                                                    .setText((job.isConnect) ?
                                                            confirmConnected : confirmBooked);
                                            ((TextView) dialog1
                                                    .findViewById(R.id.dialog_worker_booked_subtitle))
                                                    .setText((job.isConnect) ?
                                                            confirmConnectedMore : confirmBookedMore);
                                        }
                                    }
                                    dialog1.findViewById(R.id.yes)
                                            .setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog1.dismiss();
                                                }
                                            });
                                    dialog1.show();

                                } else {
                                    HandleErrors.parseError(getContext(), dialog, response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            }
                        });
            }
        });
    }

    private void onBooked() {
        booked = true;
        bookedBanner.setVisibility(View.VISIBLE);
        if (job.isConnect) {
            bookedBanner.setText(getString(R.string.connect_connected));
        }
        contactWorkerLayout.setVisibility(View.VISIBLE);
        workerEmail.setText(worker.email);
        if (!CollectionUtils.isEmpty(worker.devices)) {
            workerPhone.setText(worker.devices.get(0).phoneNumber);
        }
    }

    private void onApplicationNull() {
        booked = false;
        bookedBanner.setVisibility(View.GONE);
        contactWorkerLayout.setVisibility(View.GONE);
        book.setText("OFFER JOB");
        if (null != job) {
            if (job.isConnect) {
                book.setText("CONNECT");
            }
        }
        book.setVisibility(View.VISIBLE);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInvite();
            }
        });
    }

    private void onOffered(String workerName,
                           String jobName,
                           String startDate,
                           final int currentApplicationId) {
        withdraw.setVisibility(View.VISIBLE);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawOffer(currentApplicationId);
            }
        });
        book.setVisibility(View.GONE);
        offeredHint.setVisibility(View.VISIBLE);
        String hintTextOffered = String.format(getString(R.string.offered_hint),
                workerName, jobName, startDate);
        String hintTextConnected = String.format(getString(R.string.connect_hint_sent),
                workerName);
        offeredHintText.setText((job.isConnect) ? hintTextConnected : hintTextOffered);
    }

    private void withdrawOffer(final int appId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_withdraw_offer);
        dialog.findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        withdrawOfferCallApi(appId);
                    }
                });
        dialog.findViewById(R.id.no)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    private void withdrawOfferCallApi(int id) {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .cancelBooking(id, new Feedback("n/a"))
                    .enqueue(new Callback<ResponseObject<construction.thesquare.worker.jobmatches.model.Application>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<construction.thesquare.worker.jobmatches.model.Application>> call, Response<ResponseObject<construction.thesquare.worker.jobmatches.model.Application>> response) {
                            if (response.isSuccessful()) {
                                //
                                DialogBuilder.cancelDialog(dialog);
                                offeredHint.setVisibility(View.GONE);
                                withdraw.setVisibility(View.GONE);
                                getActivity().finish();
                                //
                            } else {
                                HandleErrors.parseError(getContext(), dialog, response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<construction.thesquare.worker.jobmatches.model.Application>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    //TODO move strings into resources
    private void onInvite() {
        String bookPrompt = String.format(getString(R.string.connect_prompt_book),
                (null != worker.firstName) ? worker.firstName : "...");
        String connectPrompt = String.format(getString(R.string.connect_prompt_connect),
                (null != worker.firstName) ? worker.firstName : "...");

        new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                .setMessage((job.isConnect) ? connectPrompt : bookPrompt)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        inviteWorker(worker.id, worker.firstName, jobId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void showWorkerInviteSent(String workerName) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_offer_confirm);
        String confirmMessageConnect = String.format(getString(R.string.connect_confirm),
                workerName);
        String confirmMessageBook = String.format(getString(R.string.offer_job_confirm),
                workerName);
        ((TextView) dialog.findViewById(R.id.dialog_offer_job_confirm))
                .setText((job.isConnect) ? confirmMessageConnect : confirmMessageBook);
        dialog.findViewById(R.id.offer_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void inviteWorker(int workerId, final String name, int jobId) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        final HashMap<String, Object> body = new HashMap<>();
        body.put("job_id", jobId);
        HttpRestServiceConsumer.getBaseApiClient()
                .quickInvite(body, workerId)
                .enqueue(new Callback<QuickInviteResponse>() {
                    @Override
                    public void onResponse(Call<QuickInviteResponse> call,
                                           Response<QuickInviteResponse> response) {
                        DialogBuilder.cancelDialog(dialog);
                        //
                        try {
                            showWorkerInviteSent(name);
                        } catch (Exception e) {
                            CrashLogHelper.logException(e);
                        }
                        //
                        fetchWorker();
                    }

                    @Override
                    public void onFailure(Call<QuickInviteResponse> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void fetchCscsDetails(int currentWorkerId) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerCSCSCard(currentWorkerId)
                .enqueue(new Callback<ResponseObject<CSCSCardWorker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<CSCSCardWorker>> call,
                                           Response<ResponseObject<CSCSCardWorker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                populateCscs(response.body());
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<CSCSCardWorker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    /**
     * CSCS
     * @param dataResponse
     */
    private void populateCscs(ResponseObject<CSCSCardWorker> dataResponse) {
        if (dataResponse != null) {

            String regnum = dataResponse.getResponse().registrationNumber;
            populateCscsStatus(dataResponse.getResponse().verificationStatus);
            if (dataResponse.getResponse().verificationStatus == 4 && !regnum.isEmpty()) {
                final char ca[] = regnum.toCharArray();
                ButterKnife.Setter<TextView, Boolean> ENABLED = new ButterKnife.Setter<TextView, Boolean>() {
                    @Override
                    public void set(TextView view, Boolean value, int index) {

                        switch (view.getId()) {
                            case R.id.cscs1:
                                cscsNumbers.get(0).setText(Character.toString(ca[0]));
                            case R.id.cscs2:
                                cscsNumbers.get(1).setText(Character.toString(ca[1]));
                            case R.id.cscs3:
                                cscsNumbers.get(2).setText(Character.toString(ca[2]));
                            case R.id.cscs4:
                                cscsNumbers.get(3).setText(Character.toString(ca[3]));
                            case R.id.cscs5:
                                cscsNumbers.get(4).setText(Character.toString(ca[4]));
                            case R.id.cscs6:
                                cscsNumbers.get(5).setText(Character.toString(ca[5]));
                            case R.id.cscs7:
                                cscsNumbers.get(6).setText(Character.toString(ca[6]));
                            case R.id.cscs8:
                                cscsNumbers.get(7).setText(Character.toString(ca[7]));
                        }
                    }
                };
                ButterKnife.apply(cscsNumbers, ENABLED, true);
            }

            if (dataResponse.getResponse().cardPicture != null)
                Picasso.with(getContext())
                        .load(FlavorSettings.API_URL + dataResponse.getResponse().cardPicture)
                        .fit().centerCrop().into(cscsImage);

            if (null != dataResponse.getResponse().expiryDate) {
                cscsExpirationView.setText(DateUtils
                        .getCscsExpirationDate(dataResponse.getResponse().expiryDate));
            }

            try {
                cscsRecordsLayout.removeAllViews();
                if (!CollectionUtils.isEmpty(dataResponse.getResponse().cscsRecords)) {
                    for (List<CSCSCardWorker.CscsRecord> cscsRecordList : dataResponse.getResponse().cscsRecords.values())
                        if (!CollectionUtils.isEmpty(cscsRecordList)) {
                            for (CSCSCardWorker.CscsRecord record : cscsRecordList) {
                                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_cscs_record, null, false);
                                TextView cscsText = (TextView) itemView.findViewById(R.id.recordText);
                                cscsText.setText(record.name + " - " + record.category.name);
                                cscsRecordsLayout.addView(itemView);
                            }
                        }
                }
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }
    private void populateCscsStatus(int status) {
        if (status == VERIFICATION_VALID) {
            cscsStatus.setText(R.string.worker_cscs_verified);
            if (booked) {
                cscsContent.setVisibility(View.VISIBLE);
            } else {
                cscsContent.setVisibility(View.GONE);
            }
        } else {
            cscsStatus.setText(getString(R.string.worker_cscs_not_verified));
            cscsContent.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.likeImage)
    void processLikeClick() {
        if (worker != null) {
            if (worker.liked) {
                likeWorkerConnector.unlikeWorker(getContext(), workerId);
            } else {
                likeWorkerConnector.likeWorker(getContext(), workerId);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != mapView) {
            mapView.onResume();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mapView) {
            mapView.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        if (null != mapView) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        if (null != mapView) {
            mapView.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        if (null != mapView) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectorSuccess() {
        fetchWorker();
    }

    /**
     * Utils
     * @param layout
     * @param text
     */
    private void populateWithMatchedItem(LinearLayout layout, String text) {
        if (TextUtils.isEmpty(text) || layout == null) return;

        try {
            View item = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_worker_details, null, false);
            ImageView status = (ImageView) item.findViewById(R.id.status);
            StrikeJosefinSansTextView textView = (StrikeJosefinSansTextView)
                    item.findViewById(R.id.worker_details);
            textView.setText(text);
            textView.setStrikeVisibility(false);
            status.setImageResource(R.drawable.red_bullet);
            status.setPadding(2, 2, 2, 2);
            layout.addView(item);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
    private void populateWithUnmatchedItem(LinearLayout layout, String text) {
        if (TextUtils.isEmpty(text) || layout == null) return;

        try {
            View item = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_worker_details, null, false);
            ImageView status = (ImageView) item.findViewById(R.id.status);
            StrikeJosefinSansTextView textView = (StrikeJosefinSansTextView)
                    item.findViewById(R.id.worker_details);
            status.setPadding(0, 0, 0, 0);
            textView.setText(text);
            textView.setStrikeVisibility(true);
            status.setImageResource(R.drawable.ic_clear_black_24dp);
            layout.addView(item);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
}
