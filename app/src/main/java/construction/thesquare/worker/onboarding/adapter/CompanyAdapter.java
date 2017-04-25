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
import construction.thesquare.shared.models.Company;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/21/2016.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyHolder> {

    static final String TAG = "CompanyAdapter";

    private List<Company> data = new ArrayList<>();

    public CompanyAdapter(List<Company> companies) {
        this.data = companies;
    }

    @Override
    public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CompanyHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_company, parent, false));
    }

    @Override
    public void onBindViewHolder(CompanyHolder holder, int position) {
        final Company company = data.get(position);
        holder.checkBox.setChecked(company.selected);
        holder.title.setText(company.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onCompany(company);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CompanyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) JosefinSansTextView title;
        @BindView(R.id.check) CheckBox checkBox;

        public CompanyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private CompanyListener listener;

    public void setListener(CompanyListener companyListener) {
        this.listener = companyListener;
    }

    public interface CompanyListener {
        void onCompany(Company company);
    }
}
