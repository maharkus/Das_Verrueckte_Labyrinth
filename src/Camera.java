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

    JPanel panel = new JPanel();
    WebcamPanel webcamPanel;
    Webcam webcam;

    Camera(Webcam webcam) {
        this.webcamPanel = new WebcamPanel(webcam);


        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcamPanel.setMirrored(true);
            webcamPanel.setVisible(true);
            this.webcamPanel = new WebcamPanel(webcam);

        }
        else {
            System.out.println("No webcam detected");
        }

    }

    public boolean hasCamera() {
        return webcam != null;
    }


    public JPanel getPanel(){
        return webcamPanel;
    }

    public Dimension getDimension (WebcamPanel dimension){
        return dimension.getSize();
    }
    public int getHeight (JPanel size) {
        return size.getHeight();
    }
    public int getWidth(JPanel size){
        return size.getWidth();
    }
}
