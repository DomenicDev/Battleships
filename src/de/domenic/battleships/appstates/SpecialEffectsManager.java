package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.SpecialEffectControl;
import java.util.ArrayList;

/**
 *
 * @author Domenic
 */
public class SpecialEffectsManager extends AbstractAppState {
    
    private ArrayList<EffectEntry> effects;
    private AssetManager assetManager;
    private VisualAppState visualAppState;
    
    public enum Effect {
        
        Fire("Fire", -1),
        Smoke("Smoke", 6),
        Explosion("Explosion", 5),
        WaterSplash("WaterSplash", 1);
        
        private Effect(String fileName, float duration) {
            this.path = "Models/Effects/" + fileName + ".j3o";
            this.duration = duration;
        }
        
        private final String path;
        private final float duration;

        public float getDuration() {
            return duration;
        }

        public String getPath() {
            return path;
        }
        
    }
    
    private class EffectEntry {
     
        private Effect effect;
        private Node node;
        private Player player;
        private Position position;

        public EffectEntry(Effect effect, Node node, Player player, Position position) {
            this.effect = effect;
            this.node = node;
            this.player = player;
            this.position = position;
        }

        public Effect getEffect() {
            return effect;
        }

        public Node getNode() {
            return node;
        }

        public Player getPlayer() {
            return player;
        }

        public Position getPosition() {
            return position;
        }        
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.visualAppState = stateManager.getState(VisualAppState.class);
        this.assetManager = app.getAssetManager();
        this.effects = new ArrayList<>();
    }
    
    public void addEffect(Player player, Position position, Effect effect) {
        if (player == null || position == null) {
            return;
        }
        
        Node n = (Node) assetManager.loadModel(effect.getPath());
        n.setQueueBucket(RenderQueue.Bucket.Translucent);
        SpecialEffectControl specialEffectControl = new SpecialEffectControl();
        specialEffectControl.setEffectDuration(effect.getDuration());
        n.addControl(specialEffectControl);
        
        Vector3f location = GameUtils.convertToVector3f(position);
        Node tileNode = visualAppState.getVisualGameFieldByPlayer(player).getGameFieldNode();
        n.setLocalTranslation(location);
        tileNode.attachChild(n);
        
        // we will not call "emitAllParticles()" for effects with an infinite duration
        if (effect.getDuration() != -1) {
            specialEffectControl.play();
        }
        
        effects.add(new EffectEntry(effect, n, player, position));
    }
    
    /**
     * Use this method to remove effects from the scene.
     * @param player player object this effect belongs to
     * @param position where is the effect placed
     * @param effect the effect which shall be removed, or null if all shall be removed
     */
    public void removeEffects(Player player, Position position, Effect effect) {
        if (player == null || position == null) {
            return;
        }
        
        // to avoid modification errors we use a second list to later remove the found entries from the global list
        ArrayList<EffectEntry> entriesToRemove = new ArrayList<>();

        // search for effects 
        for (EffectEntry entry : effects) {
            if (effect == null || entry.getEffect() == effect ) {
                if (player.equals(entry.getPlayer()) && position.equals(entry.getPosition())) {
                    entry.getNode().removeControl(SpecialEffectControl.class);
                    entry.getNode().removeFromParent();
                    entriesToRemove.add(entry);
                }
            }
        }
        
        // remove found entries from global list
        for (EffectEntry entry : entriesToRemove) {
            effects.remove(entry);
        }
        entriesToRemove.clear();
    }
    
    @Override
    public void update(float tpf) {
    }
    
    @Override
    public void cleanup() {
        for (EffectEntry entry : effects) {
            entry.getNode().removeControl(SpecialEffectControl.class);
            entry.getNode().removeFromParent();
        }
        effects.clear();
        super.cleanup();        
    }
    
}
