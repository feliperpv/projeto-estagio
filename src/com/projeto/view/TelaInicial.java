/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.view;

import com.projeto.classes.Retangulo;
import com.projeto.controller.VideoProcessor;
import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Usuario
 */
public class TelaInicial extends javax.swing.JFrame {

    public static VideoCapture video = new VideoCapture();
    String videoFilePath = new String();    
    
    private RunVideoThread runVideoThread = null;
    
    Mat frame = new Mat();
    Mat frameAux = new Mat();
    Mat frameMapaCalor = new Mat();
    VideoProcessor videoProcessor = new VideoProcessor();
    
    List<Rect> arrayRect = new ArrayList<Rect>(); 
    List<Retangulo> retangulos = new ArrayList<Retangulo>();
    
    int count = 0;
    
    boolean first = true;
    Mat atual = new Mat();
    Mat anterior = new Mat();
    
    /*
        Thread responsável por executar as manipulações do vídeo
    */
    class RunVideoThread implements Runnable{
        
        protected volatile boolean runnable = false;
        
        @Override
        public void run() {
            synchronized(this){
                if(video.isOpened()){
                    while(runnable){
                        video.read(frame);
                        
                        //Imgproc.resize(frame, frame, new Size(640, 480));
                        
                        if(frameMapaCalor.dataAddr() == 0 && count == 10){
                            
                            frameMapaCalor = frame;
                            
                        }
                        count++;
                        
                        if(frame.dataAddr() == 0){
                            
                            this.runnable = false;
                            
                        }
                                               
                        if(!frame.empty()){
                            try{
                                Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                                Imgproc.GaussianBlur(frame, frame, new Size(21.0, 21.0), 1, 1, Core.BORDER_REFLECT);
                                        
                                frame.copyTo(atual);
                                
                                if(first){
                                    frame.copyTo(anterior);
                                    first = false;
                                }                   
                                
                                Core.absdiff(atual, anterior, frame);
                                atual.copyTo(anterior);
                                
                                Imgproc.adaptiveThreshold(frame, frameAux, 255,
                                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                                Imgproc.THRESH_BINARY_INV, 9, 7);
                                                              
                                //frameAux = videoProcessor.functionHoughCircles(frame);
                                
                                arrayRect = videoProcessor.detectContours(frameAux);
                                
                                if(arrayRect != null && arrayRect.size() > 0){
                                    Iterator<Rect> it2 = arrayRect.iterator();
                                    
                                    while (it2.hasNext()) {
                                        Rect rect = it2.next();
                                        
                                        Retangulo retangulo = new Retangulo();
                                        retangulo.setPointBottomRight(rect.br());
                                        retangulo.setPointTopLeft(rect.tl());
                                        
                                            
                                        retangulos.add(retangulo);
                                        
                                        Imgproc.rectangle(frameAux, rect.br(), rect.tl(), new Scalar(255, 255, 0), 2);
                                    }
                                }
                                
                                BufferedImage image = videoProcessor.toBufferedImage(frameAux);
                                                                
                                ImageIcon imageIcon = new ImageIcon(image, "video");
                                
                                imageLabel.setIcon(imageIcon);
                                jframe.pack();
                              
                            }
                            catch(Exception ex){
                                LOGGER.log(Level.INFO, "Got an exception.", ex);
                                System.out.println("Error");
                            }
                        }
                    }
                }
            }           
        }
    }
    
    public TelaInicial() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGetVideoFile = new javax.swing.JButton();
        btnStart = new javax.swing.JButton();
        cbxSource = new javax.swing.JComboBox<>();
        txtVideoFileName = new javax.swing.JTextField();
        btnStop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnGetVideoFile.setText("File");
        btnGetVideoFile.setEnabled(false);
        btnGetVideoFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetVideoFileActionPerformed(evt);
            }
        });

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        cbxSource.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Modo de captura", "WebCam", "Arquivo de vídeo" }));
        cbxSource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxSourceActionPerformed(evt);
            }
        });

        txtVideoFileName.setEditable(false);
        txtVideoFileName.setEnabled(false);

        btnStop.setText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtVideoFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGetVideoFile, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cbxSource, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxSource, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnStart))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVideoFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGetVideoFile)
                    .addComponent(btnStop))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGetVideoFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetVideoFileActionPerformed
        final JFileChooser fileChooser = new JFileChooser();
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI", "avi");
        fileChooser.setFileFilter(filter);
        
        fileChooser.setCurrentDirectory(new File("C:\\Users\\Felipe Pavan\\Videos"));
        
        int result = fileChooser.showOpenDialog(null);
        
        System.out.println(result == JFileChooser.APPROVE_OPTION);
        if (result == JFileChooser.APPROVE_OPTION){
             
            videoFilePath = fileChooser.getSelectedFile().getPath();
            txtVideoFileName.setText(videoFilePath);
                         
        }
    }//GEN-LAST:event_btnGetVideoFileActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
             
        if (cbxSource.getSelectedItem().equals("Modo de captura")){
                        
            return;
            
        } else if (cbxSource.getSelectedItem().equals("WebCam")){
            
            video = new VideoCapture(0);
            
        } else if (cbxSource.getSelectedItem().equals("Arquivo de vídeo")){
               
            video = new VideoCapture(videoFilePath);
            
        }
        
        startVideoFrame();
        
        runVideoThread = new RunVideoThread();
        runVideoThread.runnable = true;
        
        Thread thread = new Thread(runVideoThread);
        thread.setDaemon(true);

        thread.start();
        
    }//GEN-LAST:event_btnStartActionPerformed
    
    private JFrame jframe;
    private JLabel imageLabel;
    
    public void startVideoFrame(){
    
        jframe = new JFrame("Analisando Vídeo");
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.setSize(780, 680);
        imageLabel = new JLabel();
        jframe.add(imageLabel);
        jframe.setVisible(true);
        
    }
    
    private void cbxSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxSourceActionPerformed
        
        if (cbxSource.getSelectedItem().equals("Arquivo de vídeo")){
            
            btnGetVideoFile.setEnabled(true);
            txtVideoFileName.setEnabled(true);
            
        } else {
            
            btnGetVideoFile.setEnabled(false);
            txtVideoFileName.setEnabled(false);
            
        }
        
    }//GEN-LAST:event_cbxSourceActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        
        runVideoThread.runnable = false;
        
        videoProcessor.saveMapaCalor(frameMapaCalor, retangulos);
        
        video.release();
        
    }//GEN-LAST:event_btnStopActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaInicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaInicial().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetVideoFile;
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JComboBox<String> cbxSource;
    private javax.swing.JTextField txtVideoFileName;
    // End of variables declaration//GEN-END:variables
}
