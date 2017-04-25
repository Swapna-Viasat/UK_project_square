/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.worker.onboarding.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.MediaTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class EditPassportImageFragment extends Fragment {

    @BindView(R.id.passport_photo)
    ImageView passportPhoto;
    @BindView(R.id.maximize)
    ImageView maximize;

    private Uri imageUri;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_SELECTION = 2;
    static final int REQUEST_PERMISSIONS = 3;
    static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private Worker currentWorker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker_passport, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchCurrentWorker();
        showPassportImage();
    }

    private void fetchCurrentWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .getFilteredWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(),
                        Arrays.asList("passport_upload"))
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                currentWorker = response.body().getResponse();
                                populateData();
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void populateData() {
        showPassportImage();
    }

    private void showPassportImage() {
        if (currentWorker != null && currentWorker.passportUpload != null) {
            Picasso.with(getContext())
                    .load(currentWorker.passportUpload)
                    .fit()
                    .placeholder(R.drawable.passport)
                    .error(R.drawable.passport)
                    .centerCrop()
                    .into(passportPhoto);
        } else {
            Picasso.with(getContext())
                    .load(R.drawable.passport)
                    .fit()
                    .centerCrop()
                    .into(passportPhoto);
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchOpenGalleryIntent();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    private void dispatchTakePictureIntent() {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = MediaTools.getOutputImageUri(getContext());
            } else {
                File file = MediaTools.getOutputImageFile();
                if (file != null) imageUri = Uri.fromFile(file);
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (imageUri != null) takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            else {
                DialogBuilder.showStandardDialog(getContext(), "Error", "Can not store image file to local storage");
                return;
            }
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                REQUEST_IMAGE_SELECTION);
    }

    private void uploadPicture(Context context, Bitmap file) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("passport_upload", MediaTools.encodeToBase64(file));
        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(
                        SharedPreferencesManager.getInstance(context).loadSessionInfoWorker().getUserId(), payload)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {

                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            fetchCurrentWorker();
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<Worker>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void showOriginalImage() {
        LayoutInflater layoutInflater
                = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        final Dialog settingsDialog = new Dialog(getContext());
        if (currentWorker != null && currentWorker.passportUpload != null) {
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            settingsDialog.setContentView(layoutInflater.inflate(R.layout.popup_passport_image, null));
            ImageButton close = (ImageButton) settingsDialog.findViewById(R.id.passport_preview_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingsDialog.dismiss();
                }
            });
            ImageView iv = (ImageView) settingsDialog.findViewById(R.id.original_image);
            Picasso.with(getContext()).load(currentWorker.passportUpload).fit().centerCrop().into(iv);
            //settingsDialog.getWindow().setLayout(700, 700);
            settingsDialog.show();
        } else {
            DialogBuilder.showStandardDialog(getContext(), "",
                    getString(R.string.passport_nophoto));
        }
    }

    private void showChooserDialog() {
        CharSequence[] options = {getString(R.string.onboarding_take_photo),
                getString(R.string.onboarding_choose_from_gallery),
                getString(R.string.onboarding_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.onboarding_add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        dialog.cancel();
                        break;
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @OnClick({R.id.passport_photo, R.id.maximize, R.id.next})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.passport_photo:
                showChooserDialog();
                break;
            case R.id.maximize:
                showOriginalImage();
                break;
            case R.id.next:
                proceed();
                break;
        }
    }

    private void proceed() {
        if (getActivity() == null || !isAdded()) return;

        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
                passportPhoto.setImageBitmap(bitmap);
                uploadPicture(getActivity(), bitmap);
            } else if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri));
                passportPhoto.setImageBitmap(imageBitmap);
                uploadPicture(getActivity(), imageBitmap);
            }
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }
                break;
            case REQUEST_PERMISSION_READ_STORAGE:
                if (grantResults.length > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchOpenGalleryIntent();
                }
                break;
        }
    }
}
