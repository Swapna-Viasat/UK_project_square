package construction.thesquare.shared.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextPaint;
import android.util.AttributeSet;

import construction.thesquare.R;

/**
 * Created by gherg on 12/2/2016.
 */

public class DayRateSeekBar extends AppCompatSeekBar {
    private int mThumbSize;
    private TextPaint mTextPaint;
    private double rate;

    public DayRateSeekBar(Context context) {
        this(context, null);
    }

    public DayRateSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public DayRateSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);
        mThumbSize = (int) getContext().getResources().getDisplayMetrics().density * 124;

        mTextPaint = new TextPaint();
        mTextPaint.setColor(ContextCompat.getColor(context, R.color.graySquareColor));
        //mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
        mTextPaint.setTextSize(getContext().getResources().getDisplayMetrics().density * 14);
        mTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSans-Bold.ttf"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rate = getProgress() * 5 + 30;
        String progressText = "£" + String.valueOf(getProgress() * 5 + 30) ;
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f
                - getContext().getResources().getDisplayMetrics().density * 24;


        if (getProgress() == 0 || getProgress() == 194) {
            //
        } else {
            canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
        }
    }

    public double getRate() {
        return rate;
    }

    public void setRate(int rate) {
        setProgress((rate - 30) / 5);
    }
}
