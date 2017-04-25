/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.employer.mygraftrs.fragment;

import android.app.Dialog;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.FlavorSettings;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.LikeWorkerConnector;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Company;
import construction.thesquare.shared.models.ExperienceType;
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
import construction.thesquare.worker.signup.model.CSCSCardWorker;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWorkerProfileFragment extends Fragment implements LikeWorkerConnector.Callback {

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
    @BindView(R.id.worker_profile_bio_text)
    TextView bioView;
    @BindView(R.id.worker_details_bullet_list_experience)
    LinearLayout experienceView;
    @BindView(R.id.worker_details_bullet_list_requirements)
    LinearLayout requirementsView;
    @BindView(R.id.worker_details_bullet_list_skills)
    LinearLayout skillsView;
    @BindView(R.id.worker_details_bullet_list_companies)
    TextView companiesView;
    @BindView(R.id.worker_details_preferred_location)
    TextView locationView;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindViews({R.id.cscs1, R.id.cscs2, R.id.cscs3, R.id.cscs4,
            R.id.cscs5, R.id.cscs6, R.id.cscs7, R.id.cscs8})
    List<TextView> cscsNumbers;
    @BindView(R.id.workerImage)
    ImageView cscsImage;
    @BindView(R.id.cscs_status)
    TextView cscsStatus;
    @BindView(R.id.cscsContent)
    View cscsContent;
    @BindViews({R.id.nis1, R.id.nis2, R.id.nis3, R.id.nis4,
            R.id.nis5, R.id.nis6, R.id.nis7, R.id.nis8,
            R.id.nis9})
    List<TextView> nisNumbers;
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
    @BindView(R.id.worker_details_bullet_list_experience_type)
    LinearLayout experienceTypesView;
    @BindView(R.id.book)
    JosefinSansTextView book;
    @BindView(R.id.decline)
    JosefinSansTextView decline;
    @BindView(R.id.bookedBanner)
    View bookedBanner;
    @BindView(R.id.offered_hint_view)
    ViewGroup offeredHint;
    @BindView(R.id.offered_hint_text)
    TextView offeredHintText;
    @BindView(R.id.contactWorkerLayout)
    View contactWorkerLayout;
    @BindView(R.id.nis_status)
    TextView nisStatus;
    @BindView(R.id.nisNumberLayout)
    View nisNumberLayout;
    @BindView(R.id.date_of_birth_status)
    TextView dateOfBirthStatusView;
    @BindView(R.id.passport_status)
    TextView passportStatus;
    @BindView(R.id.likeImage)
    ImageView likeImage;
    @BindView(R.id.workerBioLayout)
    View workerBioLayout;

    private static final int VERIFICATION_NONE = 1;     // Verification hasn't been requested yet.
    private static final int VERIFICATION_FAILED = 2;   // Infrastructural issues: cannot verify cards (e.g. failed to connect to citb website).
    private static final int VERIFICATION_INVALID = 3;  // Supplied card details have been confirmed as invalid.
    private static final int VERIFICATION_VALID = 4;    // Supplied card details are valid.

    private Worker worker;
    private GoogleMap googleMap;
    private int workerId;
    private LikeWorkerConnector likeWorkerConnector;

    public static MyWorkerProfileFragment newInstance(int workerId) {
        MyWorkerProfileFragment fragment = new MyWorkerProfileFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.KEY_WORKER_ID, workerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workerId = getArguments().getInt(Constants.KEY_WORKER_ID, 0);
        likeWorkerConnector = new LikeWorkerConnector(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_worker_profile, container, false);
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

            fillWorkerImage();
            fillWorkerName();
            fillWorkerBio();
            try {
                fillWorkerPosition();
                fillCompanies();
                fillLocationName();
                initMap();
                fillDateOfBirth();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }

            try {
                fillNiNumber();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }

            if (worker.nationality != null)
                nationalityView.setText(worker.nationality.name);
            if (worker.englishLevel != null)
                englishLevel.setText(worker.englishLevel.name);

            if (!CollectionUtils.isEmpty(worker.languages)) {
                List<String> languageNames = new ArrayList<>();
                for (Language l : worker.languages) languageNames.add(l.name);
                languagesView.setText(TextUtils.join(", ", languageNames));
            }

            contactWorkerLayout.setVisibility(View.VISIBLE);
            if (worker.email != null) workerEmail.setText(worker.email);
            if (!CollectionUtils.isEmpty(worker.devices)) {
                workerPhone.setText(worker.devices.get(0).phoneNumber);
            }

            fillPassportImage();
            likeImage.setImageResource(worker.liked ? R.drawable.ic_like_tab : R.drawable.ic_like);
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

        if (!CollectionUtils.isEmpty(worker.experienceTypes)) {
            for (ExperienceType e : worker.experienceTypes) {
                populateWithMatchedItem(experienceTypesView, e.name);
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

        if (!CollectionUtils.isEmpty(worker.qualifications)) {
            for (Qualification q : worker.qualifications) {
                if (q.onExperience) populateWithMatchedItem(requirementsView, q.name);
                else populateWithMatchedItem(experienceView, q.name);
            }
        }
    }

    private void fillSkills() {
        skillsView.removeAllViews();
        if (!CollectionUtils.isEmpty(worker.skills)) {
            for (Skill s : worker.skills) {
                populateWithMatchedItem(skillsView, s.name);
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
        if (worker != null && !TextUtils.isEmpty(worker.zip))
            locationView.setText(getString(R.string.employer_view_worker_commute_time, worker.commuteTime,
                    worker.zip.toUpperCase(Locale.UK)));
    }

    private void drawMarker() {
        if (worker != null && worker.location != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(new LatLng(worker.location.getLatitude(), worker.location.getLongitude())));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(worker.location.getLatitude(), worker.location.getLongitude()), 12f));
        }
    }

    private void fetchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getWorkerProfile(workerId)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            worker = response.body().getResponse();
                            initComponents();
                            fetchCscsDetails(workerId);
                            fillExperienceAndQualifications();
                            fillSkills();
                            fillExperienceTypes();
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
                            populateCscs(response.body());
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

            if (dataResponse.getResponse().cardPicture != null)
                Picasso.with(getContext())
                        .load(FlavorSettings.API_URL + dataResponse.getResponse().cardPicture)
                        .fit().centerCrop().into(cscsImage);

            cscsExpirationView.setText(DateUtils.getCscsExpirationDate(dataResponse.getResponse().expiryDate));

            try {
                cscsRecordsLayout.removeAllViews();
                if (!CollectionUtils.isEmpty(dataResponse.getResponse().cscsRecords)) {
                    for (List<CSCSCardWorker.CscsRecord> cscsRecordList : dataResponse.getResponse().cscsRecords.values())
                        if (!CollectionUtils.isEmpty(cscsRecordList)) {
                            for (CSCSCardWorker.CscsRecord record : cscsRecordList) {
                                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_cscs_record, null, false);
                                TextView cscsText = (TextView) itemView.findViewById(R.id.recordText);
                                cscsText.setTextColor(ContextCompat.getColor(getContext(), R.color.blackSquareColor));
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
        } else {
            cscsStatus.setText(getString(R.string.worker_cscs_not_verified));
            cscsContent.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.likeImage)
    void processLikeClick() {
        if (worker != null) {
            if (worker.liked) likeWorkerConnector.unlikeWorker(getContext(), workerId);
            else likeWorkerConnector.likeWorker(getContext(), workerId);
        }
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
    public void onConnectorSuccess() {
        fetchWorker();
    }

    private void populateWithMatchedItem(LinearLayout layout, String text) {
        if (TextUtils.isEmpty(text) || layout == null) return;

        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_worker_details, null, false);
        ImageView status = (ImageView) item.findViewById(R.id.status);
        StrikeJosefinSansTextView textView = (StrikeJosefinSansTextView) item.findViewById(R.id.worker_details);
        textView.setText(text);
        textView.setStrikeVisibility(false);
        status.setImageResource(R.drawable.red_bullet);
        status.setPadding(2, 2, 2, 2);
        layout.addView(item);
    }
}
