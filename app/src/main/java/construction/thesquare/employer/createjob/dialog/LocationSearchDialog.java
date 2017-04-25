package construction.thesquare.employer.createjob.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.adapter.PlacesAutocompleteAdapter;
import construction.thesquare.shared.utils.CrashLogHelper;

/**
 * Created by gherg on 1/21/17.
 */

public class LocationSearchDialog extends DialogFragment {

    public static final String TAG = "LocationSearchDialog";

    private String selection;
    private PlacesAutocompleteAdapter adapter;
    private Context context;
    private GoogleApiClient client;
    private PlacesAutocompleteAdapter.PlacesListener listener;
    private TextView textView;

    @BindView(R.id.autocomplete_rv) ListView listView;
    @BindView(R.id.autocomplete_search) EditText search;
    @BindView(R.id.autocomple_cancel) TextView cancel;
    @BindView(R.id.autocomplete_title) TextView title;
    @BindView(R.id.autocomple_done) TextView done;

    public static LocationSearchDialog
        newInstance(String selectedPostal,
                    Context context,
                    GoogleApiClient client,
                    PlacesAutocompleteAdapter.PlacesListener listener,
                    TextView textView) {
        LocationSearchDialog dialog = new LocationSearchDialog();
        dialog.context = context;
        dialog.selection = selectedPostal;
        dialog.client = client;
        dialog.listener = listener;
        dialog.textView = textView;
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        //
        View view = inflater.inflate(R.layout.autocomplete_location, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        // have to make this call otherwise shows blank white space in the title area
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // 
        adapter = new PlacesAutocompleteAdapter(context, client);
        adapter.setListener(listener, this);
        if (null != listView) {
            listView.setAdapter(adapter);
        }
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        search.setText(textView.getText().toString());
        try {
            search.setSelection(0, textView.getText().length());
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        title.setText(getString(R.string.create_job_search_postal));
    }
}