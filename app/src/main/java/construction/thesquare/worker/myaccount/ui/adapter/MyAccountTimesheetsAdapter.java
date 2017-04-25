package construction.thesquare.worker.myaccount.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.worker.myaccount.ui.activity.MyAccountBillingActivity;
import construction.thesquare.worker.myaccount.ui.activity.SubmitTimesheetActivity;

/**
 * Created by maizaga on 2/11/16.
 *
 * Timesheets screen adapter
 */

public class MyAccountTimesheetsAdapter extends RecyclerView.Adapter {

    private enum ItemType {
        HEADER(0), ITEM(1), REJECTED(2);

        private int code;

        ItemType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        static ItemType getItemType(int code) {
            for (ItemType type : values()) {
                if (type.getCode() == code) return type;
            }

            return null;
        }
    }

    private Activity activity;
    private List<Object> items;
    private List<Timesheet> timesheets;

    private TreeSet<Integer> headers = new TreeSet<>();
    private TreeSet<Integer> notRejected = new TreeSet<>();
    private TreeSet<Integer> rejected = new TreeSet<>();
    private boolean hasDue = false;
    private boolean hasAwaiting = false;
    private boolean hasApproved = false;

    public MyAccountTimesheetsAdapter(Activity context) {
        this.activity = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemType type = ItemType.getItemType(viewType);

        if (type != null)
            switch (type) {
                case HEADER:
                    View headerView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_header, parent, false);

                    return new HeaderViewHolder(headerView);
                case ITEM:
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_billing, parent, false);

                    return new ItemViewHolder(itemView);
                case REJECTED:
                    View rejectedView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_billing_rejected, parent, false);

                    return new RejectedViewHolder(rejectedView);
            }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.getItemType(getItemViewType(position));

        if (type != null)
            switch (type) {
                case HEADER:
                    String text = (String) items.get(position);
                    ((HeaderViewHolder)holder).text.setText(text);
                    break;
                case ITEM:
                    Timesheet timesheet = (Timesheet) items.get(position);
                    ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
                    itemViewHolder.companyName.setText(timesheet.getCompanyName());
                    itemViewHolder.daysWorked.setText(DateUtils.formatWeekFromDayToDay(timesheet.getFrom(), timesheet.getTo()));
                    switch (timesheet.getStatus()) {
                        case DUE:
                            itemViewHolder.buttonText.setText(R.string.submit);
                            itemViewHolder.buttonText.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.redSquareColor, null));
                            itemViewHolder.buttonText.setOnClickListener(new SubmitListemer(timesheet));
                            break;
                        case AWAITING:
                            itemViewHolder.buttonText.setText(R.string.pending);
                            itemViewHolder.buttonText.setTextColor(Color.GREEN);
                            break;
                        case APPROVED:
                            itemViewHolder.buttonText.setText("");
                            break;
                    }
                    break;
                case REJECTED:
                    Timesheet rejectedTimesheet = (Timesheet) items.get(position);
                    RejectedViewHolder rejectedViewHolder = (RejectedViewHolder) holder;
                    rejectedViewHolder.companyName.setText(rejectedTimesheet.getCompanyName());
                    rejectedViewHolder.daysWorked.setText(DateUtils.formatWeekFromDayToDay(rejectedTimesheet.getFrom(), rejectedTimesheet.getTo()));
                    rejectedViewHolder.reason.setText(rejectedTimesheet.getRejectedReason());
                    rejectedViewHolder.submit.setOnClickListener(new SubmitListemer(rejectedTimesheet));
                    break;
            }
    }

    private class SubmitListemer implements View.OnClickListener {
        private Timesheet timesheet;

        SubmitListemer(Timesheet timesheet) {
            this.timesheet = timesheet;
        }

        @Override
        public void onClick(View view) {
            activity.startActivityForResult(SubmitTimesheetActivity.startIntent(activity, timesheet),
                    MyAccountBillingActivity.REQUEST_TIMESHEET_UNITS);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notRejected.contains(position)) {
            return ItemType.ITEM.getCode();
        } else if (rejected.contains(position)) {
            return ItemType.REJECTED.getCode();
        } else if (headers.contains(position)) {
            return ItemType.HEADER.getCode();
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setTimesheets(List<Timesheet> timesheets) {
        if (timesheets == null) {
            this.timesheets = new ArrayList<>();
        } else {
            this.timesheets = timesheets;
        }

        initializeIndexes();
    }

    private void initializeIndexes() {
        if (items == null) {
            items = new ArrayList<>();
        } else {
            items.clear();
        }

        headers.clear();
        notRejected.clear();
        rejected.clear();

        if (timesheets.size() > 0) {
            int index = 0;
            for (Timesheet timesheet : timesheets) {
                switch (timesheet.getStatus()) {
                    case DUE:
                        if (!hasDue) {
                            hasDue = true;
                            items.add(activity.getString(R.string.due_header));
                            headers.add(index);
                            index++;
                        }
                        items.add(timesheet);
                        if (timesheet.isRejected()) {
                            rejected.add(index);
                        } else {
                            notRejected.add(index);
                        }
                        break;
                    case AWAITING:
                        if (!hasAwaiting) {
                            hasAwaiting = true;
                            items.add(activity.getString(R.string.awaiting_header));
                            headers.add(index);
                            index++;
                        }
                        items.add(timesheet);
                        notRejected.add(index);
                        break;
                    case APPROVED:
                        if (!hasApproved) {
                            hasApproved = true;
                            items.add(activity.getString(R.string.approved_header));
                            headers.add(index);
                            index++;
                        }
                        items.add(timesheet);
                        notRejected.add(index);
                        break;
                }
                index++;
            }
        }

        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text) TextView text;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.billing_item_company_name) TextView companyName;
        @BindView(R.id.billing_item_days_worked) TextView daysWorked;
        @BindView(R.id.billing_item_button) TextView buttonText;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class RejectedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.billing_item_rejected_company_name) TextView companyName;
        @BindView(R.id.billing_item_rejected_days_worked) TextView daysWorked;
        @BindView(R.id.billing_item_rejected_reason) TextView reason;
        @BindView(R.id.billing_item_rejected_button) TextView submit;

        RejectedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
