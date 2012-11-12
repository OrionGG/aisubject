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

    /**
     * @param args the command line arguments
     */
    public static void VideoVigilanciaProcess(String sImagePath) {
        //Cargar imagen
        Opener opener = new Opener();
        ImagePlus img = opener.openImage(sImagePath);
        
        
        ImageProcessor  oImageProcessor = img.getProcessor();
//        img.getProcessor().autoThreshold();
//        IJ.setAutoThreshold(img, "Default");
//        oImageProcessor.setThreshold(30, 50,1);
        oImageProcessor.threshold(45);
        IJ.run(img, "Convert to Mask", "");
        IJ.run(img, "Make Binary", "");
        oImageProcessor.invertLut();
       
        //remove lower line
        oImageProcessor.dilate();
        //delete noise
        oImageProcessor.erode();
        oImageProcessor.erode();
        oImageProcessor.dilate();
        oImageProcessor.dilate();

        oImageProcessor.invertLut();

                
        int[][] aPixels = oImageProcessor.getIntArray();
        
        Point oLowerPoint = GetLowerPoint(oImageProcessor, aPixels);
        
        Line oLine = GetStraightLine(oImageProcessor, oLowerPoint, aPixels);
        
        oImageProcessor.rotate(oLine.getAngle()*63.661977236758* (-1));
                
        oImageProcessor.invertLut();
        
        ImagePlus oImgRotated =  new ImagePlus("ImageRotated", oImageProcessor);
        
        ResultsTable oResultsTable = new ResultsTable();
        
        int[] aMeasurements = new int[]{Measurements.LABELS, Measurements.AREA, Measurements.PERIMETER, Measurements.CIRCULARITY, Measurements.RECT, Measurements.SLICE};
        
        int iMeasurementsValue = GetMeasurementsValue(aMeasurements);
        
        ParticleAnalyzer oParticleAnalyzer = new ParticleAnalyzer(
                ParticleAnalyzer.SHOW_MASKS+ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES+ParticleAnalyzer.IN_SITU_SHOW , 
                iMeasurementsValue,
                oResultsTable ,500.0,Double.MAX_VALUE);
        oParticleAnalyzer.setHideOutputImage(true);
        oResultsTable.disableRowLabels();        
        
        boolean b = oParticleAnalyzer.analyze(img);
        
        int nParticles = oResultsTable.getCounter();         
        
                
        for(int j=0;j<=oResultsTable.getLastColumn();j++){
            if(oResultsTable.getColumn(j) != null){
                String sColumnName = oResultsTable.getColumnHeading(j);
                
                    System.out.println(sColumnName);

                        for (int i=0; i<nParticles;i++){
                    
                    double dValue = oResultsTable.getValueAsDouble(j, i);
                    System.out.print(dValue+ "; ");
                }
                    System.out.println();
            }
        }
        
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
    
}
