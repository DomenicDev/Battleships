package de.domenic.battleships.functions;

/**
 *
 * @author Domenic
 */
public class HalphParabola extends AbstractFlightFunction {

    @Override
    public float computeFunctionValue() {
        return (float) (- a * Math.pow((passedTime/flightDuration) * Math.sqrt(c/a), 2) + c);
    }

    @Override
    public float computeSlopeOfTangent() {
        return (float) (- 2 * a * (passedTime/flightDuration) * Math.sqrt(c/a) + c);
    }
    
}
