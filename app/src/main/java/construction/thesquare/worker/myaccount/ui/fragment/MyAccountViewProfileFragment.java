package construction.thesquare.worker.myaccount.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Company;
import construction.thesquare.shared.models.ExperienceType;
import construction.thesquare.shared.models.Language;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.models.Skill;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.MediaTools;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.RatingView;
import construction.thesquare.worker.myaccount.ui.dialog.EditAccountDetailsDialog;
import construction.thesquare.worker.myaccount.ui.dialog.EditBirthDateDialog;
import construction.thesquare.worker.myaccount.ui.dialog.EditCscsDetailsDialog;
import construction.thesquare.worker.myaccount.ui.dialog.EditNationalityDialog;
import construction.thesquare.worker.myaccount.ui.dialog.EditNisDialog;
import construction.thesquare.worker.onboarding.SingleEditActivity;
import construction.thesquare.worker.reviews.activity.ReviewActivity;
import construction.thesquare.worker.settings.ui.dialog.EditNameDialog;
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maizaga on 30/10/16.
 */

public class MyAccountViewProfileFragment extends Fragment implements EditAccountDetailsDialog.InputFinishedListener,
        EditCscsDetailsDialog.OnCscsDetailsUpdatedListener, View.OnClickListener,
        EditNationalityDialog.ChangeDetailsListener {

    private static final int REQUEST_EDIT_PROFILE = 100;

    @BindView(R.id.worker_view_profile_avatar)
    CircleImageView avatarImage;

    @BindView(R.id.worker_view_profile_name)
    TextView nameView;

    @BindView(R.id.worker_view_profile_position)
    TextView positionView;

    @BindView(R.id.worker_view_profile_experience)
    TextView experienceYearsView;

    @BindView(R.id.worker_view_profile_rating)
    RatingView ratingView;

    @BindViews({R.id.worker_profile_bio_edit,
            R.id.worker_profile_experience_edit,
            R.id.worker_profile_skills_edit,
            R.id.worker_profile_companies_edit,
            R.id.worker_profile_location_edit,
            R.id.worker_profile_requirements_edit,
            R.id.worker_profile_nationality_edit,
            R.id.worker_profile_birthday_edit,
            R.id.worker_profile_email_edit,
            R.id.worker_profile_nis_edit,
            R.id.worker_profile_passport_edit,
            R.id.worker_profile_experience_type_edit
    })
    List<ImageView> editList;

    @BindViews({R.id.cscs1, R.id.cscs2, R.id.cscs3, R.id.cscs4, R.id.cscs5, R.id.cscs6, R.id.cscs7, R.id.cscs8})
    List<TextView> cscsNumbers;

    @BindViews({R.id.nis1, R.id.nis2, R.id.nis3, R.id.nis4, R.id.nis5, R.id.nis6, R.id.nis7, R.id.nis8, R.id.nis9})
    List<TextView> nisNumbers;

    @BindView(R.id.workerImage)
    ImageView cscsImage;

    @BindView(R.id.cscs_status)
    TextView cscsStatus;

    @BindView(R.id.cscsContent)
    View cscsContent;

    @BindView(R.id.worker_profile_bio_text)
    TextView bioView;

    @BindView(R.id.worker_details_bullet_list_experience)
    TextView experienceView;

    @BindView(R.id.worker_details_bullet_list_requirements)
    TextView requirementsView;

    @BindView(R.id.worker_details_bullet_list_skills)
    TextView skillsView;

    @BindView(R.id.worker_details_bullet_list_experience_type)
    TextView experienceTypesView;

    @BindView(R.id.worker_details_bullet_list_companies)
    TextView companiesView;

    @BindView(R.id.worker_details_preferred_location)
    TextView locationView;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.worker_profile_nationality_value)
    TextView nationalityView;

    @BindView(R.id.worker_profile_birthday_value)
    TextView dateOfBirthView;

    @BindView(R.id.worker_profile_languages_value)
    TextView languagesView;

    @BindView(R.id.worker_profile_passport_value)
    ImageView passportImage;

    @BindView(R.id.cscs_expires_value)
    TextView cscsExpirationView;

    @BindView(R.id.cscsRecordsLayout)
    LinearLayout cscsRecordsLayout;

    @BindView(R.id.worker_profile_email)
    TextView workerEmail;

    @BindView(R.id.worker_profile_phone)
    TextView workerPhone;

    @BindView(R.id.worker_profile_english_value)
    TextView englishLevel;

    @BindView(R.id.worker_details_preferred_commute_time)
    TextView commuteTimeView;

    //nationality, date of birth, languages spoken, nis, photo of passport
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECTION = 2;
    private static final int REQUEST_PERMISSIONS = 3;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private static final int VERIFICATION_NONE = 1;     // Verification hasn't been requested yet.
    private static final int VERIFICATION_FAILED = 2;   // Infrastructural issues: cannot verify cards (e.g. failed to connect to citb website).
    private static final int VERIFICATION_INVALID = 3;  // Supplied card details have been confirmed as invalid.
    private static final int VERIFICATION_VALID = 4;    // Supplied card details are valid.

    private Worker worker;
    private GoogleMap googleMap;
    private EditAccountDetailsDialog editAccountDetailsDialog;
    private String tempBio;
    private int experienceYears;

    public static MyAccountViewProfileFragment newInstance() {
        return new MyAccountViewProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_worker_edit_profile, container, false);
        ButterKnife.bind(this, v);
        ButterKnife.apply(editList, new ButterKnife.Action<ImageView>() {
            @Override
            public void apply(@NonNull ImageView view, int index) {
                view.setVisibility(View.VISIBLE);
            }
        });

        mapView.onCreate(savedInstanceState);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(), ConstantsAnalytics.SCREEN_WORKER_ACCOUNT_EDIT);
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
            experienceYears = worker.yearsExperience;

            try {
                fillWorkerImage();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillWorkerName();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillWorkerPosition();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillExperienceAndQualifications();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillSkills();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillCompanies();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillWorkerBio();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillLocationName();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                initMap();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillNiNumber();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            try {
                fillExperienceTypes();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            if (worker.nationality != null) {
                nationalityView.setText(worker.nationality.name);
            }
            dateOfBirthView.setText(DateUtils.getParsedBirthDate(worker.dateOfBirth));
            workerEmail.setText(worker.email);
            workerPhone.setText(SharedPreferencesManager.getInstance(getContext()).loadSessionInfoWorker().getPhoneNumber());
            if (worker.englishLevel != null) englishLevel.setText(worker.englishLevel.name);
            if (worker.passportUpload != null) {
                Picasso.with(getContext())
                        .load(worker.passportUpload)
                        .fit().centerCrop().into(passportImage);
            }

            if (!CollectionUtils.isEmpty(worker.languages)) {
                List<String> languageNames = new ArrayList<>();
                for (Language l : worker.languages) {
                    languageNames.add(l.name);
                }
                languagesView.setText(TextUtils.join(", ", languageNames));
            }
        }
    }

    private void fillNiNumber() {
        if (worker != null && !TextUtils.isEmpty(worker.niNumber)) {

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
        List<String> stringRoles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(worker.roles)) {
            for (Role role : worker.roles) {
                stringRoles.add(role.name);
            }
        }
        positionView.setText(TextUtils.join("\n", stringRoles));

        String positionString = getString(R.string.role_year_experience, worker.yearsExperience,
                getResources().getQuantityString(R.plurals.year_plural, worker.yearsExperience));
        experienceYearsView.setText(positionString.toUpperCase(Locale.UK));
        ratingView.setRating((int) worker.rating);
    }

    private void fillExperienceAndQualifications() {
        String qualificationsText = "";
        String requirementsText = "";
        if (!CollectionUtils.isEmpty(worker.qualifications)) {
            for (Qualification qualification : worker.qualifications) {
                if (qualification.onExperience) {
                    requirementsText += "• " + qualification.name + "\n";
                } else
                    qualificationsText += "• " + qualification.name + "\n";
            }
        }
        experienceView.setText(qualificationsText);
        requirementsView.setText(requirementsText);
    }

    private void fillSkills() {
        String text = "";
        if (!CollectionUtils.isEmpty(worker.qualifications)) {
            for (Skill preference : worker.skills) {
                text += "• " + preference.name + "\n";
            }
        }
        skillsView.setText(text);
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

    private void fillExperienceTypes() {
        List<String> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(worker.experienceTypes)) {
            for (ExperienceType exp : worker.experienceTypes)
                result.add(exp.name);
        }
        experienceTypesView.setText(TextTools.toBulletList(result, true));
    }

    private void fillWorkerBio() {
        if (null != worker.bio) {
            if (!TextUtils.isEmpty(worker.bio)) {
                bioView.setText(worker.bio);
            }
        }
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
        if (worker != null && !TextUtils.isEmpty(worker.zip)) {
            commuteTimeView.setText(getString(R.string.worker_commute_time, worker.commuteTime, worker.zip.toUpperCase()));
        }
    }

    private void drawMarker() {
        if (worker != null && worker.location != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(worker.location.getLatitude(), worker.location.getLongitude())));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(worker.location.getLatitude(), worker.location.getLongitude()), 12f));
        }
    }

    private void editProfile(final int page) {
        startActivityForResult(SingleEditActivity.startIntent(getActivity(), worker, page), REQUEST_EDIT_PROFILE);
    }

    private void fetchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            worker = response.body().getResponse();
                            if (worker != null) fetchCscsDetails(worker.id);
                            try {
                                initComponents();
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                        } else
                            HandleErrors.parseError(getContext(), dialog, response);
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
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

            if (dataResponse.getResponse().cardPicture != null) {
                Picasso.with(getContext())
                        .load(FlavorSettings.API_URL + dataResponse.getResponse().cardPicture)
                        .fit().centerCrop().into(cscsImage);

                if (worker.picture == null)
                    Picasso.with(getContext())
                            .load(FlavorSettings.API_URL + dataResponse.getResponse().cardPicture)
                            .fit().centerCrop().into(avatarImage);
            }

            cscsExpirationView.setText(DateUtils.getCscsExpirationDate(dataResponse.getResponse().expiryDate));

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
            cscsContent.setVisibility(View.VISIBLE);
            cscsStatus.setOnClickListener(null);
        } else {
            cscsStatus.setText(getString(R.string.worker_cscs_not_verified));
            cscsContent.setVisibility(View.GONE);
            cscsStatus.setOnClickListener(this);
        }
    }

    @Override
    public void onDone(String string, boolean onlyDigits) {
        try {
            if (!string.isEmpty()) {
                if (onlyDigits) {
                    experienceYears = Integer.parseInt(string.replaceAll("\\D", ""));
                } else {
                    tempBio = string;
                    bioView.setText(string);
                }
                HashMap<String, Object> params = new HashMap<>();
                params.put("bio", tempBio);
                params.put("years_experience", experienceYears);
                patchWorker(params);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void patchWorker(HashMap<String, Object> payload) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        if (worker != null) {
            HttpRestServiceConsumer.getBaseApiClient()
                    .patchWorker(worker.id, payload)
                    .enqueue(new Callback<ResponseObject<Worker>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Worker>> call, Response<ResponseObject<Worker>> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                worker = response.body().getResponse();
                                initComponents();
                                if (worker != null) fetchCscsDetails(worker.id);
                            } else HandleErrors.parseError(getContext(), dialog, response);
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                            HandleErrors.parseFailureError(getContext(), dialog, t);
                        }
                    });
        }
    }

    @OnClick(R.id.worker_profile_bio_edit)
    void bioEdit() {
        editAccountDetailsDialog = EditAccountDetailsDialog
                .newInstance("Bio",
                        (worker != null && !TextUtils.isEmpty(worker.bio)) ? worker.bio : "",
                        false,
                        this);
        editAccountDetailsDialog.setCancelable(false);
        editAccountDetailsDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_experience_edit)
    void qualificationsEdit() {
        editProfile(Constants.KEY_ONBOARDING_QUALIFICATIONS);
    }

    @OnClick(R.id.worker_profile_skills_edit)
    void skillsEdit() {
        editProfile(Constants.KEY_ONBOARDING_SKILLS);
    }

    @OnClick(R.id.worker_profile_companies_edit)
    void companiesEdit() {
        editProfile(Constants.KEY_ONBOARDING_COMPANIES);
    }

    @OnClick(R.id.worker_profile_location_edit)
    void locationEdit() {
        editProfile(Constants.KEY_ONBOARDING_LOCATION);
    }

    @OnClick(R.id.worker_view_profile_position)
    void editRole() {
        editProfile(Constants.KEY_ONBOARDING_ROLE);
    }

    @OnClick(R.id.worker_view_profile_experience)
    void editExperience() {
        editAccountDetailsDialog = EditAccountDetailsDialog.newInstance("Years of experience",
                String.valueOf(worker.yearsExperience), true, this);
        editAccountDetailsDialog.setCancelable(false);
        editAccountDetailsDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_requirements_edit)
    void editRequirements() {
        editProfile(Constants.KEY_STEP_REQUIREMENTS);
    }

    @OnClick(R.id.worker_profile_nationality_edit)
    void openExperienceFragment() {
        EditNationalityDialog.newInstance(this).show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_birthday_edit)
    void editBirthDate() {
        EditBirthDateDialog.newInstance(this).show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_nis_edit)
    void editNis() {
        EditNisDialog.newInstance(this).show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.worker_profile_passport_edit)
    void editPassport() {
        editProfile(Constants.KEY_STEP_PASSPORT);
    }

    @OnClick(R.id.worker_profile_experience_type_edit)
    void editExperienceTypes() {
        editProfile(Constants.KEY_ONBOARDING_SPECIFIC_EXPERIENCE);
    }

    @OnClick(R.id.worker_profile_email_edit)
    void editEmail() {
        EditAccountDetailsDialog.newInstance("Email", workerEmail.getText().toString(), false,
                new EditAccountDetailsDialog.InputFinishedListener() {
                    @Override
                    public void onDone(String input, boolean onlyDigits) {
                        if (TextTools.isEmailValid(input)) {

                            HashMap<String, Object> payload = new HashMap<>();
                            payload.put("email", input);
                            patchWorker(payload);
                        } else
                            DialogBuilder.showStandardDialog(getContext(), "", getString(R.string.validate_email));
                    }
                }).show(getFragmentManager(), "");
    }

    @OnClick(R.id.worker_view_profile_rating)
    void openReviews() {
        startActivity(new Intent(getActivity(), ReviewActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            avatarImage.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
            avatarImage.setImageBitmap(imageBitmap);
            prepPicture(getActivity(), imageBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            case REQUEST_PERMISSION_READ_STORAGE:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchOpenGalleryIntent();
                }
                break;
        }
    }

    @OnClick(R.id.worker_view_profile_avatar)
    void showChooserDialog() {
        CharSequence[] options = {getString(R.string.onboarding_take_photo),
                getString(R.string.onboarding_choose_from_gallery),
                getString(R.string.onboarding_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.onboarding_add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        dialog.cancel();
                        break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick(R.id.worker_view_profile_name)
    void editName() {
        String firstName = null;
        String lastName = null;
        if (worker != null) {
            firstName = worker.firstName;
            lastName = worker.lastName;
        }

        EditNameDialog.newInstance(firstName, lastName, new EditNameDialog.NameChangedListener() {
            @Override
            public void onNameChanged(String name, String surname) {
                HashMap<String, Object> payload = new HashMap<>();
                payload.put("first_name", name);
                payload.put("last_name", surname);
                patchWorker(payload);
            }
        }).show(getFragmentManager(), "");
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchOpenGalleryIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                REQUEST_IMAGE_SELECTION);
    }

    private void prepPicture(Context context, Bitmap bitmap) {
        try {
            File file = new File(getContext().getCacheDir(),
                    "temp" + String.valueOf(bitmap.hashCode()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
            baos.flush();
            baos.close();

            uploadPicture(context, file);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void uploadPicture(Context context, File file) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), request);
        HttpRestServiceConsumer.getBaseApiClient()
                .uploadProfileImageWorker(SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), body)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            worker = response.body().getResponse();
                            Picasso.with(getContext())
                                    .load(worker.picture)
                                    .error(R.drawable.bob)
                                    .fit()
                                    .centerCrop()
                                    .placeholder(R.drawable.bob)
                                    .into(avatarImage);
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCscsUpdated(int status) {
        switch (status) {
            case VERIFICATION_VALID:
                if (worker != null) fetchCscsDetails(worker.id);
                break;
            case VERIFICATION_FAILED:
                DialogBuilder.showStandardDialog(getContext(), "Error", getString(R.string.cscs_status_infrastructure_issue));
                break;
            case VERIFICATION_INVALID:
                DialogBuilder.showStandardDialog(getContext(), "Error", getString(R.string.cscs_status_carddetails_invalid));
                break;
            default:
                DialogBuilder.showStandardDialog(getContext(), "Error", getString(R.string.verified_cscs_failed));
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cscs_status)
            EditCscsDetailsDialog.newInstance(worker.lastName, this).show(getActivity().getSupportFragmentManager(), "");
    }

    @Override
    public void onDataChanged() {
        fetchWorker();
    }
}
