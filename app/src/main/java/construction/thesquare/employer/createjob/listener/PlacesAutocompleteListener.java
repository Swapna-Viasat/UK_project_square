package construction.thesquare.employer.createjob.listener;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import construction.thesquare.employer.createjob.adapter.PlacesAutocompleteAdapter;
import construction.thesquare.employer.createjob.dialog.LocationSearchDialog;

/**
 * Created by gherg on 1/13/2017.
 */

public class PlacesAutocompleteListener implements View.OnClickListener {

    private PlacesAutocompleteAdapter.PlacesListener listener;
    private Context context;
    private TextView searchTextView;
    private GoogleApiClient client;
    private String initText;
    private FragmentManager manager;

    public PlacesAutocompleteListener(PlacesAutocompleteAdapter.PlacesListener listener,
                                      Context context,
                                      FragmentManager fragmentManager,
                                      TextView textView,
                                      GoogleApiClient client,
                                      String string) {
        this.listener = listener;
        this.context = context;
        this.searchTextView = textView;
        this.client = client;
        this.manager = fragmentManager;
        this.initText = string;
    }

    @Override
    public void onClick(View view) {
        LocationSearchDialog dialog = LocationSearchDialog
                .newInstance(initText, context, client,
                        listener, searchTextView);
        dialog.show(manager, "sample");
    }
}