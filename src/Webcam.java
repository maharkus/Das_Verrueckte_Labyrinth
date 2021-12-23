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

public class Webcam {

    static double camResWidth;
    static double camResHeight;
    static HandMotionCounter counter;
    Image icon = Toolkit.getDefaultToolkit().getImage("resources/labIcon.png");

    public Webcam(Labyrinth canvas, VideoCapture camera) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat frame = new Mat();

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //VideoCapture camera = new VideoCapture(0);

        //Zugriff auf Camera Auflösung
        camResWidth = camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
        camResHeight = camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);


        // JLabel
        JLabel vidpanel = new JLabel();
        vidpanel.setSize(new Dimension((int) camResWidth+10, (int) camResHeight));


        //JFrame + set contentPane
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(new Dimension((int) camResWidth+20, (int) camResHeight+50));
        window.add(vidpanel);
        window.setVisible(true);
        window.setIconImage(icon);


        if (camera.isOpened()) {

            counter = new HandMotionCounter();

            //Schleife für jeden Frame der Webcam
            while (true) {
                if (camera.read(frame)) {

                    ImageIcon image = new ImageIcon(drawHand(frame, canvas));

                    vidpanel.setIcon(image);
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

    public static BufferedImage drawRectangles(BufferedImage rectangleImage) {

        Graphics2D g2d = rectangleImage.createGraphics();

        g2d.setColor(new Color(102, 0, 153));

        int thickness = 3;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(thickness));


        //Rect top center
        g2d.drawRect((int) Math.floor(camResWidth / 2) - 125, 0, 250, 200);
        //Rect left center
        g2d.drawRect(0, (int) Math.floor(camResHeight / 2) - 125, 150, 250);
        //Rect right center
        g2d.drawRect((int) Math.floor(camResWidth) - 151, (int) Math.abs(camResHeight / 2) - 125, 150, 250);

        // set background of Rect
        g2d.setColor(new Color(112, 0, 163,80));
        g2d.fillRect((int) Math.floor(camResWidth / 2) - 125, 0, 250, 200);
        g2d.fillRect(0, (int) Math.floor(camResHeight / 2) - 125, 150, 250);
        g2d.fillRect((int) Math.floor(camResWidth) - 151, (int) Math.abs(camResHeight / 2) - 125, 150, 250);

        // place text into rect
        g2d.setColor(new Color(0, 0, 0));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Vorwärts", (int)Math.floor(camResWidth / 2)-40 , 100);
        g2d.drawString("Links", 50, (int) Math.floor(camResHeight / 2));
        g2d.drawString("Rechts",(int)Math.floor(camResWidth)-100 , (int) Math.abs(camResHeight / 2));


        g2d.setStroke(oldStroke);
        g2d.dispose();

        return rectangleImage;
    }

    public static BufferedImage drawHand(Mat mat, Labyrinth canvas) {
        List<MatOfPoint> contourPoints = new ArrayList<>();
        Mat processed = mat.clone();
        Mat hierarchy = new Mat();

        // BRG to HSV
        Imgproc.cvtColor(processed, processed, Imgproc.COLOR_BGR2HSV);

        // inRange to get the black Glove
        Core.inRange(processed, new Scalar(85, 0, 0), new Scalar(168, 255, 51), processed);

        // Find Contours
        Imgproc.findContours(processed, contourPoints, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

        //Find biggest Contour
        double maxValue = 0;
        int maxValueIdx = 0;
        Scalar color2 = new Scalar(255, 255, 255);

        for (int i = 0; i < contourPoints.size(); i++) {
            double contours = Imgproc.contourArea(contourPoints.get(i));
            if (maxValue < contours) {
                maxValue = contours;
                maxValueIdx = i;
            }
        }

        //Convex Hull around biggest Contour
        List<MatOfPoint> hullpointList = new ArrayList<>();
        for (MatOfPoint contour : contourPoints) {
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

        rect = Imgproc.boundingRect(hullpointList.get(maxValueIdx));
        rectArr.add(rect);

        Imgproc.rectangle(mat, rect.br(), rect.tl(), new Scalar(255, 255, 255), 2);

        // Point top left bounding rect
        Point topLeftboundingRect = rect.tl();
        // Point bottom right bounding rect
        Point bottomRightboundingRect = rect.br();

        // point top left, top center rect
        Point topLeftRectTopCenter = new Point((int) Math.floor(camResWidth / 2) - 125, 0);
        // point bottom right, top center rect
        Point bottomRightRectTopCenter = new Point((int) Math.floor(camResWidth / 2) + 125, 200);

        // point top left, left center rect
        Point topLeftRectLeftCenter = new Point(0, (int) Math.floor(camResHeight / 2) - 125);
        //point bottom left, left center rect
        Point bottomRightRectLeftCenter = new Point(150, (int) Math.floor(camResHeight / 2) + 125);

        // point top left, right center rect
        Point topLeftRectRightCenter = new Point((int) Math.floor(camResWidth) - 151, (int) Math.abs(camResHeight / 2) - 125);
        // point bottom left, right center rect
        Point bottomRightRightCenter = new Point((int) Math.floor(camResWidth), (int) Math.abs(camResHeight / 2) + 125);


        // Test for bounding rect in purple rect
        if (topLeftboundingRect.x >= topLeftRectTopCenter.x && topLeftboundingRect.x <= bottomRightRectTopCenter.x && topLeftboundingRect.y >= topLeftRectTopCenter.y && topLeftboundingRect.y <= bottomRightRectTopCenter.y && bottomRightboundingRect.x <= bottomRightRectTopCenter.x && bottomRightboundingRect.y <= bottomRightRectTopCenter.y && bottomRightboundingRect.x >= bottomRightboundingRect.x && bottomRightboundingRect.y >= topLeftRectTopCenter.y) {
            counter.increaseCounter0();
            if (counter.getCounter()[0] == 30) {
                counter.resetCounter();
                canvas.move(canvas.curvePoints.get(canvas.player.getPositionIndex()).getDirections()[(int) (canvas.player.getAngle() / 90)]);
            }
        }

        if (topLeftboundingRect.x >= topLeftRectLeftCenter.x && topLeftboundingRect.x <= bottomRightRectLeftCenter.x && topLeftboundingRect.y >= topLeftRectLeftCenter.y && topLeftboundingRect.y <= bottomRightRectLeftCenter.y && bottomRightboundingRect.x <= bottomRightRectLeftCenter.x && bottomRightboundingRect.y <= bottomRightRectLeftCenter.y && bottomRightboundingRect.x >= topLeftRectLeftCenter.x && bottomRightboundingRect.y >= topLeftRectLeftCenter.y) {
            counter.increaseCounter2();
            if (counter.getCounter()[2] == 30) {
                counter.resetCounter();
                canvas.rotate(-90f);
            }
        }
        if (topLeftboundingRect.x >= topLeftRectRightCenter.x && topLeftboundingRect.x <= bottomRightRightCenter.x && topLeftboundingRect.y >= topLeftRectRightCenter.y && topLeftboundingRect.y <= bottomRightRightCenter.y && bottomRightboundingRect.x <= bottomRightRightCenter.x && bottomRightboundingRect.y <= bottomRightRightCenter.y && bottomRightboundingRect.x >= topLeftRectRightCenter.x && bottomRightboundingRect.y >= topLeftRectRightCenter.y) {
            counter.increaseCounter3();
            if (counter.getCounter()[3] == 30) {
                counter.resetCounter();
                canvas.rotate(90f);
            }
        }

        // Draw Contours to mat
        Imgproc.drawContours(mat, hullpointList, maxValueIdx, color2, 2);

        // Mirror frames
        Core.flip(mat, mat, +1);

        // Create Buffered Image
        BufferedImage processedesImage = convertMatToBufferedImage(mat);

        return processedesImage;
    }
}
