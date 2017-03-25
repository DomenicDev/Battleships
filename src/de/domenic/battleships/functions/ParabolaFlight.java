package de.domenic.battleships.functions;

/**
 *
 * @author Domenic
 */
public class ParabolaFlight extends AbstractFlightFunction {
    
    @Override
    public float computeFunctionValue() {
        return (float) (- a * Math.pow(- Math.sqrt(c / a) + (passedTime / flightDuration) * 2 * Math.sqrt(c / a), 2) + c);
    }

    @Override
    public float computeSlopeOfTangent() {
        return (float) (-2 * a * (-Math.sqrt(c/a) + passedTime/ flightDuration * 2 * Math.sqrt(c/a)));
    }
    
}
