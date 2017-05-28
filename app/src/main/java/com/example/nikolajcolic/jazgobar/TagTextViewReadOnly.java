package com.example.nikolajcolic.jazgobar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.LokacijaGoba;

/**
 * Created by nikolajcolic on 24/04/17.
 */

public class TagTextViewReadOnly extends android.support.v7.widget.AppCompatTextView {

    int padding_in_px;
    LokacijaGoba goba;
    boolean guiCheckState;

    public void updateObjectState() {
        goba.getGoba().setChecked(guiCheckState);
    }

    public TagTextViewReadOnly(Context context, LokacijaGoba goba, boolean clickable) {
        super(context);
        setClickable(clickable);
        setDefaultSettings();
        this.goba = goba;
        guiCheckState = goba.getGoba().isChecked();
        setCheck(guiCheckState);
        setText(goba.getGoba().getIme());
    }
    public void setDefaultSettings() {
        this.setTextColor(Color.WHITE);
        setTypeface(Typeface.DEFAULT_BOLD);
        int padding_in_dp = 6;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);

    }

    public void setCheck(boolean mc){
        guiCheckState = mc;
        setState();
    }
    private void setState() {
        if(guiCheckState) {
            this.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);
            //setElevation(padding_in_px);
        } else {
            this.setBackground(getResources().getDrawable(R.drawable.rounded_corner_gray));
            setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);
            //setElevation(0);
        }
    }
    public LokacijaGoba getTag() {
        return goba;
    }
    public TagTextViewReadOnly(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
    }

    public TagTextViewReadOnly(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
    }
}
