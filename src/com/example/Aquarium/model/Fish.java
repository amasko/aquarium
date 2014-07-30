package com.example.Aquarium.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Fish {
    private static final String TAG = Fish.class.getSimpleName() ;
    private Bitmap bitmap;
    private int x;
    private int y;
    private int size;
    private int radius;
    private boolean isPredator;
    private boolean touched;	// if fish is touched
    private Rect rect;         //rectangle with bitmap borders coordinates to find intersections
    private Speed speed;	// the speed with its directions

    public Fish(Bitmap bitmap, boolean isPredator, int size, int x, int y) {
        this.bitmap = bitmap;
        this.radius = bitmap.getWidth() / 2;
        this.isPredator = isPredator;
        this.size = size;
        this.x = x;
        this.y = y;
        this.speed = new Speed(1.5f * (6 - size));
//        this.rect = new Rect(x - bitmap.getWidth() / 2,
//                y - bitmap.getHeight() / 2,
//                x + bitmap.getWidth() / 2,
//                y + bitmap.getHeight() / 2);
//        Log.e(TAG, " size: " + size +
//                " ; predator: " + isPredator +
//                " ; speedX: " + (int) Math.ceil(speed.getXv()) +
//                " ; speedY: " + (int) Math.ceil(speed.getYv()));
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public Rect getRect() { return rect; }

    public int getSize() { return size; }

    public int getRadius() {
        return this.radius;
    }

    public boolean isTouched() {
        return touched;
    }

    public boolean isPredator() { return isPredator; }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - radius, y - radius, null);
    }

    /**
     * Method updates the fish's internal state every tick
     */
    public void update() {
        if (!touched) {
            x += (Math.ceil(speed.getXv()) * speed.getxDirection());
            y += (Math.ceil(speed.getYv()) * speed.getyDirection());
//            rect.set(x - bitmap.getWidth() / 2,
//                    y - bitmap.getHeight() / 2,
//                    x + bitmap.getWidth() / 2,
//                    y + bitmap.getHeight() / 2);
        }
    }

    /**
     * determines whether inscribed circles overlap, which means that fishes meet
     *
     * @return circles overlap
     */
    public static boolean overlapCircles(Fish f1, Fish f2) {
        int distX = f1.getX() - f2.getX();
        int distY = f1.getY() - f2.getY();
        //distance between centers squared
        int distSquared = distX * distX + distY * distY;
        int radiusSum = f1.getRadius() + f2.getRadius();
        return distSquared <= radiusSum * radiusSum;
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventX >= (x - radius) && (eventX <= (x + radius))) {
            if (eventY >= (y - radius) && (y <= (y + radius))) {
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }

    }
}
