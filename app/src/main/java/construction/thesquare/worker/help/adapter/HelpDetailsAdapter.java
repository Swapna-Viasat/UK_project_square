package construction.thesquare.worker.help.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import construction.thesquare.shared.models.Help;

/**
 * Created by swapna on 3/13/2017.
 */

public class HelpDetailsAdapter extends RecyclerView.Adapter<HelpDetailsAdapter.HelpDetailsHolder> {
    private List<Help> data = new ArrayList<>();
    private Context context;
    private HelpDetailsListener listener;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    public HelpDetailsAdapter(List<Help> list, Context context, HelpDetailsListener listener) {
        this.data = list;
        this.context = context;
        this.listener = listener;
        for (int i = 0; i < data.size(); i++) {
            expandState.append(i, false);
        }
    }

    public interface HelpDetailsListener {
        void onQuestionClicked(int id);
    }

    @Override
    public HelpDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HelpDetailsHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_help_details, parent, false));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class HelpDetailsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.question)
        JosefinSansTextView question;
        @BindView(R.id.answer)
        JosefinSansTextView answer;
        @BindView(R.id.expandableLayout)
        ExpandableLinearLayout expandableLayout;
        public HelpDetailsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public void onBindViewHolder(final HelpDetailsHolder holder, final int position) {
        final Help faq = data.get(position);
        if (faq.question != null) {
            holder.question.setText(faq.question);
        }
        holder.setIsRecyclable(false);
        holder.expandableLayout.setInRecyclerView(true);
        holder.expandableLayout.setExpanded(expandState.get(position));
        holder.answer.setText(Html.fromHtml(faq.answer));
        holder.answer.setMovementMethod(LinkMovementMethod.getInstance());
        holder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                expandState.put(position, false);
            }
        });
        if (null != listener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickButton(holder.expandableLayout);
                }
            });
        }
    }

    private void onClickButton(final ExpandableLayout expandableLayout) {
        expandableLayout.toggle();
    }

}