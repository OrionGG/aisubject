/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codebarreading;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Orion
 */
public class CodeBarReading {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
    }
    
    private static String readCode(ImagePlus oImagePlus){
        String sResult = ""; 
        int sState = 1; 
        int posX = 0;
        List linea = groupPixels(oImagePlus);
        (vacio, codigo, posX) = LeerCodigo(línea,posX);
        
        while(true) {
            switch(sState){
                case 1:
                    if (!vacio){                       
                        sResult += codigo;
                        (vacio, codigo, posX) = LeerCodigo(línea,posX);
                        sState = 2; 
                    }
                    else sState = 4;
                    break;
                case 2:
                    if (!vacio){                        
                        sResult += codigo;
                        (vacio, codigo, posX) = LeerCodigo(línea,posX);
                    }
                    else sState = 3;
                    break;
                case 3:
                    return salida;
                    break;
                case 4:
                    return “”;
                    break;
            }
        }
    }
    
    private static String readLine(int y, ImagePlus oImagePlus){
        
    }
    
    
    
    private static ArrayList<Integer>  groupPixels(int y, ImagePlus oImagePlus){
        ArrayList <Integer> oResult = new ArrayList <>();
        
        ImageProcessor oImageProcessor = oImagePlus.getProcessor(); 
        
        //gets all pixels of the image
        int[] aLine = new int[oImageProcessor.getWidth()];
        oImageProcessor.getRow(0,y, aLine, oImageProcessor.getWidth());
        
        int iPixelsCounter = 0;
        int iPreviousPixelColor = -1;
        
        for(int x = 0; x < oImageProcessor.getWidth(); x++){            
            if(aLine[x] == 0){
                if(iPreviousPixelColor == 0)
                {
                    iPixelsCounter++;
                }
                else
                {
                    oResult.add(iPixelsCounter);
                    iPreviousPixelColor = 0;
                    iPixelsCounter = 1;
                }
            } 
            else{
                if(iPreviousPixelColor == 1)
                {
                    iPixelsCounter++;
                }
                else
                {
                    oResult.add(iPixelsCounter);
                    iPreviousPixelColor = 1;
                    iPixelsCounter = 1;                    
                }
            }
        }
        oResult.add(iPixelsCounter);
        
        return oResult;
        
    }
        
    public static int decode(ArrayList<Integer> aPattern){
        int iResult = 0;
        
        for(int i = 0; i < aPattern.size(); i++)
        {            
            int iPatternElement = aPattern.get(i);            
            iResult = iResult + (int)(Math.pow(2, i)* iPatternElement);
        }
        
        return iResult;
    }
    
    public static char getCode(int pos, ArrayList<Integer> aCodes){
        
        char cResult = ' ';
        int iCode = aCodes.get(pos);        
        
        switch(iCode) {
            case 3:  cResult = '7';
                     break;
            case 5:  cResult = '4';
                     break;
            case 6:  cResult = '0';
                     break;
            case 9:  cResult = '2';
                     break;
            case 10: cResult = '9';
                     break;
            case 12: cResult = '6';
                     break;
            case 17: cResult = '1';
                     break;
            case 18: cResult = '8';
                     break;
            case 20: cResult = '5';
                     break;
            case 24: cResult = '3';
                     break;
            default: cResult = ' ';
                     break;
        }
        return cResult;
    }
    
    public static String leerLinea(int y, Image img){
        
    }
    
}
