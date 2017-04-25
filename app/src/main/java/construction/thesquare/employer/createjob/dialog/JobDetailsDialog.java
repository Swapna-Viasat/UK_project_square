package construction.thesquare.employer.createjob.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;
import construction.thesquare.shared.utils.CrashLogHelper;

/**
 * Created by gherg on 1/20/17.
 */

public class JobDetailsDialog extends DialogFragment {

    public static final String TAG = "JobDetailsDialog";

    @BindView(R.id.job_details_input)
    EditText input;
    @BindView(R.id.title)
    TextView title;
    private DetailsListener listener;
    private String initialText;

    public interface DetailsListener {
        void onDone(String details, boolean cancel);
    }

    public static JobDetailsDialog newInstance(DetailsListener detailsListener,
                                               String initialText) {
        JobDetailsDialog dialog = new JobDetailsDialog();
        dialog.listener = detailsListener;
        dialog.initialText = initialText;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_job_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText("Job Details");
        if (null != initialText) {
            input.setText(initialText);
            input.setSelection(0, initialText.length());
        }

        try {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void hide() {
        try {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
        } catch (Exception e) {
            CrashLogHelper.logException(e);
        }
    }

    @OnClick({R.id.done, R.id.cancel})
    public void action(View view) {
        switch (view.getId()) {
            case R.id.done:
                hide();
                if (null != listener) {
                    listener.onDone(input.getText().toString(), false);
                }
                break;
            case R.id.cancel:
                hide();
                if (null != listener) {
                    listener.onDone(null, true);
                }
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
    }
}