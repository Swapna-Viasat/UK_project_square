package construction.thesquare.shared.start;

import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import construction.thesquare.R;

/**
 * Created by maizaga on 18/9/16.
 */
public class ActionBarViewHolder {
    @BindView(R.id.actionbar_back_button) ImageButton backButton;
    @BindView(R.id.actionbar_add_button) ImageButton addButton;
    @BindView(R.id.actionbar_activity_indicator_layout) LinearLayout indicatorsLayout;

    private View parent;
    private List<View> indicators = new ArrayList<>();

    public ActionBarViewHolder(View parent, int indicatorsCount) {
        this.parent = parent;
        ButterKnife.bind(this, parent);
        createCountIndicators(indicatorsCount);
        setSelectedIndicator(0);
        DrawableCompat.setTint(addButton.getDrawable(), ContextCompat.getColor(parent.getContext(), R.color.redSquareColor));
        disableBackButton();
        disableAddButton();
    }

    private void createCountIndicators(int indicatorsCount) {
        for (int i=0; i<indicatorsCount; i++) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_indicator, null, false);
            indicatorsLayout.addView(v);
            indicators.add(v);
        }
    }

    public void setSelectedIndicator(int currentIndicator) {
        for (int i=0; i<indicators.size(); i++) {
            View v = indicators.get(i);
            ImageView indicatorView = ButterKnife.findById(v, R.id.indicator);
            if (i<=currentIndicator) {
                indicatorView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.rounded_filled_circle));
            } else {
                indicatorView.setImageDrawable(ContextCompat.getDrawable(parent.getContext(), R.drawable.rounded_bordered_circle));
            }
        }
    }

    public void enableBackButton(View.OnClickListener listener) {
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(listener);
    }

    public void disableBackButton() {
        backButton.setVisibility(View.INVISIBLE);
    }

    public void enableAddButton(View.OnClickListener listener) {
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(listener);
    }

    public void disableAddButton() {
        addButton.setVisibility(View.INVISIBLE);
    }
}
