package construction.thesquare.employer.help;


import android.app.Dialog;
import android.os.Bundle;
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
import butterknife.OnClick;
import construction.thesquare.R;

import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.help.HelpClickedResponse;

import construction.thesquare.employer.help.adapter.HelpDetailsAdapter;

import construction.thesquare.shared.models.Help;
import construction.thesquare.shared.settings.fragments.SettingsContactFragment;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HelpDetailsFragment extends Fragment  implements
        HelpDetailsAdapter.HelpDetailsListener, construction.thesquare.employer.help.HelpContract.View{
    private List<Help> data = new ArrayList<>();
    private HelpDetailsAdapter adapter;
    private HelpContract.UserActionListener mUserActionListener;
    @BindView(R.id.worker_search_rv)
    RecyclerView rv;
    @BindView(R.id.no_matches)
    JosefinSansTextView noMatches;
    @BindView(R.id.contact_us)
    JosefinSansTextView contact;
    public static HelpDetailsFragment newInstance(String search) {
        HelpDetailsFragment helpDetailsFragment = new HelpDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        helpDetailsFragment.setArguments(bundle);
        return helpDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserActionListener = new HelpPresenter(this,getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HelpDetailsAdapter(data, getContext(), this);
        adapter.registerAdapterDataObserver(observer);
        rv.setAdapter(adapter);
    }



    @Override
    public void onResume() {
        super.onResume();
        mUserActionListener.fetchSearch(getArguments().getString("search"));
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (data.isEmpty()) {
                noMatches.setVisibility(View.VISIBLE);
                contact.setVisibility(View.GONE);
            } else {
                noMatches.setVisibility(View.GONE);
                contact.setVisibility(View.VISIBLE);
            }
        }
    };

    @OnClick({R.id.no_matches,R.id.contact_us})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.no_matches:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_employer_content, SettingsContactFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
            case R.id.contact_us:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_employer_content, SettingsContactFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
        }
    }

    @Override
    public void displaySearchData(List<Help> helpdata) {
        if (!data.isEmpty()) data.clear();
        for (Help help : helpdata) {
            data.add(help);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onQuestionClicked(int id) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        Call<HelpClickedResponse> call = HttpRestServiceConsumer.getBaseApiClient().getSelectedQuestion(id);
        call.enqueue(new Callback<HelpClickedResponse>() {
            @Override
            public void onResponse(Call<HelpClickedResponse> call, Response<HelpClickedResponse> response) {
                if (response.isSuccessful()) {
                    DialogBuilder.cancelDialog(dialog);
                }
            }

            @Override
            public void onFailure(Call<HelpClickedResponse> call, Throwable t) {
                HandleErrors.parseFailureError(getContext(), dialog, t);
            }
        });
    }



}
