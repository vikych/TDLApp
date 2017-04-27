package com.tsi.android.tdlapp.preference;

import android.content.Context;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skydoves.colorpickerview.ColorPickerView;
import com.tsi.android.tdlapp.R;

public class ColorPreference extends DialogPreference {

    private ColorPickerView mView;

    public ColorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(android.R.layout.preference_category, parent, false);
        TextView title = (TextView) view.findViewById(android.R.id.title);
        title.setText(R.string.select_color);
        return view;
    }

    @Override
    protected View onCreateDialogView() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.activity_settings_color, null, false);
        mView = (ColorPickerView) inflate.findViewById(R.id.colorPickerView);
        return inflate;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int[] rgb = mView.getColorRGB();// [0] - R, [1] - G, [2] - B
            int color = Color.rgb(rgb[0], rgb[1], rgb[2]);

            PreferencesManager.getInstance(getContext()).saveColorPreference("color", color);
        }
        super.onDialogClosed(positiveResult);
    }
}
