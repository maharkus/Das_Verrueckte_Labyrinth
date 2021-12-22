import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class Webcam{

    static double camResWidth;
    static double camResHeight;
    static HandMotionCounter counter;

    public static void Camera(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat frame = new Mat();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture camera = new VideoCapture(0);

        //Zugriff auf Camera Auflösung
        camResWidth = camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
        camResHeight = camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        // JLabel
        JLabel vidpanel = new JLabel();

        //JFrame + set contentPane
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(vidpanel);
        window.setSize(new Dimension((int)camResWidth, (int)camResHeight));
        window.setVisible(true);

        if (camera.isOpened()) {

            counter = new HandMotionCounter();

            //Schleife für jeden Frame der Webcam
            while (true) {
                if (camera.read(frame)) {

                    ImageIcon image = new ImageIcon(contourZeichnen(frame));

                    vidpanel.setIcon(image);
                    vidpanel.setSize(new Dimension((int)camResWidth, (int)camResHeight));
                    vidpanel.setVisible(true);
                    vidpanel.repaint();
                }
            }
        }
    }

    public static BufferedImage convertMatToBufferedImage(final Mat mat) {
        // Create buffered image
        BufferedImage bufferedImage = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);

        // Write data to image
        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        mat.get(0, 0, dataBuffer.getData());

        // Draw Rectangles on Buffered Image
        drawRectangles(bufferedImage);

        return bufferedImage;
    }

    public static BufferedImage drawRectangles(BufferedImage rectangleImage){

        Graphics2D g2d = rectangleImage.createGraphics();

        g2d.setColor(Color.red);

        int thickness = 2;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(thickness));


        //Rect oben mitte
        g2d.drawRect((int)Math.floor(camResWidth/2)-100,0,200,125);
        //Rect unten mitte
        g2d.drawRect((int)Math.floor(camResWidth/2)-100,(int)Math.abs(camResHeight)-126,200,125);
        //Rect links mitte
        g2d.drawRect(0,(int)Math.floor(camResHeight/2)-125,150,250);
        //Rect rechts mitte
        g2d.drawRect((int)Math.floor(camResWidth)-151,(int)Math.abs(camResHeight/2)-125,150,250);

        g2d.setStroke(oldStroke);
        g2d.dispose();

        return rectangleImage;
    }

    public static BufferedImage contourZeichnen(Mat mat){
        List<MatOfPoint> contourPoints = new ArrayList<>();
        Mat processed = mat.clone();
        Mat hierarchy = new Mat();

        // BRG zu HSV
        Imgproc.cvtColor(processed,processed, Imgproc.COLOR_BGR2HSV);

        // inRange to get the black Glove
        Core.inRange(processed, new Scalar (85,0,0), new Scalar (168,255,51), processed);

        // Find Contours
        Imgproc.findContours(processed, contourPoints, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        //Find biggest Contour
        double maxValue = 0;
        int maxValueIdx = 0;
        Scalar color2 = new Scalar(255,255,255);

        for(int i = 0; i < contourPoints.size(); i++){
            double contours = Imgproc.contourArea(contourPoints.get(i));
            if(maxValue < contours){
                maxValue = contours;
                maxValueIdx = i;
            }
        }

        //Convex Hull around biggest Contour
        List<MatOfPoint> hullpointList = new ArrayList<>();
        for(MatOfPoint contour : contourPoints){
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);

            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullpointList.add(new MatOfPoint(hullPoints));
        }

        // draw bounding rect
        Rect rect = null;
        ArrayList<Rect> rectArr = new ArrayList<Rect>();

        for(int i= 0; i < hullpointList.size(); i++){
            rect = Imgproc.boundingRect(hullpointList.get(i));
            rectArr.add(rect);
            if(rect.width* rect.height > 16000){
                Imgproc.rectangle(mat, rect.br(), rect.tl(), new Scalar (255,255,255),2);

                //Punkt oben Links
                Point topLeftboundingRect = rect.tl();
                // Punkt unten rechts
                Point bottomRightboundingRect = rect.br();

                //Punkt oben Links
                Point topLeftRectTopCenter = new Point ((int)Math.floor(camResWidth/2)-100 , 0);
                //Punkt unten rechts
                Point bottomRightRectTopCenter = new Point ((int)Math.floor(camResWidth/2)+100 , 125);

                //Punkt oben Links
                Point topLeftRectbottomCenter = new Point ((int)Math.floor(camResWidth/2)-100, (int)Math.abs(camResHeight)-126);
                //Punkt unten rechts
                Point bottemRightRectbottomCenter = new Point ((int)Math.floor(camResWidth/2)+100, (int)Math.abs(camResHeight));

                //Punkt oben Links
                Point topLeftRectLeftCenter = new Point (0,(int)Math.floor(camResHeight/2)-125);
                //Punkt unten rechts
                Point bottomRightRectLeftCenter = new Point (150,(int)Math.floor(camResHeight/2)+125);

                //Punkt oben Links
                Point topLeftRectRightCenter = new Point ((int)Math.floor(camResWidth)-151,(int)Math.abs(camResHeight/2)-125);
                //Punkt unten rechts
                Point bottomRightRightCenter = new Point ((int)Math.floor(camResWidth),(int)Math.abs(camResHeight/2)+125);


                // Test für boundingRect in red Rectangle
                if(topLeftboundingRect.x >= topLeftRectTopCenter.x && topLeftboundingRect.x <= bottomRightRectTopCenter.x && topLeftboundingRect.y >= topLeftRectTopCenter.y && topLeftboundingRect.y <= bottomRightRectTopCenter.y && bottomRightboundingRect.x <= bottomRightRectTopCenter.x && bottomRightboundingRect.y <= bottomRightRectTopCenter.y && bottomRightboundingRect.x >= bottomRightboundingRect.x && bottomRightboundingRect.y >= topLeftRectTopCenter.y){
                    counter.increaseCounter0();
                    if(counter.getCounter()[0] == 30){
                        counter.resetCounter();
                        GameWindow.
                        canvas.move(canvas.curvePoints.get(canvas.player.getPositionIndex()).getDirections()[(int) (canvas.player.getAngle()/90)]);
                    }
                }

                if(topLeftboundingRect.x >= topLeftRectbottomCenter.x && topLeftboundingRect.x <= bottemRightRectbottomCenter.x && topLeftboundingRect.y >= topLeftRectbottomCenter.y && topLeftboundingRect.y <= bottemRightRectbottomCenter.y && bottomRightboundingRect.x <= bottemRightRectbottomCenter.x && bottomRightboundingRect.y <= bottemRightRectbottomCenter.y && bottomRightboundingRect.x >= topLeftRectbottomCenter.x && bottomRightboundingRect.y >= topLeftRectbottomCenter.y){

                    counter.increaseCounter1();
                    if(counter.getCounter()[1] == 30){
                        counter.resetCounter();
                        System.out.println("Das funktioniert");
                    }
                }

                if(topLeftboundingRect.x >= topLeftRectLeftCenter.x && topLeftboundingRect.x <= bottomRightRectLeftCenter.x && topLeftboundingRect.y >= topLeftRectLeftCenter.y && topLeftboundingRect.y <= bottomRightRectLeftCenter.y && bottomRightboundingRect.x <= bottomRightRectLeftCenter.x && bottomRightboundingRect.y <= bottomRightRectLeftCenter.y && bottomRightboundingRect.x >= topLeftRectLeftCenter.x && bottomRightboundingRect.y >= topLeftRectLeftCenter.y){
                    counter.increaseCounter2();
                    if(counter.getCounter()[2] == 30){
                        counter.resetCounter();
                        System.out.println("Das funktioniert");
                    }
                }
                if(topLeftboundingRect.x >= topLeftRectRightCenter.x && topLeftboundingRect.x <= bottomRightRightCenter.x && topLeftboundingRect.y >= topLeftRectRightCenter.y && topLeftboundingRect.y <= bottomRightRightCenter.y && bottomRightboundingRect.x <= bottomRightRightCenter.x && bottomRightboundingRect.y <= bottomRightRightCenter.y && bottomRightboundingRect.x >= topLeftRectRightCenter.x && bottomRightboundingRect.y >= topLeftRectRightCenter.y){
                    counter.increaseCounter3();
                    if(counter.getCounter()[3] == 30){
                        counter.resetCounter();
                        System.out.println("Das funktioniert");
                    }
                }
            }
        }


        // Draw Contours to mat
        Imgproc.drawContours(mat, hullpointList, maxValueIdx , color2,2);

        // Create Buffered Image
        BufferedImage processedesImage = convertMatToBufferedImage(mat);

        return processedesImage;
    }
}
