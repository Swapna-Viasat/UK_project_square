package construction.thesquare.employer.createjob.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import construction.thesquare.R;
import construction.thesquare.employer.MainEmployerActivity;
import construction.thesquare.employer.createjob.CreateJobActivity;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.PreviewJobActivity;
import construction.thesquare.employer.createjob.dialog.CRNDialog;
import construction.thesquare.employer.createjob.dialog.JobDetailsDialog;
import construction.thesquare.employer.createjob.dialog.JobNameDialog;
import construction.thesquare.employer.createjob.listener.ConnectCheckListener;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.employer.myjobs.fragment.JobDetailsFragment;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.redirects.PaymentRedirect;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.DayRateSeekBar;
import construction.thesquare.shared.view.widget.HourRateSeekBar;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.view.widget.YearRateSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectDetailsFragment extends Fragment
        implements JobDetailsDialog.DetailsListener,
                        PaymentRedirect,
                    ConnectCheckListener.IsConnectInterface,
                    JobNameDialog.NameListener {

    public static final String TAG = "SelectDetailsFragment";

    private boolean unfinished = true;

    private String tempDate, tempTime;
    public static final int BUDGET_TYPE_HOUR = 1;
    public static final int BUDGET_TYPE_DAY = 2;
    public static final int BUDGET_TYPE_YEAR = 3;
    public static final int BUDGET_TYPE_POA = 4;

    private int budgetType = 1;
    @BindView(R.id.seek_bar_end) JosefinSansTextView max;
    @BindView(R.id.seek_bar_start) JosefinSansTextView min;
    @BindView(R.id.rb_year) RadioButton rbYear;
    @BindView(R.id.rb_hour) RadioButton rbHour;
    @BindView(R.id.rb_day) RadioButton rbDay;
    @BindView(R.id.rb_poa) RadioButton rbPOA;

    // new feature items
    private boolean isConnect;
    @BindView(R.id.check_connect) CheckBox checkBoxConnect;
    @BindView(R.id.check_book) CheckBox checkBoxBook;
    private ConnectCheckListener connectCheckListener;
    @BindView(R.id.connect_contact_info) CardView connectContactInfo;
    @BindView(R.id.book_contact_info) CardView bookContactInfo;
    @BindView(R.id.connect_deadline) CardView connectDeadline;
    // contact fields
    @BindView(R.id.connect_person_input) TextInputLayout connectPersonLayout;
    @BindView(R.id.connect_person) EditText connectPerson;
    @BindView(R.id.connect_email_input) TextInputLayout connectEmailLayout;
    @BindView(R.id.connect_email) EditText connectEmail;
    @BindView(R.id.connect_phone_input) TextInputLayout connectPhoneInput;
    @BindView(R.id.connect_phone) EditText connectPhone;
    @BindView(R.id.ccp_connect) CountryCodePicker ccpConnect;
    // deadline
    @BindView(R.id.connect_date) ImageButton connectDate;
    @BindView(R.id.edit_connect_date) TextView editConnectDate;
    @BindView(R.id.connect_time) ImageButton connectTime;
    @BindView(R.id.edit_connect_time) TextView editConnectTime;
    // end new feature items

    @BindView(R.id.in_layout_extra) TextInputLayout layoutExtra;
    @BindView(R.id.extra) JosefinSansEditText extra;
    @BindView(R.id.in_layout_address) TextInputLayout layoutAddress;
    @BindView(R.id.address) JosefinSansEditText address;
    @BindView(R.id.in_layout_phone) TextInputLayout layoutPhone;
    @BindView(R.id.phone) JosefinSansEditText phone;
    @BindView(R.id.in_layout_contact) TextInputLayout layoutContact;
    @BindView(R.id.contact) JosefinSansEditText contact;
    @BindView(R.id.ccp) CountryCodePicker ccp;

    @BindView(R.id.hour_seek_bar) HourRateSeekBar hourSeek;
    @BindView(R.id.day_seek_bar) DayRateSeekBar daySeek;
    @BindView(R.id.year_seek_bar) YearRateSeekBar yearSeek;

    @BindView(R.id.overtime_switch) SwitchCompat overtimeSwitch;
    @BindView(R.id.overtime) JosefinSansEditText overtime;
    @BindView(R.id.description) JosefinSansTextView description;
    @BindView(R.id.job_name) JosefinSansTextView jobName;
    public String tempDescription = "";
    public String tempName = "";
    private JobDetailsDialog jobDetailsDialog;
    private JobNameDialog jobNameDialog;

    @BindView(R.id.edit_date) JosefinSansTextView editDate;
    @BindView(R.id.edit_time) JosefinSansTextView editTime;

    private CreateRequest request;
    private Role selectedRole;

    public static SelectDetailsFragment newInstance(CreateRequest request,
                                                    boolean singleEdit) {
        //
        SelectDetailsFragment fragment = new SelectDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_DETAILS);
    }

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_details, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    public void onConnectSelected(boolean yes) {
        isConnect = yes;
        TextTools.log(TAG, String.valueOf(isConnect));
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        connectCheckListener = new ConnectCheckListener(
                this,
                checkBoxConnect,
                checkBoxBook,
                connectContactInfo,
                connectDeadline,
                bookContactInfo);
        checkBoxConnect.setOnCheckedChangeListener(connectCheckListener);
        checkBoxBook.setOnCheckedChangeListener(connectCheckListener);

        request = (CreateRequest) getArguments().getSerializable("request");

        if (request.isConnect) {
            TextTools.log(TAG, " is a connect job create request");
            isConnect = true;
            checkBoxConnect.performClick();
        }

        CountDownTimer timer = new CountDownTimer(200, 200) {
            @Override
            public void onTick(long l) {
                //
            }

            @Override
            public void onFinish() {
                try {
                    getView().scrollTo(0, getView().getHeight());
                } catch (Exception e) {
                    CrashLogHelper.logException(e);
                }
            }
        };
        if (request.detailsLowerPart) {
            timer.start();
        }

        selectedRole = request.roleObject;

        overtimeSwitch.setChecked(false);
        overtimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    overtime.setEnabled(true);
                    request.overtime = true;
                } else {
                    overtime.setEnabled(false);
                    request.overtime = false;
                }
            }
        });

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {

            try {
                if (null != request.description) description.setText(request.description);
                if (null != request.notes) extra.setText(request.notes);
                if (null != request.date) {
                    tempDate = request.date;
                    editDate.setText(tempDate.split("-")[2] + "-"
                            + tempDate.split("-")[1] + "-"
                            + tempDate.split("-")[0]);
                    editConnectDate.setText(tempDate.split("-")[2] + "-"
                            + tempDate.split("-")[1] + "-"
                            + tempDate.split("-")[0]);
                }
                if (null != request.time) {
                    tempTime = request.time;
                    editTime.setText(request.time);
                    editConnectTime.setText(request.time);
                }
                if (null != request.contactName) {
                    contact.setText(request.contactName);
                    connectPerson.setText(request.contactName);
                }
                if (null != request.connectEmail) {
                    connectEmail.setText(request.connectEmail);
                } else {
                    connectEmail.setText("n/a...");
                }

                phone.setText(String.valueOf(request.contactPhoneNumber));
                ccp.setCountryForPhoneCode(request.contactCountryCode);
                connectPhone.setText(String.valueOf(request.contactPhoneNumber));
                ccpConnect.setCountryForPhoneCode(request.contactCountryCode);
                // ccp
                if (request.contactCountryCode == 44) {
                    ccp.setCountryForNameCode("UK");
                    ccpConnect.setCountryForNameCode("UK");
                }

                if (null != request.notes) extra.setText(request.notes);
                if (null != request.address) address.setText(request.address);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            toggleSeekBars(request.budgetType);
            overtimeSwitch.setChecked(request.overtime);
            overtime.setText(String.valueOf(request.overtimeValue));

            switch (request.budgetType) {
                case BUDGET_TYPE_DAY:
                    setDaily();
                    rbDay.setChecked(true);
                    daySeek.setRate((int) request.budget);
                    break;
                case BUDGET_TYPE_HOUR:
                    setHourly();
                    rbHour.setChecked(true);
                    hourSeek.setRate((int) request.budget);
                    break;
                case BUDGET_TYPE_YEAR:
                    setYearly();
                    rbYear.setChecked(true);
                    yearSeek.setRate((int) request.budget);
                    break;
                case BUDGET_TYPE_POA:
                    setPoa();
                    rbPOA.setChecked(true);
                    break;
            }
        }
    }

    @OnClick(R.id.post)
    public void post() {
        callApi(Constants.JOB_STATUS_LIVE);
    }

    @OnClick(R.id.description)
    public void describe() {
        jobDetailsDialog = JobDetailsDialog
                .newInstance(this, description.getText().toString());
        jobDetailsDialog.setCancelable(false);
        jobDetailsDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    @OnClick(R.id.job_name)
    public void name() {
        jobNameDialog = JobNameDialog
                .newInstance(this, jobName.getText().toString());
        jobNameDialog.setCancelable(false);
        jobNameDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    public void onDone(String string, boolean cancel) {
        if (null != jobDetailsDialog) {
            jobDetailsDialog.dismiss();
        }
        if (!cancel) {
            if (null != string) {
                tempDescription = string;
                description.setText(string);
            }
        }
    }

    public void onName(String string, boolean cancel) {
        if (null != jobNameDialog) {
            jobNameDialog.dismiss();
        }
        if (!cancel) {
            if (null != string) {
                tempName = string;
                jobName.setText(string);
            }
        }
    }


    private DialogInterface.OnClickListener gotoPaymentsListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            };

    public void onRedirect() {
        saveDraftThenSelectPlan();
    }

    private void saveDraftThenSelectPlan() {

        if (validate()) {

            loadRequest();

            HashMap<String, Object> payload = new HashMap<>();
            if (null != request.name) {
                payload.put("name", request.name);
            }
            payload.put("is_connect", request.isConnect);
            payload.put("id", request.id);
            payload.put("status", Constants.JOB_STATUS_DRAFT);
            payload.put("role", selectedRole.id);
            payload.put("workers_quantity", selectedRole.amountWorkers);
            payload.put("trades", request.trades);
            payload.put("experience", request.experience);
            payload.put("english_level", request.english);

            // beginning of wow
            try {
                //
                int[] quals = new int[request.requirements.length +
                        request.qualifications.length];
                List<Integer> reqs = new ArrayList<>();
                for (int i = 0; i < request.requirements.length; i++) {
                    reqs.add(request.requirements[i]);
                }
                List<Integer> qual = new ArrayList<>();
                for (int i = 0; i < request.qualifications.length; i++) {
                    qual.add(request.qualifications[i]);
                }
                List<Integer> combined = new ArrayList<>();
                combined.addAll(reqs); combined.addAll(qual);
                for (int i = 0; i < combined.size(); i++) {
                    quals[i] = combined.get(i);
                }
//                payload.put("update_filtered", "qualifications");
//                payload.put("update_filtered", "requirements");
                payload.put("qualifications", quals);
                //
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            // end of wow

            payload.put("skills", request.skills);
            payload.put("experience_type", request.experienceTypes);
            payload.put("description", request.description);
            payload.put("budget_type", budgetType);
            payload.put("budget", request.budget);
            payload.put("pay_overtime", request.overtime);
            payload.put("pay_overtime_value", request.overtimeValue);
            payload.put("contact_name", request.contactName);

            payload.put("contact_phone", request.contactPhone);

            payload.put("address", request.address);
            if (null != request.date && null != request.time) {
                payload.put("start_datetime", request.date + "T" + request.time + "+00:00");
                TextTools.log(TAG, String.valueOf(payload.get("start_datetime")));
            }
            if (null != request.rawDate) {
                payload.put("start_datetime", DateUtils.toPayloadDate(request.rawDate));
            }

            payload.put("extra_notes", request.notes);
            payload.put("location", request.location);
            payload.put("location_name", request.locationName);
            if (null != request.requirements) {
                for (int exp : request.requirements) {
                    if (exp == 1) {
                        // this means we required a CSCS card
                        payload.put("cscs_required", true);
                    }
                }
            }

            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .createJob(payload)
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
                                    unfinished = false;
                                    getActivity().finish();
                                    getActivity().startActivity(new Intent(getActivity(), MainEmployerActivity.class));
                                    //////////////////////////////
                                } else {
                                    HandleErrors.parseError(getContext(),
                                            dialog, response,
                                            SelectDetailsFragment.this,
                                            gotoPaymentsListener, showCRNDialog);
                                }
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
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
        }
    }


    private void callApi(int status) {

        if (validate()) {

            loadRequest();

            HashMap<String, Object> payload = new HashMap<>();
            if (null != request.name) {
                payload.put("name", request.name);
            }
            payload.put("is_connect", request.isConnect);
            payload.put("id", request.id);
            payload.put("status", status);
            payload.put("role", selectedRole.id);
            payload.put("workers_quantity", selectedRole.amountWorkers);
            payload.put("trades", request.trades);
            payload.put("experience", request.experience);
            payload.put("english_level", request.english);

            // beginning of wow
            try {
                //
                int[] quals = new int[request.requirements.length +
                        request.qualifications.length];
                List<Integer> reqs = new ArrayList<>();
                for (int i = 0; i < request.requirements.length; i++) {
                    reqs.add(request.requirements[i]);
                }
                List<Integer> qual = new ArrayList<>();
                for (int i = 0; i < request.qualifications.length; i++) {
                    qual.add(request.qualifications[i]);
                }
                List<Integer> combined = new ArrayList<>();
                combined.addAll(reqs); combined.addAll(qual);
                for (int i = 0; i < combined.size(); i++) {
                    quals[i] = combined.get(i);
                }
//                payload.put("update_filtered", "qualifications");
//                payload.put("update_filtered", "requirements");
                payload.put("qualifications", quals);
                //
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
            // end of wow

            payload.put("skills", request.skills);
            payload.put("experience_type", request.experienceTypes);
            payload.put("description", request.description);
            payload.put("budget_type", budgetType);
            payload.put("budget", request.budget);
            payload.put("pay_overtime", request.overtime);
            payload.put("pay_overtime_value", request.overtimeValue);
            payload.put("contact_name", request.contactName);

            payload.put("contact_phone", request.contactPhone);

            payload.put("address", request.address);
            if (null != request.date && null != request.time) {
                payload.put("start_datetime", request.date + "T" + request.time + "+00:00");
                TextTools.log(TAG, String.valueOf(payload.get("start_datetime")));
            }
            if (null != request.rawDate) {
                payload.put("start_datetime", DateUtils.toPayloadDate(request.rawDate));
            }

            payload.put("extra_notes", request.notes);
            payload.put("location", request.location);
            payload.put("location_name", request.locationName);
            if (null != request.requirements) {
                for (int exp : request.requirements) {
                    if (exp == 1) {
                        // this means we required a CSCS card
                        payload.put("cscs_required", true);
                    }
                }
            }

            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            HttpRestServiceConsumer.getBaseApiClient()
                    .createJob(payload)
                    .enqueue(new Callback<ResponseObject<Job>>() {
                        @Override
                        public void onResponse(Call<ResponseObject<Job>> call,
                                               Response<ResponseObject<Job>> response) {
                            try {
                                DialogBuilder.cancelDialog(dialog);

                                if (response.isSuccessful()) {
                                    unfinished = false;

                                    if (getActivity() instanceof PreviewJobActivity) {
//                                        FragmentManager fm = getActivity().getSupportFragmentManager();
//                                        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
//                                            fm.popBackStack();
//                                        }
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame,
                                                        JobDetailsFragment.newInstance(response.body().getResponse().id))
                                                .commit();
                                    } else if (getActivity() instanceof CreateJobActivity) {
//                                        FragmentManager fm = getActivity().getSupportFragmentManager();
//                                        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
//                                            fm.popBackStack();
//                                        }
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.create_job_content,
                                                        JobDetailsFragment.newInstance(response.body().getResponse().id))
                                                .commit();
                                    }

                                } else {
                                    HandleErrors.parseError(getContext(),
                                            dialog, response, SelectDetailsFragment.this,
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
        }
    }

    private final DialogInterface.OnClickListener showCRNDialog =
            new DialogInterface.OnClickListener() {
        @Override
        public void onClick(final DialogInterface dialogInterface, int id) {
            //
            if (null != dialogInterface) {

                Log.d(TAG, String.valueOf(dialogInterface.hashCode()));
                //
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

    @OnClick(R.id.preview)
    public void preview() {
        if (validate()) {
            try {

                loadRequest();

                Intent intent = new Intent(getActivity(), PreviewJobActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);
                getActivity().finish();

            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    private void loadRequest() {

        request.isConnect = isConnect;
        request.name = (!tempName.equals("") ?
                tempName : jobName.getText().toString());

        if (request.overtime) {
            request.overtimeValue = Integer.valueOf(overtime.getText().toString());
        }

        request.budgetType = budgetType;
        switch (budgetType) {
            case BUDGET_TYPE_HOUR:
                request.budget = (int) hourSeek.getRate();
                break;
            case BUDGET_TYPE_DAY:
                request.budget = (int) daySeek.getRate();
                break;
            case BUDGET_TYPE_YEAR:
                request.budget = (int) yearSeek.getRate();
                break;
        }

        request.description = (!tempDescription.equals("")) ?
                tempDescription : description.getText().toString();
        //
        //
        request.address = address.getText().toString();
        request.notes = extra.getText().toString();

        if (isConnect) {
            request.contactName = connectPerson.getText().toString();
            request.contactPhone = ccpConnect.getSelectedCountryCodeWithPlus() + " "
                    + connectPhone.getText().toString();
            request.contactCountryCode = ccpConnect.getSelectedCountryCodeAsInt();
            try {
                request.contactPhoneNumber = Long.valueOf(connectPhone.getText().toString());
            } catch (NumberFormatException e) {
                CrashLogHelper.logException(e);
            }
            request.connectEmail = connectEmail.getText().toString();
            request.date = tempDate;
            request.time = editConnectTime.getText().toString();
        } else {
            request.contactName = contact.getText().toString();
            request.contactPhone = ccp.getSelectedCountryCodeWithPlus() + " "
                    + phone.getText().toString();
            request.contactCountryCode = ccp.getSelectedCountryCodeAsInt();
            try {
                request.contactPhoneNumber = Long.valueOf(phone.getText().toString());
            } catch (NumberFormatException e) {
                CrashLogHelper.logException(e);
            }
            request.date = tempDate;
            request.time = editTime.getText().toString();
        }

        request.roleName = (request.roleObject).name;
    }

    private boolean validate() {
        boolean result = true;

        if (request.overtime) {
            if (overtime.getText().toString().isEmpty()) {
                overtime.setError("Please enter a number");
                result = false;
            }
        }

        if (isConnect) {
            if (connectPhone.getText().toString().isEmpty()) {
                connectPhone.setError("Please enter a Phone no.");
                result = false;
            }

            if (connectPerson.getText().toString().isEmpty()) {
                connectPerson.setError("Please enter a name");
                result = false;
            }

            if (connectEmail.getText().toString().isEmpty()) {
                connectEmail.setError("Please enter an address");
                result = false;
            }
        } else {
            if (phone.getText().toString().isEmpty()) {
                phone.setError("Please enter a Phone no.");
                result = false;
            }

            if (contact.getText().toString().isEmpty()) {
                contact.setError("Please enter a name");
                result = false;
            }

            if (address.getText().toString().isEmpty()) {
                address.setError("Please enter an address");
                result = false;
            }
        }

        return result;
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

    private void setHourly() {
        budgetType = BUDGET_TYPE_HOUR;
        min.setText(getString(R.string.min_hourly));
        max.setText(getString(R.string.max_hourly));
        toggleSeekBars(BUDGET_TYPE_HOUR);
    }
    private void setDaily() {
        budgetType = BUDGET_TYPE_DAY;
        min.setText(getString(R.string.min_daily));
        max.setText(getString(R.string.max_daily));
        toggleSeekBars(BUDGET_TYPE_DAY);
    }
    private void setYearly() {
        budgetType = BUDGET_TYPE_YEAR;
        min.setText(getString(R.string.min_yearly));
        max.setText(getString(R.string.max_yearly));
        toggleSeekBars(BUDGET_TYPE_YEAR);
    }
    @OnClick({R.id.rb_hour,
            R.id.rb_day,
            R.id.rb_year,
            R.id.rb_poa})
    public void onBudgetChanged(RadioButton radioButton) {
        switch(radioButton.getId()) {
            case R.id.rb_hour:
                setHourly();
                break;
            case R.id.rb_day:
                setDaily();
                break;
            case R.id.rb_year:
                setYearly();
                break;
            case R.id.rb_poa:
                setPoa();
                break;
        }
    }
    private void setPoa() {
        budgetType = BUDGET_TYPE_POA;
        toggleSeekBars(BUDGET_TYPE_POA);
    }

    private void toggleSeekBars(int i) {
        switch (i) {
            case BUDGET_TYPE_HOUR:
                daySeek.setVisibility(View.GONE);
                hourSeek.setVisibility(View.VISIBLE);
                yearSeek.setVisibility(View.GONE);
                break;
            case BUDGET_TYPE_DAY:
                hourSeek.setVisibility(View.GONE);
                daySeek.setVisibility(View.VISIBLE);
                yearSeek.setVisibility(View.GONE);
                break;
            case BUDGET_TYPE_YEAR:
                daySeek.setVisibility(View.GONE);
                hourSeek.setVisibility(View.GONE);
                yearSeek.setVisibility(View.VISIBLE);
                break;
            case BUDGET_TYPE_POA:
                daySeek.setVisibility(View.GONE);
                hourSeek.setVisibility(View.GONE);
                yearSeek.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }

    private void persistProgress() {
        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_DETAILS)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createJobCancel:

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
                                unfinished = false;
                                getActivity().finish();
                                getActivity().startActivity(new Intent(getActivity(), MainEmployerActivity.class));
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

//                AlertDialog dialog = new AlertDialog.Builder(getContext())
//                        .setMessage("Are you sure you want to exit?")
//                        .setNegativeButton("Save as draft", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                callApi(Constants.JOB_STATUS_DRAFT);
//                            }
//                        })
//                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                unfinished = false;
//                                getActivity().finish();
//                                getActivity().startActivity(new Intent(getActivity(), MainEmployerActivity.class));
//                            }
//                        })
//                        .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        })
//                        .setCancelable(false).create();
//                        dialog.show();
//
//                TextView message = (TextView) dialog.findViewById(android.R.id.message);
//                message.setTextColor(ContextCompat.getColor(getContext(), R.color.graySquareColor));
//                message.setTypeface(Typeface.createFromAsset(getActivity()
//                        .getAssets(), "fonts/JosefinSans-Italic.ttf"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.date, R.id.connect_date,
            R.id.time, R.id.connect_time})
    public void time(View view) {
        switch (view.getId()) {
            case R.id.date:
                selectDate();
                break;
            case R.id.time:
                selectTime();
                break;
            case R.id.connect_date:
                selectDate();
                break;
            case R.id.connect_time:
                selectTime();
                break;
        }
    }

    // the date picker listener
    DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            editDate.setText(String.valueOf(dayOfMonth) + "-" +
                    String.valueOf(monthOfYear + 1) + "-" +
                    String.valueOf(year));
            editConnectDate.setText(String.valueOf(dayOfMonth) + "-" +
                    String.valueOf(monthOfYear + 1) + "-" +
                    String.valueOf(year));
            tempDate = String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth);
            if (null != request.rawDate) {
                request.rawDate = null;
            }
            selectTime();
        }
    };

    @OnEditorAction(R.id.address)
    public boolean onEditorAction(EditText editText, int actionId, KeyEvent event) {
        //
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            InputMethodManager imm = (InputMethodManager)
                    editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            selectDate();
            return true;
        }
        return false;
    }

    private void selectTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 6);
        cal.set(Calendar.MINUTE, 0);
        Date random6AMdate = cal.getTime();

        com.jzxiang.pickerview.TimePickerDialog timePickerDialog
                = new com.jzxiang.pickerview.TimePickerDialog
                .Builder()
                .setCancelStringId("Cancel")
                .setSureStringId("Done")
                .setTitleStringId("Pick a Time")
                .setHourText("")
                .setMinuteText("")
                .setCallBack(new OnDateSetListener() {
                    @Override
                    public void onDateSet(com.jzxiang.pickerview.TimePickerDialog timePickerView,
                                          long millseconds) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(millseconds);
                        tempTime = String
                                .valueOf(calendar.get(Calendar.HOUR)) + ":" +
                                String.valueOf(calendar.get(Calendar.MINUTE));
                        editConnectTime.setText(tempTime);
                        editTime.setText(tempTime);
                        if (null != request.rawDate) {
                            request.rawDate = null;
                        }
                    }
                })
                .setCurrentMillseconds(random6AMdate.getTime())
                .setThemeColor(ContextCompat.getColor(getContext(), R.color.redSquareColor))
                .setType(Type.HOURS_MINS)
                .build();
        timePickerDialog.setCancelable(false);
        timePickerDialog.show(getActivity().getSupportFragmentManager(), "");
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                R.style.DialogTheme,
                dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog
                .getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
}