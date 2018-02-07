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
public class Retangulo {
    
    private Point pointTopLeft;
    
    private Point pointBottomRight;

    public Point getPointTopLeft() {
        return pointTopLeft;
    }

    public void setPointTopLeft(Point pointTopLeft) {
        this.pointTopLeft = pointTopLeft;
    }

    public Point getPointBottomRight() {
        return pointBottomRight;
    }

    public void setPointBottomRight(Point pointBottomRight) {
        this.pointBottomRight = pointBottomRight;
    }
    
}
