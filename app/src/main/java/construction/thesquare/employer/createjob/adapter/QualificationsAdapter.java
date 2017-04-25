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
 * Created by gherg on 12/8/2016.
 */

public class QualificationsAdapter extends RecyclerView.Adapter<QualificationsAdapter.QualificationHolder> {

    public static final String TAG = "QualificationsAdapter";

    private List<Qualification> data = new ArrayList<>();

    public QualificationsAdapter(List<Qualification> qualifications) {
        this.data = qualifications;
    }

    @Override
    public QualificationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QualificationHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_qualification, parent, false));
    }

    @Override
    public void onBindViewHolder(QualificationHolder holder, int position) {
        final Qualification qualification = data.get(position);
        holder.title.setText(qualification.name);
        holder.checkBox.setChecked(qualification.selected);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onQualification(qualification);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class QualificationHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.check) CheckBox checkBox;

        public QualificationHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private QualificationListener listener;

    public void setListener(QualificationListener qualificationListener) {
        this.listener = qualificationListener;
    }

    public interface QualificationListener {
        void onQualification(Qualification qualification);
    }
}
