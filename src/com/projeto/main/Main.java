/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.main;

import com.projeto.controller.MainController;
import org.opencv.core.Core;

/**
 *
 * @author Felipe Reis Pavan
 */
public class Main {
    
    public static void main(String[] args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        MainController controller = new MainController();
        controller.startApplication();
        
    }
    
}
