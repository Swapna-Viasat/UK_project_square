package construction.thesquare.employer.help;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.help.HelpRecentAskedResponse;
import construction.thesquare.shared.models.Help;
import construction.thesquare.shared.settings.fragments.SettingsContactFragment;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.view.widget.JosefinSansEditText;
import construction.thesquare.employer.help.adapter.HelpTopDetailsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by swapna on 3/15/2017.
 */

public class EmployerHelpFragment extends Fragment implements HelpTopDetailsAdapter.HelpTopDetailsListener{
    @BindView(R.id.search_input)
    JosefinSansEditText search;
    int count = 5;
    @BindView(R.id.worker_top_search_rv)
    RecyclerView rv;
    private HelpTopDetailsAdapter adapter;
    private List<Help> data = new ArrayList<>();
    @BindView(R.id.recent_faq_section)
    LinearLayout recentFagSection;
    public static EmployerHelpFragment newInstance() {
        return new EmployerHelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_worker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setTitle("Help");
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new HelpTopDetailsAdapter(data, getContext(), this);
            adapter.registerAdapterDataObserver(observer);
            rv.setAdapter(adapter);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (data.isEmpty()) {
                recentFagSection.setVisibility(View.GONE);
            } else {
                recentFagSection.setVisibility(View.VISIBLE);
            }
        }
    };

    @OnClick({R.id.search_button, R.id.contact_us})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.search_button:
                if (validateFields())
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_employer_content,
                                    HelpDetailsFragment.newInstance(search.getText().toString()))
                            .addToBackStack("")
                            .commit();
                break;
            case R.id.contact_us:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_employer_content,
                                SettingsContactFragment.newInstance())
                        .addToBackStack("contact")
                        .commit();
                break;
        }
    }

    private boolean validateFields() {
        boolean result = true;
        if (TextUtils.isEmpty(search.getText().toString())) {
            search.setError("Please enter your message");
            result = false;
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchRecentQueries(count);
    }

    public void fetchRecentQueries(final int count) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        Call<HelpRecentAskedResponse> call = HttpRestServiceConsumer.getBaseApiClient().getTopQuestions(count);
        call.enqueue(new Callback<HelpRecentAskedResponse>() {
            @Override
            public void onResponse(Call<HelpRecentAskedResponse> call, Response<HelpRecentAskedResponse> response) {
                if (response.isSuccessful()) {
                    DialogBuilder.cancelDialog(dialog);
                    if (null != response) {
                        if (null != response.body()) {
                            if (null != response.body().response) {
                                displaySearchData(response.body().response);
                            }
                        }
                    }
                } else {
                    HandleErrors.parseError(getContext(), dialog, response);
                }
            }

            @Override
            public void onFailure(Call<HelpRecentAskedResponse> call, Throwable t) {
                HandleErrors.parseFailureError(getContext(), dialog, t);
            }
        });
    }

    public void displaySearchData(List<Help> helpdata) {
        if (!data.isEmpty()) data.clear();
        for (Help help : helpdata) {
            data.add(help);
        }
        adapter.notifyDataSetChanged();
    }
}
