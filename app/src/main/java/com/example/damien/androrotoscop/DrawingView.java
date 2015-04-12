package com.example.damien.androrotoscop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.damien.androrotoscop.R;

/**
 *
 */
public class DrawingView extends View {

    private boolean isPen;
    private boolean isLigne;

    private Paint paint;
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;

    public DrawingView(Context context) {
        super(context);
        init(null, 0);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        isPen=true;
        path=new Path();
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        isLigne=false;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12);
    }

    protected void setSize(int w, int h) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        if(isPen) {
            canvas.drawPath(path, paint);
        }
        canvas.drawPath(path, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;


    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        isLigne = true;
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        if(!isLigne){
            canvas.drawPoint(mX, mY, paint);
        }
        else {
            path.lineTo(mX, mY);
            canvas.drawPath(path, paint);
            path.reset();
        }
        isLigne = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public void setColor(int color) {
        isPen = true;
        paint.setXfermode(null);
        paint.setColor(color);
    }

    public void setPen(){
        isPen = true;
        paint.setXfermode(null);
    }

    public void setEraser(){
        isPen = false;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setBitmap(Bitmap b){
        Bitmap temp = Bitmap.createBitmap(b);
        temp = Bitmap.createScaledBitmap(b, canvas.getWidth(), canvas.getHeight(), false);
        bitmap = temp.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(bitmap);
        this.invalidate();
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void changeToolWidth(int size){
        paint.setStrokeWidth(size);
    }
}
