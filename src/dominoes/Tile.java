/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

/**
 *
 * @author juanp
 */
public class Tile {
    private int upperValue;
    private int lowerValue;
    private boolean doubleTile=false;
    
    public Tile(int a, int b){
        upperValue=a;
        lowerValue=b;
        
        if(a==b)doubleTile=true;
    }
    
    public int totalPoints(){
        return upperValue+lowerValue;
    }
    
    public boolean isDouble(){
        return doubleTile;
    }
    
    public int getUpperValue(){
        return upperValue;
    }
    
    public int getLowerValue(){
        return lowerValue;
    }
    
    public boolean hasValue(int a){
        if(upperValue==a||lowerValue==a){
            return true;
        }
        else{
            return false;
        }
    }
    
    public int getComplimentary(int a){
        if(hasValue(a)){
            if(lowerValue==a){
                return upperValue;
            }
            else{
                return lowerValue;
            }
        }
        else{
            return -1;
        }
    }
    
}
