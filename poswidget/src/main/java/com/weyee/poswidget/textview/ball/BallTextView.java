package com.weyee.poswidget.textview.ball;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.blankj.utilcode.util.SizeUtils;
import com.weyee.poswidget.R;

/**
 * 球形的TextView，border的颜色和TextView的颜色一致
 *
 * @author wuqi by 2019/5/26.
 */
public class BallTextView extends View {
    private Paint mTextPaint, mStrokePaint, mBackgroundPaint;
    private int textColor, strokeColor, backgroundColor;
    private float textSize, strokeSize;
    private String text;
    private RectF mInnerRectF;
    private int mViewSize; // View的宽度

    public BallTextView(Context context) {
        this(context, null);
    }

    public BallTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BallTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallTextView);

        try {
            strokeSize = ta.getDimensionPixelSize(R.styleable.BallTextView_strokeSize, 1);
            strokeColor = ta.getColor(R.styleable.BallTextView_strokeColor, getResources().getColor(R.color.config_color_driver));
            textColor = ta.getColor(R.styleable.BallTextView_android_textColor, Color.parseColor("#333333"));
            textSize = ta.getDimension(R.styleable.BallTextView_android_textSize, SizeUtils.dp2px(14f));
            text = ta.getString(R.styleable.BallTextView_android_text);
            backgroundColor = ta.getColor(R.styleable.BallTextView_android_background, Color.TRANSPARENT);
        } finally {
            ta.recycle();
        }
        // 强制该View的背景为透明的
        //setBackground(null);
        //setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Text Paint
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setLinearText(true);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);

        // Stroke Paint
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setColor(strokeColor);
        mStrokePaint.setStrokeWidth(strokeSize);
        mStrokePaint.setStyle(Paint.Style.STROKE);

        //Background Paint
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(backgroundColor);

        mInnerRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 计算View的宽度
        mViewSize = resolveSize(0, widthMeasureSpec);

        // 必须保证宽度和长度一致，不然画出来的圆有问题
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mInnerRectF.set(0, 0, mViewSize, mViewSize);
        mInnerRectF.offset((getWidth() - mViewSize) / 2, (getHeight() - mViewSize) / 2);

        final int halfBorder = (int) (mStrokePaint.getStrokeWidth() / 2f + 0.5f);

        mInnerRectF.inset(halfBorder, halfBorder);

        float centerX = mInnerRectF.centerX();
        float centerY = mInnerRectF.centerY();

        canvas.drawCircle(centerX, centerY, mViewSize / 2 + 0.5f - mStrokePaint.getStrokeWidth(), mBackgroundPaint);

        canvas.drawOval(mInnerRectF, mStrokePaint);

        int xPos = (int) centerX;
        int yPos = (int) (centerY - (mTextPaint.descent() + mTextPaint.ascent()) / 2);

        canvas.drawText(text == null ? "" : text, xPos, yPos, mTextPaint);
    }

    private void invalidateBackgroundPaints() {
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
    }

    private void invalidateStrokePaints() {
        mStrokePaint.setColor(strokeColor);
        mStrokePaint.setStrokeWidth(strokeSize);
        invalidate();
    }

    private void invalidateTextPaints() {
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    /**
     * Gets the subtitle string attribute value.
     *
     * @return The subtitle string attribute value.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the view's subtitle string attribute value.
     *
     * @param text The example string attribute value to use.
     */
    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    /**
     * Gets the stroke color attribute value.
     *
     * @return The stroke color attribute value.
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * Sets the view's stroke color attribute value.
     *
     * @param strokeColor The stroke color attribute value to use.
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidateStrokePaints();
    }

    /**
     * Gets the background color attribute value.
     *
     * @return The background color attribute value.
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the view's background color attribute value.
     *
     * @param backgroundColor The background color attribute value to use.
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidateBackgroundPaints();
    }

    /**
     * Gets the stroke width dimension attribute value.
     *
     * @return The stroke width dimension attribute value.
     */
    public float getStrokeWidth() {
        return strokeSize;
    }

    /**
     * Sets the view's stroke width attribute value.
     *
     * @param strokeWidth The stroke width attribute value to use.
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeSize = strokeWidth;
        invalidate();
    }

    /**
     * Gets the title size dimension attribute value.
     *
     * @return The title size dimension attribute value.
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * Sets the view's title size dimension attribute value.
     *
     * @param textSize The title size dimension attribute value to use.
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        invalidateTextPaints();
    }

    /**
     * Gets the title text color attribute value.
     *
     * @return The text color attribute value.
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * Sets the view's title text color attribute value.
     *
     * @param textColor The title text color attribute value to use.
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidateTextPaints();
    }
}
