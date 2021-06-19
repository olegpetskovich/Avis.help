package com.pinus.alexdev.avis.Helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;

public class IconTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Context context;

    public IconTextView(Context context) {
        super(context);
        this.context = context;
        createView();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        createView();
    }

    private void createView(){
        setGravity(Gravity.CENTER);
        Typeface font = Typeface.createFromAsset(
                getContext().getAssets(),
                "fontawesome.otf");
        setTypeface(font);
    }
}