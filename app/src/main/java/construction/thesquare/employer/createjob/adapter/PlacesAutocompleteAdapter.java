package construction.thesquare.employer.createjob.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import construction.thesquare.R;

/**
 * Created by gherg on 12/15/2016.
 */

public class PlacesAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction>
        implements Filterable {

    private static final String TAG = "PlacesAutocomplete";
    private static final CharacterStyle STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);

    private ArrayList<AutocompletePrediction> mResultList;
    private GoogleApiClient mGoogleApiClient;
    private LatLngBounds mBounds;
    private final LatLngBounds BOUNDS_ENTIRE_WORLD =
            new LatLngBounds(new LatLng(-85, -180), new LatLng(85, 180));
    private DialogFragment callingDialog;

    public PlacesAutocompleteAdapter(Context context, GoogleApiClient googleApiClient) {
        super(context, R.layout.item_location_search);
        mGoogleApiClient = googleApiClient;
        mBounds = BOUNDS_ENTIRE_WORLD;
    }

    public interface PlacesListener {
        void onPlace(String id, String primary, String secondary,
                     String name, DialogFragment dialog);
    }

    private PlacesListener listener;

    public void setListener(PlacesListener placesListener, DialogFragment dialog) {
        this.listener = placesListener;
        this.callingDialog = dialog;
    }

    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public int getCount() {
        return (mResultList == null) ? 0 : mResultList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_location_search, parent, false);
        }
        final AutocompletePrediction item = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.title);

        if (item != null)
            title.setText(item.getFullText(STYLE_NORMAL));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != listener) {
                    listener.onPlace(item.getPlaceId(),
                            item.getPrimaryText(STYLE_NORMAL).toString(),
                            item.getSecondaryText(STYLE_NORMAL).toString(),
                            item.getFullText(STYLE_NORMAL).toString(),
                            callingDialog);
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();
                if (constraint != null) {
                    filterData = getAutocomplete(constraint);
                }
                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {

            AutocompleteFilter filter = new AutocompleteFilter
                    .Builder()
                    .setCountry("GB")
                    .build();

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                        .getAutocompletePredictions(mGoogleApiClient,
                                constraint.toString(), mBounds, filter);

            AutocompletePredictionBuffer autocompletePredictions =
                    results.await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();

            if (!status.isSuccess()) {
                Toast.makeText(getContext(), "Error contacting API: " + status.getStatusMessage(),
                        Toast.LENGTH_SHORT).show();

                autocompletePredictions.release();
                return null;
            }

            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }

        return null;
    }
}