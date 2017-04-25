package construction.thesquare.worker.myaccount.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.worker.myaccount.ui.activity.MyAccountBillingActivity;
import construction.thesquare.worker.myaccount.ui.adapter.SubmitTimesheetAdapter;

/**
 * Created by maizaga on 11/11/16.
 *
 */

public class SubmitTimesheetFragment extends Fragment {

    private static final String TIMESHEET = "timesheet";

    @BindView(R.id.submit_timesheet_recycler_view)
    RecyclerView recyclerView;

    private Timesheet timesheet;

    public static SubmitTimesheetFragment newInstance(Timesheet timesheet) {
        Bundle args = new Bundle();
        args.putParcelable(TIMESHEET, timesheet);

        SubmitTimesheetFragment fragment = new SubmitTimesheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            timesheet = getArguments().getParcelable(TIMESHEET);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_submit_timesheet, container, false);
        ButterKnife.bind(this, v);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        SubmitTimesheetAdapter adapter = new SubmitTimesheetAdapter();
        adapter.setTimesheet(timesheet);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @OnClick(R.id.submit_timesheet_button)
    void onSubmit() {
        Intent intent = new Intent();
        timesheet.setStatus(Timesheet.TimesheetStatus.AWAITING);
        intent.putExtra(MyAccountBillingActivity.TIMESHEET_BUNDLE, timesheet);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}
