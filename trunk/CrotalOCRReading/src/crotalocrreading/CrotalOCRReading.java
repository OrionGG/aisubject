/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crotalocrreading;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.BinaryProcessor;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;
import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author Orion
 */
public class CrotalOCRReading {
    private static final String IMAGEFILEOPENERROR = "ERROR Image file could not be opened: ";
    
    private static String OutputDefaultFolder = "Output";

    public static String readCrotalImage(String sImagePath){   
        
        String sResult = "";
        
        ImagePlus oImagePlus = openImage(sImagePath);
        
        ImageProcessor oImageProcessor = oImagePlus.getProcessor();    
        oImageProcessor.dilate();
        
        File oProcFile = new File(sImagePath);
        
        saveImagePlus(oProcFile.getParent(), oProcFile.getName(), oImagePlus);
        
        
        File oFile = new File(combine(combine(oProcFile.getParent(), OutputDefaultFolder), oProcFile.getName()));
        
        Tesseract instance = Tesseract.getInstance();  // JNA Interface Mapping
        // Tesseract1 instance = new Tesseract1(); // JNA Direct Mapping
        
        try {
            sResult = instance.doOCR(oFile);
            sResult = sResult.trim();
            
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }        
        
        try{
            Integer.parseInt(sResult);
        }
        catch (NumberFormatException e) {
            StringBuilder oStringBuilder = new StringBuilder(sResult);
            
            for(int i = 0; i< oStringBuilder.length();i++){
                String sDigit = oStringBuilder.substring(i,i+1);
                sDigit = fixNumber(sDigit);
                oStringBuilder.replace(i, i+1, sDigit);
            }
            sResult = oStringBuilder.toString();
        }    
        
        System.out.println("Result number:");
        System.out.println(sResult);
        System.out.println();
        return sResult;
    }
    
    private static ImagePlus openImage(String sImagePath) {
        Opener opener = new Opener(); 
        ImagePlus oImagePlus = null;
        try{
            oImagePlus = opener.openImage(sImagePath);
        }
        catch(Exception e){
            System.out.println(IMAGEFILEOPENERROR + sImagePath);
        }
        
        return oImagePlus;
    }
        
        private static void saveImagePlus(String sImageFolder, String sFileName, ImagePlus oImagePlus){
        if(oImagePlus != null){

            String sOutputFolder = combine(sImageFolder, OutputDefaultFolder);
                  
            
            File oOutputFolder = new File(sOutputFolder);
            
            if(!oOutputFolder.exists()){
                oOutputFolder.mkdir();            
            }
            
            String sOutputFile = combine(sOutputFolder,sFileName);           
            
            FileSaver oFileSaver = new FileSaver(oImagePlus);
            oFileSaver.saveAsTiff(sOutputFile);
        }
        else{
            System.out.println(IMAGEFILEOPENERROR + ": " + combine(sImageFolder, sFileName));
        }
    }
        
        
    
    public static String combine (String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }
    
    public static String fixNumber(String sDigit){
        String sResult = sDigit;
        
        sDigit = sDigit.toUpperCase();
        switch (sDigit){
            case "O": 
            case "D": 
            case "C": 
                sResult = "0";
                break;
            case "I":
            case "L":                
                sResult = "1";
                break;
            case "Z":
                sResult = "2";
                break;
            case "S":
            case "$":
                sResult = "5";
                break;
            case "T":
                sResult = "7";
                break;
            case "B":
            case "P":
            case "&":                
                sResult = "8";
                break;
            case "G":
                sResult = "9";
                break;
            default:
                break;                    
        }
           
        return sResult;
    }
    
}
