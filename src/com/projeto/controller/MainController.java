/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.controller;

import com.projeto.view.TelaInicial;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author Usuario
 */
public class MainController {
    
     public void startApplication(){
        
        TelaInicial view = new TelaInicial();
        view.setDefaultCloseOperation(EXIT_ON_CLOSE);
        view.setVisible(true);
        
    }
    
}
