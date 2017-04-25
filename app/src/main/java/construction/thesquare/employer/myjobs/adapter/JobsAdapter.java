package construction.thesquare.employer.myjobs.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/29/2016.
 */

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobHolder> {

    public static final String TAG = "JobsAdapter";

    public static final int ACTION_REPUBLISH = 121;
    public static final int ACTION_DELETE = 122;

    private List<Job> data = new ArrayList<>();

    public interface JobsListener {
        void onJob(Job job);

        void onAction(Job job, int actionId);

        void onViewDraft(Job job);
    }

    private JobsListener listener;

    public JobsAdapter(List<Job> jobs, JobsListener jobsListener) {
        this.data = jobs;
        this.listener = jobsListener;
    }

    @Override
    public JobHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JobHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_job_emp, parent, false));
    }

    @Override
    public void onBindViewHolder(JobHolder holder, int position) {
        final Job job = data.get(position);

        if (null != job.role) {
            holder.occupation.setText(job.role.name);
        }

        if (null != job.owner) {
            if (null != job.owner.picture) {
                holder.companyName.setVisibility(View.GONE);
            } else {
                holder.companyName.setVisibility(View.VISIBLE);
            }
        }

        holder.experience.setText(String.format(holder.itemView.getResources()
                        .getString(R.string.employer_jobs_experience), job.experience,
                holder.itemView.getResources()
                        .getQuantityString(R.plurals.year_plural, job.experience)));

        if (null != job.start) {
            try {

                if (job.isConnect) {
                    holder.starts.setText(String.format(holder.itemView.getResources()
                            .getString(R.string.employer_jobs_app_deadline),
                                    DateUtils.getFormattedJobDate(job.start)));
                } else {
                    holder.starts.setText(String.format(holder.itemView.getResources()
                            .getString(R.string.employer_jobs_starts), DateUtils.getFormattedJobDate(job.start)));
                }

            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }

        if (null != job.locationName) {
            holder.location.setText(job.locationName);
        }

        if (null != job.budgetType) {
            holder.salaryNumber.setVisibility(View.VISIBLE);
            holder.salaryPeriod.setText("PER " + job.budgetType.name);

            if (job.budgetType.id == 4) {
                holder.salaryPeriod.setText("£POA");
                holder.salaryNumber.setVisibility(View.GONE);
            }
        }

        String temp = String.valueOf(NumberFormat
                .getInstance(Locale.UK).format(Double.valueOf(job.budget)));
        holder.salaryNumber.setText("£ " + temp);

        // statuses
        if (job.status.id == Job.TAB_LIVE) {
            bindLive(holder, job);
        } else if (job.status.id == Job.TAB_OLD || job.status.id == 4) {
            bindOld(holder, job);
        } else {
            bindDraft(holder, job);
        }

        if (null != job.company) {
            if (null != job.company.name) {
                holder.companyName.setText(job.company.name);
            }
            if (null != job.company.logo) {
                holder.logo.setVisibility(View.VISIBLE);
                Picasso.with(holder.itemView.getContext())
                        .load(job.company.logo)
                        .into(holder.logo);
            } else {
                holder.logo.setVisibility(View.GONE);
            }
        }


        holder.jobId.setText("Job ref ID: " + job.jobRef);
        if (null != job.name) {
            holder.jobName.setText(job.name);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class JobHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_match_start_location)
        FrameLayout startLocationFrame;
        @BindView(R.id.banner_cancelled)
        ImageView cancelled;
        @BindView(R.id.view_more)
        JosefinSansTextView editDraft;

        @BindView(R.id.item_job_location)
        JosefinSansTextView location;
        @BindView(R.id.item_job_occupation)
        JosefinSansTextView occupation;
        @BindView(R.id.item_job_experience)
        JosefinSansTextView experience;
        @BindView(R.id.item_job_start_date)
        JosefinSansTextView starts;
        @BindView(R.id.item_job_salary_period)
        JosefinSansTextView salaryPeriod;
        @BindView(R.id.item_job_salary_number)
        JosefinSansTextView salaryNumber;
        @BindView(R.id.item_job_id)
        JosefinSansTextView jobId;
        @BindView(R.id.item_job_name)
        JosefinSansTextView jobName;
        @BindView(R.id.item_job_company_name)
        JosefinSansTextView companyName;
        @BindView(R.id.item_job_logo)
        ImageView logo;

        @BindView(R.id.delete_draft)
        View remove;
        @BindView(R.id.awarded)
        JosefinSansTextView awarded;

        @BindView(R.id.item_job_action_block)
        FrameLayout actions;
        @BindView(R.id.item_job_status)
        JosefinSansTextView action;

        public JobHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void bindLive(JobHolder holder, final Job job) {
        holder.actions.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onJob(job);
                }
            }
        });
    }

    public void bindOld(JobHolder holder, final Job job) {

        if (job.status.id == 4) {
            holder.cancelled.setVisibility(View.VISIBLE);
        } else {
            holder.cancelled.setVisibility(View.GONE);
        }

        holder.actions.setVisibility(View.VISIBLE);
        holder.action.setText(holder.itemView
                .getContext().getString(R.string.employer_jobs_republish));
        // TODO: enable this awarded to label when we have the info from backend
        // holder.awarded.setVisibility(View.VISIBLE);
        holder.awarded.setVisibility(View.GONE);
        holder.awarded.setText(String
                .format(holder.itemView.getContext()
                        .getString(R.string.employer_jobs_awarded), String.valueOf(job.worker)));
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onAction(job, ACTION_REPUBLISH);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onJob(job);
                }
            }
        });
    }

    public void bindDraft(JobHolder holder, final Job job) {
        holder.actions.setVisibility(View.GONE);
        holder.remove.setVisibility(View.VISIBLE);
        holder.editDraft.setVisibility(View.VISIBLE);
        holder.editDraft.setText("Edit");
        holder.editDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onViewDraft(job);
                }
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onAction(job, ACTION_DELETE);
                }
            }
        });
    }
}