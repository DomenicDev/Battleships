package de.domenic.battleships.functions;

import de.domenic.battleships.game.GameUtils;

/**
 *
 * @author Domenic
 */
public abstract class AbstractFlightFunction {
    
    protected float a, b, c, d, e;
    protected float passedTime = 0;
    protected float flightDuration = GameUtils.FLIGHT_DURATION;
    
    public abstract float computeFunctionValue();
    
    public abstract float computeSlopeOfTangent();
    
    public void update(float tpf) {
        this.passedTime += tpf;
    }
    
    public void reset() {
        passedTime = 0;
    }

    public void setFlightDuration(float flightDuration) {
        this.flightDuration = flightDuration;
    }

    public float getFlightDuration() {
        return flightDuration;
    }

    public float getPassedTime() {
        return passedTime;
    }    

    public void setA(float a) {
        this.a = a;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setC(float c) {
        this.c = c;
    }

    public void setD(float d) {
        this.d = d;
    }

    public void setE(float e) {
        this.e = e;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getC() {
        return c;
    }

    public float getD() {
        return d;
    }

    public float getE() {
        return e;
    }
    
    
    
}
