package de.domenic.battleships.scenario;

import com.jme3.post.FilterPostProcessor;

/**
 *
 * @author Domenic
 */
public class SeaScenario extends AbstractScenario {

    FilterPostProcessor fpp;

    @Override
    protected void buildScene() {

        rootNode.attachChild(assetManager.loadModel("Scenes/Sea.j3o"));

        fpp = assetManager.loadFilter("Scenes/SeaFilter.j3f");
        viewPort.addProcessor(fpp);

      
    }

    @Override
    public void cleanup() {
        super.cleanup();
        viewPort.removeProcessor(fpp);
    }

}
