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
import construction.thesquare.shared.models.ExperienceType;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/8/2016.
 */

public class ExperienceTypeAdapter
        extends RecyclerView.Adapter<ExperienceTypeAdapter.ExperienceTypeHolder> {

    public static final String TAG = "ExperienceTypeAdapter";

    private List<ExperienceType> data = new ArrayList<>();

    public ExperienceTypeAdapter(List<ExperienceType> experienceTypes) {
        this.data = experienceTypes;
    }

    @Override
    public ExperienceTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExperienceTypeHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_experience_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ExperienceTypeHolder holder, int position) {
        final ExperienceType experienceType = data.get(position);
        holder.title.setText(experienceType.name);
        holder.checkBox.setChecked(experienceType.selected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onExperienceType(experienceType);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ExperienceTypeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.check) CheckBox checkBox;

        public ExperienceTypeHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private ExperienceTypeListener listener;

    public void setListener(ExperienceTypeListener experienceTypeListener) {
        this.listener = experienceTypeListener;
    }

    public interface ExperienceTypeListener {
        void onExperienceType(ExperienceType experienceType);
    }
}
