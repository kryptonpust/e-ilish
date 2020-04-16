package com.makovsky.badgemenucreator.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Denis Makovskyi
 */

public final class Utils {

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNonNull(Object object) {
        return object != null;
    }

    public static boolean isNonNullEmpty(String str) {
        return !TextUtils.isEmpty(str);
    }

    public static void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void escapeView(View v) {
        v.setVisibility(View.GONE);
    }

    public static int getDisplayWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
