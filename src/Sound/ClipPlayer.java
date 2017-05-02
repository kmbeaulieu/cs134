
package Sound;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Thank you stack overflow
 */
public class ClipPlayer {
    
    /**
     * Load a file from a path
     * @param filePath pathname to look for the sound file
     * @return a clip
     */
    public Clip loadClip(String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
        Clip in = null;
        ClassLoader cl = getClass().getClassLoader();
        
        AudioInputStream audioIn = AudioSystem.getAudioInputStream( new File( filePath ) );
        in = AudioSystem.getClip();
        in.open(audioIn);
    
    return in;
    }
    
    public void playClip( Clip clip )
{
    if(clip!=null){
       if( clip.isRunning() ) clip.stop();
            clip.setFramePosition( 0 );
    clip.start(); 
    }    
}
    
}
