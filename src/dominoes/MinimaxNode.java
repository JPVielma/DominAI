/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import java.util.LinkedList;
import java.util.Stack;
import static dominoes.Constants.*;

/**
 *
 * @author juanp
 */
public class MinimaxNode {
    private int alpha;
    private int beta;
    public Tile state, tile;
    LinkedList<MinimaxNode> children = new LinkedList<>();
    private int[] playerTiles=new int[4];
    private int utility=0;
    private Player[] players=new Player[4];
    boolean min, user, stop;
    float probability, nodeProb;
    int value, n, depth, nChildren, nHand, nUniverse;
    TileCollection currentHand, world;
    Logging logging;
    
    public MinimaxNode(int index, Tile st, TileCollection hand, TileCollection world, int[] playerTiles, Tile t, Player[] ps, int alpha, int beta){
        logging=new Logging();
//        logging.logString(MINIMAX_NODECREATED, "New Minimax node");
        
        this.alpha=alpha;
        this.beta=beta;
        
        players=ps;
        depth=index;
        n=index%4;
        stop=false;
        
        
        //initUser
        if(n==0){
            user=true;
        }
        else{
            user=false;
        }
           
        this.world=world;
        currentHand=hand;
        this.playerTiles=playerTiles;
        
        //init MIN
        if(n==0||n==2){
            min=false;
        }
        else{
            min=true;
        }
        
        if(playerTiles[0]==0 || playerTiles[2]==0){
            stop=true;
            utility=10;
            return;
        }
        else if(playerTiles[1]==0 || playerTiles[3]==0){
            stop=true;
            utility=-10;
            return;
        }
        
        //init state, get probability
        tile=t;
        state=st;
        int a=state.getUpperValue();
        int b=state.getLowerValue();
        
        
        if(!user){
            if(a==b){
                 probability=players[n].getValueProb(a);
            }
            else{
                probability=players[n].getValueProb(a)+players[n].getValueProb(b)-(players[n].getValueProb(a)*players[n].getValueProb(b));
            }

            //init Utility
            if(probability<0.5){
                if(min){
                    utility=2;
                }
                else{
                    utility=-2; 
                }
            }
        }
        else{
            if(currentHand.getCandidates(a, a).isEmpty()){ //no podemos tirar del lado a
                utility=-1;
            }
            if(currentHand.getCandidates(b, b).isEmpty()){ //no podemos tirar del lado b
                utility=-1;
            }
        }
        
        if(probability==0){
            nodeProb=1;
        }
        else{
            nodeProb=probability;
        }
        
        nChildren=children.size();
        nHand=currentHand.size();
        nUniverse=world.size();
        
        logging.logString(MINIMAX_STATE, "["+st.getUpperValue()+","+st.getLowerValue()+"]Depth:"+depth+" Hand Size:"+nHand+" Universe Size:"+nUniverse+" Player["+n+"] Tiles:"+playerTiles[n]);
    }
    
    boolean isMin(){
        return min;
    }
    
    int getAlpha(){
        return alpha;
    }
    
    int getbeta(){
        return beta;
    }
    
    void setAlpha(int a){
        alpha=a;
    }
    
    void setBeta(int b){
        beta=b;
    }
    
    LinkedList getOptions(){
        return children;
    }
    
    Player getPlayer(){
        return players[n];
    }
    
    int getUtility(){
        return utility;
    }
    
    public void setChildren(){
        if(!stop){
            Tile s;
            int a=state.getLowerValue();
            int b=state.getUpperValue();
            int a2, b2;
            int[] auxTiles=new int[4];
            Stack<TileCollection> universe=new Stack();
            Stack<TileCollection> newHand=new Stack();
            MinimaxNode newNode;
            Tile t;
            if(!user){
                LinkedList<Tile> tiles = world.getCandidates(a, b);
                for(int i=0; i<tiles.size(); i++){//apply alphaBeta Prunning
                    auxTiles=playerTiles.clone();
                    if(players[n].getValueProb(a)==0 && players[n].getValueProb(b)==0){//no tiene ninguna ficha y pasa
                        newHand.push(new TileCollection(currentHand));
                        universe.push(new TileCollection(world));
                        utility++;
                        children.add(new MinimaxNode(depth+1, state, newHand.pop(), universe.pop(), auxTiles, null, players, alpha, beta));
                    }
                    else{
                        if(tiles.get(i).getLowerValue()==a || tiles.get(i).getUpperValue()==a){
                            a2=tiles.get(i).getComplimentary(a);
                            if(players[n].getBinProb(a2,b)==1){
                                newHand.push(new TileCollection(currentHand));
                                universe.push(new TileCollection(world));
                                universe.peek().delete(tiles.get(i).getLowerValue(), tiles.get(i).getUpperValue());
                                t=new Tile(a2, b);
                                auxTiles[n]--;
                                if(validateTiles(auxTiles))return;
                                children.add(new MinimaxNode(depth+1, t, newHand.pop(), universe.pop(), auxTiles, tiles.get(i), players, alpha, beta));
                            }
                        }
                          if((tiles.get(i).getLowerValue()==b || tiles.get(i).getUpperValue()==b) && !tiles.get(i).isDouble() && a!=b){
                            b2=tiles.get(i).getComplimentary(b);
                            if(players[n].getBinProb(a,b2)==1){
                                newHand.push(new TileCollection(currentHand));
                                universe.push(new TileCollection(world));
                                universe.peek().delete(tiles.get(i).getLowerValue(), tiles.get(i).getUpperValue());
                                t=new Tile(a, b2);
                                auxTiles[n]--;
                                if(validateTiles(auxTiles))return;
                                children.add(new MinimaxNode(depth+1, t, newHand.pop(), universe.pop(), auxTiles,tiles.get(i), players, alpha, beta));
                            }
                        }
                    }
                }
            }
            else{
                LinkedList<Tile> tiles = currentHand.getCandidates(a, b);
                for(int i=0; i<tiles.size(); i++){
                    auxTiles=playerTiles.clone();
                    if(tiles.get(i).getLowerValue()==a || tiles.get(i).getUpperValue()==a){
                        a2=tiles.get(i).getComplimentary(a);
                        newHand.push(new TileCollection(currentHand));
                        universe.push(new TileCollection(world));
                        newHand.peek().delete(tiles.get(i).getLowerValue(), tiles.get(i).getUpperValue());
                        t=new Tile(a2, b);
                        auxTiles[n]--;
                        if(validateTiles(auxTiles))return;
                        children.add(new MinimaxNode(depth+1, t, newHand.pop(), universe.pop(), auxTiles, tiles.get(i), players, alpha, beta));
                    }
                    if((tiles.get(i).getLowerValue()==b || tiles.get(i).getUpperValue()==b) && !tiles.get(i).isDouble() && a!=b){
                        b2=tiles.get(i).getComplimentary(b);
                        newHand.push(new TileCollection(currentHand));
                        universe.push(new TileCollection(world));
                        newHand.peek().delete(tiles.get(i).getLowerValue(), tiles.get(i).getUpperValue());
                        t=new Tile(a, b2);
                        auxTiles[n]--;
                        if(validateTiles(auxTiles))return;
                        children.add(new MinimaxNode(depth+1, t, newHand.pop(), universe.pop(), auxTiles, tiles.get(i), players, alpha, beta));
                    }
                }
            }
        }
    }
    
    public int minimax(int limit){
        int minval=30; int max=-30;int val;
        float probMin=0; float probMax=0;
        if(limit>=depth){
            this.setChildren();
        }
        if(depth==0 && children.size()==1)return 1;
        for(int i=0; i<children.size(); i++){

            children.get(i).setAlpha(alpha);
            children.get(i).setBeta(beta);
            
            if(min){
                if(minval>=alpha){
                    children.get(i).setValue(limit);
                }
                else{
                    logging.logString(PRUNNING, "PRUNNED MIN"+minval+"<"+alpha);
                }
            }
            else{
                if(max<=beta){
                    children.get(i).setValue(limit);
                }
                else{
                    logging.logString(PRUNNING, "PRUNNED MAX"+max+">"+beta);
                }
            }
            
            val=children.get(i).getValue();
            
            if(val>=max){
                if(!min){
                    alpha=val;
                }
                if(val==max){
                    if(probMax>children.get(i).getProbability()){
                        max=val;
                        probMax=children.get(i).getProbability();
                    }
                }
                else{
                    max=val;
                }
            }
            if(val<=minval){
                if(min){
                    beta=val;
                }
                if(val==minval){
                    if(probMin>children.get(i).getProbability()){
                        minval=val;
                        probMin=children.get(i).getProbability();
                    }
                }
                else{
                    minval=val;
                }
            }
        }
        if(min){
            if(minval>30)return minval;
            else return 0;
        }
        else{
            if(max>-30)return max;
            return 0;
        }
    }
    
    public boolean validateTiles(int[] tiles){
        if(gameIsClosed()){
            if(closeGame()){
                utility=10;
                logging.logString(MINIMAX_VICTORY ,"VICTORY Player["+n+"] Tiles:"+tiles[n]);
            }
            else{
                utility=-10;
                logging.logString(MINIMAX_DEFEAT ,"DEFEAT Player["+n+"] Tiles:"+tiles[n]);
            }
            stop=true;  
            return true;
        }
        if(tiles[n]==0){
            if(min){
                utility=-10;
                logging.logString(MINIMAX_DEFEAT ,"DEFEAT Player["+n+"] Tiles:"+tiles[n]);
            }
            else{
                utility=10;
                logging.logString(MINIMAX_VICTORY ,"VICTORY Player["+n+"] Tiles:"+tiles[n]);
            }
            stop=true;  
            return true;
        }
        return false;
    }
    
   public LinkedList<MinimaxNode> getChildrern(){
       return children;
   }
   
   public int getValue(){
       return value;
   }
   
   public void setValue(int limit){
       value=utility+this.minimax(limit);
   }
   
   public MinimaxNode getBestMove(){
        int val=0, max=-20, node=-1;
        float prob=1;
        for(int i=0; i<children.size(); i++){
            val=children.get(i).getValue();
            if(val>=max){
                max=val;
                    if(val==max){
                        if(children.get(i).getProbability()<prob){
                            prob=children.get(i).getProbability();
                            node=i;
                        }
                    }else{
                        node=i;
                    }       
            }
        }
        if(node==-1){
//            System.out.println("WARNING! null reference");
            return null;}
        return children.get(node);
   }
   
   public float getProbability(){
       return probability;
   }
   
   private boolean closeGame(){
       int count=currentHand.getSum()+world.greatestPossibleSum(players[2], players[2].getRemainingTiles());
       return count<(world.getSum()/2);
   }
   
   private boolean gameIsClosed(){
       return (world.getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && currentHand.getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty());
   }
    
}
