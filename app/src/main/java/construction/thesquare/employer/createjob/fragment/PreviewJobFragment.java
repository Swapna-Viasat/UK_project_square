package construction.thesquare.employer.createjob.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.dialog.CRNDialog;
import construction.thesquare.employer.myjobs.fragment.JobDetailsFragment;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.redirects.PaymentRedirect;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Evgheni Gherghelejiu
 * on 01/11/2016
 */
public class PreviewJobFragment extends Fragment
        implements PaymentRedirect {

    public static final String TAG = "PreviewJobFragment";

    // action buttons
    @BindView(R.id.draft) TextView saveAsDraftButton;
    //

    @BindViews({
            R.id.preview_occupation,
            R.id.preview_experience,
            R.id.preview_reporting_to,
            R.id.preview_start_date,
            R.id.job_details_english_level_label,
            R.id.job_details_overtime_label,
            R.id.preview_location,
            R.id.preview_salary_number,
            R.id.job_details_description_label,
            R.id.job_details_skills_label,
            R.id.job_details_reqs_label,
            R.id.job_details_qualifications_label,
            R.id.job_details_experience_types_label})
    List<TextView> edits;

    // new feature
    @BindView(R.id.job_details_connect_contact) ViewGroup connectContact;
    @BindView(R.id.job_details_reporting_to) ViewGroup reportingTo;
    @BindView(R.id.job_details_connect_person) TextView connectPerson;
    @BindView(R.id.job_details_connect_phone) TextView connectPhone;
    @BindView(R.id.job_details_connect_email) TextView connectEmail;
    @BindView(R.id.job_details_connect_date) TextView connectDeadline;

    @BindView(R.id.preview_logo) ImageView logo;
    @BindView(R.id.preview_occupation) JosefinSansTextView role;
    @BindView(R.id.preview_trades) JosefinSansTextView trades;
    @BindView(R.id.preview_experience) JosefinSansTextView experience;
    @BindView(R.id.preview_salary_period) JosefinSansTextView salaryPeriod;
    @BindView(R.id.preview_salary_number) JosefinSansTextView salaryNumber;
    @BindView(R.id.preview_start_date) JosefinSansTextView startDate;
    @BindView(R.id.preview_location) JosefinSansTextView location;

    private CreateRequest createRequest;
    private boolean showCancel;

    @BindView(R.id.job_details_description) JosefinSansTextView description;
    @BindView(R.id.job_details_skills) JosefinSansTextView skills;
    @BindView(R.id.job_details_english_level) JosefinSansTextView englishLevel;
    @BindView(R.id.job_details_overtime) JosefinSansTextView overtime;
    @BindView(R.id.job_details_qualifications) JosefinSansTextView qualifications;
    @BindView(R.id.job_details_qualifications2) JosefinSansTextView qualifications2;
    @BindView(R.id.job_details_experience_types) JosefinSansTextView experienceTypes;
    @BindView(R.id.job_details_owner) JosefinSansTextView owner;
    @BindView(R.id.job_details_owner_phone) JosefinSansTextView ownerPhone;
    @BindView(R.id.job_details_owner_address) JosefinSansTextView ownerAddress;
    @BindView(R.id.job_details_notes) JosefinSansTextView notes;
    @BindView(R.id.job_details_date) JosefinSansTextView date;

    private SupportMapFragment mapFragment;

    private boolean fromViewMore;

    public static PreviewJobFragment newInstance(CreateRequest request,
                                                 boolean fromViewMore) {
        PreviewJobFragment fragment = new PreviewJobFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("from_view_more", fromViewMore);
        bundle.putSerializable("request", request);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_PREVIEW);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_preview_job, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createRequest = (CreateRequest) getArguments().getSerializable("request");

        if (createRequest.isConnect) {
            reportingTo.setVisibility(View.GONE);
            connectContact.setVisibility(View.VISIBLE);
        } else {
            reportingTo.setVisibility(View.VISIBLE);
            connectContact.setVisibility(View.GONE);
        }

        if (createRequest.isLive) {
            saveAsDraftButton.setVisibility(View.GONE);
        }

        fromViewMore = getArguments().getBoolean("from_view_more");
        createRequest.detailsLowerPart = false;
        showCancel = getActivity().getIntent()
                .getBooleanExtra("show_cancel", false);
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                try {
                    LatLng loc = new LatLng(createRequest.location.latitude,
                            createRequest.location.longitude);
                    googleMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(loc, 12));
                    googleMap.addMarker(new MarkerOptions().position(loc));
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            }
        });
    }

    @OnClick({
            R.id.preview_occupation,
            R.id.preview_experience,
            R.id.preview_reporting_to,
            R.id.preview_connect,
            R.id.preview_start_date,
            R.id.job_details_english_level_label,
            R.id.job_details_overtime_label,
            R.id.preview_location,
            R.id.preview_salary_number,
            R.id.job_details_description_label,
            R.id.job_details_skills_label,
            R.id.job_details_reqs_label,
            R.id.job_details_qualifications_label,
            R.id.job_details_experience_types_label})
    public void edit(View view) {
        Fragment fragment = new Fragment();
        switch (view.getId()) {
            case R.id.preview_salary_number:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_reporting_to:
                createRequest.detailsLowerPart = true;
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_connect:
                createRequest.detailsLowerPart = true;
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_start_date:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_location:
                fragment = SelectLocationFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_description_label:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_skills_label:
                fragment = SelectSkillsFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_occupation:
                fragment = SelectRoleFragment.newInstance(createRequest, true);
                break;
            case R.id.preview_experience:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_reqs_label:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_english_level_label:
                fragment = SelectExperienceFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_qualifications_label:
                fragment = SelectQualificationsFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_experience_types_label:
                fragment = SelectExperienceTypeFragment.newInstance(createRequest, true);
                break;
            case R.id.job_details_overtime_label:
                fragment = SelectDetailsFragment.newInstance(createRequest, true);
                break;
        }
        if (null != fragment) {
            ((AppCompatActivity) getActivity())
                    .getSupportActionBar().setTitle("");
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity())
                .getSupportActionBar().setTitle(getString(R.string.create_job_preview));
        if (showCancel) {
            //
        } else {
            getView().findViewById(R.id.cancel).setVisibility(View.GONE);
        }
        if (getActivity().getIntent().getBooleanExtra("editable", true)) {
            //
        } else {
            disableEditing();
        }
        if (fromViewMore) {
            ((TextView) getView().findViewById(R.id.publish)).setText("Save");
        } else {
            //
        }
        try {

            role.setText(createRequest.roleName);
            if (null != createRequest.tradeStrings) {
                trades.setText("(" + TextTools.toCommaList(createRequest.tradeStrings, false) + ")");
            } else {
                trades.setVisibility(View.GONE);
            }
            experience.setText(String
                    .format(getString(R.string.create_job_experience_years),
                            createRequest.experience,
                            getResources().getQuantityString(R.plurals.year_plural,
                                    createRequest.experience)));

            /**
             * Reporting to!
             */
            if (null != createRequest.contactName) {
                owner.setText(createRequest.contactName);
                connectPerson.setText(createRequest.contactName);
            }
            ownerPhone.setText("+ " + createRequest.contactCountryCode
                    + " " + createRequest.contactPhoneNumber);
            connectPhone.setText("+ " + createRequest.contactCountryCode
                    + " " + createRequest.contactPhoneNumber);
            if (null != createRequest.address) {
                ownerAddress.setText(createRequest.address);
            }
            if (null != createRequest.connectEmail) {
                connectEmail.setText(createRequest.connectEmail);
            }
            //
            date.setText(createRequest.date + " - " + createRequest.time);
            connectDeadline.setText(createRequest.date + " - " + createRequest.time);
            //
            notes.setText(createRequest.notes);
            // lists
            description.setText(createRequest.description);
            skills.setText(TextTools.toBulletList(createRequest.skillStrings, true));
            qualifications.setText(TextTools.toBulletList(createRequest.requirementStrings, true));
            qualifications2.setText(TextTools.toBulletList(createRequest.qualificationStrings, true));
            experienceTypes.setText(TextTools.toBulletList(createRequest.experienceTypeStrings, true));
            // salary
            salaryNumber.setText(String.valueOf(getString(R.string.pound_sterling) + " " +
                    NumberFormat.getInstance(Locale.UK).format(createRequest.budget)));
            switch (createRequest.budgetType) {
                case 1:
                    salaryPeriod.setText("per hour");
                    salaryPeriod.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    salaryPeriod.setText("per day");
                    salaryPeriod.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    salaryPeriod.setText("per year");
                    salaryPeriod.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    salaryNumber.setText("£POA");
                    salaryPeriod.setVisibility(View.GONE);
                    break;
            }

            // overtime
            if (createRequest.overtime) {
                Log.d(TAG, String.valueOf(createRequest.overtimeValue));
                overtime.setText(String.format(getString(R.string.job_details_overtime_text),
                        String.valueOf(createRequest.overtimeValue)));
            } else {
                overtime.setText("n/a");
            }
            // english level
            if (null != createRequest.englishLevelString) {
                Log.d(TAG, String.valueOf(createRequest.englishLevelString));
                englishLevel.setText(createRequest.englishLevelString);
            }
            // description
            if (null != createRequest.description) {
                description.setText(createRequest.description);
            }

            startDate.setText(createRequest.date);

            location.setText(createRequest.locationName);

            if (null != createRequest.logo) {
                Picasso.with(getContext())
                        .load(createRequest.logo)
                        .into(logo);
            }

            if (createRequest.id != 0) {
                //Toast.makeText(getContext(), String.valueOf(createRequest.id), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private HashMap<String, Object> loadRequest(int status) {
        HashMap<String, Object> payload = new HashMap<>();
        try {

            if (null != createRequest.connectEmail) {
                payload.put("connect_email", createRequest.connectEmail);
            }
            if (null != createRequest.name) {
                payload.put("name", createRequest.name);
            }
            payload.put("is_connect", createRequest.isConnect);

            if (createRequest.id != 0) {
                payload.put("id", createRequest.id);
            }

            payload.put("status", status);
            payload.put("role", createRequest.role);
            payload.put("workers_quantity", (createRequest.roleObject).amountWorkers);
            payload.put("trades", createRequest.trades);
            payload.put("experience", createRequest.experience);
            payload.put("english_level", createRequest.english);

            // beginning of wow
            int[] quals = new int[createRequest.requirements.length +
                    createRequest.qualifications.length];
            List<Integer> reqs = new ArrayList<>();
            for (int i = 0; i < createRequest.requirements.length; i++) {
                reqs.add(createRequest.requirements[i]);
            }
            List<Integer> qual = new ArrayList<>();
            for (int i = 0; i < createRequest.qualifications.length; i++) {
                qual.add(createRequest.qualifications[i]);
            }
            List<Integer> combined = new ArrayList<>();
            combined.addAll(reqs); combined.addAll(qual);
            for (int i = 0; i < combined.size(); i++) {
                quals[i] = combined.get(i);
            }
            payload.put("qualifications", quals);
            // end of wow

            payload.put("skills", createRequest.skills);
            payload.put("experience_type", createRequest.experienceTypes);
            payload.put("description", createRequest.description);
            payload.put("budget_type", createRequest.budgetType);
            payload.put("budget", createRequest.budget);
            payload.put("pay_overtime", createRequest.overtime);
            payload.put("pay_overtime_value", createRequest.overtimeValue);
            payload.put("contact_name", createRequest.contactName);

            payload.put("contact_phone", "+" + String.valueOf(createRequest.contactCountryCode) +
                    " " + String.valueOf(createRequest.contactPhoneNumber));

            payload.put("address", createRequest.address);
            // date and time
            payload.put("start_datetime", createRequest.date + "T" + createRequest.time + "+00:00");
            if (null != createRequest.rawDate) {
                payload.put("start_datetime", DateUtils.toPayloadDate(createRequest.rawDate));
            }
            TextTools.log(TAG, String.valueOf(payload.get("start_datetime")));

            payload.put("extra_notes", createRequest.notes);
            payload.put("location", createRequest.location);
            payload.put("location_name", createRequest.locationName);
            //payload.put("cscs_required", false);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        return payload;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job_post_step, menu);
        int positionOfMenuItem = 0;
        MenuItem item = menu.getItem(positionOfMenuItem);
        SpannableString s = new SpannableString("Cancel");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        item.setTitle(s);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createJobCancel:
                if (getActivity().getIntent()
                        .getBooleanExtra("editable", true)) {
                    if (fromViewMore) {
                        discard();
                    } else {
                        final Dialog abandonDialog = new Dialog(getContext());
                        abandonDialog.setCancelable(false);
                        abandonDialog.setContentView(R.layout.dialog_abandon_post);
                        abandonDialog.findViewById(R.id.abandon_dismiss)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        abandonDialog.dismiss();
                                    }
                                });
                        abandonDialog.findViewById(R.id.abandon_abandon)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        abandonDialog.dismiss();
                                        //
                                        discard();
                                    }
                                });
                        abandonDialog.findViewById(R.id.abandon_save)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        abandonDialog.dismiss();
                                        callApi(Constants.JOB_STATUS_DRAFT);
                                    }
                                });
                        abandonDialog.show();
                    }
                } else {
                    discard();
                }
                return true;
        }
        return false;
    }

    private void discard() {
        getActivity()
                .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .remove(Constants.KEY_STEP)
                .putBoolean(Constants.KEY_UNFINISHED, false)
                .remove(Constants.KEY_REQUEST)
                .commit();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainEmployerActivity.class));
    }

    @OnClick(R.id.publish)
    public void publish() {
        callApi(Constants.JOB_STATUS_LIVE);
    }

    @OnClick(R.id.draft)
    public void saveDraft() {
        callApi(Constants.JOB_STATUS_DRAFT);
    }

    @OnClick(R.id.cancel)
    public void cancel() {
        //
        final Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_cancel_job);
        dialog.findViewById(R.id.no)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        callApiCancel(createRequest.id);
                    }
                });
        dialog.show();
    }

    private void callApiCancel(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .cancelJob(id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);

                            getActivity()
                                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                                    .edit()
                                    .putBoolean(Constants.KEY_UNFINISHED, false)
                                    .putInt(Constants.KEY_STEP, 0)
                                    .remove(Constants.KEY_REQUEST)
                                    .commit();

                            Intent intent = new Intent(getActivity(), MainEmployerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
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

    private DialogInterface.OnClickListener gotoPaymentsListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveDraftThenSelectPlan();
                }
            };

    public void onRedirect() {
        saveDraftThenSelectPlan();
    }

    private void saveDraftThenSelectPlan() {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .createJob(loadRequest(Constants.JOB_STATUS_DRAFT))
                    .enqueue(new Callback<ResponseObject<Job>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Job>> call,
                                               Response<ResponseObject<Job>> response) {
                            try {

                                DialogBuilder.cancelDialog(dialog);

                                if (response.isSuccessful()) {
                                    getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(Constants.DRAFT_JOB_AWAIT_PLAN, true)
                                            .putInt(Constants.DRAFT_JOB_ID, response.body().getResponse().id)
                                            .commit();
                                    discard();
                                } else {
                                    HandleErrors.parseError(getContext(), dialog,
                                            response,
                                            PreviewJobFragment.this,
                                            gotoPaymentsListener, showCRNDialog);
                                }
                            } catch (Exception e) {
                                //
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                            try {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            } catch (Exception e) {
                                //
                            }
                        }
                    });

        } catch (Exception e) {
            CrashLogHelper.logException(e);
            new AlertDialog.Builder(getContext())
                    .setMessage("Something went wrong!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void callApi(int status) {
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .createJob(loadRequest(status))
                    .enqueue(new Callback<ResponseObject<Job>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Job>> call,
                                               Response<ResponseObject<Job>> response) {
                            try {

                                DialogBuilder.cancelDialog(dialog);

                                if (response.isSuccessful()) {

                                    getActivity()
                                            .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(Constants.KEY_UNFINISHED, false)
                                            .putInt(Constants.KEY_STEP, 0)
                                            .remove(Constants.KEY_REQUEST)
                                            .commit();

                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.frame,
                                                    JobDetailsFragment.newInstance(response.body().getResponse().id))
                                            .commit();

                                } else {
                                    HandleErrors.parseError(getContext(), dialog,
                                            response, PreviewJobFragment.this,
                                            gotoPaymentsListener, showCRNDialog);
                                }
                            } catch (Exception e) {
                                //
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                            try {
                                HandleErrors.parseFailureError(getContext(), dialog, t);
                            } catch (Exception e) {
                                //
                            }
                        }
                    });

        } catch (Exception e) {
            CrashLogHelper.logException(e);
            new AlertDialog.Builder(getContext())
                    .setMessage("Something went wrong!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void disableEditing() {
        for (TextView view : edits) {
            hideEditDrawableRight(view);
        }
        //getView().findViewById(R.id.cancel).setVisibility(View.GONE);
        getView().findViewById(R.id.publish).setVisibility(View.GONE);
    }

    private void hideEditDrawableRight(TextView textView) {
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    private final DialogInterface.OnClickListener showCRNDialog =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int id) {
                    if (null != dialogInterface) {
                        dialogInterface.dismiss();
                    }
                    CRNDialog.newInstance(new CRNDialog.CRNListener() {
                        @Override
                        public void onResult(final boolean success) {
                            Log.d(TAG, "yes");
                            if (success) {
                                callApi(Constants.JOB_STATUS_LIVE);
                            } else {
                                new AlertDialog.Builder(getContext())
                                        .setMessage(getString(R.string.create_job_invalid_crn))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }
                    }).show(getChildFragmentManager(), "");
                }
            };
}