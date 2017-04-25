package construction.thesquare.employer.myjobs.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.PreviewJobActivity;
import construction.thesquare.employer.myjobs.adapter.JobDetailsPagerAdapter;
import construction.thesquare.employer.myjobs.dialog.ViewMoreDialog;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/30/2016.
 */

public class JobDetailsFragment extends Fragment
        implements ViewMoreDialog.ViewMoreListener {

    public static final String TAG = "JobDetailsFragment";

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    // job details
    @BindView(R.id.item_job_location) JosefinSansTextView location;
    @BindView(R.id.item_job_start_date) JosefinSansTextView startDate;
    @BindView(R.id.item_job_salary_period) JosefinSansTextView payPeriod;
    @BindView(R.id.item_job_salary_number) JosefinSansTextView payNumber;
    @BindView(R.id.item_job_occupation) JosefinSansTextView occupation;
    @BindView(R.id.item_job_experience) JosefinSansTextView experience;
    @BindView(R.id.item_job_logo) ImageView logo;
    @BindView(R.id.item_job_company_name) JosefinSansTextView name;
    @BindView(R.id.item_job_id) JosefinSansTextView id;
    @BindView(R.id.item_job_name) JosefinSansTextView nameTextView;
    @BindView(R.id.view_more) JosefinSansTextView viewMore;
    @BindView(R.id.toggle_edit) Switch toggleEdit;

    @BindView(R.id.item_job_applied) TextView amountApplied;
    @BindView(R.id.item_job_offered) TextView amountOfferred;
    @BindView(R.id.item_job_booked) TextView amountBooked;
    @BindView(R.id.item_job_stats) LinearLayout stats;


    private JobDetailsPagerAdapter adapter;

    public static JobDetailsFragment newInstance(int id) {
        JobDetailsFragment fragment = new JobDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_JOB_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_JOB_DETAILS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewMore.setVisibility(View.VISIBLE);
    }

    private void showEdits() {
        //
    }

    private void hideEdits() {
        //
    }

    public void onAction(int action) {
        switch (action) {
            case ViewMoreDialog.EDIT_DESCRIPTION:
                //
                break;
            case ViewMoreDialog.EDIT_ENGLISH_LEVEL:
                //
                break;
            case ViewMoreDialog.EDIT_EXPERIENCE_TYPES:
                //
                break;
            case ViewMoreDialog.EDIT_OVERTIME:
                //
                break;
            case ViewMoreDialog.EDIT_QUALIFICATIONS:
                //
                break;
            case ViewMoreDialog.EDIT_REQUIREMENTS:
                //
                break;
            case ViewMoreDialog.EDIT_SKILLS:
                //
                break;
            default:
                //
                break;
        }
    }

    public void setupViewMore(final Job job) {
        // it's possible the user will leave by this time, creating a npe. :(
        try {
            getView().findViewById(R.id.view_more)
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // my super cool view more dialogFragment is no longer liked by Sian
                    // he is very sad now... :(
//                    ViewMoreDialog dialog = ViewMoreDialog.newInstance(JobDetailsFragment.this, job);
//                    dialog.show(getActivity().getSupportFragmentManager(), "view_more");
                }
            });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void setupEditing(final Job job) {
        try {
            final CreateRequest result = new CreateRequest();
            //
            if (null != job.status) {
                if (job.status.id == 2) {
                    result.isLive = true;
                }
            }

            if (null != job.connectEmail) {
                TextTools.log(TAG, "connect_email not null");
                result.connectEmail = job.connectEmail;
            } else {
                TextTools.log(TAG, "connect_email null");
            }
            result.isConnect = job.isConnect;

            result.id = job.id;
            result.roleName = job.role.name;
            result.role = job.role.id;
            result.roleObject = job.role;
            result.experience = job.experience;
            result.budget = job.budget;
            result.budgetType = job.budgetType.id;
            result.location = job.location;
            result.locationName = job.locationName;
            result.contactName = job.contactName;
            result.contactPhone = job.contactPhone;
            result.contactCountryCode = job.contactCountryCode;
            result.contactPhoneNumber = job.contactPhoneNumber;
            result.address = job.address;
            result.notes = job.notes;
            result.description = job.description;
            result.english = job.english;
            result.overtime = job.payOvertime;
            result.overtimeValue = job.overtimeRate;
            String englishString = "Basic";
            switch (job.english) {
                case 3:
                    englishString = "Fluent";
                    break;
                case 4:
                    englishString = "Native";
                    break;
            }
            result.englishLevelString = englishString;

            /**
             * Loading Trades!
             */
            if (null != job.trades) {
                if (!job.trades.isEmpty()) {
                    result.tradeObjects = job.trades;
                    int[] tradeIds = new int[job.trades.size()];
                    List<String> tradeStrings = new ArrayList<>();
                    for (int i = 0; i < job.trades.size(); i++) {
                        tradeIds[i] = job.trades.get(i).id;
                        tradeStrings.add(job.trades.get(i).name);
                    }
                    result.trades = tradeIds;
                    result.tradeStrings = tradeStrings;
                }
            }

            /**
             * Loading qualifications!
             */
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    // filtering out qualifications that are requirements
                    List<Qualification> q2 = new ArrayList<>();
                    for (Qualification q : job.qualifications) {
                        if (!q.onExperience) {
                            q2.add(q);
                        }
                    }
                    int[] qualificationIds = new int[q2.size()];
                    List<String> qualificationStrings = new ArrayList<>();
                    for (int i = 0; i < q2.size(); i++) {
                        qualificationIds[i] = q2.get(i).id;
                        qualificationStrings.add(q2.get(i).name);
                    }
                    result.qualificationObjects = q2;
                    result.qualifications = qualificationIds;
                    result.qualificationStrings = qualificationStrings;
                }
            }

            /**
             * Loading skills!
             */
            if (null != job.skills) {
                if (!job.skills.isEmpty()) {
                    int[] skillIds = new int[job.skills.size()];
                    List<String> skillNames = new ArrayList<>();
                    for (int i = 0; i < job.skills.size(); i++) {
                        skillIds[i] = job.skills.get(i).id;
                        skillNames.add(job.skills.get(i).name);
                    }
                    result.skills = skillIds;
                    result.skillStrings = skillNames;
                }
            }

            /**
             * Loading requirements.
             */
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    // extracting the requirements from qualifications
                    List<Qualification> q2 = new ArrayList<>();
                    for (Qualification q : job.qualifications) {
                        if (q.onExperience) {
                            q2.add(q);
                        }
                    }
                    int[] requirementIds = new int[q2.size()];
                    List<String> requirementStrings = new ArrayList<>();
                    for (int i = 0; i < q2.size(); i++) {
                        requirementIds[i] = q2.get(i).id;
                        requirementStrings.add(q2.get(i).name);
                    }
                    result.requirementObjects = q2;
                    result.requirements = requirementIds;
                    result.requirementStrings = requirementStrings;
                }
            }

            /**
             * Loading experience types!
             */
            if (null != job.experienceTypes) {
                if (!job.experienceTypes.isEmpty()) {
                    result.experienceTypeObjects = job.experienceTypes;
                    int[] experienceTypeIds = new int[job.experienceTypes.size()];
                    List<String> experienceTypeNames = new ArrayList<>();
                    for (int i = 0; i < job.experienceTypes.size(); i++) {
                        experienceTypeIds[i] = job.experienceTypes.get(i).id;
                        experienceTypeNames.add(job.experienceTypes.get(i).name);
                    }
                    result.experienceTypes = experienceTypeIds;
                    result.experienceTypeStrings = experienceTypeNames;
                }
            }

            /**
             * Loading logo.
             */
            if (null != job.owner) {
                if (null != job.owner.picture) {
                    result.logo = job.owner.picture;
                }
            }

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date = format.parse(job.start);
            calendar.setTime(date);
            String startDate = calendar.get(Calendar.YEAR) + "-" +
                    String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" +
                    calendar.get(Calendar.DAY_OF_MONTH);
            String startTime = calendar.get(Calendar.HOUR) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" + "00";
            result.date = startDate;
            result.time = startTime;

            /**
             *
             */
            getView().findViewById(R.id.view_more)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), PreviewJobActivity.class);
                            intent.putExtra("request", result);
                            intent.putExtra("show_cancel", true);
                            intent.putExtra("from_view_more", true);
                            intent.putExtra("editable", job.isEditable);
                            getActivity().finish();
                            startActivity(intent);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            CrashLogHelper.logException(e);
        }
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
                Intent intent = new Intent(getActivity(), MainEmployerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("");
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
        fetchInfo(getArguments().getInt(Constants.KEY_JOB_ID));
    }

    private void fetchInfo(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchJob(id)
                .enqueue(new Callback<ResponseObject<Job>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Job>> call,
                                           Response<ResponseObject<Job>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {

                            populate(response.body().getResponse());

                        } else {
                            viewMore.setVisibility(View.GONE);
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Job>> call, Throwable t) {
                        viewMore.setVisibility(View.GONE);
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populate(final Job job) {

        setupEditing(job);

        if (job.status.id == Job.TAB_LIVE) {
            adapter = new JobDetailsPagerAdapter(getContext(),
                    getChildFragmentManager(),
                    getArguments().getInt(Constants.KEY_JOB_ID),
                    job.isConnect ? Constants.ADAPTER_FOR_CONNECT
                            : Constants.ADAPTER_FOR_BOOK);
            viewPager.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(2);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(2);
        }

        if (null != job.role) {
            occupation.setText(job.role.name);
        }

        if (null != job.name) {
            nameTextView.setText(job.name);
        }

        if (null != job.locationName) {
            location.setText(job.locationName);
        }

        payNumber.setText("£" + String.valueOf(NumberFormat
                .getInstance(Locale.UK).format(Double.valueOf(job.budget))));

        if (null != job.budgetType) {
            if (null != job.budgetType.name) {
                payPeriod.setText("Per " + job.budgetType.name);
            }

            if (job.budgetType.id == 4) {
                payPeriod.setText("£ POA");
                payNumber.setVisibility(View.GONE);
            }
        }

        experience.setText(String
                .format(getString(R.string.employer_jobs_experience),
                        job.experience,
                        getResources()
                                .getQuantityString(R.plurals.year_plural,
                                        job.experience)));

        if (null != job.company) {
            if (null != job.company.name) {
                name.setText(job.company.name);
            }
            if (null != job.company.logo) {
                name.setVisibility(View.GONE);
                logo.setVisibility(View.VISIBLE);
                Picasso.with(getContext())
                        .load(job.company.logo)
                        .into(logo);
            } else {
                logo.setVisibility(View.GONE);
                name.setVisibility(View.VISIBLE);
            }
        }

        id.setText("Job ref ID: " + job.jobRef);

        if (null != job.start) {
            try {

                if (!job.isConnect) {
                    startDate.setText(String.format(getResources()
                            .getString(R.string.employer_jobs_starts), DateUtils.getFormattedJobDate(job.start)));
                } else {
                    startDate.setText(String.format(getResources()
                            .getString(R.string.employer_jobs_app_deadline), DateUtils.getFormattedJobDate(job.start)));
                }

            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }

        stats.setVisibility(View.VISIBLE);
        amountApplied.setText(String
                .format(getString(R.string.job_details_applied_amount),
                        job.amountApplied));
        amountOfferred.setText(String
                .format(getString(R.string.job_details_offered_amount),
                        job.amountOfferred));
        amountBooked.setText(String
                .format(getString(R.string.job_details_booked_amount),
                        job.amountApplied));
    }

}