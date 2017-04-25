package construction.thesquare.employer.createjob.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.CreateRequest;
import construction.thesquare.employer.createjob.adapter.PlacesAutocompleteAdapter;
import construction.thesquare.employer.createjob.listener.PlacesAutocompleteListener;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.ZipCodeVerifier;
import construction.thesquare.shared.data.model.Location;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.ZipResponse;
import construction.thesquare.shared.models.Employer;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.JosefinSansTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectLocationFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener,
                    PlacesAutocompleteAdapter.PlacesListener {

    public static final String TAG = "SelectLocationFragment";
    private boolean unfinished = true;

    @BindView(R.id.title) JosefinSansTextView title;

    private CreateRequest request;
    private static final double londonLat = 51.5074;
    private static final double londonLong = 0.1278;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private construction.thesquare.shared.data.model.Location centerMapLocation;
    View rootView;

    @BindView(R.id.filter) TextView filter;

    public static SelectLocationFragment newInstance(CreateRequest request,
                                                     boolean singleEdit) {
        SelectLocationFragment selectLocationFragment = new SelectLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdit);
        selectLocationFragment.setArguments(bundle);
        return selectLocationFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_EMPLOYER_CREATE_JOB_LOCATION);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //@swapna added code for back button flow crashing
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_employer_create_job_location, container, false);
        } catch (InflateException e) {
            return rootView;
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        request = (CreateRequest) getArguments().getSerializable("request");

        centerMapLocation = new construction.thesquare.shared.data.model.Location();
        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);
        buildGoogleApiClient();
        title.setText(getString(R.string.create_job_location));
        if (null != googleApiClient) {
            filter.setOnClickListener(new
                    PlacesAutocompleteListener(this,
                    getContext(), getChildFragmentManager(), filter, googleApiClient,
                    filter.getText().toString()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_job, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_create_job) {
            unfinished = false;
            if (getActivity()
                    .getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                    .edit()
                    .putInt(Constants.KEY_STEP, 0)
                    .remove(Constants.KEY_REQUEST)
                    .commit()) {
                getActivity().finish();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        persistProgress();
    }
    private void persistProgress() {
        Location pickedLocation = new Location();
        if (null != googleMap) {
            TextTools.log(TAG, "google map not null");
            pickedLocation.latitude = googleMap.getCameraPosition().target.latitude;
            TextTools.log(TAG, String.valueOf(googleMap.getCameraPosition().target.latitude));
            pickedLocation.longitude = googleMap.getCameraPosition().target.longitude;
            TextTools.log(TAG, String.valueOf(googleMap.getCameraPosition().target.longitude));
        } else {
            TextTools.log(TAG, "google map is null");
        }
        // request.location = centerMapLocation;
        request.location = pickedLocation;
        TextTools.log(TAG, request.location.toString());
        request.locationName = filter.getText().toString();

        getActivity().getSharedPreferences(Constants.CREATE_JOB_FLOW, MODE_PRIVATE)
                .edit()
                .putInt(Constants.KEY_STEP, Constants.KEY_STEP_LOCATION)
                .putBoolean(Constants.KEY_UNFINISHED, unfinished)
                .putString(Constants.KEY_REQUEST, GsonConfig.buildDefault().toJson(request))
                .commit();
    }

    public void onPlace(String id, String primary, String secondary,
                        String name, DialogFragment dialog) {
        if (null != dialog) dialog.dismiss();

        TextTools.log(TAG, id);
        TextTools.log(TAG, primary);
        TextTools.log(TAG, secondary);
        TextTools.log(TAG, name);

        filter.setText(name);
        if (null != googleMap) {
            centerOnPostalCode(primary, googleMap);
        }

        // will do something with this code later

//        if (null != googleMap) {
//            PendingResult<PlaceBuffer> result = Places.GeoDataApi
//                    .getPlaceById(googleApiClient, id);
//            result.setResultCallback(new ResultCallbacks<PlaceBuffer>() {
//                @Override
//                public void onSuccess(@NonNull PlaceBuffer places) {
//                    //
//                    if (places.getCount() > 0) {
//                        TextTools.log(TAG, String.valueOf(places.get(0).getAddress().toString()));
//
//                        String postalCode = places.get(0).getAddress().toString();
//                        centerOnPostalCode(postalCode, googleMap);
//                    }
//                }
//
//                @Override
//                public void onFailure(@NonNull Status status) {
//                    //
//                    TextTools.log(TAG, "error");
//                }
//            });
//        }

    }

    @OnClick(R.id.next)
    public void next() {
        Location pickedLocation = new Location();
        if (null != googleMap) {
            pickedLocation.latitude = googleMap.getCameraPosition().target.latitude;
            pickedLocation.longitude = googleMap.getCameraPosition().target.longitude;
        } else {
            //
        }

        request.location = pickedLocation;
        request.locationName = filter.getText().toString();

        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            //
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, PreviewJobFragment.newInstance(request, false))
                    .commit();
            //
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.create_job_content,
                            SelectDetailsFragment.newInstance(request, false))
                    .addToBackStack("")
                    .commit();
        }

    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        googleApiClient.connect();
    }

    @SuppressWarnings("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        if (null != mapFragment) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {

                    try {
                        googleMap = map;
                        LatLng london = new LatLng(londonLat, londonLong);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 12));
                        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                            @Override
                            public void onCameraMove() {
                                centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
                                centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;
                            }
                        });

                        fetchMe(googleMap);
                    } catch (Exception e) {
                        CrashLogHelper.logException(e);
                    }

                    try {
                        centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
                        centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;
                    } catch (Exception e) {
                        CrashLogHelper.logException(e);
                    }
                }
            });
        }
    }
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // TODO: add a default location fallback
    }
    public void onConnectionSuspended(int i) {
        // TODO: add a default location fallback
    }

    private void fetchMe(final GoogleMap googleMap) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meEmployer()
                .enqueue(new Callback<ResponseObject<Employer>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Employer>> call,
                                           Response<ResponseObject<Employer>> response) {
                        //
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.body() != null
                                    && response.body().getResponse() != null
                                    && response.body().getResponse().company != null) {
                                if (response.body().getResponse().company.postCode != null) {
                                    // centering on postal code
                                    try {
                                        centerOnPostalCode(response.body()
                                                .getResponse().company.postCode, googleMap);
                                    } catch (Exception e) {
                                        CrashLogHelper.logException(e);
                                    }
                                }
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Employer>> call, Throwable t) {
                        //
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }
    private void centerOnPostalCode(String code, final GoogleMap googleMap) {
        // also populate the search field with it
        filter.setText(code);
        filter.setOnClickListener(new
                PlacesAutocompleteListener(this,
                getContext(), getChildFragmentManager(), filter, googleApiClient,
                filter.getText().toString()));
        //
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            ZipCodeVerifier.getInstance()
                    .api()
                    .verify(code.trim(), ZipCodeVerifier.API_KEY)
                    .enqueue(new Callback<ZipResponse>() {
                        @Override
                        public void onResponse(Call<ZipResponse> call,
                                               Response<ZipResponse> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                if (null == response.body().message) {
                                    LatLng latLng = new LatLng(response.body().lat, response.body().lang);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                }
                            } else {
                                //
                            }
                        }

                        @Override
                        public void onFailure(Call<ZipResponse> call, Throwable t) {
                            //
                            DialogBuilder.cancelDialog(dialog);
                            //
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }
}