package construction.thesquare.worker.onboarding.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.models.Role;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.view.widget.JosefinSansTextView;

/**
 * Created by gherg on 12/6/2016.
 */

public class RolesAdapter extends RecyclerView.Adapter<RolesAdapter.RoleHolder> {

    public static final String TAG = "RolesAdapter";

    private List<Role> data = new ArrayList<>();

    public RolesAdapter(List<Role> roles) {
        this.data = roles;
    }

    @Override
    public RoleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RoleHolder holder = new RoleHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_role_worker, parent, false));
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RoleHolder holder, final int position) {
        final Role role = data.get(position);
        holder.title.setText(role.name);
        if (role.selected) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(holder.itemView.getRootView().getContext(), R.color.redSquareColor));
            holder.blur.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.blur.setVisibility(View.GONE);
        }

        try {
            Picasso.with(holder.itemView.getContext()).cancelRequest(holder.image);

            Picasso.with(holder.itemView.getContext())
                    .load(role.image)
                    .fit()
                    .centerCrop()
                    .into(holder.image);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onRoleTapped(role, holder.itemView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class RoleHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        JosefinSansTextView title;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.blur)
        View blur;

        public RoleHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private RolesListener listener;

    public void setListener(RolesListener rolesListener) {
        this.listener = rolesListener;
    }

    public interface RolesListener {
        void onRoleTapped(Role role, View itemView);
    }
}
