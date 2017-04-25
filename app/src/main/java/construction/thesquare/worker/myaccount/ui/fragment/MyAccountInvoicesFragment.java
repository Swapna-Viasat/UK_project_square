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
import construction.thesquare.shared.data.model.Invoice;
import construction.thesquare.worker.myaccount.ui.adapter.MyAccountInvoicesAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountInvoicesFragment extends Fragment {

    private static final String INVOICES = "invoices";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<Invoice> invoices;

    public static MyAccountInvoicesFragment newInstance(ArrayList<Invoice> invoices) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(INVOICES, invoices);
        
        MyAccountInvoicesFragment fragment = new MyAccountInvoicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            invoices = getArguments().getParcelableArrayList(INVOICES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_account_invoices, container, false);
        ButterKnife.bind(this, v);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        MyAccountInvoicesAdapter adapter = new MyAccountInvoicesAdapter(getActivity());
        adapter.setInvoices(invoices);
        recyclerView.setAdapter(adapter);

        return v;
    }
}
