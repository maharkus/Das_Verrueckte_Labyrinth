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

    Webcam webcam = Webcam.getDefault();
    JPanel panel = new JPanel();
    WebcamPanel webcamPanel;

    Camera() {
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            WebcamPanel webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setMirrored(true);
            webcamPanel.setVisible(true);
        }
        else {
            System.out.println("No webcam detected");
        }
    }

    public int getWidth(JPanel size){
        return size.getWidth();
    }
    public JPanel getPanel(){
        if (webcam != null) {
            return webcamPanel;
        }
        else {
            return panel;
        }
    }
}
