package com.bcmobileappdevelopment.votidea.HelperClass;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

public class IconTextView extends AppCompatTextView {
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
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "Font Awesome 5 Free-Solid-900.otf");
        setTypeface(tf);
    }
}
