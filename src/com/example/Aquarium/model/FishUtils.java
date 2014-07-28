package com.example.Aquarium.model;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.Aquarium.MainView;
import com.example.Aquarium.R;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FishUtils {

    private static final Map<Fishes, Bitmap> pictureMap = new HashMap<Fishes, Bitmap>();
    public static Random random = new Random();
    public static final Map<Long, Integer> creationScheduler = new ConcurrentSkipListMap<Long, Integer>();

    private FishUtils() {

    }

    public enum Fishes {
        PREDATOR1(true, 1),
        PREDATOR2(true, 2),
        PREDATOR3(true, 3),
        PREDATOR4(true, 4),
        PREDATOR5(true, 5),
        SIMPLE1(false, 1),
        SIMPLE2(false, 2),
        SIMPLE3(false, 3),
        SIMPLE4(false, 4),
        SIMPLE5(false, 5);
        private boolean isPredator;
        private int size;

        Fishes(boolean isPredator, int size) {
            this.isPredator = isPredator;
            this.size = size;
        }

        public boolean isPredator() {
            return isPredator;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "fish mask: size " + size + " . " + isPredator;
        }
    }

    private static void fillMap( MainView view) {
        pictureMap.put(Fishes.PREDATOR1, BitmapFactory.decodeResource(view.getResources(), R.drawable.black10));
        pictureMap.put(Fishes.PREDATOR2, BitmapFactory.decodeResource(view.getResources(), R.drawable.black20));
        pictureMap.put(Fishes.PREDATOR3, BitmapFactory.decodeResource(view.getResources(), R.drawable.black30));
        pictureMap.put(Fishes.PREDATOR4, BitmapFactory.decodeResource(view.getResources(), R.drawable.black40));
        pictureMap.put(Fishes.PREDATOR5, BitmapFactory.decodeResource(view.getResources(), R.drawable.black50));
        pictureMap.put(Fishes.SIMPLE1, BitmapFactory.decodeResource(view.getResources(), R.drawable.white10));
        pictureMap.put(Fishes.SIMPLE2, BitmapFactory.decodeResource(view.getResources(), R.drawable.white20));
        pictureMap.put(Fishes.SIMPLE3, BitmapFactory.decodeResource(view.getResources(), R.drawable.white30));
        pictureMap.put(Fishes.SIMPLE4, BitmapFactory.decodeResource(view.getResources(), R.drawable.white40));
        pictureMap.put(Fishes.SIMPLE5, BitmapFactory.decodeResource(view.getResources(), R.drawable.white50));
    }

    public static CopyOnWriteArrayList<Fish> createPopulation(MainView view) {
        fillMap(view);
        List<Fishes> temp = new ArrayList<Fishes>(pictureMap.keySet());
        int size = temp.size();
        CopyOnWriteArrayList<Fish> list = new CopyOnWriteArrayList<Fish>();
        for (Fishes aTemp : temp) {
            int r = random.nextInt(size);
            Fishes fishes = temp.get(r);
            list.add(new Fish(pictureMap.get(fishes),
                    fishes.isPredator,
                    fishes.getSize(),
                    rndXpos(view.getWidth()),
                    rndYpos(view.getHeight())));
        }
        return list;
    }

    public static void spawnNewFish(List<Fish> lst, int quantity, MainView view) {
        List<Fishes> temp = new ArrayList<Fishes>(pictureMap.keySet());
        int size = temp.size();
        for (int i = 0; i < quantity; i++) {
            int r = random.nextInt(size);
            Fishes fishes = temp.get(r);
            lst.add(new Fish(pictureMap.get(fishes),
                    fishes.isPredator(),
                    fishes.getSize(),
                    rndXpos(view.getWidth()),
                    rndYpos(view.getHeight())));
        }
    }

    private static int rndXpos(int w) {
        return random.nextInt(w);
    }

    private static int rndYpos(int h) {
        return random.nextInt(h);
    }
}
