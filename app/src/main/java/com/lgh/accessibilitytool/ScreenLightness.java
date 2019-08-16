package com.lgh.accessibilitytool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

public class ScreenLightness {
    private static final String ARGB = "argb";
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private ImageView imageView;
    private SharedPreferences sharedPreferences;
    private int argb;

    public ScreenLightness(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        argb = sharedPreferences.getInt(ARGB, 0x00000000);
    }

    public void showFloat() {
        if (imageView != null) return;
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.format = PixelFormat.TRANSPARENT;
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.FILL_VERTICAL;
        params.alpha = 1f;
        imageView = new ImageView(context);
        imageView.setBackgroundColor(argb);
        windowManager.addView(imageView, params);
    }

    public void dismiss() {
        if (imageView != null) {
            windowManager.removeViewImmediate(imageView);
            params = null;
            imageView = null;
        }
    }

    public void showControlDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.screen_lightness_set, null);
        SeekBar seekBar_R = view.findViewById(R.id.seekBar_R);
        SeekBar seekBar_G = view.findViewById(R.id.seekBar_G);
        SeekBar seekBar_B = view.findViewById(R.id.seekBar_B);
        SeekBar seekBar_A = view.findViewById(R.id.seekBar_A);
        ScreenLightness.HandleSeekBar handleSeekBar = new ScreenLightness.HandleSeekBar();
        seekBar_R.setOnSeekBarChangeListener(handleSeekBar);
        seekBar_G.setOnSeekBarChangeListener(handleSeekBar);
        seekBar_B.setOnSeekBarChangeListener(handleSeekBar);
        seekBar_A.setOnSeekBarChangeListener(handleSeekBar);
        seekBar_R.setProgress(Color.red(argb));
        seekBar_G.setProgress(Color.green(argb));
        seekBar_B.setProgress(Color.blue(argb));
        seekBar_A.setProgress(Color.alpha(argb));
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sharedPreferences.edit().putInt(ARGB, argb).apply();
            }
        }).create();
        Window win = dialog.getWindow();
        win.setBackgroundDrawableResource(R.drawable.dialogbackground);
        win.setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY);
        WindowManager.LayoutParams params = win.getAttributes();
        win.setDimAmount(0);
        dialog.show();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        params.width = (metrics.widthPixels / 6) * 5;
        params.height = metrics.heightPixels / 2;
        win.setAttributes(params);
    }

    class HandleSeekBar implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBar_R:
                    argb = (argb & 0xff00ffff) | progress << 16;
                    break;
                case R.id.seekBar_G:
                    argb = (argb & 0xffff00ff) | progress << 8;
                    break;
                case R.id.seekBar_B:
                    argb = (argb & 0xffffff00) | progress;
                    break;
                case R.id.seekBar_A:
                    argb = (argb & 0x00ffffff) | progress << 24;
                    break;
            }
            if (imageView != null) {
                imageView.setBackgroundColor(argb);
                windowManager.updateViewLayout(imageView, params);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}