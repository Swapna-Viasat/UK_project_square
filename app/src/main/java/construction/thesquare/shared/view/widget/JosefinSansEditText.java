package construction.thesquare.shared.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;

import construction.thesquare.R;

/**
 * Created by maizaga on 3/10/16.
 */

public class JosefinSansEditText extends AppCompatEditText {

    public JosefinSansEditText(Context context) {
        super(context);
    }

    public JosefinSansEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JosefinSansEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.JosefinSans);
            String fontName = getFontName(typedArray.getInt(R.styleable.JosefinSans_josefin_style, -1));
            typedArray.recycle();
            setFontFamily(fontName);
        }
    }

    public void setFontFamily(String fontName) {
        if(TextUtils.isEmpty(fontName)) {
            fontName = "JosefinSans-SemiBold.ttf";
        }

        if (!isInEditMode()) {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
            if (font != null) {
                setTypeface(font);
            }
        }
    }

    private String getFontName(int value) {
        String name;
        switch (value) {
            case 3:
                name = "JosefinSans-Bold.ttf";
                break;
            case 4:
                name = "JosefinSans-Italic.ttf";
                break;
            case 5:
                name = "JosefinSans-Light.ttf";
                break;
            default:
                name = "JosefinSans-SemiBold.ttf";
        }

        return name;
    }
}
