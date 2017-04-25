/*
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 The Square Tech. All rights reserved.
 */

package construction.thesquare.worker.myaccount.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.data.HttpRestServiceConsumer;
import construction.thesquare.shared.data.model.ResponseObject;
import construction.thesquare.shared.data.persistence.SharedPreferencesManager;
import construction.thesquare.shared.models.EnglishLevel;
import construction.thesquare.shared.models.Language;
import construction.thesquare.shared.models.Nationality;
import construction.thesquare.shared.models.Worker;
import construction.thesquare.shared.utils.CollectionUtils;
import construction.thesquare.shared.utils.CrashLogHelper;
import construction.thesquare.shared.utils.DialogBuilder;
import construction.thesquare.shared.utils.HandleErrors;
import construction.thesquare.worker.onboarding.OnLanguagesSelectedListener;
import construction.thesquare.worker.onboarding.adapter.FluencyAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditNationalityDialog extends DialogFragment implements FluencyAdapter.FluencyListener,
        OnLanguagesSelectedListener {
    private static final String TAG = "EditNationalityDialog";

    @BindView(R.id.spinner_nationality)
    Spinner spinnerNationality;
    @BindView(R.id.english)
    RecyclerView fluency;
    @BindView(R.id.openDialog)
    View openDialog;
    @BindView(R.id.lang)
    TextView languagesTextView;

    private List<String> selectedLanguages = new ArrayList<>();
    private ArrayAdapter nationalityAdapter;
    private List<Nationality> nationalities;
    private Map<String, Integer> countryIds = new HashMap<>();
    private Worker currentWorker;
    private FluencyAdapter fluencyAdapter;
    private List<EnglishLevel> levels = new ArrayList<>();
    private ChangeDetailsListener listener;
    private int english;
    private List<Language> fetchedLanguages;

    public interface ChangeDetailsListener {
        void onDataChanged();
    }

    public static EditNationalityDialog newInstance(ChangeDetailsListener listener) {
        EditNationalityDialog dialog = new EditNationalityDialog();
        dialog.setListener(listener);
        return dialog;
    }

    private void setListener(ChangeDetailsListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit_worker_nationality, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                if (english > 0) {
                    patchWorker();
                    break;
                } else
                    DialogBuilder.showStandardDialog(getContext(), "",
                            getString(R.string.onboarding_english_level_error));
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes((WindowManager.LayoutParams) params);
        }
        super.onResume();

        fetchCurrentWorker();
        fetchNationality();
        fetchEnglishLevels();
        fetchLanguages();
    }

    private void fetchCurrentWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .meWorker()
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        if (response.isSuccessful()) {
                            DialogBuilder.cancelDialog(dialog);
                            try {
                                currentWorker = response.body().getResponse();
                                populateLanguages();
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

    private void fetchNationality() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchNationality()
                .enqueue(new Callback<ResponseObject<List<Nationality>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Nationality>>> call,
                                           Response<ResponseObject<List<Nationality>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processNationality(response.body().getResponse());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Nationality>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });

    }

    private void processNationality(List<Nationality> nationalityList) {
        nationalities = nationalityList;
        if (nationalityList != null) {
            List<String> countrynames = new ArrayList<String>();
            try {
                countrynames.add("Please select");
                for (Nationality country : nationalityList) {
                    countrynames.add(country.name);
                    countryIds.put(country.name, country.id);
                }
                nationalityAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, countrynames);
                nationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerNationality.setAdapter(nationalityAdapter);

                populateNationality();
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    private void fetchEnglishLevels() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HttpRestServiceConsumer.getBaseApiClient()
                .fetchEnglishLevels()
                .enqueue(new Callback<ResponseObject<List<EnglishLevel>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<EnglishLevel>>> call,
                                           Response<ResponseObject<List<EnglishLevel>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            processEnglishLevels(response.body().getResponse());
                            populateSavedEnglishLevel();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<EnglishLevel>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void fetchLanguages() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());
        HttpRestServiceConsumer.getBaseApiClient()
                .fetchLanguage()
                .enqueue(new Callback<ResponseObject<List<Language>>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<List<Language>>> call,
                                           Response<ResponseObject<List<Language>>> response) {

                        DialogBuilder.cancelDialog(dialog);

                        if (response.isSuccessful() && response.body().getResponse() != null) {
                            showLanguagesSelectDialog(response.body().getResponse());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseObject<List<Language>>> call, Throwable t) {
                        HandleErrors.parseFailureError(getContext(), dialog, t);
                    }
                });
    }

    private void processEnglishLevels(List<EnglishLevel> englishLevels) {
        if (englishLevels != null) {
            try {
                levels.clear();
                levels.addAll(englishLevels);
                fluencyAdapter = new FluencyAdapter(levels);
                fluencyAdapter.setListener(this);
                fluency.setLayoutManager(new LinearLayoutManager(getContext()));
                fluency.setAdapter(fluencyAdapter);
            } catch (Exception e) {
                CrashLogHelper.logException(e);
            }
        }
    }

    @Override
    public void onFluency(EnglishLevel level) {
        for (EnglishLevel e : levels) {
            if (e.id != level.id) {
                e.selected = false;
            }
        }
        english = level.id;
        level.selected = true;
        fluencyAdapter.notifyDataSetChanged();
    }


    private void patchWorker() {
        final Dialog dialog = DialogBuilder.showCustomDialog(getContext());

        HashMap<String, Object> request = new HashMap<>();
        request.put("nationality_id", countryIds.get(spinnerNationality.getSelectedItem()));
        request.put("english_level_id", english);
        request.put("languages_ids", getLanguagesIds());

        HttpRestServiceConsumer.getBaseApiClient()
                .patchWorker(SharedPreferencesManager.getInstance(getContext()).getWorkerId(), request)
                .enqueue(new Callback<ResponseObject<Worker>>() {
                    @Override
                    public void onResponse(Call<ResponseObject<Worker>> call,
                                           Response<ResponseObject<Worker>> response) {
                        DialogBuilder.cancelDialog(dialog);
                        if (response.isSuccessful()) {
                            if (listener != null) listener.onDataChanged();
                            dismiss();
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

    private void populateNationality() {
        if (currentWorker != null && currentWorker.nationality != null && nationalities != null) {
            for (Nationality nationality : nationalities) {
                if (nationality.id == currentWorker.nationality.id) {
                    spinnerNationality.setSelection(nationalities.indexOf(nationality) + 1);
                }
            }
        }
    }

    private void populateSavedEnglishLevel() {
        if (currentWorker != null && currentWorker.englishLevel != null) {
            for (EnglishLevel level : levels) {
                if (currentWorker.englishLevel.id == level.id) {
                    level.selected = true;
                    english = level.id;
                }
            }
        } else {
            for (EnglishLevel level : levels) {
                if (level != null && level.id == 1) { // 1 = Basic
                    level.selected = true;
                    english = 1;
                }
            }
        }
        fluencyAdapter.notifyDataSetChanged();
    }

    private void showLanguagesSelectDialog(List<Language> languageList) {
        if (getActivity() == null || !isAdded()) return;

        fetchedLanguages = languageList;

        List<String> languageNames = new ArrayList<>();
        for (Language language : languageList) languageNames.add(language.name);

        final CharSequence[] dialogList = languageNames.toArray(new CharSequence[languageNames.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getContext());
        builderDialog.setTitle(getString(R.string.onboarding_select_language));

        openDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageSelectDialog(dialogList);
            }
        });
    }

    private void openLanguageSelectDialog(CharSequence[] dialogList) {
        if (getContext() != null)
            DialogBuilder.showMultiSelectDialog(getContext(), dialogList, this);
    }

    @Override
    public void onLanguagesSelected(List<String> selectedLangs) {
        languagesTextView.setText(TextUtils.join(", ", selectedLangs));
        selectedLanguages = selectedLangs;
    }

    private List<Integer> getLanguagesIds() {
        List<Integer> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(selectedLanguages) && !CollectionUtils.isEmpty(fetchedLanguages)) {
            for (String language : selectedLanguages) {
                for (Language language1 : fetchedLanguages) {
                    if (TextUtils.equals(language, language1.name))
                        result.add(language1.id);
                }
            }
        }
        return result;
    }

    private void populateLanguages() {
        selectedLanguages = new ArrayList<>();
        if (currentWorker != null && !CollectionUtils.isEmpty(currentWorker.languages)) {
            for (Language language : currentWorker.languages) {
                selectedLanguages.add(language.name);
            }
        }
        languagesTextView.setText(TextUtils.join(", ", selectedLanguages));
    }
}
