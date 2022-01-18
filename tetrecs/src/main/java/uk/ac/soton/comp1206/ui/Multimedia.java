package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Multimedia {

    private static MediaPlayer musicPlayer;
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    private static boolean audioEnabled = true;
    private MediaPlayer audioClip;

    /**
     * Plays a sound audio
     * @param audioName the name of the audio file
     */
    public void playAudio(String audioName) {
        String toPlay = Multimedia.class.getResource("/sounds/"+audioName).toExternalForm();
        try{
            audioClip = new MediaPlayer(new Media(toPlay));
            audioClip.play();
            logger.info("Playing sound: "+audioName);
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Couldn't play audio sound");
        }
    }

    /**
     * Plays a music track on an infinite loop until the next track is called
     * @param musicName the name of the music track
     */
    public void playMusic(String musicName) {
        String toPlay = Multimedia.class.getResource("/music/"+musicName).toExternalForm();
        try{
            //stop previous track
            if(musicPlayer!=null){
                musicPlayer.stop();
            }
            musicPlayer = new MediaPlayer(new Media(toPlay));
            logger.info("Playing music: "+musicName);
            //listen for the players end event and then wait zero seconds
            musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(Duration.ZERO));
            //finaly play the track again from the beginning
            musicPlayer.play();
        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Couldn't play music");
        }
    }

    /**
     * Plays a music track and return back to the menu scene when the track ends
     * @param musicName the name of the music track
     */
    public void playMusic(String musicName,GameWindow gameWindow) {
        String toPlay = Multimedia.class.getResource("/music/"+musicName).toExternalForm();
        try{
            //stop previous track
            if(musicPlayer!=null){
                musicPlayer.stop();
            }
            musicPlayer = new MediaPlayer(new Media(toPlay));
            logger.info("Playing music: "+musicName);
            if(gameWindow==null) {
                //listen for the players end event and then wait zero seconds
                musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(Duration.ZERO));
            }
            else{
                musicPlayer.setOnEndOfMedia(() -> {
                    gameWindow.cleanup();
                    gameWindow.showMenu();
                });
            }
            //finaly play the track again from the beginning
            musicPlayer.play();
            musicPlayer.setVolume(0);

        } catch (Exception e) {
            audioEnabled = false;
            e.printStackTrace();
            logger.error("Couldn't play music");
        }
    }
}
