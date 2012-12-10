/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codebarreading;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.BinaryProcessor;
import ij.process.ImageProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 *
 * @author Orion
 */
public class CodeBarReading {
    
    private static int MinimumArgumentNumber = 1;
    private static int ImageFolderIndex = 0;
    private static int OutputFolderIndex = 1;    
    
    private static String OutputDefaultFolder = "Output";
    
    private static String IMAGEFOLDEREMPTYERROR = "ERROR Image Folder can no be open: ";
    private static String IMAGENULLERROR = "ERROR No image returned.";
    
    private static final int SIZEBARS = 2;
    private static final int INITIALBLACKBARS = 2;
    private static final int INITIALWHITEBARS = 2;
    
    private static final int BLACKBARSFORELEMENT = 5;
    private static final int WHITEBARSFORELEMENT = 5;
    
    private static final int ENDBLACKBARS = 2;
    private static final int ENDWHITEBARS = 2;

    private static final double MINRELIABILITY = 5;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws java.io.IOException{
        // TODO code application logic here
        if(MinimumArgumentNumber <= args.length){
            String sImageFolder = args[ImageFolderIndex];
            
            if(sImageFolder != ""){                  
                
                ArrayList<File> aListOfFiles = getFiles(sImageFolder);
                
                for (File oFile : aListOfFiles)
                {
                    String sAbsolutePath = oFile.getAbsolutePath();
                    
                    ImagePlus oImagePlus = openImage(sAbsolutePath);

                    System.out.println("File name: " + oImagePlus.getTitle());
                    
                    CodeBarResult oCodeBarResult = readFullCode(oImagePlus);
                    
                    if((oCodeBarResult.getCodeBar() == "")|| oCodeBarResult.getReliability() < MINRELIABILITY){
                        
                        
                        int[] kernel = {0, 1, 0,
                                        0, 1, 0,
                                        0, 1, 0};
                        
                        ImageProcessor oImageProcessor = oImagePlus.getProcessor();                        
                        oImageProcessor.convolve3x3(kernel);
                        oImageProcessor.threshold(1);
                        
                        oCodeBarResult = readFullCode(oImagePlus);
                        
                        if(oCodeBarResult.getCodeBar() == ""|| oCodeBarResult.getReliability() < MINRELIABILITY){                 

                            float[] kernel2 = { 0, 0, 1, 0, 0,
                                                0, 0, 1, 0, 0,
                                                0, 0, 1, 0, 0,
                                                0, 0, 1, 0, 0,
                                                0, 0, 1, 0, 0,};
                            oImageProcessor.convolve(kernel2, 5, 5);
                            oImageProcessor.threshold(1);

                            oCodeBarResult = readFullCode(oImagePlus);
                        }
                    }                  
                    
                    
                    String sFileName = oFile.getName();
                    saveImagePlus(args, sImageFolder, sFileName, oImagePlus);
                        
                    System.out.println("FinalCode: " + oCodeBarResult.getCodeBar());
                    System.out.println("Lines: " + oCodeBarResult.getLines());
                    System.out.println("Reliability(%): " + oCodeBarResult.getReliability());

                }
                
            }
            else{
                throw new java.io.IOException(IMAGEFOLDEREMPTYERROR + sImageFolder);
            }
            
        }
        
        
    }

    private static CodeBarResult readFullCode(ImagePlus oImagePlus){

        ImageProcessor oImageProcessor = oImagePlus.getProcessor(); 

        //gets the height of the image and iterate
        int y = oImageProcessor.getHeight();   
        
        int y2 = y/2;

        ImageThread tIT1 = new ImageThread(0, y2, oImagePlus);
        ImageThread tIT2 = new ImageThread(y2, y, oImagePlus);
        
        tIT1.start();
        tIT2.start();
        
            
        while(tIT1.isAlive() || tIT2.isAlive()){
            try{
                tIT1.join();
                tIT2.join();
            }
            catch(Exception e){
                 System.out.println("b");
            }
        }
        
        HashMap<String, Integer> mFinalFullCodeLineCounter = new HashMap<>();
        
        for(Entry<String, Integer> oEntry : tIT1.mFullCodeLineCounter.entrySet()){
            String sKey = oEntry.getKey();
            int iValue = oEntry.getValue();  
            if(tIT2.mFullCodeLineCounter.containsKey(sKey)){
                mFinalFullCodeLineCounter.put(sKey, iValue + tIT2.mFullCodeLineCounter.get(sKey) );
            }
            else{
                mFinalFullCodeLineCounter.put(sKey, iValue);
            }
            
        }
        
        for(Entry<String, Integer> oEntry : tIT2.mFullCodeLineCounter.entrySet()){
            String sKey = oEntry.getKey();
            int iValue = oEntry.getValue();  
            if(! mFinalFullCodeLineCounter.containsKey(sKey)){
                mFinalFullCodeLineCounter.put(sKey, iValue);
            }
            
        }
        
        
        String sFinalCode = getMostReadCode(mFinalFullCodeLineCounter);
        
        int iCount =  mFinalFullCodeLineCounter.get(sFinalCode);
                
        CodeBarResult oCodeBarResult = new CodeBarResult();
        oCodeBarResult.setCodeBar(sFinalCode);
        oCodeBarResult.setLines(iCount);
        oCodeBarResult.setReliability(((double)iCount/y) * 100);
        
        return oCodeBarResult;

    }

//    private static CodeBarResult readFullCode(ImagePlus oImagePlus){
//        
//        
//        ImageProcessor oImageProcessor = oImagePlus.getProcessor(); 
//        
//        
//        HashMap<String, Integer> mFullCodeLineCounter = new HashMap<>();
//        
//        //gets the height of the image and iterate
//        int y = oImageProcessor.getHeight();
//        
//        String sFinalCode = "";
//        
//        for(int i = 0; i < y; i++){
//            String sLine = readLine(i,oImagePlus);
//            
//            int iCount = 0;
//            if(mFullCodeLineCounter.containsKey(sLine)){
//                iCount = mFullCodeLineCounter.get(sLine);   
//            }
//            
//            iCount++;
//            mFullCodeLineCounter.put(sLine, iCount);
//            
//            double dReliability = ((double)iCount/y) * 100;
//            if(sLine!= "" && dReliability >= MINRELIABILITY){
//                sFinalCode = sLine;
//                break;
//            }
//        }
//        
        
//        String sFinalCode = getMostReadCode(mFullCodeLineCounter);
        
//        int iCount =  mFullCodeLineCounter.get(sFinalCode);
//                
//        CodeBarResult oCodeBarResult = new CodeBarResult();
//        oCodeBarResult.setCodeBar(sFinalCode);
//        oCodeBarResult.setLines(iCount);
//        oCodeBarResult.setReliability(((double)iCount/y) * 100);
//        
//        return oCodeBarResult;
//    }
    
    public static String readLine(int y, ImagePlus oImagePlus){
        String sResult = ""; 
        int sState = 1; 
        int posX = 0;
        
        ArrayList<Integer> line = groupPixels(y, oImagePlus);
        
        if(line.size() < (INITIALBLACKBARS+INITIALWHITEBARS+ENDBLACKBARS)){
            return "";
        }
        
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
                default:
                    break;
            }
        }
    }
    
    
    
    private static ArrayList<Integer>  groupPixels(int y, ImagePlus oImagePlus){
        ArrayList <Integer> oResult = new ArrayList <>();
        
        ImageProcessor oImageProcessor = oImagePlus.getProcessor(); 

        IJ.run(oImagePlus, "Convert to Mask", "");
        IJ.run(oImagePlus, "Make Binary", "");

        
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
            int iPatternElement = aPattern.get(aPattern.size() - i - 1 );            
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

    private static void readStart(ArrayList<Integer> line, CodeReader oCodeReader) {
        oCodeReader.setPos(oCodeReader.getPos()+1);
        oCodeReader.setCode("");
                
        readBlackBars(line, oCodeReader, INITIALBLACKBARS);
        
        oCodeReader.setPos(oCodeReader.getPos()+1);
        readWhiteBars(line, oCodeReader, INITIALWHITEBARS);
        
        int iNewPos = oCodeReader.getPos()+ INITIALBLACKBARS+ INITIALWHITEBARS-1;
        oCodeReader.setPos(iNewPos);
        
    }

    private static void readCode(ArrayList<Integer> line, CodeReader oCodeReader) {
        oCodeReader.setCode("");
        
        if((oCodeReader.getPos() + ((ENDBLACKBARS - 1) * 2) +  ((ENDWHITEBARS - 1) * 2) ) == line.size()){
            //we are in he before last position => we are in the end of the codebar
            readBlackBars(line, oCodeReader, ENDBLACKBARS);
            int iNewPos = oCodeReader.getPos()+ ((ENDBLACKBARS - 1) * 2);
            oCodeReader.setPos(iNewPos);
        }
        else if((oCodeReader.getPos() + (BLACKBARSFORELEMENT - 1) +  WHITEBARSFORELEMENT ) >= line.size()){
            return;
        }
        else{
            readBlackBars(line, oCodeReader, BLACKBARSFORELEMENT);
        
            oCodeReader.setPos(oCodeReader.getPos()+1);
            readWhiteBars(line, oCodeReader, WHITEBARSFORELEMENT);

            int iNewPos = oCodeReader.getPos()+ BLACKBARSFORELEMENT + WHITEBARSFORELEMENT-1;
            oCodeReader.setPos(iNewPos);
        }
        

    }

    private static boolean isEnd(CodeReader oCodeReader) {
        boolean bResult = false;
        
        bResult = "E".equals(oCodeReader.getCode());
        
        return bResult;
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


    private static void readBlackBars(ArrayList<Integer> line, CodeReader oCodeReader, int iBarsToRead) {
        
        ArrayList<Integer> aBlackBars = new ArrayList<>();
            
        for(int i = 0; i < iBarsToRead; i++){
            
            int iBlackIndex = oCodeReader.getPos() + (i * 2);
            
            int iPixelsCuantity = line.get(iBlackIndex);
            
            int iPatternElement = getPatternElement(iPixelsCuantity, oCodeReader.getSingleBarSize());            
            
            aBlackBars.add(iPatternElement);
        }
        
        int iCode = decode(aBlackBars);
        oCodeReader.setCode(oCodeReader.getCode() + getChar(iCode)); 
        
    }

    private static void readWhiteBars(ArrayList<Integer> line, CodeReader oCodeReader, int iBarsToRead) {
        ArrayList<Integer> aWhiteBars = new ArrayList<>();
            
        for(int i = 0; i < iBarsToRead; i++){
            
            int iWhiteIndex = oCodeReader.getPos() + (i * 2);
            
            int iPixelsCuantity = line.get(iWhiteIndex);
            
            int iPatternElement = getPatternElement(iPixelsCuantity, oCodeReader.getSingleBarSize());            
            
            aWhiteBars.add(iPatternElement);
        }
        
        int iCode = decode(aWhiteBars);
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

    public static ArrayList<File> getFiles(String sImageFolder) 
    {
        ArrayList<File> aFilesResult = new ArrayList<>();
        
        File folder = new File(sImageFolder);
        File[] listOfFiles = folder.listFiles(); 

        for (int i = 0; i < listOfFiles.length; i++) 
        {
          if (listOfFiles[i].isFile()) 
          {
              aFilesResult.add(listOfFiles[i]);
          }
        }
        
        return aFilesResult;
    }
    
    public static String combine (String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    private static void saveImagePlus(String[] args,  String sImageFolder, String sFileName, ImagePlus oImagePlus)  throws java.io.IOException{
        if(oImagePlus != null){

            String sOutputFolder = "";

            if(OutputFolderIndex < args.length){
                sOutputFolder = args[OutputFolderIndex];
                if(sOutputFolder == ""){
                    sOutputFolder = combine(sImageFolder, OutputDefaultFolder);
                }
            }
            else{
                sOutputFolder = combine(sImageFolder, OutputDefaultFolder);
            }      
            
            File oOutputFolder = new File(sOutputFolder);
            
            if(!oOutputFolder.exists()){
                oOutputFolder.mkdir();            
            }
            
            String sOutputFile = combine(sOutputFolder,sFileName);           
            
            FileSaver oFileSaver = new FileSaver(oImagePlus);
            oFileSaver.saveAsTiff(sOutputFile);
        }
        else{
            System.out.println(IMAGENULLERROR + ": " + combine(sImageFolder, sFileName));
        }
    }
    
    private static ImagePlus openImage(String sImagePath) {
        Opener opener = new Opener(); 
        ImagePlus oImagePlus = null;
        try{
            oImagePlus = opener.openImage(sImagePath);
        }
        catch(Exception e){
            
            int a = 0;
        }
        
        return oImagePlus;
    }

    private static String getMostReadCode(HashMap<String, Integer> mFullCodeLineCounter) {
        String sResult = "";     
        
        
        TreeMap<Integer, String> mFullCodeLineShorted = new TreeMap<>();        

        for(Map.Entry<String, Integer> oEntry : mFullCodeLineCounter.entrySet()){
            String sLine = oEntry.getKey();
            int iCount = oEntry.getValue();

            if(sLine != ""){                    
                mFullCodeLineShorted.put(iCount, sLine);
            }

        }

        if(!mFullCodeLineShorted.isEmpty()){
            sResult = mFullCodeLineShorted.lastEntry().getValue();
        }
        
        return sResult;
        
        
    }
}
