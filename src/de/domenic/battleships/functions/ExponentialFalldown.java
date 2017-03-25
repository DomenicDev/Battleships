package de.domenic.battleships.functions;

/**
 *
 * @author Domenic
 */
public class ExponentialFalldown extends AbstractFlightFunction {

    @Override
    public float computeFunctionValue() {
        return (float) (- a * Math.exp(b * ((passedTime / flightDuration) * (Math.log(c/a)/Math.log(Math.exp(b))))) + c);
    }

    @Override
    public float computeSlopeOfTangent() {
        return (float) (-a * b * (Math.log(c/a)/Math.log(Math.exp(b))) * Math.exp(b * ((passedTime / flightDuration) * (Math.log(c/a)/Math.log(Math.exp(b))))));
    }
    
}
