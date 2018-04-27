/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.classes;

import org.opencv.core.Point;

/**
 *
 * @author Felipe Pavan
 */
public class Ponto {

    private Point point;

    private int contador;

    public Ponto() {
        this.contador = 0;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

}
