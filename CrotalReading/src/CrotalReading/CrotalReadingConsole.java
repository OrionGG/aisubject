/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CrotalReading;

import ij.ImagePlus;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Orion
 */
public class CrotalReadingConsole {
    private static int ArgumentNumber = 2;
    private static int ImageFolderIndex = 0;
    private static int NumbersToDetectIndex = 1;
    
    private static String IMAGEFOLDEREMPTYERROR = "ERROR Image Folder can no be open: ";
    private static String NUMBERTTODETECTCONVERTERROR = "ERROR Argument does not contain a parsable integer: ";
    private static String IMAGENULLERROR = "ERROR No image returned.";

    
    /**
     *  args[0] = string with the folder where the images will be
     *  args[1] = integer with the numbers to detect
     * 
     **/
    public static void main(String args[]) throws java.io.IOException{
        if(ArgumentNumber <= args.length){
            String sImageFolderIndex = args[ImageFolderIndex];
            if(sImageFolderIndex != ""){  
                int iNumbersToDetect = 0;
                try{
                    iNumbersToDetect = Integer.parseInt(args[NumbersToDetectIndex]);
                }
                catch (NumberFormatException nfe){
                      throw new java.io.IOException(NUMBERTTODETECTCONVERTERROR + args[NumbersToDetectIndex] + ". " + nfe.getMessage());                    
                }
                
                ArrayList<File> aListOfFiles = getFiles(sImageFolderIndex);
                
                for (File oFile : aListOfFiles)
                {
                    String sFileName = oFile.getName();
                    
                    ImagePlus oImagePlus = CrotalReader.VideoVigilanciaProcess(sFileName, iNumbersToDetect);
                    
                    if(oImagePlus != null){
                        
                    }
                    else{
                        throw new java.io.IOException(IMAGENULLERROR);
                    }
                }
                
            }
            else{
                throw new java.io.IOException(IMAGEFOLDEREMPTYERROR + sImageFolderIndex);
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
    
}
