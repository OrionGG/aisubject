/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CrotalReading;
import ij.gui.ImageCanvas;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
/**
 *
 * @author Orion
 */
public class CrotalReaderJFrame extends javax.swing.JFrame {

    JFileChooser fc;
    private double dAngle = 0.0;
    /**
     * Creates new form CrotalReaderJFrame
     */
    public CrotalReaderJFrame() {
        initComponents();
        //Create a file chooser
        fc = new JFileChooser();
        
        jProgressBar1.setMinimum(CrotalReader.INITIALTHRESHOLD);
        jProgressBar1.setMaximum(CrotalReader.MAXTHRESHOLD);
        jProgressBar1.setValue(CrotalReader.INITIALTHRESHOLD);
        jProgressBar1.setStringPainted(true);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilePath = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnBrowe = new javax.swing.JButton();
        btnRotate = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        label1 = new java.awt.Label();
        jPImage = new javax.swing.JPanel();
        btnNumbers = new javax.swing.JButton();
        jTxtNumberDet = new javax.swing.JTextField();
        jLResult = new javax.swing.JLabel();
        jTResult = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtFilePath.setText("D:\\Master Vision Artificial\\AI\\Practicas\\Tema5-MuestraCrotales\\Muestra\\Crotal1.tif");
        txtFilePath.setToolTipText("");

        jLabel1.setText("Image File:");

        btnBrowe.setText("Browse");
        btnBrowe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBroweActionPerformed(evt);
            }
        });

        btnRotate.setText("Rotate");
        btnRotate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRotateActionPerformed(evt);
            }
        });

        label1.setText("Numbers to Detect:");

        javax.swing.GroupLayout jPImageLayout = new javax.swing.GroupLayout(jPImage);
        jPImage.setLayout(jPImageLayout);
        jPImageLayout.setHorizontalGroup(
            jPImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPImageLayout.setVerticalGroup(
            jPImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnNumbers.setLabel("Numbers");
        btnNumbers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNumbersActionPerformed(evt);
            }
        });

        jTxtNumberDet.setText("4");
        jTxtNumberDet.setMaximumSize(new java.awt.Dimension(3, 1));
        jTxtNumberDet.setMinimumSize(new java.awt.Dimension(1, 1));
        jTxtNumberDet.setPreferredSize(new java.awt.Dimension(15, 20));

        jLResult.setText("Result:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, 614, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTxtNumberDet, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 11, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBrowe, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNumbers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRotate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTResult))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFilePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(label1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTxtNumberDet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBrowe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRotate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNumbers)
                        .addGap(15, 15, 15)
                        .addComponent(jLResult)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTResult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 446, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBroweActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBroweActionPerformed
         
 //Set up the file chooser.
        if (fc == null) {
            fc = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
            fc.addChoosableFileFilter(new ImageFilter());
            fc.setAcceptAllFileFilterUsed(false);

	    //Add custom icons for file types.
            fc.setFileView(new ImageFileView());

	    //Add the preview pane.
            fc.setAccessory(new ImagePreview(fc));
        }

        //Show it.
        int returnVal = fc.showDialog(CrotalReaderJFrame.this,
                                      "File Chooser");

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtFilePath.setText(file.getAbsolutePath());
        } else {
            txtFilePath.setText("");
        }

        //Reset the file chooser for the next time it's shown.
        fc.setSelectedFile(null);
    }//GEN-LAST:event_btnBroweActionPerformed

    private void btnRotateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRotateActionPerformed
        
        String sImagePath = txtFilePath.getText();
        if(sImagePath != ""){
            ImageCanvas oImageCanvas = new ImageCanvas(CrotalReader.rotateImage(sImagePath)); 
            
            jPImage.removeAll();;
            jPImage.add(oImageCanvas);
        }
        
    }//GEN-LAST:event_btnRotateActionPerformed

    private void btnNumbersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNumbersActionPerformed
        btnNumbers.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        jProgressBar1.setValue(CrotalReader.INITIALTHRESHOLD);
        
        // TODO add your handling code here:
         String sImagePath = txtFilePath.getText();
        if(sImagePath != ""){
            
            int iNumbersToSearch = Integer.parseInt(jTxtNumberDet.getText().trim());                   
            
            ImageCanvas oImageCanvas = new ImageCanvas(CrotalReader.VideoVigilanciaProcess(sImagePath, iNumbersToSearch));
            
            jPImage.removeAll();;
            jPImage.add(oImageCanvas);
        }
    }//GEN-LAST:event_btnNumbersActionPerformed

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
            java.util.logging.Logger.getLogger(CrotalReaderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrotalReaderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrotalReaderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrotalReaderJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CrotalReaderJFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowe;
    private javax.swing.JButton btnNumbers;
    private javax.swing.JButton btnRotate;
    private javax.swing.JLabel jLResult;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPImage;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTextField jTResult;
    private javax.swing.JTextField jTxtNumberDet;
    private java.awt.Label label1;
    private javax.swing.JTextField txtFilePath;
    // End of variables declaration//GEN-END:variables
}
