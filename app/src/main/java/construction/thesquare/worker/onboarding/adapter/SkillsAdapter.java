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
import construction.thesquare.shared.models.Skill;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/8/2016.
 */

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillHolder> {

    public static final String TAG = "SkillsAdapter";

    private List<Skill> data = new ArrayList<>();

    public SkillsAdapter(List<Skill> skills) {
        this.data = skills;
    }

    @Override
    public SkillHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SkillHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_skill, parent, false));
    }

    @Override
    public void onBindViewHolder(SkillHolder holder, int position) {
        final Skill skill = data.get(position);
        holder.title.setText(skill.name);
        holder.checkBox.setChecked(skill.selected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onSkill(skill);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SkillHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.check) CheckBox checkBox;

        public SkillHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private SkillListener listener;

    public void setListener(SkillListener skillListener) {
        this.listener = skillListener;
    }

    public interface SkillListener {
        void onSkill(Skill skill);
    }
}
