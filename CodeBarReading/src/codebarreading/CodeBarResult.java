/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codebarreading;

/**
 *
 * @author Orion
 */
public class CodeBarResult {
    private String sCodeBar;
    private Integer iLines;
    private Double dReliability;

    /**
     * @return the sCodeBar
     */
    public String getCodeBar() {
        return sCodeBar;
    }

    /**
     * @param sCodeBar the sCodeBar to set
     */
    public void setCodeBar(String sCodeBar) {
        this.sCodeBar = sCodeBar;
    }

    /**
     * @return the iLines
     */
    public Integer getLines() {
        return iLines;
    }

    /**
     * @param iLines the iLines to set
     */
    public void setLines(Integer iLines) {
        this.iLines = iLines;
    }

    /**
     * @return the dReliability
     */
    public Double getReliability() {
        return dReliability;
    }

    /**
     * @param dReliability the dReliability to set
     */
    public void setReliability(Double dReliability) {
        this.dReliability = dReliability;
    }
    
}
