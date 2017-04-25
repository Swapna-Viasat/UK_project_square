package construction.thesquare.shared.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.util.AttributeSet;

import construction.thesquare.R;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2016 FusionWorks. All rights reserved.
 */

public class StrikeJosefinSansTextView extends JosefinSansTextView {

    private Paint paint;
    private boolean addStrike = false;
    private Rect rect;

    public StrikeJosefinSansTextView(Context context) {
        super(context);
        init(context);
    }

    public StrikeJosefinSansTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StrikeJosefinSansTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        rect = new Rect();
        paint.setColor(ContextCompat.getColor(context, R.color.redSquareColor));
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (addStrike) {
            int count = getLineCount();
            final Layout layout = getLayout();
            float xStart, xStop, xDiff;
            int firstCharInLine, lastCharInLine;
            float strokeWidth = getResources().getDisplayMetrics().density * 1;

            for (int i = 0; i < count; i++) {
                getLineBounds(i, rect);
                firstCharInLine = layout.getLineStart(i);
                lastCharInLine = layout.getLineEnd(i);

                xStart = layout.getPrimaryHorizontal(firstCharInLine);
                xDiff = layout.getPrimaryHorizontal(firstCharInLine + 1) - xStart;
                xStop = layout.getPrimaryHorizontal(lastCharInLine - 1) + xDiff;

                canvas.drawLine(xStart, rect.centerY() + strokeWidth, xStop, rect.centerY() + strokeWidth, paint);
            }
        }
        super.onDraw(canvas);
    }

    public void setStrikeVisibility(boolean addStrike) {
        this.addStrike = addStrike;
        invalidate();
    }
}
