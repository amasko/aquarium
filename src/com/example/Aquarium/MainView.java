package com.example.Aquarium;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.Aquarium.model.Fish;
import com.example.Aquarium.model.FishUtils;
import com.example.Aquarium.model.Speed;
import com.example.Aquarium.model.SupplementThread;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = MainView.class.getSimpleName();
    private final Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.background_picture);
    Rect screenRect; //represents the area of the screen to draw background
    Paint paint;
    private MainThread thread;
    private SupplementThread supplementThread;
    private CopyOnWriteArrayList<Fish> list;
    private final Set<Fish> removeFishSet = new HashSet<Fish>();
    String avgFps;

    public MainView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenRect = new Rect(0, 0, getWidth(), getHeight());
        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setTextSize(30);
        //paint.setFilterBitmap(true);
        list = FishUtils.createPopulation(this);
        supplementThread = new SupplementThread(this, list);
        supplementThread.setRunning(true);
        supplementThread.start();
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        boolean retry = true;
        while (retry) {
            try {
                supplementThread.setRunning(false);
                thread.setRunning(false);
                supplementThread.join();
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "threads interrupted while join");
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (Fish fish : list) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // delegating event handling to the
                fish.handleActionDown((int) event.getX(), (int) event.getY());

                // check if in the lower part of the screen we exit
                if (event.getY() > getHeight() - 50) {
                    thread.setRunning(false);
                    ((Activity) getContext()).finish();
                } else {
                    Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
                }
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // gestures
                if (fish.isTouched()) {
                    // was picked up and is being dragged
                    fish.setX((int) event.getX());
                    fish.setY((int) event.getY());
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // touch was released
                if (fish.isTouched()) {
                    fish.setTouched(false);
                }
            }
        }
        return true;
    }



    public void setAvgFps(String avgFps) {
        this.avgFps = avgFps;
    }



    public void render(Canvas canvas) {
        canvas.drawBitmap(background, null, screenRect, paint); // stretches background img to the size of the screen
        for (Fish f : list) {
            f.draw(canvas);
        }
        //Log.w(TAG, "FPS: " + avgFps);
        if (avgFps != null) {
            canvas.drawText(avgFps, this.getWidth() - 150, 30, paint);
        }
    }

    private void eatenFish(Fish fish1, Fish fish2) {
        if (Rect.intersects(fish1.getRect(), fish2.getRect())) {
            if (fish1.isPredator() && fish2.isPredator() && fish1.getSize() > fish2.getSize() ||
                    fish1.isPredator() && !fish2.isPredator() && fish1.getSize() >= fish2.getSize() - 1) {
                Log.i(TAG, "!!! Some fish iterated is going to be eaten! fish: ispredator: " + fish1.isPredator() +
                        "; size: " + fish1.getSize() +
                        " ; f: ispredator: " + fish1.isPredator() +
                        "; size: " + fish2.getSize() + " end;");
                removeFishSet.add(fish2);
            } else if (fish1.isPredator() && fish2.isPredator() && fish1.getSize() < fish2.getSize() ||
                    !fish1.isPredator() && fish2.isPredator() && fish2.getSize() >= fish1.getSize() - 1) {
                Log.i(TAG, "!!! Current fish is going to be eaten! fish: ispredator: " + fish1.isPredator() +
                        "; size: " + fish1.getSize() +
                        " ; f: ispredator: " + fish2.isPredator() +
                        "; size: " + +fish2.getSize() + " end;");
                removeFishSet.add(fish1);
            }
        }
    }

    public void checkCollisions() {
        //List<Fish> checkList;       //list view for nested loop
        //ListIterator<Fish> listIterator = list.listIterator();
        int i = 0;
        for (Fish f : list) {
            for (int j = i + 1; j < list.size(); j++) {
                eatenFish(f, list.get(j));
            }
            i++;
        }
        //old algorithm
//        while (listIterator.hasNext() && i < list.size() && list.size() > 1) {
//            Fish fish = listIterator.next();
//            checkList = list.subList(i, list.size() - 1);
//              if (!checkList.isEmpty()) {
//                for (Fish f : checkList) {
//                    eatenFish(fish, f);
//                }
//            } else {
//                eatenFish(list.get(list.size() - 1), list.get(list.size() - 2));
//            }
//            i++;
//        }
        //old algo end
        if (!removeFishSet.isEmpty()) {
            list.removeAll(removeFishSet);
            long timeStamp = System.currentTimeMillis();
            FishUtils.creationScheduler.put(timeStamp, removeFishSet.size());
            Log.d(TAG, "adding to map: " + removeFishSet.size() + " elements, time: " + timeStamp);
            removeFishSet.clear();
        }
    }

    /**
     * Method iterates through all the objects, handles interactions with
     * screen borders and calls their update method
     */
    public void update() {
        for (Fish f : list) {
            // check collision with right wall if heading right
            if (f.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
                    && f.getX() + f.getBitmap().getWidth() / 2 >= getWidth()) {
                f.getSpeed().toggleXDirection();
            }
            // check collision with left wall if heading left
            if (f.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
                    && f.getX() - f.getBitmap().getWidth() / 2 <= 0) {
                f.getSpeed().toggleXDirection();
            }
            // check collision with bottom wall if heading down
            if (f.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
                    && f.getY() + f.getBitmap().getHeight() / 2 >= getHeight()) {
                f.getSpeed().toggleYDirection();
            }
            // check collision with top wall if heading up
            if (f.getSpeed().getyDirection() == Speed.DIRECTION_UP
                    && f.getY() - f.getBitmap().getHeight() / 2 <= 0) {
                f.getSpeed().toggleYDirection();
            }
            // Update fish position
            f.update();
        }
    }

    @Override
    public String toString() {
        return "Current view " + TAG + " not null";
    }

    /**
     * temporary wtf method
     */
    public final void threadsStop() {
        Log.w(TAG, "closing threads...!");
        supplementThread.setRunning(false);
        thread.setRunning(false);
        //boolean retry = true;
        try {
            supplementThread.join(100);
            thread.join(100);
//                Log.e(TAG, "checking if joined " + supplementThread.isAlive() + " ; " + thread.isAlive());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "threads interrupted while join");
        }
    }
}
