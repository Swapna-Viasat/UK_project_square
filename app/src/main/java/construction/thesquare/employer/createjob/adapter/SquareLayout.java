package construction.thesquare.employer.createjob.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by gherg on 12/6/2016.
 */

public class SquareLayout extends FrameLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int width, int height) {
        // note we are applying the width value as the height
        super.onMeasure(width, width);
    }

}
