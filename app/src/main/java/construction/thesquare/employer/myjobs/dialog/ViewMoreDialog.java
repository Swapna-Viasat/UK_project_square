package construction.thesquare.employer.myjobs.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.utils.JobTools;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 2/19/17.
 */

public class ViewMoreDialog extends DialogFragment {

    public static final String TAG = "ViewMoreDialog";

    @BindView(R.id.job_details_description) JosefinSansTextView details;
    @BindView(R.id.job_details_qualifications2) JosefinSansTextView qualifications2;
    @BindView(R.id.job_details_qualifications) JosefinSansTextView qualifications;
    @BindView(R.id.job_details_english_level) JosefinSansTextView englishLevel;
    @BindView(R.id.job_details_skills) JosefinSansTextView skills;
    @BindView(R.id.job_details_experience_types) JosefinSansTextView experienceTypes;
    @BindView(R.id.job_details_overtime) JosefinSansTextView overtime;

    public interface ViewMoreListener {
        void onAction(int action);
    }

    private ViewMoreListener listener;
    private Job job;

    public static ViewMoreDialog newInstance(ViewMoreListener viewMoreListener,
                                            Job job) {
        ViewMoreDialog viewMoreDialog = new ViewMoreDialog();
        viewMoreDialog.setCancelable(false);
        viewMoreDialog.job = job;
        viewMoreDialog.listener = viewMoreListener;
        return viewMoreDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_job_view_more, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (null != job) {
            if (null != job.description) {
                details.setText(job.description);
            }
            if (null != job.qualifications) {
                if (!job.qualifications.isEmpty()) {
                    qualifications2.setText(TextTools
                            .toBulletList(JobTools.extractQualifications(job), true));
                }
            }
            String eng = "Basic";
            switch (job.english) {
                case 2:
                    eng = "Fluent";
                    break;
                case 3:
                    eng = "Native";
                    break;
            }
            englishLevel.setText(eng);
            if (null != job.skills) {
                if (!job.skills.isEmpty()) {
                    skills.setText(TextTools
                            .toBulletList(JobTools.extractSkills(job), true));
                }
            }
            if (null != job.experienceTypes) {
                if (!job.experienceTypes.isEmpty()) {
                    experienceTypes.setText(TextTools
                            .toBulletList(JobTools.extractExperienceTypes(job), true));
                }
            }
            overtime.setText(String.format(getString(R.string.job_details_overtime_text),
                    job.overtimeRate));
        }
    }

    @OnClick(R.id.close)
    public void close() {
        this.dismiss();
    }

    @OnClick({R.id.view_more_description_label,
            R.id.view_more_english_level_label,
            R.id.view_more_overtime_label,
            R.id.view_more_reqs_label,
            R.id.view_more_qualifications_label,
            R.id.view_more_experience_types_label,
            R.id.view_more_skills_label
    })
    public void onEdit(View view) {
        if (null != listener) {
            switch (view.getId()) {
                case R.id.view_more_description_label:
                    listener.onAction(EDIT_DESCRIPTION);
                    break;
                case R.id.view_more_english_level_label:
                    listener.onAction(EDIT_ENGLISH_LEVEL);
                    break;
                case R.id.view_more_overtime_label:
                    listener.onAction(EDIT_OVERTIME);
                    break;
                case R.id.view_more_reqs_label:
                    listener.onAction(EDIT_REQUIREMENTS);
                    break;
                case R.id.view_more_qualifications_label:
                    listener.onAction(EDIT_QUALIFICATIONS);
                    break;
                case R.id.view_more_experience_types_label:
                    listener.onAction(EDIT_EXPERIENCE_TYPES);
                    break;
                case R.id.view_more_skills_label:
                    listener.onAction(EDIT_SKILLS);
                    break;
            }
        }
    }

    public static final int EDIT_DESCRIPTION = 23;
    public static final int EDIT_QUALIFICATIONS = 24;
    public static final int EDIT_REQUIREMENTS = 25;
    public static final int EDIT_ENGLISH_LEVEL = 26;
    public static final int EDIT_SKILLS = 27;
    public static final int EDIT_EXPERIENCE_TYPES = 28;
    public static final int EDIT_OVERTIME = 29;
}
