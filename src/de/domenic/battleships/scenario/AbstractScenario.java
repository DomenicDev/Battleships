package de.domenic.battleships.scenario;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.domenic.battleships.functions.AbstractFlightFunction;
import de.domenic.battleships.functions.ExponentialFalldown;
import de.domenic.battleships.functions.HalphParabola;
import de.domenic.battleships.functions.ParabolaFlight;

/**
 * <p>A Scenario describes the environment and scene for a game, for example an 
 * battle on sea or even in space.</p>
 *
 * @author Domenic
 */
public abstract class AbstractScenario extends AbstractAppState {

    protected Node rootNode;
    protected AssetManager assetManager;
    protected ViewPort viewPort;

    protected AbstractFlightFunction directFlight, indirektFlight, currentlyUsedFunction;    

    protected Spatial attackingObject;
    private Spatial attackingObjectClone;

    private boolean onAttack;
    
    private Vector3f attackStartPos, attackGoalPos;    
    private Vector3f interpolatedVector = new Vector3f();
    private Vector3f lookingDirection = new Vector3f();

 
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.assetManager = app.getAssetManager();
        this.viewPort = app.getViewPort();
        this.rootNode = ((SimpleApplication) app).getRootNode();

        directFlight = new ParabolaFlight();
        directFlight.setA(20);
        directFlight.setC(8);

        indirektFlight = new ExponentialFalldown();
        indirektFlight.setA(0.2f);
        indirektFlight.setB(0.8f);
        indirektFlight.setC(5);

//        indirektFlight = new HalphParabola();
//        indirektFlight.setA(20);
//        indirektFlight.setC(9);

        attackingObject = assetManager.loadModel("Models/Weapons/Rocket.j3o");

        buildScene();
    }

    /**
     * This method is called just after initialize(). Here you can set up the
     * environment, scene, effects, skybox, etc.
     */
    protected abstract void buildScene();

    /**
     * Will show an attacking object flying from the start position to the goal position.
     * @param start The start position in world space
     * @param goal The goal position in world space
     * @param direct should be true when the object shall start at a ships position
     */
    public void showAttackAnimation(Vector3f start, Vector3f goal, boolean direct) {
        //remove old one first
        if (attackingObjectClone != null) {
            attackingObjectClone.removeFromParent();
        }
        this.onAttack = true;
        this.attackStartPos = start;
        this.attackGoalPos = goal;
        this.attackingObjectClone = attackingObject.clone();
        this.rootNode.attachChild(attackingObjectClone);
        this.directFlight.reset();
        this.indirektFlight.reset();
        this.currentlyUsedFunction = direct ? directFlight : indirektFlight;
    }

    @Override
    public void update(float tpf) {
        if (onAttack) {
            if (attackingObjectClone != null && currentlyUsedFunction != null) {

                currentlyUsedFunction.update(tpf);
                
                if (currentlyUsedFunction.getPassedTime() >= currentlyUsedFunction.getFlightDuration()) {
                    onAttack = false;
                    attackingObjectClone.removeFromParent();
                    currentlyUsedFunction = null;
                    return;
                }

                // compute position 
                interpolatedVector.interpolateLocal(attackStartPos, attackGoalPos, (currentlyUsedFunction.getPassedTime() / currentlyUsedFunction.getFlightDuration()));
                interpolatedVector.setY(currentlyUsedFunction.computeFunctionValue());
                attackingObjectClone.setLocalTranslation(interpolatedVector);
                
                // compute looking direction
                lookingDirection.set(attackGoalPos.subtract(attackStartPos, lookingDirection)); // to set x and z values
                lookingDirection.setY(currentlyUsedFunction.computeSlopeOfTangent()); // to set y value
                lookingDirection.normalizeLocal(); // normalize to get a (unit) direction vector
                
                // now we will add the location of the object and get a point the object can look at rather than a direction
                lookingDirection.addLocal(attackingObjectClone.getLocalTranslation());
                
                // we want the object to look at this point we defined above
                attackingObjectClone.lookAt(lookingDirection, Vector3f.UNIT_Y);
            }
        }
    }
}
