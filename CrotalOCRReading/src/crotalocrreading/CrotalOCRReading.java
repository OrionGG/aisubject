/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crotalocrreading;

import ij.ImagePlus;
import ij.io.Opener;
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author Orion
 */
public class CrotalOCRReading {
    private static final String IMAGEFILEOPENERROR = "ERROR Image file could not be opened: ";

    public static String readCrotalImage(String sImagePath){
        String sResult = "";
        
        File oFile = new File(sImagePath);
        
        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
        instance.setDatapath("D:\\Master Vision Artificial\\AI\\Tess4J");
        
        try {
            sResult = instance.doOCR(oFile);
            System.out.println(sResult);
            
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        
        return sResult;
    }
    
    
}
