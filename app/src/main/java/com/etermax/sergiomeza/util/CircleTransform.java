package com.etermax.sergiomeza.util;

/**
 * Created by sergiomeza on 3/24/17.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;

import com.etermax.sergiomeza.R;
import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {

    Context mContext;
    Boolean hasBorder;

    public CircleTransform(Context mContext, Boolean hasBorder) {
        this.mContext = mContext;
        this.hasBorder = hasBorder;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size/2f;
        canvas.drawCircle(r, r, r, paint);

        if(hasBorder) {
            Paint paint1 = new Paint();
            paint1.setColor(ResourcesCompat.getColor(mContext.getResources(), R.color.colorPrimaryDark, null));
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setAntiAlias(true);
            paint1.setStrokeWidth(2);
            canvas.drawCircle(r, r, r, paint1);
        }

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}
