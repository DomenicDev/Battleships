package de.domenic.battleships.game;

import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 *
 * @author Domenic
 */
public class SpecialEffectControl extends AbstractControl {

    private final ArrayList<ParticleEmitter> emitters = new ArrayList<>();
    private Node particleRootNode;

    private float effectDuration = 5; // default value
    private float effectTime;

    private boolean emiting;
    
     @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial instanceof Node) {
            Node n = (Node) spatial;
            n.depthFirstTraversal((Spatial s) -> {
                if (s instanceof ParticleEmitter) {
                    ParticleEmitter emitter = (ParticleEmitter) s;
                    this.emitters.add(emitter);
                }
            });
            particleRootNode = n;
        }
    }
  
    public Node getEffectRootNode() {
        return particleRootNode;
    }

    public void play() {
        emitters.stream().forEach((emitter) -> {
            emitter.emitAllParticles();
        });
        emiting = true;
        effectTime = 0;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (emiting && effectDuration != -1) {
            effectTime += tpf;
            if (effectTime >= effectDuration) {
                particleRootNode.removeFromParent();
                particleRootNode.removeControl(this);
                emiting = false;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    /**
     * @return the effectDuration
     */
    public float getEffectDuration() {
        return effectDuration;
    }

    /**
     * Sets the intended duration for this effect.
     * After this duration the effect will be removed from the scene.
     * 
     * @param effectDuration the effectDuration to set or "-1" for infinite "mode"
     */
    public void setEffectDuration(float effectDuration) {
        this.effectDuration = effectDuration;
    }

    /**
     * @return the effectTime
     */
    public float getEffectTime() {
        return effectTime;
    }

    /**
     * @param effectTime the effectTime to set
     */
    public void setEffectTime(float effectTime) {
        this.effectTime = effectTime;
    }


}
