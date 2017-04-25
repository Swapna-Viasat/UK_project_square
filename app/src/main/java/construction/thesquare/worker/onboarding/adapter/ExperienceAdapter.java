package construction.thesquare.worker.onboarding.adapter;

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
import construction.thesquare.shared.models.ExperienceQualification;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/7/2016.
 */

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceHolder> {

    static final String TAG = "ExperienceAdapter";

    private List<ExperienceQualification> data = new ArrayList<>();

    public ExperienceAdapter(List<ExperienceQualification> experienceQualifications) {
        this.data = experienceQualifications;
    }

    @Override
    public ExperienceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExperienceHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_experience, parent, false));
    }

    @Override
    public void onBindViewHolder(ExperienceHolder holder, int position) {
        final ExperienceQualification experience = data.get(position);
        holder.checkBox.setChecked(experience.selected);
        holder.title.setText(experience.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onExperience(experience);
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
        void onExperience(ExperienceQualification experience);
    }
}
