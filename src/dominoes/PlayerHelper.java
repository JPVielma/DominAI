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
public class PlayerHelper {
    
    public PlayerHelper(){
        
    }
    
    public void tilePlayed(Player p1, Player p2, Player p3, int a, int b){
        p1.addBone(a, b);
        p2.addBone(a, b);
        p3.addBone(a, b);
    }
    
    public boolean normalize(Player p, Player op1, Player op2){
        int pAux, op1Aux, op2Aux;
        boolean rval=false;
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                pAux=p.getBinProb(i, j);
                op1Aux=op1.getBinProb(i, j);
                op2Aux=op2.getBinProb(i, j);
                
                if(pAux==1 && op1Aux==1){
                    op1.addBone(i, j);
                    rval=true;
                }
                if(pAux==1 && op2Aux==1){
                    op2.addBone(i, j);
                    rval=true;
                }
            }
        }
        return rval;
    }
    public void calculateProbability(Player p1, Player p2, Player p3){
        int sum;
        boolean repeat;
        
        do{
            repeat=false;
             if(p1.getPossibleN()==p1.getRemainingTiles()){
                normalize(p1, p2, p3);//we don't care if the first was modified
            }
            if(p2.getPossibleN()==p2.getRemainingTiles()){
                repeat=normalize(p2, p1, p3);
            }
            if(p3.getPossibleN()==p3.getRemainingTiles()){
                 repeat=normalize(p3, p2, p1);
            }
        }while(repeat);

        do{
            for(int i=0; i<7; i++){
                for(int j=i; j<7; j++){
                    sum=p1.getBinProb(i, j)+p2.getBinProb(i, j)+p3.getBinProb(i, j);
                    p1.calcRelativeProbAtIndex(i, j, sum);
                    p2.calcRelativeProbAtIndex(i, j, sum);
                    p3.calcRelativeProbAtIndex(i, j, sum);
                }
            }
        }while(p1.normalizeRelativeProb()||p2.normalizeRelativeProb()||p3.normalizeRelativeProb());

        
        int it1, it2, it3;
        
        for(int i=0; i<7; i++){
            for(int j=0; j<7; j++){
                if(p1.getBinProb(i, j)==1){
                    it1=p1.getIndeterminedTiles();
                }
                else{
                    it1=0;
                }
                if(p2.getBinProb(i, j)==1){
                    it2=p2.getIndeterminedTiles();
                }
                else{
                    it2=0;
                }
                if(p3.getBinProb(i, j)==1){
                    it3=p3.getIndeterminedTiles();
                }
                else{
                    it3=0;
                }

                p1.calcAbsProbAtIndex(i, j, it2, it3);
                p2.calcAbsProbAtIndex(i, j, it1, it3);
                p3.calcAbsProbAtIndex(i, j, it2, it1);
            }
        }
    }
}
