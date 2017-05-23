/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedList;
import static dominoes.Constants.*;

/**
 *
 * @author juanp
 */
public class Dominoes {

    static TileCollection hand=new TileCollection();
    static TileCollection universe = new TileCollection();
    static int[] playerTiles=new int[4];
    static int depth=4, userTiles=7;
    static Player[] players=new Player[4];
    static Player p0=new Player(0);
    static Player p1=new Player("Jugador 1");
    static Player p2=new Player("Jugador 2");
    static Player p3=new Player("Jugador 3");
        
    
    
    
    //FirstPlay
    //Listener
    public static void greed(){
        //si cerro la que abrio, no tiene X
        //si le pego al juego de su pareja, no tiene X
        
        //si no doblo no la tiene
        
    }
    public static void getBestMove(int a, int b){
        PlayerHelper helper=new PlayerHelper();
        helper.calculateProbability(p1, p2, p3);
        Tile state=new Tile(a, b);
        
        playerTiles[0]=userTiles;
        playerTiles[1]=p1.getRemainingTiles();
        playerTiles[2]=p2.getRemainingTiles();
        playerTiles[3]=p3.getRemainingTiles();
        
        players[1]=p1;
        players[2]=p2;
        players[3]=p3;
        
        MinimaxNode root=new MinimaxNode(0, state, hand, universe, playerTiles, state, players, -9999, 9999);
        root.setValue(depth);
        
        MinimaxNode child=root.getBestMove();
       if(child!=null)System.out.println("Tire ["+child.tile.getUpperValue()+","+child.tile.getLowerValue()+"], deje los extremos "+child.state.getUpperValue()+","+child.state.getLowerValue());
       else System.out.println("No se pudo determinar un movimiento.");
       
       root=null;
    }
    
    static void initUniverse(){
        for(int i=0; i<7; i++){
            for(int j=i; j<7; j++){
                universe.addTile(i, j);
            }
        }
        return;
    }
    
    static void menu(){
        System.out.println("Menu");
        System.out.println("1. Inicializar Mano de Juego");
        System.out.println("2. Registrar Tiro de un Jugador");
        System.out.println("3. Registrar Pase de un Jugador");
        System.out.println("4. Obtener Mejor Opcion");
        System.out.println("5. Registrar Movimiento propio");
        System.out.println("6. Determinar ficha de Inicio");
        System.out.println("7. Reiniciar");
        System.out.println("8. Terminar");
    }
    
    static void init(){
        p1.initPlayer();
        p2.initPlayer();
        p3.initPlayer();
        initUniverse();
        hand=new TileCollection();
        depth=4;
        userTiles=7;
    }
    
    public static void playerDraw(Player p, Player op1, Player op2, int a, int b){
        p.draw();
        p.addBone(a, b);
        op1.addBone(a, b);
        op2.addBone(a, b);
        depth++;
        universe.delete(a, b);
    }
    
    public static void userTurn(){
        Scanner scanner=new Scanner(System.in);
        int a, b;
            System.out.println("Primer Valor:");
                    a=scanner.nextInt();
                    System.out.println("Segundo Valor:");
                    b=scanner.nextInt();
            userTiles--;
            hand.delete(a, b);
            Logging logging=new Logging();
            hand.log(VS_STATE, "Hand: ");
            logging.logString(VS_PLAYER_DRAW, "User Draw ["+a+","+b+"]");
        depth++;
    }
    
    public static void start(){
        Tile t=hand.getBestStart(p1);
        System.out.println("Draw ["+t.getLowerValue()+","+t.getUpperValue()+"]");
    }
    
    public static void main(String[] args) {
        
        Logging logging = new Logging();
//        TestSuite test;
//        TestHelper tHelper;
//        int won, tie, lost;
//        won=0; tie=0; lost=0;
//        for(int i=0; i<100; i++){
//            tHelper=new TestHelper();
//            tHelper=null;
////            test=new TestSuite();
////            switch(test.getScore()){
////                case -1:
////                    lost++;
////                    break;
////                case 0:
////                    tie++;
////                    break;
////                case 1:
////                    won++;
////                    break;
////            }
////            test=null;
////             System.out.println("Won:"+won+" Lost:"+lost+" Tied:"+tie);
//        }
//       
        PlayerHelper helper=new PlayerHelper();
        Scanner input = new Scanner(System.in);
        int op, a, b, c;
        playerTiles[0]=7;
        init();
        do{
        menu();
        op=input.nextInt();
        switch(op){
            case 1:
                for(int i=0; i<7; i++){
                    System.out.println("Primer Valor:");
                    a=input.nextInt();
                    System.out.println("Segundo Valor:");
                    b=input.nextInt();
                    hand.addTile(a, b);
                    universe.delete(a, b);
                    helper.tilePlayed(p1, p2, p3, a, b);
                }
                hand.log(VS_STATE, "Hand:");
                break;
            case 2:
                System.out.println("Elija un Jugador:\n1)"+p1.getName()+"\n2)"+p2.getName()+"\n3)"+p3.getName());
                c=input.nextInt();
                System.out.println("Que ficha fue tirada?");
                System.out.println("Primer Valor:");
                    a=input.nextInt();
                    System.out.println("Segundo Valor:");
                    b=input.nextInt();
                    logging.logString(VS_PLAYER_DRAW, "Player "+c+" draws ["+a+","+b+"]");
                switch(c){
                    case 1:
                        playerDraw(p1, p2, p3, a, b);
                        break;
                    case 2:
                        playerDraw(p2, p1, p3, a, b);
                        break;
                    case 3:
                        playerDraw(p3, p2, p1, a, b);
                        break;
                }
                break;
            case 3:
                System.out.println("Elija un Jugador:\n1)"+p1.getName()+"\n2)"+p2.getName()+"\n3)"+p3.getName());
                c=input.nextInt();
                System.out.println("A que valor");
                System.out.println("Valor:");
                    a=input.nextInt();
                logging.logString(VS_PLAYER_PASS, "Player "+c+" pass to "+a);
                switch(c){
                    case 1:
                        p1.pass(a);
                        break;
                    case 2:
                        p2.pass(a);
                        break;
                    case 3:
                        p3.pass(a);
                        break;
                }
                break;
            case 4:
    
//                System.out.println("\n"+p1.name);
//                for(int i=0; i<7; i++){
//                    System.out.print(i+":");
//                    for(int j=0; j<7; j++){
//                        System.out.print("["+String.format("%.2f", p1.getAbsProb(i, j))+"]");
//                    }
//                    System.out.print(":"+String.format("%.0f", p1.getValueProb(i)*100)+"%\n");
//           
//                }
//                 System.out.println("\n"+p2.name);
//               for(int i=0; i<7; i++){
//                    System.out.print(i+":");
//                    for(int j=0; j<7; j++){
//                        System.out.print("["+String.format("%.2f", p2.getAbsProb(i, j))+"]");
//                    }
//                    System.out.print(":"+String.format("%.0f", p2.getValueProb(i)*100)+"%\n");
//           
//                }
//                System.out.println("\n"+p3.name);
//               for(int i=0; i<7; i++){
//                    System.out.print(i+":");
//                    for(int j=0; j<7; j++){
//                        System.out.print("["+String.format("%.2f", p3.getAbsProb(i, j))+"]");
//                    }
//                    System.out.print(":"+String.format("%.0f", p3.getValueProb(i)*100)+"%\n");
//           
//                }
               System.out.println("Cuales son los valores a los extremos de la Cadena?");
                System.out.println("Primer Valor:");
                    a=input.nextInt();
                    System.out.println("Segundo Valor:");
                    b=input.nextInt();
                    logging.logString(VS_STATE, "state:["+a+","+b+"]");
                    getBestMove(a, b);
                break;
            case 5:
                userTurn();
                break;
            case 6:
                start();
                break;
            case 7:
                System.out.println("Cual fue el resultado?");
                System.out.println("1. Ganado");
                System.out.println("2. Perdido");
                System.out.println("3. Abortado");
                c=input.nextInt();
                if(c==1)logging.logString(VS_VICTORY, "VICTORY\n\n");
                else if(c==2)logging.logString(VS_DEFEAT, "DEFEAT\n\n");
                init();
        }
    }while(op!=8);
    }
    
}
