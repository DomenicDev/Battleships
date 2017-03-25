package de.domenic.battleships.controls;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Domenic
 */
public class ShipRotationControl extends AbstractControl {
    
    private Quaternion rot;
    private float[] initAngles;
    private float[] newAngles;
    private float strength = 1f;
    private float speed = 1f;
    private float time;
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial); 
        
        if (spatial != null) {
            time = (float) (Math.random() * 42f);
            rot = new Quaternion().fromAngles(new float[] {0,0,0});
            
            initAngles = new float[3]; // need to store the start values
            newAngles = new float[3]; // here the new angles will be stored
            
            spatial.getLocalRotation().toAngles(initAngles);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        time += tpf * speed;
        
     //   newAngles[0] = FastMath.sin(time) * strength * FastMath.DEG_TO_RAD;
        newAngles[2] = FastMath.sin(time) * strength * FastMath.DEG_TO_RAD;
        
        newAngles[0] += initAngles[0];
        newAngles[1] += initAngles[1];
        newAngles[2] += initAngles[2];
        
        rot.fromAngles(newAngles);
        spatial.setLocalRotation(rot);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public float getSpeed() {
        return speed;
    }

    public float getStrength() {
        return strength;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }
}
