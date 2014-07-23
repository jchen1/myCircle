package f.myCircle.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by jeff on 7/23/14.
 */
public class CircularDrawable extends BitmapDrawable {
    private float mCircleRadius;
    private final RectF mBackgroundRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mPaint;

    CircularDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds)
    {
        super.onBoundsChange(bounds);
        mBackgroundRect.set(bounds);
        mCircleRadius = Math.min(bounds.width() / 2, bounds.height() / 2);
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawCircle(mBackgroundRect.width() / 2, mBackgroundRect.height() / 2, mCircleRadius, mPaint);
    }

    @Override
    public int getOpacity()
    {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha)
    {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf)
    {
        mPaint.setColorFilter(cf);
    }
}
