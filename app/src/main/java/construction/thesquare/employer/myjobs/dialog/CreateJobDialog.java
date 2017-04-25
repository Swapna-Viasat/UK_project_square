package construction.thesquare.employer.myjobs.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.myjobs.adapter.JobsSpinnerAdapter;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.CrashLogHelper;

/**
 * Created by gherg on 1/10/2017.
 */

public class CreateJobDialog extends DialogFragment {

    public static final String TAG = "CreateJobDialog";
    @BindView(R.id.new_check) CheckBox selectNew;
    @BindView(R.id.duplicate_check) CheckBox selectDuplicate;
    @BindView(R.id.new_text) TextView newText;
    @BindView(R.id.duplicate_text) TextView duplicateText;
    @BindView(R.id.spinner) Spinner spinner;
    private CreateJobDialogListener listener;
    private List<Job> data = new ArrayList<>();

    public static CreateJobDialog newInstance(CreateJobDialogListener listener,
                                              List<Job> jobs) {
        CreateJobDialog dialog = new CreateJobDialog();
        dialog.listener = listener;
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_DATA, (Serializable) jobs);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //
        View view = inflater.inflate(R.layout.dialog_create_job, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        // have to make this call otherwise shows blank white space in the title area
        if (getDialog() != null && getDialog().getWindow() != null)
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //
        selectNew.setOnCheckedChangeListener(new
                CheckListener(selectDuplicate, newText, duplicateText, spinner));
        selectDuplicate.setOnCheckedChangeListener(new
                CheckListener(selectNew, newText, duplicateText, spinner));

        try {
            data.clear();
            if (getArguments().getSerializable(Constants.KEY_DATA) != null)
                data.addAll((List<Job>) getArguments().getSerializable(Constants.KEY_DATA));
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        if (data.isEmpty()) {
            selectDuplicate.setVisibility(View.GONE);
            duplicateText.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        } else {
            selectDuplicate.setVisibility(View.VISIBLE);
            duplicateText.setVisibility(View.VISIBLE);
            ArrayAdapter<Job> adapter = new JobsSpinnerAdapter(data, getContext());
            spinner.setAdapter(adapter);
        }
    }

    public interface CreateJobDialogListener {
        void onCancel();
        void onCreateNew(Job job);
        void onDuplicate(Job job);
    }

    @OnClick({R.id.cancel, R.id.ok})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.ok:
                if (null != listener) {
                    if (selectDuplicate.isChecked()) {
                        listener.onDuplicate((Job) spinner.getSelectedItem());
                    } else {
                        listener.onCreateNew((Job) spinner.getSelectedItem());
                    }
                }
                break;
            case R.id.cancel:
                if (null != listener) {
                    listener.onCancel();
                }
                break;
        }
    }

    private class CheckListener implements CompoundButton.OnCheckedChangeListener {

        private CompoundButton otherCheckBox;
        private TextView textViewNew;
        private TextView textViewDuplicate;
        private Spinner jobsSpinner;

        public CheckListener(CompoundButton other,
                             TextView textView1,
                             TextView textView2,
                             Spinner spinner) {
            this.otherCheckBox = other;
            this.jobsSpinner = spinner;
            this.textViewDuplicate = textView2;
            this.textViewNew = textView1;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            compoundButton.setChecked(b);
            otherCheckBox.setChecked(!b);


            if (compoundButton.getId() == R.id.duplicate_check) {
                textViewDuplicate.setTextColor(ContextCompat.getColor(getActivity(), R.color.redSquareColor));
                textViewNew.setTextColor(ContextCompat.getColor(getActivity(), R.color.graySquareColor));
                jobsSpinner.setVisibility(View.VISIBLE);
            } else {
                textViewDuplicate.setTextColor(ContextCompat.getColor(getActivity(), R.color.graySquareColor));
                textViewNew.setTextColor(ContextCompat.getColor(getActivity(), R.color.redSquareColor));
                jobsSpinner.setVisibility(View.GONE);
            }
        }
    }
}