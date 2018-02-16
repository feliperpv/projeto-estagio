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
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import com.projeto.classes.Retangulo;
import com.projeto.classes.Rgb;
import com.sun.javafx.geom.Vec3f;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void saveMapaCalor(List<Mat> listFrames, int count, List<Retangulo> retangulos){
        
        List<Point> pontos = new ArrayList<Point>();
        
        for (Retangulo retangulo : retangulos){
            
            pontos.add(calculaPontoMédio(retangulo.getPointBottomRight(), retangulo.getPointTopLeft()));
            
        }
        
        int cols = listFrames.get(0).cols();
        int rows = listFrames.get(0).rows();  
        double[] color = new double[3];
        boolean first = true;
        Rgb[][] matrix = new Rgb[rows][cols];
        
        for (Mat img : listFrames){
            
            for(int altura = 0; altura < img.height(); altura++){
                for(int largura = 0; largura < img.width(); largura++){
                    
                    color = img.get(altura, largura);
                    
                    if (first){                  
                        Rgb rgb = new Rgb();
                        
                        if(color != null){
                            rgb.setRed(color[0]);
                            rgb.setGreen(color[1]);
                            rgb.setBlue(color[2]);
                        }
                        matrix[altura][largura] = rgb;
                        
                    } else {
                        
                        color = img.get(altura, largura);
                        
                        if (color != null){
                            matrix[altura][largura].setRed(matrix[altura][largura].getRed() + color[0]);
                            matrix[altura][largura].setGreen(matrix[altura][largura].getGreen() + color[1]);
                            matrix[altura][largura].setBlue(matrix[altura][largura].getBlue() + color[2]);
                        }
                        
                    }
                }
            }
           first = false;
        }
        
        for(int altura = 0; altura < rows; altura++){
                for(int largura = 0; largura < cols; largura++){            
                    matrix[altura][largura].setRed(Math.rint(matrix[altura][largura].getRed()/count));
                    matrix[altura][largura].setGreen(Math.rint(matrix[altura][largura].getGreen()/count));
                    matrix[altura][largura].setBlue(Math.rint(matrix[altura][largura].getBlue()/count));
            }
        }
                
        Mat frameMapaCalor = new Mat(rows, cols, listFrames.get(0).type());
        
        for(int altura = 0; altura < rows; altura++){
                for(int largura = 0; largura < cols; largura++){            
                    
                    double[] pixel = new double[3];
                    pixel[0] = matrix[altura][largura].getRed();
                    pixel[1] = matrix[altura][largura].getGreen();
                    pixel[2] = matrix[altura][largura].getBlue();
                    
                    frameMapaCalor.put(altura, largura, pixel);
            }
        }
                
        //paintMapaCalor(frameMapaCalor, pontos);
        
        Imgcodecs.imwrite("mapacalor.jpg", frameMapaCalor);
        System.out.println("Acabei");
        
    }
    
    public Mat paintMapaCalor(Mat frameMapaCalor, List<Point> pontos){
        
//        CvMat matrix = opencv_core.CvMat.createHeader(frameMapaCalor.height(), frameMapaCalor.width());
        
//        CvScalar scalar = new CvScalar();
//        scalar.setVal(0, 0);
//        scalar.setVal(1, 255);
//        scalar.setVal(2, 128);
        
        for (Point ponto : pontos){
            
            Imgproc.circle(frameMapaCalor, new Point(ponto.x, ponto.y), 1, new Scalar(255, 128, 0), -1, 8, 0);
            //cvSet2D(matrix, (int) ponto.x, (int) ponto.y, scalar);
            
        }                 
        
        return frameMapaCalor;
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
