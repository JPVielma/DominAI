/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import static dominoes.Constants.*;
import java.util.Random;

/**
 *
 * @author juanp
 */
public class TestSuite {
    private Player[] players=new Player[4];
    TileCollection universe;
    TileCollection[] hands=new TileCollection[4];
    TileCollection[] alternateHands=new TileCollection[4];
    private Tile state;
    private int depth;
    int[] playerTiles=new int[4];
    private boolean finished, tie, won, won2;
    public Logging logging;
    
    public TestSuite(){
        tie=false;
        won=false;
        won2=false;
        
        players[1]=new Player();
        players[2]=new Player();
        players[3]=new Player();
        
        playerTiles[0]=7;
        playerTiles[1]=players[1].getRemainingTiles();
        playerTiles[2]=players[2].getRemainingTiles();
        playerTiles[3]=players[3].getRemainingTiles();
        
        depth=4;
        
        universe=new TileCollection();
        
        for(int i=0; i<4; i++){
            hands[i]=new TileCollection();
            alternateHands[i]=new TileCollection();
        }
        
        logging=new Logging();
        initUniverse();
        handOutTiles();
        start();
        compare();
    }
    
    private void compare(){
        if(won==won2){
            if(won)logging.logString(COMPARISON, "NO CHANGES, VICTORY.");
            else logging.logString(COMPARISON, "NO CHANGES, DEFEAT.");
        }
        else if (won && !won2){
             logging.logString(COMPARISON, "WON");
        }
        else if (won2 && !won){
             logging.logString(COMPARISON, "LOST");
        }
    }
    
    private void initUniverse(){
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                universe.addTile(i, j);
            }
        }
    }
    
    private void handOutTiles(){
        PlayerHelper helper=new PlayerHelper();
        TileCollection world=new TileCollection(universe);
        Tile t;
        for(int i=0; world.size()>0; i++){
            t=world.getRandom();
            hands[i%4].addTile(t.getLowerValue(), t.getUpperValue());
            alternateHands[i%4].addTile(t.getLowerValue(), t.getUpperValue());
            world.delete(t.getLowerValue(), t.getUpperValue());
            if(i%4==0){
                universe.delete(t.getLowerValue(), t.getUpperValue());
                helper.tilePlayed(players[1], players[2], players[3], t.getLowerValue(), t.getUpperValue());
            }
        }
    }
    
    private void start(){
        logging.logString(0, "Test Suite Started");
//        //First Turn, [6,6] starts
        for(int i=0; i<4; i++){
            if(hands[i].exists(6, 6)){
                depth=i+1;
                hands[i].delete(6, 6);
                alternateHands[0].delete(6,6);
                universe.delete(6, 6);
                state=new Tile(6, 6);
                simulate();
                state=new Tile(6, 6);
                depth=i+1;
                simulateAlternative();
                return;
            }
        }

//      //User Starts
//        PlayerHelper helper=new PlayerHelper();
//        helper.calculateProbability(players[1], players[2], players[3]);
//        Tile startTile=hands[0].getBestStart(players[1]);
//        int a=startTile.getLowerValue();
//        int b=startTile.getUpperValue();
//                depth=5;
//                hands[0].delete(a, b);
//                alternateHands[0].delete(a,b);
//                universe.delete(a, b);
//                state=new Tile(a, b);
//                simulate();
//                state=new Tile(a, b);
//depth=i+1;
//                simulateAlternative();

//     //Oponent Starts
//        Random r=new Random();
//        int i=r.nextInt(2);
//        if(i==0){
//            i=3;
//        }
//        Tile t=hands[i].getHighest();
//        if(t==null)System.out.println("NULL REFERENCE");
//        int a=t.getLowerValue();
//        int b=t.getUpperValue();
//                depth=i+5;
//                hands[i].delete(a, b);
//                alternateHands[0].delete(a,b);
//                universe.delete(a, b);
//                state=new Tile(a, b);
//                simulate();
//                state=new Tile(a, b);
//depth=i+1;
//                simulateAlternative();
    }
    
    private void simulate(){
        if(universe.getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && hands[0].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
            int myCount=hands[0].getSum()+hands[2].getSum();
            int opCount=hands[1].getSum()+hands[3].getSum();
            logging.logString(GAME_BLOCKED, "Game Blocked");
            if(myCount>opCount){
                logging.logString(PLAYER_VICTORY, "VICTORY.");
                won=true;
            }
            else if(myCount==opCount){
                logging.logString(GAME_BLOCKED, "TIE.");
                tie=true;
            }
            else{ 
                logging.logString(PLAYER_VICTORY, "DEFEAT.");
                won=false;
            }
            return;
        }
        
        PlayerHelper helper=new PlayerHelper();
        int turn=depth%4;
        hands[turn].log(TEST_PLAYER_TURN, "Player "+turn+" state:["+state.getLowerValue()+","+state.getUpperValue()+"] | hand:");
        int a, b;
        Tile t;
        MinimaxNode move;
        if(turn==0){
            universe.log(DEBUG, "Universe:");
            if(hands[0].size()==1 && hands[0].getCandidates(state.getLowerValue(), state.getUpperValue()).size()==1){
                logging.logString(PLAYER_VICTORY, "VICTORY");
                won=true;
                return;
            }
            try{
                if(hands[0].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
                    t=null;
                    move=null;
                }
                else{
                    move=getBestMove();
                    t=move.tile;
                }
            }
            catch(NullPointerException e){
                t=null;
                move=null;
            }
            
            if(t==null){
                logging.logString(TEST_PLAYER_PASS, "User Pass on ["+state.getLowerValue()+","+state.getUpperValue()+"]");
            }
            else{
                hands[0].delete(t.getLowerValue(), t.getUpperValue());
                state=move.state;
                playerTiles[0]--;
                logging.logString(TEST_PLAYER_DRAW, "User Draw ["+t.getLowerValue()+","+t.getUpperValue()+"]");
            }
            move=null;
        }
        else{
            t=hands[turn].getHighestMatch(state.getLowerValue(), state.getUpperValue());
            if(t==null){
//            if(hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
                players[turn].pass(state.getUpperValue());
                players[turn].pass(state.getLowerValue());
                logging.logString(TEST_PLAYER_PASS, "Player["+turn+"] Pass on ["+state.getLowerValue()+","+state.getUpperValue()+"]");
                
            }
            else{   
                a=t.getLowerValue();
                b=t.getUpperValue();
                if(t.getLowerValue()==state.getLowerValue()){
                    state=new Tile(t.getComplimentary(t.getLowerValue()), state.getUpperValue());
                }
                else if(t.getUpperValue()==state.getLowerValue()){
                    state=new Tile(t.getComplimentary(t.getUpperValue()), state.getUpperValue());
                }
                else if(t.getLowerValue()==state.getUpperValue()){
                    state=new Tile(t.getComplimentary(t.getLowerValue()), state.getLowerValue());
                }
                else {
                    state=new Tile(t.getComplimentary(t.getUpperValue()), state.getLowerValue());
                }
                players[turn].draw();
                helper.tilePlayed(players[1], players[2], players[3], a, b);
                hands[turn].delete(a, b);
                universe.delete(a, b);
                logging.logString(TEST_PLAYER_DRAW, "Player["+turn+"] Draws ["+t.getLowerValue()+","+t.getUpperValue()+"]");
            }
        }
        depth++;
        
        if(validate(true))return;
        simulate();
    }
    
    private boolean validate(boolean flag){
        String s;
        if(flag){
            for(int i=0; i<4; i++){
                if(hands[i].size()==0){
                    s= "Player["+i+"] won.";
                    if(i==0||i==2){
                        won=true;
                        s=s+" VICTORY.";
                    }
                    else{ 
                        won=false;
                        s=s+" DEFEAT.";
                    }
                    logging.logString(PLAYER_VICTORY, s);
                    return true;
                }
            }
        }
        else{
             for(int i=0; i<4; i++){
                if(alternateHands[i].size()==0){
                    s= "Player["+i+"] won.";
                    if(i==0||i==2){
                        won2=true;
                        s=s+" VICTORY.";
                    }
                    else{ 
                        won2=false;
                        s=s+" DEFEAT.";
                    }
                    logging.logString(ALTERNATE_PLAYER_VICTORY, s);
                    return true;
                }
            }
        }
        return false;
    }
    
    public void playerDraw(Player p, Player op1, Player op2, int a, int b){
        p.draw();
        p.addBone(a, b);
        op1.addBone(a, b);
        op2.addBone(a, b);
        universe.delete(a, b);
    }
    
    public MinimaxNode getBestMove(){
        PlayerHelper helper=new PlayerHelper();
        helper.calculateProbability(players[1], players[2], players[3]);
        
        playerTiles[1]=players[1].getRemainingTiles();
        playerTiles[2]=players[2].getRemainingTiles();
        playerTiles[3]=players[3].getRemainingTiles();
        
        MinimaxNode root=new MinimaxNode(0, state, hands[0], universe, playerTiles, state, players, -9999, 9999);
        root.setValue(depth);
        
       MinimaxNode child=root.getBestMove();
       try{
            if(child!=null)return child;
       }catch(NullPointerException e){
           return null;
       }
       return null;
    }
    
    public int getScore(){
        if(tie)return 0;
        else if(won) return 1;
        else return -1;
    }
    
       private void simulateAlternative(){
        if(alternateHands[1].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && alternateHands[0].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && alternateHands[2].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && alternateHands[3].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
            int myCount=alternateHands[0].getSum()+alternateHands[2].getSum();
            int opCount=alternateHands[1].getSum()+alternateHands[3].getSum();
            logging.logString(ALTERNATE_GAME_BLOCKED, "Game Blocked");
            if(myCount>opCount){
                logging.logString(PLAYER_VICTORY, "VICTORY.");
                won=true;
            }
            else if(myCount==opCount){
                logging.logString(ALTERNATE_GAME_BLOCKED, "TIE.");
                tie=true;
            }
            else{ 
                logging.logString(ALTERNATE_PLAYER_VICTORY, "DEFEAT.");
                won=false;
            }
            return;
        }
        int turn=depth%4;
        alternateHands[turn].log(ALTERNATE_PLAYER_TURN, "Player "+turn+" state:["+state.getLowerValue()+","+state.getUpperValue()+"] | hand:");
        int a, b;
        Tile t;
       
            t=alternateHands[turn].getHighestMatch(state.getLowerValue(), state.getUpperValue());
            if(t==null){
//            if(hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
                logging.logString(ALTERNATE_PLAYER_PASS, "Player["+turn+"] Pass on ["+state.getLowerValue()+","+state.getUpperValue()+"]");
                
            }
            else{   
                a=t.getLowerValue();
                b=t.getUpperValue();
                if(t.getLowerValue()==state.getLowerValue()){
                    state=new Tile(t.getComplimentary(t.getLowerValue()), state.getUpperValue());
                }
                else if(t.getUpperValue()==state.getLowerValue()){
                    state=new Tile(t.getComplimentary(t.getUpperValue()), state.getUpperValue());
                }
                else if(t.getLowerValue()==state.getUpperValue()){
                    state=new Tile(t.getComplimentary(t.getLowerValue()), state.getLowerValue());
                }
                else {
                    state=new Tile(t.getComplimentary(t.getUpperValue()), state.getLowerValue());
                }
                alternateHands[turn].delete(a, b);
                logging.logString(ALTERNATE_PLAYER_DRAW, "Player["+turn+"] Draws ["+t.getLowerValue()+","+t.getUpperValue()+"]");
            }
  
        depth++;
        
        if(validate(false))return;
        simulateAlternative();
    }
}
