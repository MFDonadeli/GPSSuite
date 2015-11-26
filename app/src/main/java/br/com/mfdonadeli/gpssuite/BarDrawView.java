package br.com.mfdonadeli.gpssuite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mfdonadeli on 10/23/15.
 */
public class BarDrawView extends View {

    private int size, color;
    private String text;

    public BarDrawView(Context context){
        super(context);
    }

    public BarDrawView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void setSize(int size) {
        this.size = size;

        invalidate();
        requestLayout();
    }

    public void setColor(int color) {
        this.color = color;

        invalidate();
        requestLayout();
    }

    public void setText(String text) {
        this.text = text;

        invalidate();
        requestLayout();
    }

    protected void onDraw(Canvas canvas)
    {
        Paint myPaint = new Paint();
        myPaint.setColor(color);
        //myPaint.setStyle(Paint.Style.STROKE);
        //myPaint.setStrokeWidth(3);
        canvas.drawRect(10, 10, size*5, 100, myPaint); //ltrb
        myPaint.setColor(Color.BLACK);
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(R.dimen.small_font_size);
        canvas.drawText(text, (size*5)/2, 100, myPaint);
    }
}


