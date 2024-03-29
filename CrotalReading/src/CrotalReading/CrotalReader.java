/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CrotalReading;

import ij.*;
import ij.gui.ImageWindow;
import ij.io.*;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Binary;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Orion
 */
public class CrotalReader {
    
    public static final int INITIALTHRESHOLD = 40;
    public static final int MAXTHRESHOLD = 80;
    
    private static final double MAXDENSITYNUMBER = 4000.0;
    private static final double INITIALENSITYNUMBER = 2500.0;
    private static final double MINDENSITYNUMBER = 500.0;
    
    private static int MINPARTICLESNUMBER = 4;
    
    private static int iTotalTime = 0;
    
    private static final String IMAGEFILEOPENERROR = "ERROR Image file could not be opened: ";
    

    /**
     * @param args the command line arguments
     */
    public static ImagePlus VideoVigilanciaProcess(String sImagePath, int iNumbersToSearch) {
        //get the angle to rotate the image
        Double dAngle = getRotationAngle(sImagePath);
        
        MINPARTICLESNUMBER = iNumbersToSearch;
        
        System.out.println("TotalTime: " + iTotalTime);
        
        //get the image with the numbers to read
        ImagePlus img = getNumberImage(sImagePath, dAngle);
                
        return img;
        
    }
    
    public static Point GetLowerPoint(ImageProcessor oImageProcessor, int[][] aPixels ){
        Point oLowerPoint = new Point();
        boolean bLowerPointFound = false;
        //look for the lower pixel
        for(int y = oImageProcessor.getHeight() - 2; y >= 0; y--){
            for(int x = 0; x < oImageProcessor.getWidth(); x++){
                if(aPixels[x][y] != 0){
                    oLowerPoint.x = x;
                    oLowerPoint.y = y;
                    bLowerPointFound = true;
                    break;
                }            
            }
            if (bLowerPointFound){
                break;
            }
        }
        return oLowerPoint;
    
    }
    
    public static Line GetStraightLine(ImageProcessor oImageProcessor, Point oLowerPoint,  int[][] aPixels){
        Point oLineInitialHorPoint = new Point();
        
        //Get initial or last point in the horizontal line
        if( (int)(oImageProcessor.getWidth()*0.5) < oLowerPoint.x){
            oLineInitialHorPoint.x = oLowerPoint.x - (int)(oImageProcessor.getWidth()*0.5);
        }
        else{
            
            oLineInitialHorPoint.x = oLowerPoint.x + (int)(oImageProcessor.getWidth()*0.5);
        }
        
        oLineInitialHorPoint.y = GetValueLastInitialPoint(oImageProcessor, oLowerPoint, oLineInitialHorPoint, aPixels);


        Vector2D vStraightLineInitialHorPoint = new Vector2D(oLineInitialHorPoint.x, oLineInitialHorPoint.y);
        Vector2D vLowerPoint = new Vector2D(oLowerPoint.x, oLowerPoint.y);
        
                
        Line oLine = new Line(vStraightLineInitialHorPoint, vLowerPoint);
        return oLine;
    
    }
    
    public static int GetValueLastInitialPoint(ImageProcessor oImageProcessor,Point oLowerPoint,Point oLineInitialHorPoint,  int[][] aPixels){
        int iValueLastInitialPoint = 0;
        
        for(int y = oImageProcessor.getHeight() - 2; y >= 0; y--){

            if(aPixels[oLineInitialHorPoint.x][y] != 0){
                iValueLastInitialPoint= y+1;
                break;
            } 
         }
        
        return iValueLastInitialPoint;
    }
    
    public static int GetMeasurementsValue(int[] aMeasurements){
        int iResult = 0;
        
        for(int i = 0; i< aMeasurements.length;i++){
            iResult = iResult + aMeasurements[i];
        }
        
        return iResult;
        
    }

    public static double getRotationAngle(String sImagePath) {
        double dAngle = 0.0;
        
        ImagePlus oImagePlus = openImage(sImagePath);
        
        if(oImagePlus != null){        
            ImageProcessor oImageProcessor = prepareImageForRotation(oImagePlus);      

            //gets all pixels of the image
            int[][] aPixels = oImageProcessor.getIntArray();

            //gets the lower point of the crotal
            Point oLowerPoint = GetLowerPoint(oImageProcessor, aPixels);

            //gets the lower line of the crotal
            Line oLine = GetStraightLine(oImageProcessor, oLowerPoint, aPixels);

            dAngle = oLine.getAngle()*63.661977236758* (-1);
        }
        return dAngle;
    }

    private static ImagePlus getNumberImage(String sImagePath, double dAngle) {
        System.out.println("ImagePath: " + sImagePath);
        
        long startTime = System.currentTimeMillis();

        ImagePlus img = null;
        
        
        int nParticles = 0;
        int iLastThreshold = 0;
        double dLastDigitSize = 0;
        
        int iInitialThreshold = INITIALTHRESHOLD;
        
        while(nParticles < MINPARTICLESNUMBER && iInitialThreshold < MAXTHRESHOLD ){           
            int iMaxParticles = 0;
            
            double dMinSize = INITIALENSITYNUMBER;
            
            while(nParticles < MINPARTICLESNUMBER &&  MINDENSITYNUMBER < dMinSize){ 
                
                img = prepareImageForGetNumbers(sImagePath, iInitialThreshold);
                
                if(img == null)
                {
                    return img;
                }
                
                nParticles = getParticles(img, dMinSize);

                iMaxParticles = (nParticles>iMaxParticles)?nParticles:iMaxParticles;
                
                
                dLastDigitSize = dMinSize;
                dMinSize = dMinSize - ((200 * MINPARTICLESNUMBER)/(nParticles+1));
            }
            
            iLastThreshold = iInitialThreshold;
            iInitialThreshold = iInitialThreshold + ((2 * MINPARTICLESNUMBER) + 1  - (2 * (iMaxParticles)));
                
        }
        
        ImageProcessor  oFinalImageProcessor = img.getProcessor();
        dilateDigits(oFinalImageProcessor);
        
        long estimatedTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Threshold: " + iLastThreshold);
        System.out.println("Digit Size: " + dLastDigitSize);
        
        System.out.println("Elapsed Time: " + estimatedTime);
        iTotalTime += estimatedTime; 
        
        //skew image
        oFinalImageProcessor.rotate(dAngle);
        
        return img;
    }

    private static ImagePlus prepareImageForGetNumbers(String sImagePath, int iInitialThreshold) {
        Opener opener = new Opener(); 
        ImagePlus img = opener.openImage(sImagePath);
        
        if(img == null)
        {
            return img;
        }

        ImageProcessor  oImageProcessor = img.getProcessor();
        oImageProcessor = oImageProcessor.convertToByte(false);
        oImageProcessor.threshold(iInitialThreshold);


        oImageProcessor.erode();
        oImageProcessor.erode();

        
        return img;
    }
    
    public static ImagePlus rotateImage(String sImagePath){        
        //get the angle to rotate the image
        double dAngle = getRotationAngle(sImagePath);
        
        ImagePlus oImagePlus = openImage(sImagePath);
        ImageProcessor  oImageProcessor = prepareImageForRotation(oImagePlus);
        oImageProcessor.rotate(dAngle);
        
        return oImagePlus;
    }

    private static void dilateDigits(ImageProcessor oFinalImageProcessor) {        
                oFinalImageProcessor.dilate();
                oFinalImageProcessor.dilate();
    }

    private static int getParticles(ImagePlus img, double dMinSize) {
        
        ResultsTable oResultsTable = new ResultsTable();

        int[] aMeasurements = new int[]{Measurements.LABELS, Measurements.AREA, Measurements.PERIMETER, Measurements.CIRCULARITY, Measurements.RECT, Measurements.SLICE};

        int iMeasurementsValue = GetMeasurementsValue(aMeasurements);

        ParticleAnalyzer oParticleAnalyzer = new ParticleAnalyzer(
                ParticleAnalyzer.SHOW_MASKS+ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES+ParticleAnalyzer.IN_SITU_SHOW , 
                iMeasurementsValue,
                oResultsTable,dMinSize,MAXDENSITYNUMBER);
        oParticleAnalyzer.setHideOutputImage(true);
        oResultsTable.disableRowLabels();        

        boolean b = oParticleAnalyzer.analyze(img);

        int nParticles = oResultsTable.getCounter();    
        return nParticles;
    }

    private static ImageProcessor prepareImageForRotation(ImagePlus img) {
        ImageProcessor  oImageProcessor = img.getProcessor();        
        oImageProcessor.autoThreshold();

        //remove lower line
        oImageProcessor.erode();
        //delete noise
        oImageProcessor.dilate();
        
        return oImageProcessor;
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
    
}
