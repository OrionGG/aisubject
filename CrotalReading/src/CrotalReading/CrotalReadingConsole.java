/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CrotalReading;

import ij.ImagePlus;
import ij.io.FileSaver;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author Orion
 */
public class CrotalReadingConsole {
    private static int MinimumArgumentNumber = 2;
    private static int ImageFolderIndex = 0;
    private static int NumbersToDetectIndex = 1;
    private static int OutputFolderIndex = 2;    
    
    private static String OutputDefaultFolder = "Output";
    
    private static String IMAGEFOLDEREMPTYERROR = "ERROR Image Folder can no be open: ";
    private static String NUMBERTTODETECTCONVERTERROR = "ERROR Argument does not contain a parsable integer: ";
    private static String IMAGENULLERROR = "ERROR No image returned.";
    

    
    /**
     *  args[0] = string with the folder where the images will be
     *  args[1] = integer with the numbers to detect
     *  args[2] = string with the output folder where the result images will be saved
     *  args[2] = args[0]/output by default
     * 
     **/
    public static void main(String args[]) throws java.io.IOException{
        if(MinimumArgumentNumber <= args.length){
            String sImageFolder = args[ImageFolderIndex];
            
            if(sImageFolder != ""){  
                int iNumbersToDetect = 0;
                try{
                    iNumbersToDetect = Integer.parseInt(args[NumbersToDetectIndex]);
                }
                catch (NumberFormatException nfe){
                      throw new java.io.IOException(NUMBERTTODETECTCONVERTERROR + args[NumbersToDetectIndex] + ". " + nfe.getMessage());                    
                }
                
                ArrayList<File> aListOfFiles = getFiles(sImageFolder);
                
                for (File oFile : aListOfFiles)
                {
                    String sAbsolutePath = oFile.getAbsolutePath();
                    
                    ImagePlus oImagePlus = CrotalReader.VideoVigilanciaProcess(sAbsolutePath, iNumbersToDetect);
                    

                    String sFileName = oFile.getName();
                    saveImagePlus(args, sImageFolder, sFileName, oImagePlus);

                }
                
            }
            else{
                throw new java.io.IOException(IMAGEFOLDEREMPTYERROR + sImageFolder);
            }
            
        }
        
        
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
    
}
