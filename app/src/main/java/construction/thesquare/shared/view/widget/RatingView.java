package construction.thesquare.shared.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import construction.thesquare.R;

/**
 * Created by maizaga on 16/10/16.
 */

public class RatingView extends LinearLayout {

    @BindViews({R.id.worker_my_account_star_1, R.id.worker_my_account_star_2, R.id.worker_my_account_star_3, R.id.worker_my_account_star_4, R.id.worker_my_account_star_5})
    List<ImageView> starsList;

    private int value;

    public RatingView(Context context) {
        super(context);
        init(null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setRating(int rating) {
        this.value = (0 <= rating && rating <=5) ? rating : 0;
        updateUI();
    }

    public void makeStarsRed() {
        for (ImageView star : starsList) {
            star.setColorFilter(ContextCompat.getColor(getContext(), R.color.redSquareColor));
        }
        updateUI();
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_five_stars, this);
        ButterKnife.bind(this, this);
        value = 0;
        updateUI();
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RatingView);
            final Boolean value = typedArray.getInt(R.styleable.RatingView_editable, 0) != 0;
            typedArray.recycle();
            ButterKnife.apply(starsList, new ButterKnife.Action<ImageView>() {
                @Override
                public void apply(@NonNull ImageView view, int index) {
                    view.setClickable(value);
                }
            });
        }
    }

    @OnClick({R.id.worker_my_account_star_1, R.id.worker_my_account_star_2, R.id.worker_my_account_star_3, R.id.worker_my_account_star_4, R.id.worker_my_account_star_5})
    void onStarClicked(ImageView v) {
        value = Integer.parseInt((String)v.getTag());
        updateUI();
    }

    private void updateUI() {
        ButterKnife.apply(starsList, new ButterKnife.Action<ImageView>() {
            @Override
            public void apply(@NonNull ImageView view, int index) {
                int currentTag = Integer.parseInt((String)view.getTag());
                if (currentTag <= value) {
                    view.setImageResource(R.drawable.ic_star_black_24dp);
                } else {
                    view.setImageResource(R.drawable.ic_star_border_black_24dp);
                }
            }
        });
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
