package com.weyee.sdk.util.roundlayout;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.weyee.sdk.util.Tools;

/**
 * @author wuqi by 2019-07-12.
 */
public class RoundFrameLayout extends FrameLayout {
    private final RectF roundRect = new RectF();
    private float rect_radius = 6;
    private final Paint maskPaint = new Paint();
    private final Paint zonePaint = new Paint();


    public RoundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundFrameLayout(Context context) {
        super(context);
        init();
    }


    private void init() {
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //
        zonePaint.setAntiAlias(true);
        zonePaint.setColor(Color.WHITE);
        //
        rect_radius = Tools.dp2px(5);
    }

    public void setRectRadius(float radius) {
        rect_radius = radius;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int w = getWidth();
        int h = getHeight();
        roundRect.set(0, 0, w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.saveLayer(roundRect, zonePaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRoundRect(roundRect, rect_radius, rect_radius, zonePaint);
        //
        canvas.saveLayer(roundRect, maskPaint, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);
        canvas.restore();
    }
}
