import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;

public class Camera {
    public static void main(String[] args){
    }

    JPanel panel = new JPanel();
    WebcamPanel webcamPanel;
    Webcam webcam;

    Camera(Webcam webcam) {
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcamPanel.setMirrored(true);
            webcamPanel.setVisible(true);
        }
        else {
            System.out.println("No webcam detected");
        }
        this.webcamPanel = new WebcamPanel(webcam);
    }

    public boolean hasCamera() {
        return webcam != null;
    }


    public JPanel getPanel(){
        return webcamPanel;
    }

    public int getWidth(JPanel size){
        return size.getWidth();
    }
}