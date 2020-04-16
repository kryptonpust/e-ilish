package com.makovsky.badgemenucreator.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.makovsky.badgemenucreator.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by Denis Makovskyi
 */

public class BadgeTextView extends AppCompatTextView {

    private static final int KEY_SHADOW_COLOR = 0x55000000;
    private static final int FILL_SHADOW_COLOR = 0x55000000;

    private static final float SHADOW_RADIUS = 3.5f;
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;

    private boolean isHighLightMode;

    private int backgroundColor;
    private int shadowRadius;
    private int shadowYOffset;
    private int shadowXOffset;

    private int basePadding;
    private int diffWidthHeight;

    private float density;

    public BadgeTextView(final Context context) {
        this(context, null);
    }

    public BadgeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setGravity(Gravity.CENTER);

        density = getContext().getResources().getDisplayMetrics().density;
        shadowRadius = (int) (density * SHADOW_RADIUS);
        shadowYOffset = (int) (density * Y_OFFSET);
        shadowXOffset = (int) (density * X_OFFSET);
        basePadding = (shadowRadius * 2);

        float textHeight = getTextSize();
        float textWidth = textHeight / 4;
        diffWidthHeight = (int) (Math.abs(textHeight - textWidth) / 2);

        int horizontalPadding = basePadding + diffWidthHeight;
        setPadding(horizontalPadding, basePadding, horizontalPadding, basePadding);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeTextView);
        backgroundColor = typedArray.getColor(R.styleable.BadgeTextView_android_background, Color.WHITE);
        typedArray.recycle();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        String changedText = text == null ? "" : text.toString().trim();
        if (isHighLightMode && !changedText.equals("")) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            setLayoutParams(layoutParams);
            isHighLightMode = false;
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        refreshBackgroundDrawable(width, height);
    }

    public void setBadgeCount(String count) {
        setBadgeCount(count, true);
    }

    public void setBadgeCount(String count, boolean isGoneWhenZero) {
        int value = -1;
        try {
            value = Integer.parseInt(count);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        }

        if (value != -1) {
            setBadgeCount(value, isGoneWhenZero);
        }
    }

    public void setBadgeCount(int count) {
        setBadgeCount(count, true);
    }

    @SuppressLint("SetTextI18n")
    public void setBadgeCount(int count, boolean isGoneWhenZero) {
        if (count > 0 && count <= 99) {
            setText(String.valueOf(count));
            setVisibility(View.VISIBLE);

        } else if (count > 99) {
            setText("99+");
            setVisibility(View.VISIBLE);

        } else if (count <= 0) {
            setText("0");
            if (isGoneWhenZero) {
                setVisibility(View.GONE);

            } else {
                setVisibility(View.VISIBLE);
            }
        }
    }

    public void setHighLightMode() {
        setHighLightMode(false);
    }

    public void setHighLightMode(boolean isDisplayInToolbarMenu) {
        isHighLightMode = true;
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = dpToPx(getContext(), 8);
        params.height = params.width;
        if (isDisplayInToolbarMenu && params instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams) params).topMargin = dpToPx(getContext(), 8);
            ((FrameLayout.LayoutParams) params).rightMargin = dpToPx(getContext(), 8);
        }
        setLayoutParams(params);

        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        setLayerType(View.LAYER_TYPE_SOFTWARE, drawable.getPaint());
        drawable.getPaint().setColor(backgroundColor);
        drawable.getPaint().setAntiAlias(true);

        setBackground(drawable);
        setText("");
        setVisibility(View.VISIBLE);
    }

    public void clearHighLightMode() {
        isHighLightMode = false;
        setBadgeCount(0);
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
        refreshBackgroundDrawable(getWidth(), getHeight());
    }

    private void refreshBackgroundDrawable(int targetWidth, int targetHeight) {
        if (targetWidth <= 0 || targetHeight <= 0) {
            return;
        }

        CharSequence text = getText();
        if (text == null) {
            return;
        }

        if (text.length() == 1) {
            int max = Math.max(targetWidth, targetHeight);
            final int diameter = max - (2 * shadowRadius);

            OvalShape oval = new OvalShadow(shadowRadius, diameter);
            ShapeDrawable circle = new ShapeDrawable(oval);
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(
                    shadowRadius, shadowXOffset,
                    shadowYOffset, KEY_SHADOW_COLOR);
            circle.getPaint().setColor(backgroundColor);
            setBackground(circle);

        } else if (text.length() > 1) {
            SemiCircleRectDrawable semiCircleRectDrawable = new SemiCircleRectDrawable();
            setLayerType(View.LAYER_TYPE_SOFTWARE, semiCircleRectDrawable.getPaint());
            semiCircleRectDrawable.getPaint().setShadowLayer(shadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);
            semiCircleRectDrawable.getPaint().setColor(backgroundColor);
            setBackground(semiCircleRectDrawable);
        }

    }

    private class OvalShadow extends OvalShape {
        private int circleDiameter;
        private Paint shadowPaint;
        private RadialGradient radialGradient;

        OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            BadgeTextView.this.shadowRadius = shadowRadius;
            this.circleDiameter = circleDiameter;
            this.shadowPaint = new Paint();
            this.radialGradient = new RadialGradient(
                    this.circleDiameter / 2, this.circleDiameter / 2,
                    BadgeTextView.this.shadowRadius, new int[]{FILL_SHADOW_COLOR, Color.TRANSPARENT},
                    null, Shader.TileMode.CLAMP);
            this.shadowPaint.setShader(radialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = BadgeTextView.this.getWidth();
            final int viewHeight = BadgeTextView.this.getHeight();
            canvas.drawCircle(
                    viewWidth / 2, viewHeight / 2,
                    (circleDiameter / 2 + shadowRadius), shadowPaint);
            canvas.drawCircle(
                    viewWidth / 2, viewHeight / 2,
                    (circleDiameter / 2), paint);
        }
    }

    class SemiCircleRectDrawable extends Drawable {
        private final Paint paint;
        private RectF rectF;

        SemiCircleRectDrawable() {
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);
            if (rectF == null) {
                rectF = new RectF(
                        left + diffWidthHeight, top + shadowRadius + 4,
                        right - diffWidthHeight, bottom - shadowRadius - 4);

            } else {
                rectF.set(
                        left + diffWidthHeight, top + shadowRadius + 4,
                        right - diffWidthHeight, bottom - shadowRadius - 4);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        Paint getPaint() {
            return paint;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            float radius = (float) (rectF.bottom * 0.4);
            if (rectF.right < rectF.bottom) {
                radius = (float) (rectF.right * 0.4);
            }
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
    }

    public static int dpToPx(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);

        } catch (Exception e) {
            return (int) (dpValue + 0.5f);
        }
    }
}
