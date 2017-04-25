package construction.thesquare.shared.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import construction.thesquare.R;

/**
 * Created by maizaga on 13/11/16.
 */

public class GraftrsCounterView extends RelativeLayout {

    public interface CounterEvents {
        void onNumberChanged(GraftrsCounterView view);
    }

    @BindView(R.id.counter_bordered)
    ImageView bordered;
    @BindView(R.id.counter_filled)
    ImageView filled;
    @BindView(R.id.counter)
    EditText counter;

    private int count;
    private boolean circleShape;
    private CounterEvents listener;

    public GraftrsCounterView(Context context) {
        super(context);
        init(null);
    }

    public GraftrsCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GraftrsCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_counter, this);
        ButterKnife.bind(this, this);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.GraftrsCounterView);
            circleShape = typedArray.getInt(R.styleable.GraftrsCounterView_shape, 0) > 0;
            count = typedArray.getInt(R.styleable.GraftrsCounterView_count, 0);
            typedArray.recycle();
        } else {
            count = 0;
            circleShape = false;
        }

        updateView();
    }

    private void updateView() {
        if (circleShape) {
            bordered.setImageResource(R.drawable.rounded_bordered_circle_thin);
            filled.setImageResource(R.drawable.rounded_filled_circle);
        } else {
            bordered.setImageResource(R.drawable.square_bordered_thin);
            filled.setImageResource(R.drawable.square_filled);
        }

        if (count > 0) {
            counter.setText(String.format(Locale.getDefault(), "%d", count));
            fill(true);
        } else {
            counter.setText("");
            fill(false);
        }
    }

    private void fill(boolean fill) {
        filled.setVisibility(fill ? VISIBLE : GONE);
        bordered.setVisibility(fill ? GONE : VISIBLE);
    }

    public void setCircleShape(boolean circleShape) {
        this.circleShape = circleShape;
        updateView();
    }

    public void setCount(int count) {
        this.count = count;
        updateView();
    }

    @Override
    public void setEnabled(boolean enabled) {
        counter.setInputType(enabled ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_NULL);
        counter.setEnabled(enabled);
    }

    public int getCount() {
        return count;
    }

    @OnTextChanged(value = R.id.counter, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onCounterValueChanged(Editable text) {
        fill(!TextUtils.isEmpty(text));
    }

    @OnEditorAction(R.id.counter)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            counter.clearFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(counter.getWindowToken(), 0);

            String text = counter.getText().toString();
            int count = 0;
            if (!TextUtils.isEmpty(text)) {
                count = Integer.parseInt(text);
            }
            this.count = count;
            updateView(); // If 0, then clear TextView and show it empty

            if (listener != null) listener.onNumberChanged(this);

            return true;
        }

        return false;
    }

    public void setListener(CounterEvents listener) {
        this.listener = listener;
    }
}
