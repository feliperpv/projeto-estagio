/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.controller;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_core.cvSet2D;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import com.projeto.classes.Retangulo;
import java.awt.Image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Usuario
 */
public class VideoProcessor {
    
    public BufferedImage toBufferedImage(Mat matrix){

        int type = BufferedImage.TYPE_BYTE_GRAY;
        
        if(matrix.channels() > 1){
            
            type = BufferedImage.TYPE_3BYTE_BGR;
            
        }
        
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        
        byte[] buffer = new byte[bufferSize];
        
        matrix.get(0, 0, buffer); //get all pixels
        
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        
        return image;
    }
    
    public Mat functionHoughCircles(Mat frame){
        
        Mat circles = new Mat();
        
        Imgproc.HoughCircles(frame, circles, Imgproc.HOUGH_GRADIENT,
                1, (double)frame.rows()/8, 200, 60, 0, 0);
        
        for (int i = 0; i < circles.cols(); i++){
            double[] c = circles.get(0,i);
            
            double x = Math.round(c[0]); 
            double y = Math.round(c[1]);
            int radius = (int) Math.round(c[2]);
            
            //Armazenar para fazer o mapa de calor
            Point center = new Point(x, y);
            
            //Ponto central do círculo
            Imgproc.circle(frame, center, 1,
                    new Scalar(255, 0, 0), -1, 8, 0);
            
            //Contorno do círculo
            Imgproc.circle(frame, center, radius,
                    new Scalar(255, 0, 0), 2, 8, 0);
          
        }
        
        return frame;
    }

    public void saveMapaCalor(Mat frameMapaCalor, List<Retangulo> retangulos){
        
        List<Point> pontos = new ArrayList<Point>();
        
        for (Retangulo retangulo : retangulos){
            
            System.out.println("br" + retangulo.getPointBottomRight());
            System.out.println("tl" + retangulo.getPointTopLeft());
            pontos.add(calculaPontoMédio(retangulo.getPointBottomRight(), retangulo.getPointTopLeft()));
            
        }
        
        System.out.println("pontos" + pontos);
        
        cvSaveImage("mapacalor.jpg", paintMapaCalor(frameMapaCalor, pontos));
        
    }
    
    public CvMat paintMapaCalor(Mat frameMapaCalor, List<Point> pontos){
        
        CvMat matrix = opencv_core.CvMat.createHeader(frameMapaCalor.height(), frameMapaCalor.width());
        
        CvScalar scalar = new CvScalar();
        scalar.setVal(0, 0);
        scalar.setVal(1, 255);
        scalar.setVal(2, 128);
        
        for (Point ponto : pontos){
            
            cvSet2D(matrix, (int) ponto.x, (int) ponto.y, scalar);
            
        }
                 
        return matrix;
    }
    
    public Point calculaPontoMédio(Point br, Point tl){
        
        Point pontoMedio = new Point();
        
        pontoMedio.x = (br.x + tl.x)/2.0;
        pontoMedio.y = (br.y + tl.y)/2.0;
                
        return pontoMedio;
    }
    
    public ArrayList<Rect> detectContours(Mat frame){
        
        Mat v = new Mat();
        Mat vv = frame.clone();
        
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        
        Imgproc.findContours(vv, contours, v, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        
        double maxArea = 120;
        int maxAreaIdx = -1;
        Rect r = null;
        
        ArrayList<Rect> arrayRect = new ArrayList<Rect>();
                
         for (int idx = 0; idx < contours.size(); idx++) { 
            Mat contour = contours.get(idx); 
            double contourarea = Imgproc.contourArea(contour); 
            
            if (contourarea > maxArea) {
                
                maxAreaIdx = idx;
                r = Imgproc.boundingRect(contours.get(maxAreaIdx));
                arrayRect.add(r);
                
            }
 
        }
        
        return arrayRect;
    }
}
