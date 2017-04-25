package construction.thesquare.shared.settings.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.ContactCategory;
import construction.thesquare.shared.models.StatusMessageResponse;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.shared.utils.MediaTools;
import construction.thesquare.shared.utils.TextTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Evgheni on 11/17/2016.
 */

public class SettingsContactFragment extends Fragment {

    public static final String TAG = "SettingsContact";

    @BindView(R.id.image1)
    ImageView imageView1;
    @BindView(R.id.image2)
    ImageView imageView2;
    @BindView(R.id.image3)
    ImageView imageView3;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.messageInput)
    EditText messageInput;

    private static final int CAMERA_PIC_ONE = 234;
    private static final int CAMERA_PIC_TWO = 435;
    private static final int CAMERA_PIC_THREE = 923;

    private static final int GALLERY_PIC_ONE = 235;
    private static final int GALLERY_PIC_TWO = 436;
    private static final int GALLERY_PIC_THREE = 924;

    private static final int REQUEST_PERMISSION_READ_STORAGE = 4;

    private List<ContactCategory> contactCategories;
    private String image1, image2, image3;

    public static SettingsContactFragment newInstance() {
        return new SettingsContactFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_contact, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchContactCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.employer_settings_contact));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().setTitle(getString(R.string.settings));
    }

    private void fetchContactCategories() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchContactCategories()
                .enqueue(new Callback<ResponseObject<List<ContactCategory>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<ContactCategory>>> call,
                                           Response<ResponseObject<List<ContactCategory>>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                contactCategories = response.body().getResponse();
                                spinner.setAdapter(new ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_spinner_dropdown_item, getCategories()));
                            } catch (Exception e) {
                                CrashLogHelper.logException(e);
                            }
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<ContactCategory>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private List<String> getCategories() {
        List<String> result = new ArrayList<>();
        result.add("What is the nature of your enquiry?");

        if (!CollectionUtils.isEmpty(contactCategories))
            for (ContactCategory c : contactCategories) {
                result.add(c.name);
            }
        return result;
    }

    private void sendMessage() {
        HashMap<String, String> payload = new HashMap<>();
        int id;
        if (SharedPreferencesManager.getInstance(getContext()).getWorkerId() > 0)
            id = SharedPreferencesManager.getInstance(getContext()).getWorkerId();
        else id = SharedPreferencesManager.getInstance(getContext()).getEmployerId();

        payload.put("user_id", String.valueOf(id));
        if (!CollectionUtils.isEmpty(contactCategories)) {
            payload.put("category_id", String.valueOf(contactCategories.get(spinner.getSelectedItemPosition() - 1).id));
        }
        payload.put("message", messageInput.getText().toString());
        payload.put("image_1", image1);
        payload.put("image_2", image2);
        payload.put("image_3", image3);

        callApi(payload);
    }

    private boolean validateFields() {
        boolean result = true;
        if (TextUtils.isEmpty(messageInput.getText().toString())) {
            messageInput.setError("Please enter your message");
            result = false;
        } else if (spinner.getSelectedItemPosition() < 1) {
            DialogBuilder.showStandardDialog(getContext(), "", "Please select nature of your enquiry");
            result = false;
        }
        return result;
    }

    private void callApi(HashMap<String, String> body) {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .postContactMessage(body)
                .enqueue(new Callback<StatusMessageResponse>() {
                    @Override
                    public void onResponse(Call<StatusMessageResponse> call,
                                           Response<StatusMessageResponse> response) {
                        if (response.isSuccessful()) {
                            //
                            DialogBuilder.cancelDialog(dialog);

                            DialogBuilder.showStandardDialog(getContext(), getString(R.string.contact_us_thanks), getString(R.string.contact_us_thanks_message),  new DialogBuilder.OnClickStandardDialog() {
                                @Override
                                public void onOKClickStandardDialog(Context context) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            });
                        } else {
                            HandleErrors.parseError(getContext(), dialog, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusMessageResponse> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    @OnClick({R.id.img1, R.id.img2, R.id.img3, R.id.send})
    public void toggles(View view) {
        switch (view.getId()) {
            case R.id.img1:
                openChooserDialog(1);
                break;
            case R.id.img2:
                openChooserDialog(2);
                break;
            case R.id.img3:
                openChooserDialog(3);
                break;
            case R.id.send:
                if (validateFields()) sendMessage();
                break;
        }
    }

    private void openChooserDialog(final int code) {
        CharSequence[] options = {getString(R.string.onboarding_take_photo),
                getString(R.string.onboarding_choose_from_gallery),
                getString(R.string.onboarding_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.onboarding_add_photo));

        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        takePicture(code);
                        break;
                    case 1:
                        openGallery(code);
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

    private void takePicture(int n) {
        int code = CAMERA_PIC_ONE;
        switch (n) {
            case 1:
                code = CAMERA_PIC_ONE;
                break;
            case 2:
                code = CAMERA_PIC_TWO;
                break;
            case 3:
                code = CAMERA_PIC_THREE;
                break;
        }

        TextTools.log(TAG, "taking picture");
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            pictureIntent(code);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, n);
        }
    }

    private void openGallery(int n) {
        int code = GALLERY_PIC_ONE;
        switch (n) {
            case 1:
                code = GALLERY_PIC_ONE;
                break;
            case 2:
                code = GALLERY_PIC_TWO;
                break;
            case 3:
                code = GALLERY_PIC_THREE;
                break;
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            dispatchOpenGalleryIntent(code);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
        }
    }

    private void dispatchOpenGalleryIntent(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.onboarding_select_image)),
                code);
    }

    private void pictureIntent(int n) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, n);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_PIC_ONE:
                    Bundle extras1 = data.getExtras();
                    Bitmap bitmap1 = (Bitmap) extras1.get("data");
                    imageView1.setImageBitmap(bitmap1);
                    image1 = MediaTools.encodeToBase64(bitmap1);
                    break;
                case CAMERA_PIC_TWO:
                    Bundle extras2 = data.getExtras();
                    Bitmap bitmap2 = (Bitmap) extras2.get("data");
                    imageView2.setImageBitmap(bitmap2);
                    image2 = MediaTools.encodeToBase64(bitmap2);
                    break;
                case CAMERA_PIC_THREE:
                    Bundle extras3 = data.getExtras();
                    Bitmap bitmap3 = (Bitmap) extras3.get("data");
                    imageView3.setImageBitmap(bitmap3);
                    image3 = MediaTools.encodeToBase64(bitmap3);
                    break;
                case GALLERY_PIC_ONE:
                    Uri imageUri1 = data.getData();
                    Bitmap imageBitmap1 = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri1));
                    imageView1.setImageBitmap(imageBitmap1);
                    image1 = MediaTools.encodeToBase64(imageBitmap1);
                    break;
                case GALLERY_PIC_TWO:
                    Uri imageUri2 = data.getData();
                    Bitmap imageBitmap2 = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri2));
                    imageView2.setImageBitmap(imageBitmap2);
                    image2 = MediaTools.encodeToBase64(imageBitmap2);
                    break;
                case GALLERY_PIC_THREE:
                    Uri imageUri3 = data.getData();
                    Bitmap imageBitmap3 = BitmapFactory.decodeFile(MediaTools.getPath(getActivity(), imageUri3));
                    imageView3.setImageBitmap(imageBitmap3);
                    image3 = MediaTools.encodeToBase64(imageBitmap3);
                    break;
            }
        }
    }
}
