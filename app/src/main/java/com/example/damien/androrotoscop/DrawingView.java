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
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.example.damien.androrotoscop.R;

/**
 * View used to draw
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

    /** Initialise attributes of the drawingView
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        isPen=true;
        path=new Path();
        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        canvas=new Canvas(bitmap);
        isLigne=false;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        this.changeToolWidth(1);
    }

    /** Set the size of the drawingView
     *
     * @param w : width
     * @param h : heigth
     */
    protected void setSize(int w, int h) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        c.drawBitmap(bitmap, 0, 0, paint);
        if(isPen) {
            c.drawPath(path, paint);
        }
        canvas.drawPath(path, paint);
    }

    // Previous position
    private float mX, mY;

    // Distance tolerance
    private static final float TOUCH_TOLERANCE = 4;

    /** Begin to touch
     *
     * @param x
     * @param y
     */
    private void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /** On move touch
     *
     * @param x
     * @param y
     */
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

    /** On stop touching
     *
     */
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

    /** Override onTouchEvent to do the drawing
     *
     * @param event
     * @return
     */
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

    /** Change the color and set pen as tool
     *
     * @param color
     */
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
        paint.setColor(0x00000000);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setAlpha(0x00);
    }

    /** Change the image used
     *
     * @param b
     */
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

    /** Modify the size of the tool
     *
     * @param size
     */
    public void changeToolWidth(int size){
        DisplayMetrics dm = getResources().getDisplayMetrics() ;
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, dm);
        paint.setStrokeWidth(strokeWidth);
    }
}
