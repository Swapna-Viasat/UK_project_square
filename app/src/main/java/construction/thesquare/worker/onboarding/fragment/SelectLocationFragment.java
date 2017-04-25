package construction.thesquare.worker.onboarding.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.employer.createjob.persistence.GsonConfig;
import construction.thesquare.shared.analytics.Analytics;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.ZipCodeVerifier;
import construction.thesquare.shared.data.model.Location;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.model.ZipResponse;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.Constants;
import construction.thesquare.shared.utils.ConstantsAnalytics;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.KeyboardUtils;
import construction.thesquare.shared.utils.TextTools;
import construction.thesquare.shared.view.widget.CommuteTimeSeekBar;
import construction.thesquare.worker.myaccount.ui.dialog.EditAccountDetailsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by gherg on 12/6/2016.
 */

public class SelectLocationFragment extends Fragment {

    public static final String TAG = "SelectLocationFragment";
    private int workerId;
    private Worker currentWorker;

    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Location centerMapLocation;
    private View rootView;
    private static final double londonLat = 51.5074;
    private static final double londonLong = 0.1278;
    private String address;
    private String workerZipCode;

    @BindView(R.id.filter)
    TextView filter;
    @BindView(R.id.seek_commute)
    CommuteTimeSeekBar seekCommute;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentWorker = (Worker) getArguments().getSerializable(Constants.KEY_CURRENT_WORKER);


        Analytics.recordCurrentScreen(getActivity(),
                ConstantsAnalytics.SCREEN_WORKER_ONBOARDING_LOCATION);
    }

    public static SelectLocationFragment newInstance(boolean singleEdition, Worker worker) {
        SelectLocationFragment selectLocationFragment = new SelectLocationFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_SINGLE_EDIT, singleEdition);
        bundle.putSerializable(Constants.KEY_CURRENT_WORKER, worker);
        selectLocationFragment.setArguments(bundle);
        return selectLocationFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_worker_select_location,
                    container, false);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }

        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerId = SharedPreferencesManager.getInstance(getContext()).getWorkerId();

        centerMapLocation = new construction.thesquare.shared.data.model.Location();

        mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPostCode();
            }
        });
    }

    @OnClick(R.id.next)
    public void next() {
        patchWorker();
    }

    private void proceed() {
        if (getArguments() != null && getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            return;
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.onboarding_content, SelectRoleFragment
                        .newInstance(false, currentWorker))
                .addToBackStack("")
                .commit();
    }

    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> location = new HashMap<>();
        location.put("latitude", centerMapLocation.getLatitude());
        location.put("longitude", centerMapLocation.getLongitude());

        HashMap<String, Object> request = new HashMap<>();
        request.put("address", filter.getText().toString());
        if (workerZipCode != null) request.put("post_code", workerZipCode);
        request.put("location", location);
        request.put("commute_time", seekCommute.getRate());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(workerId, request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            proceed();
                        } else HandleErrors.parseError(getContext(), dialog, response);
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private GoogleMap.OnCameraIdleListener cameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
            centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;

            TextTools.log(TAG, String.valueOf(googleMap.getCameraPosition().target.toString()));
        }
    };

    private void moveToInitialLocation() {
        LatLng london = new LatLng(londonLat, londonLong);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 13f));
        if (currentWorker != null) centerOnPostalCode(currentWorker.zip);
    }

    private void centerOnPostalCode(String code) {
        if (TextUtils.isEmpty(code)) return;
        try {
            final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
            ZipCodeVerifier.getInstance()
                    .api()
                    .verify(code, ZipCodeVerifier.API_KEY)
                    .enqueue(new Callback<ZipResponse>() {
                        @Override
                        public void onResponse(Call<ZipResponse> call, Response<ZipResponse> response) {
                            DialogBuilder.cancelDialog(dialog);
                            if (response.isSuccessful()) {
                                if (response.body().message == null) {
                                    LatLng latLng = new LatLng(response.body().lat, response.body().lang);

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ZipResponse> call, Throwable t) {
                            DialogBuilder.cancelDialog(dialog);
                        }
                    });
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void editPostCode() {
        EditAccountDetailsDialog.newInstance("Post code", currentWorker.zip, false,
                new EditAccountDetailsDialog.InputFinishedListener() {
                    @Override
                    public void onDone(String input, boolean onlyDigits) {
                        validateZip(input);
                    }
                }).show(getFragmentManager(), "");
    }

    private void validateZip(final String zipCode) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        ZipCodeVerifier.getInstance()
                .api()
                .verify(zipCode, ZipCodeVerifier.API_KEY)
                .enqueue(new Callback<ZipResponse>() {
                    @Override
                    public void onResponse(Call<ZipResponse> call, Response<ZipResponse> response) {
                        DialogBuilder.cancelDialog(dialog);

                        if (null != response.body()) {
                            if (null != response.body().message) {
                                if (response.body().message.equals(ZipCodeVerifier.BAD_REQUEST)) {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.validate_zip))
                                            .show();
                                } else {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.validate_zip))
                                            .show();
                                }
                            } else {

                                LatLng latLng = new LatLng(response.body().lat, response.body().lang);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

                                if (!CollectionUtils.isEmpty(response.body().addresses)) {
                                    workerZipCode = zipCode;
                                    if (currentWorker != null) currentWorker.zip = zipCode;
                                    showAddressDialog(zipCode, response.body().addresses);
                                }
                                // all good
                            }
                        } else {
                            // response body null
                            new android.app.AlertDialog.Builder(getContext())
                                    .setMessage(getString(R.string.validate_zip))
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ZipResponse> call, Throwable t) {
                        DialogBuilder.cancelDialog(dialog);
                    }
                });
    }

    private void showAddressDialog(final String workerZipCode, final List<String> result) {
        if (getActivity() == null || !isAdded()) return;

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.autocomplete);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels * 8 / 12);
        ((TextView) dialog.findViewById(R.id.autocomplete_title))
                .setText(getString(R.string.create_job_address));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, result);
        ListView listView = (ListView) dialog.findViewById(R.id.autocomplete_rv);
        final EditText search = (EditText) dialog.findViewById(R.id.autocomplete_search);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search.setText(result.get(position));
            }
        });

        dialog.findViewById(R.id.autocomple_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.autocomple_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // all good
                if (!search.getText().toString().isEmpty()) {
                    address = search.getText().toString();
                    filter.setText(address.replace(", , , ,", ", ") + ", " + workerZipCode);
                    if (currentWorker != null) currentWorker.address = filter.getText().toString();
                    dialog.dismiss();
                } else
                    DialogBuilder.showStandardDialog(getContext(), "", "Please select an address");
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorker();

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;

                    moveToInitialLocation();
                    centerMapLocation.latitude = googleMap.getCameraPosition().target.latitude;
                    centerMapLocation.longitude = googleMap.getCameraPosition().target.longitude;
                    googleMap.setOnCameraIdleListener(cameraIdleListener);
                    fetchMe();
                }
            });
        }
    }

    private void fetchMe() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful()) {
                            TextTools.log(TAG, "success");
                            if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT))
                                currentWorker = response.body().getResponse();
                            populateData();
                            if (currentWorker != null) centerOnPostalCode(currentWorker.zip);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void populateData() {
        if (getActivity() == null || !isAdded()) return;

        if (currentWorker != null) {
            if (!TextUtils.isEmpty(currentWorker.address)) filter.setText(currentWorker.address);

            if (currentWorker.location != null) {
                LatLng latLng = new LatLng(currentWorker.location.getLatitude(),
                        currentWorker.location.getLongitude());

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

                centerMapLocation = currentWorker.location;
                seekCommute.setRate(currentWorker.commuteTime);
            }
        }
    }

    @Override
    public void onPause() {
        persistProgress();
        KeyboardUtils.hideKeyboard(getActivity());
        super.onPause();
    }

    private void loadWorker() {
        String workerJson = getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW,
                Context.MODE_PRIVATE).getString(Constants.KEY_PERSISTED_WORKER, "");

        if (!TextUtils.isEmpty(workerJson))
            currentWorker = GsonConfig.buildDefault().fromJson(workerJson, Worker.class);
    }

    private void persistProgress() {
        if (getArguments().getBoolean(Constants.KEY_SINGLE_EDIT)) return;

        if (currentWorker != null) {
            currentWorker.address = filter.getText().toString();
            currentWorker.location = new construction.thesquare.shared.data.model.Location();
            currentWorker.location.latitude = centerMapLocation.getLatitude();
            currentWorker.location.longitude = centerMapLocation.getLongitude();
            currentWorker.commuteTime = seekCommute.getRate();
        }

        getActivity().getSharedPreferences(Constants.WORKER_ONBOARDING_FLOW, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_PERSISTED_WORKER, GsonConfig.buildDefault().toJson(currentWorker))
                .apply();
    }
}
