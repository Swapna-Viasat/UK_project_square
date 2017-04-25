package construction.thesquare.employer.myjobs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import construction.thesquare.R;
import construction.thesquare.shared.models.Job;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 1/3/2017.
 */

public class JobsSpinnerAdapter extends ArrayAdapter<Job> {

    public static final String TAG = "JobsSpinnerAdapter";

    private List<Job> data = new ArrayList<>();

    public JobsSpinnerAdapter(List<Job> jobs, Context context) {
        super(context, R.layout.spinner_item, jobs);
        this.data = jobs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            TextTools.log(TAG, "convert view null");
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spinner_item, parent, false);
        } else {
            TextTools.log(TAG, "convert view not null");
        }

        ((JosefinSansTextView) view.findViewById(R.id.name))
                .setText(data.get(position).role.name + " - " +
                        String.format(getContext().getString(R.string.employer_jobs_experience),
                                data.get(position).experience,
                                getContext().getResources()
                                        .getQuantityString(R.plurals.year_plural,
                                                data.get(position).experience)));

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
