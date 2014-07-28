package com.example.Aquarium.model;

import android.util.Log;
import com.example.Aquarium.MainView;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SupplementThread extends Thread {
    private static final String TAG = SupplementThread.class.getSimpleName();
    private Map<Long, Integer> map = FishUtils.creationScheduler;
    private MainView view;
    private List<Fish> list;
    private volatile boolean running;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public SupplementThread(MainView view, List<Fish> list) {
        super();
        this.view = view;
        this.list = list;
    }

    @Override
    public void run(){
        while (running) {
            if (map.isEmpty()) {
                try {
                    Thread.sleep(25000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Supplier was interrupted!");
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "Map is not empty: size " + map.size());
                Iterator<Long> iterator = map.keySet().iterator(); //Navigable set iterator, ascending order
                while (iterator.hasNext()) {
                    long key = iterator.next();
                    long timeInterval = System.currentTimeMillis() - key;
                    if (timeInterval <= 0 || timeInterval >= 30000) {
                        Log.w(TAG, "!!Out of range timeInterval: " + timeInterval);
                        FishUtils.spawnNewFish(list, map.get(key), view);
                        iterator.remove();
                    } else {
                        try {
                            Thread.sleep(30000 - timeInterval);
                            Log.w(TAG, "Sleeping for: " + (30000 - timeInterval) + " ms;");
                        } catch (InterruptedException e) {
                            Log.e(TAG, " interrupted while sleeping < 30 sec! ");
//                            e.printStackTrace();
                        }
                        FishUtils.spawnNewFish(list, map.get(key), view);
                        iterator.remove();
                        Log.e(TAG, " Fish spawned! list size: " + list.size());
                    }
                }
            }
        }
    }
}
