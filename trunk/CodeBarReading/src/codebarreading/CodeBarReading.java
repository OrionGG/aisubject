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
    
    private static final int SIZEBARS = 2;
    private static final int INITIALBLACKBARS = 2;
    private static final int INITIALWHITEBARS = 2;
    
    private static final int ENDBLACKBARS = 2;
    private static final int ENDWHITEBARS = 2;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
    }
    
    private static String readFullCode(ImagePlus oImagePlus){
        
    }
    
    private static String readLine(int y, ImagePlus oImagePlus){
        String sResult = ""; 
        int sState = 1; 
        int posX = 0;
        ArrayList<Integer> line = groupPixels(y, oImagePlus);
        CodeReader oCodeReader = new CodeReader();
        oCodeReader.setCode("");
        oCodeReader.setPos(posX);
        oCodeReader.setSingleBarSize(getSingleBarSize(line));
        
        readStart(line, oCodeReader);
        
        while(true) {
            switch(sState){
                case 1:
                    if (!oCodeReader.isEmpty()){          
                        readCode(line,oCodeReader);
                        sState = 2; 
                    }
                    else sState = 4;
                    break;
                case 2:
                    if (!isEnd(oCodeReader) && !oCodeReader.isEmpty()){                        
                        sResult += oCodeReader.getCode();
                        readCode(line,oCodeReader);
                    }
                    else sState = 3;
                    break;
                case 3:
                    if(isEnd(oCodeReader)){
                        return sResult;
                    }
                    else sState = 4;
                    break;
                case 4:
                    return "";
            }
        }
    }
    
    
    
    private static ArrayList<Integer>  groupPixels(int y, ImagePlus oImagePlus){
        ArrayList <Integer> oResult = new ArrayList <>();
        
        ImageProcessor oImageProcessor = oImagePlus.getProcessor(); 
        
        //gets all pixels of the image
        int[] aLine = new int[oImageProcessor.getWidth()];
        oImageProcessor.getRow(0,y, aLine, oImageProcessor.getWidth());
        
        int iPixelsCounter = 0;
        int iPreviousPixelColor = 0;
        
        for(int x = 0; x < oImageProcessor.getWidth(); x++){            
            if(aLine[x] == iPreviousPixelColor){
                    iPixelsCounter++;
            }
            else
            {
                oResult.add(iPixelsCounter);
                iPreviousPixelColor = aLine[x];
                iPixelsCounter = 1;
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
    
    public static char getChar(int iCode){
        
        char cResult = ' ';
                
        switch(iCode) {
            case 0:  cResult = 'S';
                     break;
            case 2:  cResult = 'E';
                     break;
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

    private static boolean readStart(ArrayList<Integer> line, CodeReader oCodeReader) {
        oCodeReader.setPos(oCodeReader.getPos()+1);
        oCodeReader.setCode("");
                
        readStartBlack(line, oCodeReader);
        
        oCodeReader.setPos(oCodeReader.getPos()+1);
        readStartWhite(line, oCodeReader);
        
        int iNewPos = oCodeReader.getPos()+ INITIALBLACKBARS+ INITIALWHITEBARS - 1;
        oCodeReader.setPos(iNewPos);
        
        return !("".equals(oCodeReader.getCode().trim()));
    }

    private static void readCode(ArrayList<Integer> line, CodeReader oCodeReader) {
        oCodeReader.setPos(oCodeReader.getPos()+1);
        oCodeReader.setCode("");
    }

    private static boolean isEnd(CodeReader oCodeReader) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static int getSingleBarSize(ArrayList<Integer> line) {
        int iResult = 0;
        
        int iPixelsAcum = 0;
        //we delete the first and last lines
        for(int i = 1; i < line.size()-1; i++){
            iPixelsAcum += line.get(i);
        }
        
        iResult = iPixelsAcum/(line.size()- SIZEBARS);
        
        return iResult;
        
    }


    private static void readStartBlack(ArrayList<Integer> line, CodeReader oCodeReader) {
        
        ArrayList<Integer> aStartBlackBars = new ArrayList<>();
            
        for(int i = 0; i < INITIALBLACKBARS; i++){
            
            int iBlackIndex = oCodeReader.getPos() + (i * 2);
            
            int iPixelsCuantity = line.get(iBlackIndex);
            
            int iPatternElement = getPatternElement(iPixelsCuantity, oCodeReader.getSingleBarSize());            
            
            aStartBlackBars.add(iPatternElement);
        }
        
        int iCode = decode(aStartBlackBars);
        oCodeReader.setCode(oCodeReader.getCode() + getChar(iCode)); 
        
    }

    private static void readStartWhite(ArrayList<Integer> line, CodeReader oCodeReader) {
        ArrayList<Integer> aStartBlackBars = new ArrayList<>();
            
        for(int i = 0; i < INITIALWHITEBARS; i++){
            
            int iWhiteIndex = oCodeReader.getPos() + (i * 2);
            
            int iPixelsCuantity = line.get(iWhiteIndex);
            
            int iPatternElement = getPatternElement(iPixelsCuantity, oCodeReader);            
            
            aStartBlackBars.add(iPatternElement);
        }
        
        int iCode = decode(aStartBlackBars);
        oCodeReader.setCode(oCodeReader.getCode() + getChar(iCode)); 
    }

    private static int getPatternElement(int iPixelsCuantity, int iSingleBarSize) {
        int iResult = -1;
        
        if(iPixelsCuantity <= iSingleBarSize){
            iResult = 0;
        }
        else{
            iResult = 1;
        }
        
        return iResult;
    }

    
}
