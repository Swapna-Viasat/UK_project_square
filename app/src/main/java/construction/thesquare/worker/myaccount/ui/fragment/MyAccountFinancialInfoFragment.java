package construction.thesquare.worker.myaccount.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.utils.ImageUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFinancialInfoFragment extends Fragment {

    private static final int REQUEST_CODE_SELECT_PASSPORT_IMAGE = 1;
    private static final int REQUEST_CODE_SELECT_BILL_IMAGE = 2;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @BindView(R.id.fin_info_bank) EditText bankView;
    @BindViews(value = {R.id.fin_info_account_1, R.id.fin_info_account_2, R.id.fin_info_account_3, R.id.fin_info_account_4,
            R.id.fin_info_account_5, R.id.fin_info_account_6, R.id.fin_info_account_7, R.id.fin_info_account_8})
    List<EditText> accountViews;
    @BindViews(value = {R.id.fin_info_sort_1, R.id.fin_info_sort_2, R.id.fin_info_sort_3, R.id.fin_info_sort_4,
            R.id.fin_info_sort_5, R.id.fin_info_sort_6})
    List<EditText> sortViews;
    @BindView(R.id.fin_info_search) EditText searchView;
    @BindView(R.id.fin_info_address) TextView addressView;
    @BindViews(value = {R.id.fin_info_insurance_1, R.id.fin_info_insurance_2, R.id.fin_info_insurance_3, R.id.fin_info_insurance_4, R.id.fin_info_insurance_5,
            R.id.fin_info_insurance_6, R.id.fin_info_insurance_7, R.id.fin_info_insurance_8, R.id.fin_info_insurance_9})
    List<EditText> insuranceViews;
    @BindView(R.id.fin_info_utr) EditText utrView;
    @BindView(R.id.fin_info_lcrn) EditText lcrnView;
    @BindView(R.id.fin_info_vat) EditText vatView;
    @BindView(R.id.fin_info_gpsc) EditText gpscView;
    @BindView(R.id.fin_info_passport_photo) ImageView passportImage;
    @BindView(R.id.fin_info_bill_photo) ImageView billImage;

    private boolean selectPassportPhoto;

    public static MyAccountFinancialInfoFragment newInstance() {

        Bundle args = new Bundle();

        MyAccountFinancialInfoFragment fragment = new MyAccountFinancialInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_account_financial_info, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.fin_info_search_button)
    void onSearchClicked() {
        performSearch();
    }

    @OnClick(R.id.fin_info_passport_photo)
    void onPassportClicked(View view) {
        selectPassportPhoto = true;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            openSelectPhoto();
        }
    }

    @OnClick(R.id.fin_info_bill_photo)
    void onBillClicked() {
        selectPassportPhoto = false;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            openSelectPhoto();
        }
    }

    private void performSearch() {
        String text = searchView.getText().toString();
        searchView.clearFocus();
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(text, 1);
            if (addresses.size() > 0 && addresses.get(0).getMaxAddressLineIndex() >= 0) {
                addressView.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openSelectPhoto();
            } else {
                Toast.makeText(getActivity(), "Read External Storage Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openSelectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, selectPassportPhoto ? REQUEST_CODE_SELECT_PASSPORT_IMAGE : REQUEST_CODE_SELECT_BILL_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SELECT_PASSPORT_IMAGE:
                if (resultCode == Activity.RESULT_OK) onSelectFromGalleryResult(data, passportImage);
                break;
            case REQUEST_CODE_SELECT_BILL_IMAGE:
                if (resultCode == Activity.RESULT_OK) onSelectFromGalleryResult(data, billImage);
        }
    }

    private void onSelectFromGalleryResult(Intent data, ImageView imageView) {
        if (data != null) {
            try {
                Uri targetUri = data.getData();
                Bitmap bmp = ImageUtils.getBitmapFromReturnedImage(getActivity(), targetUri, 100, 100);
                imageView.setImageBitmap(bmp);
            } catch (Exception e) {
                Log.e("MyAccountFinancialInfo", "Error setting image: " + e.getMessage());
            }
        }
    }
}
