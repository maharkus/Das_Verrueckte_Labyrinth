import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Camera {
    public static void main(String[] args){
    }

    public JPanel webcamDisplay(){
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());


        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setVisible(true);
        panel.setMirrored(true);

        return panel;
    }
    public int getWidth(JPanel size){
        return size.getWidth();

    }
}
