/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.controller;

import java.awt.Image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.opencv.core.Mat;
import org.opencv.core.Point;
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
                    new Scalar(255, 255, 100), -1, 8, 0);
            
            //Contorno do círculo
            Imgproc.circle(frame, center, radius,
                    new Scalar(255, 255, 100), 2, 8, 0);
          
        }
        
        return frame;
    }

    public void saveMapaCalor(Mat frameMapaCalor){
        
        Imgcodecs.imwrite("mapacalor.jpg", frameMapaCalor);
        
    }
}
