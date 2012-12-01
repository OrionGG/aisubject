/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codebarreading;

/**
 *
 * @author Orion
 */
public class CodeReader {
    private String sCode;
    private int iPos;
    private int iSingleBarSize;

    /**
     * @return the sCode
     */
    public String getCode() {
        return sCode;
    }

    /**
     * @param sCode the sCode to set
     */
    public void setCode(String sCode) {
        this.sCode = sCode;
    }

    /**
     * @return the iPos
     */
    public int getPos() {
        return iPos;
    }

    /**
     * @param iPos the iPos to set
     */
    public void setPos(int iPos) {
        this.iPos = iPos;
    }
    
    
    public boolean isEmpty(){
        return (sCode.trim().length() < 2);
    }

    /**
     * @return the iSingleBarSize
     */
    public int getSingleBarSize() {
        return iSingleBarSize;
    }

    /**
     * @param iSingleBarSize the iSingleBarSize to set
     */
    public void setSingleBarSize(int iSingleBarSize) {
        this.iSingleBarSize = iSingleBarSize;
    }

    
}
