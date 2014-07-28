package com.example.Aquarium.model;

import android.util.Log;

import java.util.Random;

public class Speed {
    public static final int DIRECTION_RIGHT	= 1;
    public static final int DIRECTION_LEFT	= -1;
    public static final int DIRECTION_UP	= -1;
    public static final int DIRECTION_DOWN	= 1;

    private float xv;	// X velocity
    private float yv;	// Y velocity
    private float spd;
    private Random random = FishUtils.random;
    private int xDirection = random.nextBoolean() ? DIRECTION_RIGHT : DIRECTION_LEFT;
    private int yDirection = random.nextBoolean() ? DIRECTION_DOWN : DIRECTION_UP;

    public Speed(float spd) {     //cannot be less then 1 pixel per tick
        float angle = generateAngle();
        this.spd = spd;
        this.xv = (float) Math.sin(angle) * spd;
        this.yv = (float) Math.cos(angle) * spd;
    }

    private float generateAngle() {
        return (float) (Math.PI / 10) * (random.nextInt(4) + 1);
    }

    public float getXv() {
        return xv;
    }
    public float getYv() {
        return yv;
    }
    public int getxDirection() {
        return xDirection;
    }
    public int getyDirection() {
        return yDirection;
    }

    // changes the direction on the X axis
    public void toggleXDirection() {
        xDirection = xDirection * -1;
        yDirection = random.nextBoolean() ? DIRECTION_DOWN : DIRECTION_UP;
        float angle = generateAngle();
        xv = (float) Math.sin(angle) * spd;
        yv = (float) Math.cos(angle) * spd;
    }
    // changes the direction on the Y axis
    public void toggleYDirection() {
        yDirection = yDirection * -1;
        xDirection = random.nextBoolean() ? DIRECTION_RIGHT : DIRECTION_LEFT;
        float angle = generateAngle();
        xv = (float) Math.sin(angle) * spd;
        yv = (float) Math.cos(angle) * spd;
    }
}
