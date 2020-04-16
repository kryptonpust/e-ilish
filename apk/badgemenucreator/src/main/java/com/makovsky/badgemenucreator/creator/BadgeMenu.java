package com.makovsky.badgemenucreator.creator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.PopupMenu;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.makovsky.badgemenucreator.R;
import com.makovsky.badgemenucreator.utils.Utils;
import com.makovsky.badgemenucreator.views.BadgeTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denis Makovskyi
 */

public class BadgeMenu {

    public static class Builder {

        private boolean isShowPopupMenu;
        private boolean isShowPopupWindow;

        private int menuTitleSize;
        private int menuTitleStyle;
        private int menuTitleColor;
        private int menuIconTintColor;
        private int badgeCount;
        private int badgeTextColor;
        private int badgeBackgroundColor;
        private int menuRes;

        private String menuTitleText;
        private Drawable menuIconDrawable;

        private PopupMenuListener popupMenuListener;
        private PopupWindowListener popupWindowListener;

        public Builder() {
            menuTitleSize = 10;
            menuTitleStyle = Typeface.NORMAL;
            menuTitleColor = Color.WHITE;
            badgeTextColor = Color.WHITE;
            badgeBackgroundColor = Color.RED;
        }

        public Builder menuTitleSize(int menuTitleSize) {
            this.menuTitleSize = menuTitleSize;
            return this;
        }

        public Builder menuTitleStyle(int menuTitleStyle) {
            this.menuTitleStyle = menuTitleStyle;
            return this;
        }

        public Builder menuTitleColor(int menuTitleColor) {
            this.menuTitleColor = menuTitleColor;
            return this;
        }

        public Builder menuIconTintColor(int iconTintColor) {
            this.menuIconTintColor = iconTintColor;
            return this;
        }

        public Builder badgeCount(int badgeCount) {
            this.badgeCount = badgeCount;
            return this;
        }

        public Builder badgeTextColor(int textColor) {
            this.badgeTextColor = textColor;
            return this;
        }

        public Builder badgeBackgroundColor(int textBackgroundColor) {
            this.badgeBackgroundColor = textBackgroundColor;
            return this;
        }

        public Builder menuTitleText(String text) {
            this.menuTitleText = text;
            return this;
        }

        public Builder menuIconDrawable(Drawable iconDrawable) {
            this.menuIconDrawable = iconDrawable;
            return this;
        }

        public Builder popupMenu(@MenuRes int menuRes, PopupMenuListener popupMenuListener) {
            this.isShowPopupMenu = true;
            this.isShowPopupWindow = false;
            this.menuRes = menuRes;
            this.popupMenuListener = popupMenuListener;
            return this;
        }

        public Builder popupWindow(@MenuRes int menuRes, PopupWindowListener popupWindowListener) {
            this.isShowPopupMenu = false;
            this.isShowPopupWindow = true;
            this.menuRes = menuRes;
            this.popupWindowListener = popupWindowListener;
            return this;
        }

        boolean isShowPopupMenu() {
            return isShowPopupMenu;
        }

        boolean isShowPopupWindow() {
            return isShowPopupWindow;
        }

        int getMenuTitleSize() {
            return menuTitleSize;
        }

        int getMenuTitleStyle() {
            return menuTitleStyle;
        }

        int getMenuTitleColor() {
            return menuTitleColor;
        }

        int getMenuIconTintColor() {
            return menuIconTintColor;
        }

        int getBadgeCount() {
            return badgeCount;
        }

        int getBadgeTextColor() {
            return badgeTextColor;
        }

        int getBadgeBackgroundColor() {
            return badgeBackgroundColor;
        }

        @MenuRes
        int getMenuRes() {
            return menuRes;
        }

        String getMenuTitleText() {
            return menuTitleText;
        }

        Drawable getMenuIconDrawable() {
            return menuIconDrawable;
        }

        PopupMenuListener getPopupMenuListener() {
            return popupMenuListener;
        }

        PopupWindowListener getPopupWindowListener() {
            return popupWindowListener;
        }
    }

    public static void update(final Activity activity, @NonNull final MenuItem menuItem, @NonNull BadgeMenu.Builder builder) {
        update(activity, menuItem, null, builder);
    }

    public static void update(final Activity activity, @NonNull final View actionView, @NonNull BadgeMenu.Builder builder) {
        update(activity, null, actionView, builder);
    }

    private static void update(@NonNull final Activity activity, final MenuItem menuItem, final View actionView, @NonNull final BadgeMenu.Builder builder) {
        final FrameLayout badgeLayout = Utils.isNonNull(menuItem)
                ? (FrameLayout) menuItem.getActionView()
                : Utils.isNonNull(actionView)
                ? (FrameLayout) actionView
                : null;
        if (badgeLayout == null) {
            return;
        }

        final TextView vMenuTitle = badgeLayout.findViewById(R.id.menu_item_title);
        final ImageView vMenuIcon = badgeLayout.findViewById(R.id.menu_item_icon);
        final BadgeTextView vBadge = badgeLayout.findViewById(R.id.menu_item_badge);

        if (Utils.isNonNullEmpty(builder.getMenuTitleText())) {
            vMenuTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, builder.getMenuTitleSize());
            vMenuTitle.setTypeface(null, builder.getMenuTitleStyle());
            vMenuTitle.setTextColor(builder.getMenuTitleColor());
            vMenuTitle.setText(builder.getMenuTitleText());

            Utils.showView(vMenuTitle);

        } else {
            Utils.escapeView(vMenuTitle);
        }

        if (Utils.isNonNull(builder.getMenuIconDrawable())) {
            if (builder.getMenuIconTintColor() != Color.TRANSPARENT) {
                vMenuIcon.setColorFilter(builder.getMenuIconTintColor());
            }
            vMenuIcon.setImageDrawable(builder.getMenuIconDrawable());

            Utils.showView(vMenuIcon);

        } else {
            Utils.escapeView(vMenuIcon);
        }

        if (builder.getBadgeCount() > 0) {
            if (builder.getBadgeBackgroundColor() != Color.TRANSPARENT) {
                vBadge.setBackgroundColor(builder.getBadgeBackgroundColor());
            }

            if (builder.getBadgeTextColor() != Color.TRANSPARENT) {
                vBadge.setTextColor(builder.getBadgeTextColor());
            }
            vBadge.setBadgeCount(builder.getBadgeCount(), true);

            Utils.showView(vBadge);

        } else {
            Utils.escapeView(vBadge);
        }

        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!builder.isShowPopupMenu() && !builder.isShowPopupWindow() && Utils.isNonNull(menuItem)) {
                    activity.onMenuItemSelected(Window.FEATURE_OPTIONS_PANEL, menuItem);

                } else if (builder.isShowPopupMenu()) {
                    createPopupMenu(
                            activity, view,
                            builder.getMenuRes(), builder.getPopupMenuListener());

                } else if (builder.isShowPopupWindow()) {
                    createPopupWindow(
                            activity, view,
                            builder.getMenuRes(), builder.getPopupWindowListener());
                }
            }
        });

        if (Utils.isNonNull(menuItem)) {
            menuItem.setVisible(true);
        }
    }

    private static void createPopupMenu(@NonNull Activity activity, @NonNull View targetView, @MenuRes int menuRes, final PopupMenuListener popUpMenuListener) {
        PopupMenu popupMenu = makePopupMenu(activity, targetView, menuRes);
        if (Utils.isNonNull(popUpMenuListener)) {
            popUpMenuListener.onSubmenuInflated(popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    popUpMenuListener.onMenuItemClicked(item);
                    return false;
                }
            });
        }
        popupMenu.show();
    }

    private static void createPopupWindow(@NonNull Activity activity, @NonNull View targetView, @MenuRes int menuRes, PopupWindowListener popupWindowListener) {
        Pair<PopupWindow, LinearLayout> popupWindow = makePopupWindow(activity);
        List<View> actionViews = extractActionViews(makePopupMenu(activity, targetView, menuRes).getMenu(), popupWindowListener);
        attachListenersToActionViews(popupWindow, actionViews, popupWindowListener);
        prepareAndShow(activity, popupWindow.first, actionViews, targetView);
    }

    private static PopupMenu makePopupMenu(@NonNull Activity activity, @NonNull View targetView, @MenuRes int menuRes) {
        PopupMenu popupMenu = new PopupMenu(activity, targetView);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        return popupMenu;
    }

    private static Pair<PopupWindow, LinearLayout> makePopupWindow(@NonNull Activity activity) {
        @SuppressLint("InflateParams") View popupView = LayoutInflater.from(activity).inflate(R.layout.layout_popup_window, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        LinearLayout windowContainer = popupView.findViewById(R.id.menu_popup_window_container);
        return new Pair<>(popupWindow, windowContainer);
    }

    private static List<View> extractActionViews(@NonNull Menu menu, PopupWindowListener listener) {
        List<View> actionViews = null;
        for (int i = 0; i < menu.size(); i++) {
            View actionView = menu.getItem(i).getActionView();
            actionView.setId(menu.getItem(i).getItemId());
            actionView.setTag(menu.getItem(i).getTitle());

            if (Utils.isNonNull(listener)) {
                listener.onItemViewInflated(actionView);
            }

            if (Utils.isNull(actionViews)) {
                actionViews = new ArrayList<>(Collections.singletonList(actionView));

            } else {
                actionViews.add(actionView);
            }
        }

        return actionViews;
    }

    private static void attachListenersToActionViews(@NonNull final Pair<PopupWindow, LinearLayout> popupWindow, @NonNull List<View> actionViews, final PopupWindowListener listener) {
        for (final View actionView : actionViews) {
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow.first.isShowing()) {
                        popupWindow.first.dismiss();
                    }
                    if (Utils.isNonNull(listener)) {
                        listener.onItemViewClicked(actionView.getId());
                    }
                }
            });
            popupWindow.second.addView(actionView);
        }
        popupWindow.second.requestLayout();
    }

    private static void prepareAndShow(@NonNull Activity activity, @NonNull PopupWindow popupWindow, @NonNull List<View> actionViews, @NonNull View targetView) {
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(activity, R.color.material_grey_200)));
        popupWindow.setWidth(measureActionViews(activity, targetView, actionViews));
        popupWindow.showAsDropDown(targetView);
    }

    private static int measureActionViews(@NonNull Activity activity, @NonNull View targetView, @NonNull List<View> actionViews) {
        final int maxAllowedWidth = Math.max(Utils.getDisplayWidth(activity) / 2, activity.getResources().getDimensionPixelSize(R.dimen.dialog_pref_width));
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int maxWidth = 0;

        for (View actionView : actionViews) {
            actionView.measure(widthMeasureSpec, heightMeasureSpec);
            final int actionViewWidth = actionView.getMeasuredWidth();
            if (actionViewWidth >= maxAllowedWidth) {
                return maxAllowedWidth;

            } else if (actionViewWidth > maxWidth) {
                maxWidth = actionViewWidth;
            }
        }

        if (maxWidth + maxWidth * 50 / 100 <= maxAllowedWidth) {
            maxWidth = maxWidth + maxWidth * 50 / 100;
        }

        if (maxWidth < maxAllowedWidth) {
            int[] targetLocation = new int[2];
            targetView.getLocationOnScreen(targetLocation);
            int optimalWidth = Utils.getDisplayWidth(activity) - targetLocation[0];
            if (optimalWidth <= maxAllowedWidth && targetLocation[0] + optimalWidth <= Utils.getDisplayWidth(activity)) {
                return optimalWidth;
            }
        }

        return maxWidth;
    }
}
