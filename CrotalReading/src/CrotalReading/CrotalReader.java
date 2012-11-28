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
    
    private static final int INITIALTHRESHOLD = 40;
    private static final int MAXTHRESHOLD = 80;
    
    private static final double MAXDENSITYNUMBER = 2500.0;
    private static final double MINDENSITYNUMBER = 500.0;
    
    private static final int MAXPARTICLESNUMBER = 4;
    

    /**
     * @param args the command line arguments
     */
    public static void VideoVigilanciaProcess(String sImagePath) {
        //get the angle to rotate the image
        double dAngle = getRotationAngle(sImagePath);  
        
        //get the image with the numbers to read
        ImagePlus img = getNumberImage(sImagePath, dAngle);
                
        //Presentarla en pantalla
        ImageWindow w = new ImageWindow(img);
        w.centerNextImage();
        
        img.show();
        
        
    }
    
    public static Point GetLowerPoint(ImageProcessor oImageProcessor, int[][] aPixels ){
        Point oLowerPoint = new Point();
        boolean bLowerPointFound = false;
        //look for the lower pixel
        for(int y = oImageProcessor.getHeight() -1; y >= 0; y--){
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
        if( oImageProcessor.getWidth() - oLowerPoint.x < oLowerPoint.x){
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
        
        for(int y = oImageProcessor.getHeight() -1; y >= 0; y--){

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

    private static double getRotationAngle(String sImagePath) {
        
        Opener opener = new Opener(); 
        ImagePlus img = null;
        try{
            img = opener.openImage(sImagePath);
        }
        catch(Exception e){
            
            int a = 0;
        }
        
        if(img == null){
            return 0;            
        }
        
        ImageProcessor  oImageP = img.getProcessor();
        oImageP.autoThreshold();
        
        IJ.run(img, "Convert to Mask", "");
        IJ.run(img, "Make Binary", "");

        //remove lower line
        oImageP.erode();
        //delete noise
        oImageP.dilate();

        //gets all pixels of the image
        int[][] aPixels = oImageP.getIntArray();

        //gets the lower point of the crotal
        Point oLowerPoint = GetLowerPoint(oImageP, aPixels);

        //gets the lower line of the crotal
        Line oLine = GetStraightLine(oImageP, oLowerPoint, aPixels);

        return oLine.getAngle()*63.661977236758* (-1);
    }

    private static ImagePlus getNumberImage(String sImagePath, double dAngle) {
        ImagePlus img = null;
        
        double dMinSize = MAXDENSITYNUMBER;
        int nParticles = 0;
        
        while(nParticles < MAXPARTICLESNUMBER &&  MINDENSITYNUMBER < dMinSize){            
            int iMaxParticles = 0;
            System.out.println("dMinSize: " + dMinSize);
            
            int iInitialThreshold = INITIALTHRESHOLD;
            while(nParticles < MAXPARTICLESNUMBER && iInitialThreshold < MAXTHRESHOLD ){
                System.out.println("Threshold: " + iInitialThreshold);
                
                img = prepareImage(sImagePath, dAngle, iInitialThreshold);
                
                nParticles = getParticles(img, dMinSize);

                iMaxParticles = (nParticles>iMaxParticles)?nParticles:iMaxParticles;
                
                iInitialThreshold = iInitialThreshold + (9 - (int)Math.pow(2,nParticles));
                                
            }
            dMinSize = dMinSize - (400/(iMaxParticles+1));
        }
        
        ImageProcessor  oFinalImageProcessor = img.getProcessor();
        dilateDigits(oFinalImageProcessor);
        
        return img;
    }

    private static ImagePlus prepareImage(String sImagePath, double dAngle, int iInitialThreshold) {
        Opener opener = new Opener(); 
        ImagePlus img = opener.openImage(sImagePath);

        ImageProcessor  oImageProcessor = img.getProcessor();
        oImageProcessor.threshold(iInitialThreshold);

        IJ.run(img, "Convert to Mask", "");
        IJ.run(img, "Make Binary", "");

        oImageProcessor.invertLut();

        oImageProcessor.erode();
        oImageProcessor.erode();

        oImageProcessor.rotate(dAngle);
        
        return img;
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
                oResultsTable,dMinSize,4000.0);
        oParticleAnalyzer.setHideOutputImage(true);
        oResultsTable.disableRowLabels();        

        boolean b = oParticleAnalyzer.analyze(img);

        int nParticles = oResultsTable.getCounter();    
        return nParticles;
    }
    
}
