package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author Domenic
 */
public class CameraControl extends AbstractAppState implements AnalogListener, ActionListener {

    private boolean onCyclePath;
    private boolean canRotate; // when right mouse button is hold
    private boolean smoothMotions;

    private Camera cam;

    private InputManager inputManager;

    private Vector3f focusPoint;

    private Vector3f goalLocation;
    private Vector3f goalLookPosition;

    private float radius;
    private float value; // to calculate position on cycle
    private float heightOffset = 1;
    private float rotatingSpeed = 1f;
    
    private float[] angles;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.inputManager = app.getInputManager();
        this.inputManager.setCursorVisible(true);

        this.cam = app.getCamera();
    //    this.cam.setFrustumPerspective(50, (float) cam.getWidth() / cam.getHeight(), 0.05f, 200);
        registerWithInput();
        reset();
        
        angles = new float[3];
        angles[0] = FastMath.HALF_PI;
        angles[1] = - FastMath.PI;
        angles[2] = 0;
    }
    
    public void reset() {
        cam.setFrustumPerspective(55f, (float) cam.getWidth() / cam.getHeight(), 0.05f, 200);

        Vector3f left = new Vector3f(-1, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f direction = new Vector3f(-1, 0, 0);
        cam.setAxes(left, up, direction);
    }

    private void registerWithInput() {
        inputManager.addMapping("CAM_LEFT", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("CAM_RIGHT", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MOUSE_BUTTON_RIGHT", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, new String[]{"CAM_LEFT", "CAM_RIGHT", "MOUSE_BUTTON_RIGHT"});
    }

    public void setOnCyclePath(boolean onCyclePath) {
        this.onCyclePath = onCyclePath;
    }

    public void setFocusPoint(Vector3f focusPoint) {
        this.focusPoint = focusPoint;
    }

    public void setSmoothMotions(boolean smoothMotions) {
        this.smoothMotions = smoothMotions;
    }

    public boolean isSmoothMotions() {
        return smoothMotions;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setHeightOffset(float heightOffset) {
        this.heightOffset = heightOffset;
    }

    public boolean isOnCyclePath() {
        return onCyclePath;
    }

    public void setRotatingSpeed(float rotatingSpeed) {
        this.rotatingSpeed = rotatingSpeed;
    }

    public Vector3f getFocusPoint() {
        return focusPoint;
    }

    public float getHeightOffset() {
        return heightOffset;
    }

    public float getRotatingSpeed() {
        return rotatingSpeed;
    }

    public void setFixedCamOrientation(Vector3f camLoc, Vector3f lookAtPos) {
        goalLocation = null;
        goalLookPosition = null;
        if (!isSmoothMotions()) {
            this.cam.setLocation(camLoc);
            this.cam.lookAt(lookAtPos, Vector3f.UNIT_Y);

        } else {
            goalLocation = camLoc;
            goalLookPosition = lookAtPos;
        }
      
        onCyclePath = false;
    }

    public Camera getCamera() {
        return cam;
    }
    
    

    @Override
    public void update(float tpf) {
        

        if (onCyclePath && focusPoint != null) {

            float x = radius * FastMath.sin(value) + focusPoint.x;
            float y = focusPoint.y + heightOffset;
            float z = radius * FastMath.cos(value) + focusPoint.z;

            if (!isSmoothMotions()) {
                cam.getLocation().set(x, y, z);
                cam.lookAt(focusPoint, Vector3f.UNIT_Y);
            } else {
                Vector3f goalLoc = new Vector3f(x, y, z);
                cam.setLocation(cam.getLocation().interpolateLocal(goalLoc, (float) tpf / 0.1f));
                cam.lookAt(focusPoint, Vector3f.UNIT_Y);
            }

        } else if (!onCyclePath && goalLocation != null) {
            if (cam.getLocation().distance(goalLocation) > 0.1) {
                float factor = tpf / 0.3f;
                cam.setLocation(cam.getLocation().interpolateLocal(goalLocation, factor));                
            }
            cam.lookAt(goalLookPosition, Vector3f.UNIT_Y);
        }

    }

    @Override
    public void cleanup() {
        inputManager.deleteMapping("CAM_LEFT");
        inputManager.deleteMapping("CAM_RIGHT");
        inputManager.deleteMapping("MOUSE_BUTTON_RIGHT");
        inputManager.removeListener(this);
        super.cleanup();
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (isEnabled() && isOnCyclePath() && canRotate) {
            if (name.equals("CAM_LEFT")) {
                this.value += value * rotatingSpeed;
            } else if (name.equals("CAM_RIGHT")) {
                this.value -= value * rotatingSpeed;
            }
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("MOUSE_BUTTON_RIGHT")) {
            canRotate = isPressed;
            inputManager.setCursorVisible(!isPressed);
        }
    }

}
