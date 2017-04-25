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

import java.util.Locale;

import construction.thesquare.R;

/**
 * Created by Vadim Goroshevsky
 * Copyright (c) 2017 FusionWorks. All rights reserved.
 */

public class CommuteTimeSeekBar extends AppCompatSeekBar {
    private int mThumbSize;
    private TextPaint mTextPaint;
    private int rate;
    private int minCommuteTime = 20;
    private Rect bounds;

    public CommuteTimeSeekBar(Context context) {
        this(context, null);
    }

    public CommuteTimeSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public CommuteTimeSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mThumbSize = (int) getContext().getResources().getDisplayMetrics().density * 124;

        mTextPaint = new TextPaint();
        mTextPaint.setColor(ContextCompat.getColor(context, R.color.graySquareColor));
        mTextPaint.setTextSize(getContext().getResources().getDisplayMetrics().density * 14);
        mTextPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/JosefinSans-Bold.ttf"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        bounds = new Rect();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rate = getProgress() + minCommuteTime;

        int hours = rate / 60;
        int minutes = rate % 60;
        String progressText;

        if (hours > 0) progressText = String.format(Locale.UK, "%dh%02dmin", hours, minutes);
        else progressText = String.format(Locale.UK, "%02dmin", minutes);

        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;

        float thumbY = getHeight() / 2f + bounds.height() / 2f - getContext().getResources().getDisplayMetrics().density * 15;

        if (rate > minCommuteTime && rate < 120) {
            canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
        }
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        setProgress(rate - minCommuteTime);
    }
}
