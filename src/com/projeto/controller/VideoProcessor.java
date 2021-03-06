/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.controller;

import com.projeto.classes.Ponto;
import com.projeto.classes.Retangulo;
import com.projeto.classes.Rgb;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author Usuario
 */
public class VideoProcessor {

    public BufferedImage toBufferedImage(Mat matrix) {

        int type = BufferedImage.TYPE_BYTE_GRAY;

        if (matrix != null && !matrix.empty() && matrix.channels() > 1) {

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

    public void saveMapaCalor(List<Mat> listFrames, int count, List<Retangulo> retangulos) {

        int cols = listFrames.get(0).cols();
        int rows = listFrames.get(0).rows();
        double[] color = new double[3];
        boolean first = true;
        Rgb[][] matrix = new Rgb[rows][cols];
        
        //Este trecho do código serve para gerar uma imagem baseada na imagem captura, ao invés de 
        //gerar um fundo preto. Se ficar comentado a saida, será uma mapa de calor com fundo preto
        /*
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
         */

        Mat frameMapaCalor = new Mat(rows, cols, listFrames.get(0).type());

        for (int altura = 0; altura < rows; altura++) {
            for (int largura = 0; largura < cols; largura++) {

                double[] pixel = new double[3];
                //Criar o frame para o mapa de calor colorido
                //pixel[0] = matrix[altura][largura].getRed();
                //pixel[1] = matrix[altura][largura].getGreen();
                //pixel[2] = matrix[altura][largura].getBlue();
                
                //Criar o frame para o mapa de calor preto
                pixel[0] = 0;
                pixel[1] = 0;
                pixel[2] = 0;

                frameMapaCalor.put(altura, largura, pixel);
            }
        }

        Ponto[][] matAux = new Ponto[rows][cols];
        double maior = 0;

        for (Retangulo retangulo : retangulos) {

            Point pontoMedio = calculaPontoMédio(retangulo.getPointBottomRight(), retangulo.getPointTopLeft());

            if (matAux[(int) pontoMedio.y][(int) pontoMedio.x] != null) {

                matAux[(int) pontoMedio.y][(int) pontoMedio.x].
                        setContador(matAux[(int) pontoMedio.y][(int) pontoMedio.x].getContador() + 1);

            } else {

                Ponto ponto = new Ponto();
                ponto.setPoint(pontoMedio);
                ponto.setContador(1);

                matAux[(int) pontoMedio.y][(int) pontoMedio.x] = ponto;
            }

            if (matAux[(int) pontoMedio.y][(int) pontoMedio.x].getContador() > maior) {
                maior = matAux[(int) pontoMedio.y][(int) pontoMedio.x].getContador();
            }

        }

        frameMapaCalor = paintMapaCalor(frameMapaCalor, matAux, cols, rows, maior);
        
        Imgproc.cvtColor(frameMapaCalor, frameMapaCalor, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(frameMapaCalor, frameMapaCalor, new Size(9.0, 9.0), 1, 1, Core.BORDER_REFLECT);
        
        Imgcodecs.imwrite("mapacalorGray.jpg", frameMapaCalor);
        
        Mat mapaCalorColored = new Mat(rows, cols, listFrames.get(0).type());
        mapaCalorColored = paintMapaCalorFinal(frameMapaCalor, mapaCalorColored, cols, rows);
               
        Imgcodecs.imwrite("mapacalorColored.jpg", mapaCalorColored);
        System.out.println("FIM");
        System.out.println("normalizador: " + maior);
    }

    public Mat paintMapaCalorFinal(Mat source, Mat dst, int cols, int rows) {
        
        Scalar scalar = new Scalar(0, 0, 0);
        
        for (int altura = 0; altura < rows; altura++) {
            for (int largura = 0; largura < cols; largura++) {
                
                double cores[] =  source.get(altura, largura);
                double cor = cores[0];

                if(cor == 0){
                    scalar = new Scalar(0, 0, 0);
                } else if (cor > 0 && cor < 30){
                    scalar = new Scalar(0, 0, 255);
                } else if (cor >= 30 && cor < 120){
                    scalar = new Scalar(0, 255, 255);
                } else if (cor >= 120 && cor <= 255){
                    scalar = new Scalar(0, 255, 0);
                }
                
                Imgproc.circle(dst, new Point(largura, altura), 1, scalar, -1, 8, 0);
                                
            }
        }
        
        return dst;
    }

    public Mat paintMapaCalor(Mat frameMapaCalor, Ponto[][] matAux, int cols, int rows, double normalizador) {

        double indice = 0;
        Scalar cor = new Scalar(0, 255, 0);
        
        for (int altura = 0; altura < rows; altura++) {
            for (int largura = 0; largura < cols; largura++) {

                if (matAux[altura][largura] != null) {
                    Point point = new Point();
                    point.x = matAux[altura][largura].getPoint().x;
                    point.y = matAux[altura][largura].getPoint().y;

                    if (matAux[altura][largura].getContador() != 0) {
                        indice = (matAux[altura][largura].getContador() / normalizador);
                    }

                    if (indice > 0 && indice < 0.3) {
                        cor = new Scalar(0, 0, 255);
                    } else if (indice >= 0.3 && indice < 0.7) {
                        cor = new Scalar(0, 255, 255);
                    } else if (indice >= 0.7 && indice <= 1) {
                        cor = new Scalar(0, 255, 0);
                    }

                    Imgproc.circle(frameMapaCalor, point, 1, cor, -1, 8, 0);

                }
            }
        }

        return frameMapaCalor;
    }

    public Point calculaPontoMédio(Point br, Point tl) {

        Point pontoMedio = new Point();

        pontoMedio.x = (br.x + tl.x) / 2.0;
        pontoMedio.y = (br.y + tl.y) / 2.0;

        return pontoMedio;
    }

    public ArrayList<Rect> detectContours(Mat frame) {

        Mat v = new Mat();
        Mat vv = new Mat();

        if (frame != null && !frame.empty()) {
            vv = frame.clone();
        }
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
