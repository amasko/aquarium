package com.example.Aquarium.model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Explosion {
    private static final String TAG = Explosion.class.getSimpleName();

    public static final int STATE_ALIVE 	= 0;	// at least 1 particle is alive
    public static final int STATE_DEAD 		= 1;	// all particles are dead

    private Particle[] particles;			// particles in the explosion
    private int x, y;						// the explosion's origin
    private float gravity;					// the gravity of the explosion (+ upward, - down)
    private float wind;						// speed of wind on horizontal
    private int size;						// number of particles
    private int state;						// whether it's still active or not

    public Explosion(int particleNr, int x, int y) {
        Log.d(TAG, "Explosion created at " + x + "," + y);
        this.state = STATE_ALIVE;
        this.particles = new Particle[particleNr];
        for (int i = 0; i < this.particles.length; i++) {
            Particle p = new Particle(x, y);
            this.particles[i] = p;
        }
        this.size = particleNr;
    }

    // helper methods -------------------------
    public boolean isAlive() {
        return this.state == STATE_ALIVE;
    }
    public boolean isDead() {
        return this.state == STATE_DEAD;
    }

    public void update() {
        if (this.state != STATE_DEAD) {
            boolean isDead = true;
            for (Particle particle : this.particles) {
                if (particle.isAlive()) {
                    particle.update();
                    isDead = false;
                }
            }
            if (isDead)
                this.state = STATE_DEAD;
        }
    }

    public int getState() {
        return state;
    }

    public void update(Rect container) {
        if (this.state != STATE_DEAD) {
            boolean isDead = true;
            for (Particle particle : this.particles) {
                if (particle.isAlive()) {
                    particle.update(container);
//					this.particles[i].update();
                    isDead = false;
                }
            }
            if (isDead)
                this.state = STATE_DEAD;
        }
    }

    public void draw(Canvas canvas) {
        for (Particle particle : this.particles) {
            if (particle.isAlive()) {
                particle.draw(canvas);
            }
        }
    }
}
