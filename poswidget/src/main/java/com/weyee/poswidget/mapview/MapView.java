package com.weyee.poswidget.mapview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.weyee.poswidget.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author wuqi by 2019-11-30.
 */
public class MapView extends View {
    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Context mContext;

    private int raw;

    private Paint mPaint;

    private String[] colorArray = new String[]{"#FF239BD7", "#FF30A9E5", "#FF80CBF1", "#FFFFFFFF"};


    private void init(Context context, @Nullable AttributeSet attrs) {
        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MapView);
        raw = ta.getResourceId(R.styleable.MapView_svg, 0);
        ta.recycle();

        loadSVGThread.start();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private List<ProvinceItem> itemList = new ArrayList<>();

    private Thread loadSVGThread = new Thread() {
        @Override
        public void run() {
            super.run();

            //Dom 解析 SVG文件

            InputStream inputStream = mContext.getResources().openRawResource(raw);


            DocumentBuilderFactory facotory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = facotory.newDocumentBuilder();

                Document doc = builder.parse(inputStream);

                Element rootElement = doc.getDocumentElement();
                NodeList items = rootElement.getElementsByTagName("path");

                List<ProvinceItem> list = new ArrayList<>();

                float left = -1;
                float top = -1;
                float right = -1;
                float bottom = -1;


                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String pathData = element.getAttribute("android:pathData");
                    String fillColor = element.getAttribute("android:fillColor");
                    String strokeColor = element.getAttribute("android:strokeColor");
                    String strokeWidth = element.getAttribute("android:strokeWidth");
                    String text = element.getAttribute("android:text");

                    @SuppressLint("RestrictedApi")
                    Path path = PathParser.createPathFromPathData(pathData);

                    //将Path转化成矩形
                    RectF rectF = new RectF();
                    path.computeBounds(rectF, true);

                    left = left == -1 ? rectF.left : Math.min(left, rectF.left);
                    top = top == -1 ? rectF.top : Math.min(top, rectF.top);
                    right = right == -1 ? rectF.right : Math.max(right, rectF.right);
                    bottom = bottom == -1 ? rectF.bottom : Math.max(bottom, rectF.bottom);

                    ProvinceItem item = new ProvinceItem(path);
                    item.setContent(text);
                    item.setStrokeColor(Color.parseColor(strokeColor));
                    item.setFillColor(Color.parseColor(fillColor));
                    item.setStrokeWidth(SizeUtils.dp2px(Float.valueOf(strokeWidth)));
                    list.add(item);
                }

                //float left, float top, float right, float bottom
                totalRect = new RectF(left, top, right, bottom);

                itemList = list;

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    requestLayout();
                    invalidate();
                });

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private ProvinceItem select;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (itemList == null) return;
        canvas.save();
        canvas.scale(scale, scale);
        for (ProvinceItem provinceItem : itemList) {
            if (select == provinceItem) {
                provinceItem.drawItem(canvas, mPaint, true);
            } else {
                provinceItem.drawItem(canvas, mPaint, false);
            }
        }

    }

    private RectF totalRect;
    private float scale = 1.0f;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //当前控件高宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (totalRect != null) {
            float mapWidth = totalRect.width();
            scale = width / mapWidth;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX() / scale, event.getY() / scale);
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        if (itemList == null) return;
        ProvinceItem selectItem = null;
        for (ProvinceItem provideItem : itemList) {
            if (provideItem.isTouch(x, y)) {
                selectItem = provideItem;
            }
        }

        if (selectItem != null) {
            select = selectItem;
            postInvalidate();

            ToastUtils.showShort(select.content);
        }
    }

    public static class ProvinceItem {

        private Path path;
        private int fillColor;
        private int strokeColor;
        private int strokeWidth;
        private String content;

        public ProvinceItem(Path path) {
            this.path = path;
        }

        public ProvinceItem(String content,Path path, int fillColor, int strokeColor, int strokeWidth) {
            this.content = content;
            this.path = path;
            this.fillColor = fillColor;
            this.strokeColor = strokeColor;
            this.strokeWidth = strokeWidth;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public int getStrokeColor() {
            return strokeColor;
        }

        public void setStrokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
        }

        public int getFillColor() {
            return fillColor;
        }

        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }

        public int getStrokeWidth() {
            return strokeWidth;
        }

        public void setStrokeWidth(int strokeWidth) {
            this.strokeWidth = strokeWidth;
        }

        public void drawItem(Canvas canvas, Paint paint, boolean isSelect) {

            if (isSelect) {
                paint.clearShadowLayer();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                canvas.drawPath(path, paint);

                paint.clearShadowLayer();
                paint.setStrokeWidth(strokeWidth);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(strokeColor);
                canvas.drawPath(path, paint);

            } else {

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(fillColor);
                paint.setShadowLayer(8, 0, 0, 0xffffff);
                canvas.drawPath(path, paint);

                paint.clearShadowLayer();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(strokeColor);
                paint.setStrokeWidth(strokeWidth);
                canvas.drawPath(path, paint);

            }
        }

        public boolean isTouch(float x, float y) {

            Region region = new Region();
            //将Path转化为RectF矩形
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

            return region.contains((int) x, (int) y);
        }
    }
}
