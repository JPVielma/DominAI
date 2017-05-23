/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import java.util.Arrays;

/**
 *
 * @author juanp
 */
public final class Player {
    private final int[][] binProb=new int[7][7];
    private final float[][] relProb=new float[7][7];
    private final float[][] absProb=new float[7][7];
    int bonesLeft;
    private int[] opened, closed, ignored;
    String name;
    
    public Player(){
        initPlayer();
    }
    
    public Player(String name){
        initPlayer();
        this.name=name;
    }
    
    public Player(int n){//User
        if(n==0){
            for(int[] row:binProb){
            Arrays.fill(row, 0);
        }
        for(float[] row:relProb){
            Arrays.fill(row, 0);
        }
        for(float[] row:absProb){
            Arrays.fill(row, 0);
        }
        bonesLeft=7;
        }
        
    }
    
    public void initPlayer(){
        for(int[] row:binProb){
            Arrays.fill(row, 1);
        }
        for(float[] row:relProb){
            Arrays.fill(row, 0);
        }
        for(float[] row:absProb){
            Arrays.fill(row, 0);
        }
//        Arrays.fill(opened, 0);
//        Arrays.fill(closed, 0);
//        Arrays.fill(ignored, 0);
        bonesLeft=7;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String n){
        name=n;
    }
    
    public void addBone(int a, int b){
        binProb[a][b]=0;
        binProb[b][a]=0;
    }
    
    public void draw(){
        bonesLeft--;
    }
    
    public void rollBack(int a, int b){
        binProb[a][b]=1;
        binProb[b][a]=1;
    }
    
    public void calcRelativeProbAtIndex(int a, int b, int val){
        if(val!=0){
            float n=(float)binProb[a][b]/val;
            relProb[a][b]=n;
            relProb[b][a]=n;
        }
        else{
            relProb[a][b]=0;
            relProb[b][a]=0;
        }
    }
    
    public int getBinProb(int a, int b){
        return binProb[a][b];
    }
    
    public float getRelProb(int a, int b){
        return relProb[a][b];
    }
    
    public float getAbsProb(int a, int b){
        return absProb[a][b];
    }
    
    public void pass(int val){
        for(int i=0; i<7;i++){
            binProb[val][i]=0;
            binProb[i][val]=0;
        }
    }
    
    public int getIndeterminedTiles(){
        int count=0;
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                if(relProb[i][j]==1){
                    count++;
                }
            }
        }
        return bonesLeft-count;
    }
    
    public void calcAbsProbAtIndex(int a, int b, int p1, int p2){
        if(binProb[a][b]!=0){
            float p0=(float)getIndeterminedTiles();
            float total=(float)p1+p2+p0;
            float prob;
            if(total!=0){
                prob=p0/total;
            }
            else{
                prob=0;
            }
            absProb[a][b]=prob;
            absProb[b][a]=prob;
        }
        else{
            absProb[a][b]=0;
            absProb[b][a]=0;
        }
    }
    
    public int getPossibleN(){
        int count=0;
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                if(binProb[i][j]==1){
                    count++;
                }
            }
        }
        return bonesLeft-count;
    }
    
    public int getRemainingTiles(){
        return bonesLeft;
    }
    public boolean normalizeRelativeProb(){
        
        boolean change=false;
        if(getIndeterminedTiles()==0){
            for(int i=0; i<7; i++){
                for(int j=i; j<7; j++){
                    if(relProb[i][j]!=1){
                        if(binProb[i][j]!=0 || binProb[j][i]!=0){
                            binProb[i][j]=0;
                            binProb[j][i]=0;
                            change=true;
                        }   
                    }
                }
            }
        }
        return change;
    }
    
    public float getValueProb(int a){
        float prob=0;
        for(int i=0; i<7; i++){
            prob=prob+absProb[a][i]-(prob*absProb[a][i]);
        }
        return prob;
    }
   
}
