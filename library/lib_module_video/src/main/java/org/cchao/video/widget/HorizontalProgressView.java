package org.cchao.video.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.cchao.video.R;

/**
 * @author cchen6
 * @Date on 2020/1/10
 * @Description
 */
public class HorizontalProgressView extends View {

    private int width;
    private int height;

    private int horizontalColor;

    private int horizontalProgressColor;

    private boolean isRound = true;

    private Paint paint;
    private RectF rectF;

    private int progress = 0;

    public HorizontalProgressView(Context context) {
        this(context, null);
    }

    public HorizontalProgressView(Context context, AttributeSet atts) {
        this(context, atts, 0);
    }

    public HorizontalProgressView(Context context, AttributeSet atts, int defStyle) {
        super(context, atts, defStyle);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(atts, R.styleable.HorizontalProgressView, defStyle, 0);
        horizontalColor = typedArray.getColor(R.styleable.HorizontalProgressView_horizontalColor, Color.WHITE);
        horizontalProgressColor = typedArray.getColor(R.styleable.HorizontalProgressView_horizontalProgressColor, Color.TRANSPARENT);
        isRound = typedArray.getBoolean(R.styleable.HorizontalProgressView_horizontalRound, true);
        typedArray.recycle();
        paint = new Paint();
        rectF = new RectF(0, 0, width, height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        rectF.set(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStrokeWidth(height);

        rectF.set(0, 0, width, height);
        paint.setColor(horizontalColor);
        if (isRound) {
            canvas.drawRoundRect(rectF, height / 2, height / 2, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }

        paint.setColor(horizontalProgressColor);
        int degree = progress * width / 100;
        rectF.set(0, 0, degree, height);
        if (isRound) {
            canvas.drawRoundRect(rectF, height / 2, height / 2, paint);
        } else {
            canvas.drawRect(rectF, paint);
        }
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("进度值不能小于0");
        }
        if (progress > 100) {
            progress = 100;
        }
        if (progress <= 100) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getHorizontalColor() {
        return horizontalColor;
    }

    public void setHorizontalColor(int horizontalColor) {
        this.horizontalColor = horizontalColor;
    }

    public int getHorizontalProgressColor() {
        return horizontalProgressColor;
    }

    public void setHorizontalProgressColor(int horizontalProgressColor) {
        this.horizontalProgressColor = horizontalProgressColor;
    }
}