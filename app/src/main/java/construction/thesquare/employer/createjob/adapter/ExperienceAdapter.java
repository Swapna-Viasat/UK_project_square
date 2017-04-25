package construction.thesquare.employer.createjob.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Qualification;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/7/2016.
 */

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceHolder> {

    public static final String TAG = "ExperienceAdapter";

    private List<Qualification> data = new ArrayList<>();

    public ExperienceAdapter(List<Qualification> requirements) {
        this.data = requirements;
    }

    @Override
    public ExperienceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExperienceHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_experience, parent, false));
    }

    @Override
    public void onBindViewHolder(ExperienceHolder holder, int position) {
        final Qualification requirement = data.get(position);
        holder.checkBox.setChecked(requirement.selected);
        holder.title.setText(requirement.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onRequirement(requirement);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ExperienceHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.check) CheckBox checkBox;

        public ExperienceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private ExperienceListener listener;

    public void setListener(ExperienceListener experienceListener) {
        this.listener = experienceListener;
    }

    public interface ExperienceListener {
        void onRequirement(Qualification experience);
    }
}
