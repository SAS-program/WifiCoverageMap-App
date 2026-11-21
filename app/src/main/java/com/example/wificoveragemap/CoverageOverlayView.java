package com.example.wificoveragemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CoverageOverlayView extends View {

    public static class Point {
        public float x, y;
        public int rssi;

        public Point(float x, float y, int rssi) {
            this.x = x;
            this.y = y;
            this.rssi = rssi;
        }
    }

    private List<Point> points = new ArrayList<>();
    private Paint heatPaint = new Paint();
    private Paint dotPaint = new Paint();
    private float tapX = -1, tapY = -1;

    public CoverageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dotPaint.setColor(Color.BLUE);
        dotPaint.setStyle(Paint.Style.FILL);
        heatPaint.setStyle(Paint.Style.FILL);
    }

    public void addPoint(float x, float y, int rssi) {
        points.add(new Point(x, y, rssi));
        invalidate();
    }

    public void clearPoints() {
        points.clear();
        invalidate();
    }

    public float getTapX() { return tapX; }
    public float getTapY() { return tapY; }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            tapX = event.getX();
            tapY = event.getY();
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point p : points) {
            int alpha = 120;
            int color;
            if (p.rssi >= -50) color = Color.rgb(0, 200, 0);
            else if (p.rssi >= -65) color = Color.rgb(150, 220, 0);
            else if (p.rssi >= -75) color = Color.rgb(255, 200, 0);
            else if (p.rssi >= -85) color = Color.rgb(255, 120, 0);
            else color = Color.rgb(200, 0, 0);

            heatPaint.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
            canvas.drawCircle(p.x, p.y, 80, heatPaint);

            dotPaint.setColor(Color.BLUE);
            canvas.drawCircle(p.x, p.y, 8, dotPaint);
        }

        if (tapX >= 0 && tapY >= 0) {
            Paint tapPaint = new Paint();
            tapPaint.setColor(Color.MAGENTA);
            tapPaint.setStyle(Paint.Style.STROKE);
            tapPaint.setStrokeWidth(4);
            canvas.drawCircle(tapX, tapY, 20, tapPaint);
        }
    }
}