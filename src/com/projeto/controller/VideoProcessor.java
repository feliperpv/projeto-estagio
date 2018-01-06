/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Mat;

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
    
}
