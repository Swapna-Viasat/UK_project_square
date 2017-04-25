package construction.thesquare.worker.myaccount.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.shared.data.model.TimesheetUnit;
import construction.thesquare.shared.utils.DateUtils;
import construction.thesquare.shared.view.widget.GraftrsCounterView;
import construction.thesquare.shared.view.widget.GraftrsTick;

/**
 * Created by maizaga on 14/11/16.
 *
 */

public class SubmitTimesheetAdapter extends RecyclerView.Adapter {
    private static final int HEADER = 0;
    private static final int ITEM = 1;

    private enum ViewType {
        COMPANY_HEADER(0), HEADER(1), ITEM(2), RESULTS(3);

        private int code;

        ViewType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        static ViewType getViewType(int code) {
            for (ViewType type : values()) {
                if (code == type.code) {
                    return type;
                }
            }

            return null;
        }
    }

    private Timesheet timesheet;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewType type = ViewType.getViewType(viewType);

        if (type == null) return null;

        switch (type) {
            case COMPANY_HEADER:
                View companyView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_submit_timesheet_company, parent, false);

                return new CompanyViewHolder(companyView);
            case HEADER:
                View headerView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_submit_timesheet_header, parent, false);

                return new HeaderViewHolder(headerView);
            case ITEM:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_submit_timesheet, parent, false);

                return new ItemViewHolder(itemView);
            case RESULTS:
                View resultsView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_submit_timesheet_results, parent, false);

                return new ResultsViewHolder(resultsView);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewType type = ViewType.getViewType(getItemViewType(position));

        if (type == null) return;

        switch (type) {
            case COMPANY_HEADER:
                CompanyViewHolder companyViewHolder = (CompanyViewHolder) holder;
//                companyViewHolder.companyImage = timesheet.getCompanyLogo()// TODO Load Logo with URL
                companyViewHolder.companyName.setText(timesheet.getCompanyName());
                companyViewHolder.daysWorked.setText(DateUtils.formatWeekFromDayToDay(timesheet.getFrom(), timesheet.getTo()));
                break;
            case HEADER:
                // Nothing to load, just static titles
                break;
            case ITEM:
                final TimesheetUnit item = timesheet.getTimesheetUnits()[position - 2];

                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.dayNameView.setText(item.getShortDayText());
                itemViewHolder.dayView.setText(item.getDayText());
                itemViewHolder.didntWorkView.setChecked(!item.hasWorkedHours());
                itemViewHolder.workedView.setCount(item.getHoursWorked());
                itemViewHolder.overtimeView.setCount(item.getOvertime());

                itemViewHolder.didntWorkView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemViewHolder.didntWorkView.setChecked(true);
                        itemViewHolder.workedView.setCount(0);
                        itemViewHolder.overtimeView.setCount(0);
                        itemViewHolder.overtimeView.setEnabled(false);
                        item.setHoursWorked(0);
                        item.setOvertime(0);
                        notifyItemChanged(getItemCount() - 1);
                    }
                });

                itemViewHolder.workedView.setListener(new GraftrsCounterView.CounterEvents() {
                    @Override
                    public void onNumberChanged(GraftrsCounterView view) {
                        // TODO Verify if working hours can be greater that 8. On the contrary, limit that here.
                        itemViewHolder.overtimeView.setEnabled(view.getCount() > 0);
                        itemViewHolder.didntWorkView.setChecked(view.getCount() == 0);
                        if (view.getCount() == 0) itemViewHolder.overtimeView.setCount(0);
                        item.setHoursWorked(view.getCount());
                        notifyItemChanged(getItemCount() - 1);
                    }
                });

                itemViewHolder.overtimeView.setListener(new GraftrsCounterView.CounterEvents() {
                    @Override
                    public void onNumberChanged(GraftrsCounterView view) {
                        item.setOvertime(view.getCount());
                    }
                });
                break;
            case RESULTS:
                ResultsViewHolder resultsViewHolder = (ResultsViewHolder) holder;
                resultsViewHolder.result.setText(String.format(Locale.UK, "£%.2f", timesheet.getResultIncome()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return timesheet.getTimesheetUnits().length + 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewType.COMPANY_HEADER.getCode();
        } else if (position == 1) {
            return ViewType.HEADER.getCode();
        } else if (position == getItemCount() - 1) {
            return ViewType.RESULTS.getCode();
        } else {
            return ViewType.ITEM.getCode();
        }
    }

    public void setTimesheet(Timesheet timesheet) {
        this.timesheet = timesheet;
    }

    static class CompanyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.submit_timesheet_company_image) ImageView companyImage;
        @BindView(R.id.submit_timesheet_company_name) TextView companyName;
        @BindView(R.id.submit_timesheet_days_worked) TextView daysWorked;

        public CompanyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.submit_timesheet_day_name) TextView dayNameView;
        @BindView(R.id.submit_timesheet_day) TextView dayView;
        @BindView(R.id.submit_timesheet_didnt_work) GraftrsTick didntWorkView;
        @BindView(R.id.submit_timesheet_worked) GraftrsCounterView workedView;
        @BindView(R.id.submit_timesheet_overtime) GraftrsCounterView overtimeView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ResultsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.submit_timesheet_result) TextView result;

        public ResultsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
