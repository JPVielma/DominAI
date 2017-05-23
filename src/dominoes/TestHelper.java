/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import static dominoes.Constants.*;

/**
 *
 * @author juanp
 */
public class TestHelper {
     private Player[][] players=new Player[4][4];
     private TileCollection[] universe=new TileCollection[4];
     private TileCollection[] hands=new TileCollection[4];
     Tile state;
     int index;
     int depth;
     Logging logging;
     boolean won, tie;
     
     public TestHelper(){
        tie=false;
        won=false;
        
        for(int i=0; i<4; i++){

            players[i][1]=new Player("Player 1");
            players[i][2]=new Player("Player 2");
            players[i][3]=new Player("Player 3");
          
            universe[i]=new TileCollection();
            hands[i]=new TileCollection();
        }
        
//         players[3][1]=new Player("Player 1");
//         players[3][2]=new Player("Player 2");
//         players[3][3]=new Player("Player 3");
       
        depth=4;
        logging=new Logging();
        initUniverse();
        handOutTiles();
        start();
     }
     
     public void playerPass(int a, int n){
         int aux;
         for(int i=0; i<4; i++){
             aux=(4-i+n)%4;
             if(aux!=0){
                 players[i][aux].pass(a);
             }
         }
     }
     
     public void playerDraw(int n){
         int aux;
         for(int i=0; i<4; i++){
              aux=(4-i+n)%4;
             if(aux!=0){
                 players[i][aux].draw();
             }
         }
     }
     
     public void addBone(int a, int b){
         for(int i=0; i<4; i++){
             for(int j=0; j<4; j++){
                 if(j!=0){
                    players[i][j].draw();
                 }
             }
         }
     }
     
     private void tilePlayed(int a, int b){
         for(int i=0; i<4; i++){
             universe[i].delete(a, b);
         }
     }
     
     private void initUniverse(){
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                universe[0].addTile(i, j);
                universe[1].addTile(i, j);
                universe[2].addTile(i, j);
                universe[3].addTile(i, j);
                
            }
        }
    }
     
    private void handOutTiles(){
        PlayerHelper helper=new PlayerHelper();
        TileCollection world=new TileCollection(universe[0]);
        Tile t;
        for(int i=0; world.size()>0; i++){
            t=world.getRandom();
            hands[i%4].addTile(t.getLowerValue(), t.getUpperValue());
            world.delete(t.getLowerValue(), t.getUpperValue());
            universe[i%4].delete(t.getLowerValue(), t.getUpperValue());
            helper.tilePlayed(players[i%4][1], players[i%4][2], players[i%4][3], t.getLowerValue(), t.getUpperValue());
        }
    }
     
     public MinimaxNode getBestMove(int n){
        PlayerHelper helper=new PlayerHelper();
        helper.calculateProbability(players[n][1], players[n][2], players[n][3]);
        
        int[] playerTiles=new int[4];
        
        playerTiles[0]=hands[0].size();
        playerTiles[1]=hands[1].size();
        playerTiles[2]=hands[2].size();
        playerTiles[3]=hands[3].size();
        
        logging.logString(DEBUG, "Starting Minimax:");
        MinimaxNode root=new MinimaxNode(0, state, hands[n], universe[n], playerTiles, state, players[n], -9999, 9999);
        if(n%2==0)root.setValue(12);
        else root.setValue(2);
        
       MinimaxNode child=root.getBestMove();
       try{
            if(child!=null)return child;
       }catch(NullPointerException e){
           return null;
       }
         System.out.println("Nothing found");
       return null;
    }
     
     public void start(){
        for(int i=0; i<4; i++){
            if(hands[i].exists(6, 6)){
                depth+=i+1;
                hands[i].delete(6, 6);
                tilePlayed(6,6);
                state=new Tile(6, 6);
                simulate();
                return;
            }
        }
     }
     
     private void simulate(){
        int turn=depth%4;
        universe[turn].log(BEGIN_TEST_BLOCK+1, "Universe: ");
        if(universe[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty() && hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
            int myCount=hands[0].getSum()+hands[2].getSum();
            int opCount=hands[1].getSum()+hands[3].getSum();
            logging.logString(GAME_BLOCKED, "Game Blocked Count="+myCount+" OPCount="+opCount);
            if(myCount<opCount){
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
        hands[turn].log(TEST_PLAYER_TURN, "Player "+turn+" state:["+state.getLowerValue()+","+state.getUpperValue()+"] | hand:");
        int a, b;
        Tile t;
        MinimaxNode move;
        if(turn==0 || turn==2 || turn==1 || turn==3){
            if(hands[turn].size()==1 && hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).size()==1){
                if(turn==0 || turn==2){
                    logging.logString(PLAYER_VICTORY, "VICTORY");
                    won=true;
                }
                else{
                    logging.logString(PLAYER_VICTORY, "DEFEAT");
                    won=false;
                }
                return;
            }
            try{
                if(hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
                    t=null;
                    move=null;
                }
                else{
                    move=getBestMove(turn);
                    t=move.tile;
                }
            }
            catch(NullPointerException e){
                t=null;
                move=null;
            }
            
            if(t==null){
                if(hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).size()>0){
                    forceDraw(turn);
                }
                else{
                    playerPass(state.getUpperValue(), turn);
                    playerPass(state.getLowerValue(), turn);
                    logging.logString(TEST_PLAYER_PASS, "Player "+turn+" Pass on ["+state.getLowerValue()+","+state.getUpperValue()+"]");
                }
            }
            else{
                hands[turn].delete(t.getLowerValue(), t.getUpperValue());
                state=move.state;
                tilePlayed(t.getLowerValue(), t.getUpperValue());
                logging.logString(TEST_PLAYER_DRAW, "Player "+turn+" Draw ["+t.getLowerValue()+","+t.getUpperValue()+"]");
            }
        }
        else{
            t=hands[turn].getHighestMatch(state.getLowerValue(), state.getUpperValue());
            if(t==null){
//            if(hands[turn].getCandidates(state.getLowerValue(), state.getUpperValue()).isEmpty()){
                playerPass(state.getUpperValue(), turn);
                playerPass(state.getLowerValue(), turn);
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
                playerDraw(turn);
                addBone(a, b);
                tilePlayed(a,b);
                hands[turn].delete(a, b);
                logging.logString(TEST_PLAYER_DRAW, "Player["+turn+"] Draws ["+t.getLowerValue()+","+t.getUpperValue()+"]");
            }
        }
        depth++;
        
        if(validate())return;
        move=null;
        simulate();
    }
     
     private boolean validate(){
         String s;
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
         return false;
     }
     
     public void  forceDraw(int n){
         System.out.println("FORCE DRAW");
         Tile t=hands[n].getHighestMatch(state.getUpperValue(), state.getLowerValue());
         int a=t.getLowerValue();
                int b=t.getUpperValue();
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
                playerDraw(n);
                addBone( a, b);
                playerDraw(n);
                hands[n].delete(a, b);
                logging.logString(TEST_PLAYER_DRAW, "Player["+n+"] Draws ["+a+","+b+"]");
     }
}
