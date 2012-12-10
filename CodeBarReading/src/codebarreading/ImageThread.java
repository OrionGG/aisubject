/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codebarreading;

import ij.ImagePlus;
import java.util.HashMap;

/**
 *
 * @author Orion
 */
public class ImageThread extends Thread {
    HashMap<String, Integer> mFullCodeLineCounter = new HashMap<>();
    private int iy1;
    private int iy2;
    private ImagePlus oImagePlus; 
        
    
    public ImageThread(int y1, int y2, ImagePlus oImagePlusC){
        oImagePlus = oImagePlusC;
        iy1 = y1;
        iy2 = y2;
    }
    
    public void run(){        
        
        
        String sFinalCode = "";
        
        for(int i = iy1; i < iy2; i++){
            String sLine = CodeBarReading.readLine(i,oImagePlus);
            
            int iCount = 0;
            if(mFullCodeLineCounter.containsKey(sLine)){
                iCount = mFullCodeLineCounter.get(sLine);   
            }
            
            iCount++;
            mFullCodeLineCounter.put(sLine, iCount);
            
        }
        
    }

}
