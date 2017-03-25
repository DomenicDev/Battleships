package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.audio.Listener;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.HashMap;

/**
 *
 * @author Domenic
 */
public class SoundAppState extends AbstractAppState {

    public enum SoundEffect {

        ShipExplosion("destroyed.WAV"),
        HitShip("hit.WAV"),
        Miss("miss.WAV"),
        Shoot("shoot.WAV");
        
        

        private SoundEffect(String path) {
            this.path = "Sounds/Effects/" + path;
        }

        private final String path;

        public String getSoundPath() {
            return this.path;
        }
    }

    public enum Music {

        Homelander("Homelander.WAV");

        private Music(String path) {
            this.path = "Sounds/Music/" + path;
        }

        private final String path;

        public String getMusicPath() {
            return this.path;
        }
    }

    private Listener listener;
    private AssetManager assetManager;

    private final HashMap<SoundEffect, AudioNode> sounds = new HashMap<>();
    private final HashMap<Music, AudioNode> music = new HashMap<>();

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.listener = app.getListener();
        this.assetManager = app.getAssetManager();

        Node rootNode = ((SimpleApplication) app).getRootNode();

        for (SoundEffect sound : SoundEffect.values()) {
            AudioNode audio = new AudioNode(assetManager, sound.getSoundPath(), AudioData.DataType.Buffer);
            audio.setLooping(false);
            audio.setPositional(false);
            rootNode.attachChild(audio);
            sounds.put(sound, audio);
        }

        for (Music track : Music.values()) {
            AudioNode audio = new AudioNode(assetManager, track.getMusicPath(), AudioData.DataType.Stream);
            audio.setVolume(0.5f);
            audio.setLooping(true);
            audio.setPositional(false);
            rootNode.attachChild(audio);
            music.put(track, audio);
        }

    }
    
    public void setSoundMuted(boolean muted) {
        if (muted) {
            listener.setVolume(0);
        } else {
            listener.setVolume(1);
        }
    }

    public void playSoundEffect(SoundEffect sound, Vector3f position) {
        playSound(sound, position);
        
    }
    
    private void playSound(SoundEffect sound, Vector3f position) {
        AudioNode audio = sounds.get(sound);

        if (audio != null) {           
            if (position == null) {
                audio.setPositional(false);
            } else {
                audio.setPositional(true);
            }
            audio.playInstance();
        }
    }

    public void playMusicTrack(Music music) {
        playMusic(music);
    }
    
    private void playMusic(Music music) {
        AudioNode track = this.music.get(music);
        if (track != null) {
            track.play();
        }
    } 
    
    public void stopCurrentMusicTrack() {
        for (AudioNode audio : music.values()) {
            if (audio.getStatus() != AudioSource.Status.Stopped) {
                audio.stop();
            }
        }
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();
        stopCurrentMusicTrack();
        for (AudioNode audio : music.values()) {
            audio.removeFromParent();
        }
         for (AudioNode audio : sounds.values()) {
            audio.removeFromParent();
        }
         sounds.clear();
         music.clear();
    }

}
