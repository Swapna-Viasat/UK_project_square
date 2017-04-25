package construction.thesquare.worker.myaccount.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.shared.data.model.Timesheet;
import construction.thesquare.worker.myaccount.ui.adapter.MyAccountTimesheetsAdapter;

/**
 * Created by maizaga on 1/11/16.
 *
 * Time Sheets View
 */

public class MyAccountTimesheetsFragment extends Fragment {

    private static final String TIMESHEETS = "timesheets";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<Timesheet> timesheets;

    public static MyAccountTimesheetsFragment newInstance(ArrayList<Timesheet> timesheets) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(TIMESHEETS, timesheets);

        MyAccountTimesheetsFragment fragment = new MyAccountTimesheetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            timesheets = getArguments().getParcelableArrayList(TIMESHEETS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account_billing_timesheets, container, false);
        ButterKnife.bind(this, rootView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        MyAccountTimesheetsAdapter adapter = new MyAccountTimesheetsAdapter(getActivity());
        adapter.setTimesheets(timesheets);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void setTimesheets(List<Timesheet> timesheets) {
        this.timesheets = timesheets;
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
