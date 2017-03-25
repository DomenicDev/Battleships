/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 * DOES NOT REALLY WORK YET
 * WE HAVE PROBLEMS WHEN DETACHING!
 *
 * @author Domenic
 */
public class MainMenuSceneState extends AbstractAppState {
    
    private Node scene;
    private FilterPostProcessor filter;
    
    private ViewPort viewPort;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.viewPort = app.getViewPort();
        
        scene = (Node) app.getAssetManager().loadModel("Scenes/MainMenuScene.j3o");
        filter = app.getAssetManager().loadFilter("Scenes/SeaFilter.j3f");
        
        ((SimpleApplication)app).getRootNode().attachChild(scene);
        app.getViewPort().addProcessor(filter);
        
        app.getCamera().setLocation(new Vector3f(-8.170833f, 0.88593036f, 6.4856124f));
        app.getCamera().setRotation(new Quaternion(9.892684E-4f, 0.97432506f, -0.004281722f, 0.225103f));
    }
    
    @Override
    public void update(float tpf) {
    }
    
    @Override
    public void cleanup() {
        scene.removeFromParent();
      if (viewPort.getProcessors().contains(filter)) {
          viewPort.removeProcessor(filter);
      }
        super.cleanup();
    }
    
}
