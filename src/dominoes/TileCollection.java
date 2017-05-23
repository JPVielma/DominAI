/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import static java.lang.Math.abs;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author juanp
 */
public class TileCollection {
    LinkedList<Tile> tiles = new LinkedList<>();
     
     public TileCollection(){
         
     }
     
     public TileCollection(TileCollection hand){
         tiles=(LinkedList) hand.tiles.clone();
     }
     
     public void addTile(int a, int b){
         Tile tile=new Tile(a, b);
         tiles.add(tile);
     }
     
     public LinkedList<Tile> getCandidates(int n, int m){
         LinkedList<Tile> candidates= new LinkedList<>();
         int u, l;
         for(int i=0; i<tiles.size(); i++){
             u=tiles.get(i).getUpperValue();
             l=tiles.get(i).getLowerValue();
             if(u==n || l==n || u==m || l==m){
                 candidates.add(tiles.get(i));
             }
         }
         
         return candidates;
     }
     
     public void delete(int a, int b){
        int l, u;
        for(int i=0; i<tiles.size(); i++){
             u=tiles.get(i).getUpperValue();
             l=tiles.get(i).getLowerValue();
             if((u==a && l==b) || (u==b && l==a)){
                 tiles.remove(i);
             }
        }
     }
     
     public Tile getRandom(){
        Random r=new Random();
        int i=abs(r.nextInt())%tiles.size();
        return tiles.get(i);
     }
     
     public int size(){
         return tiles.size();
     }
     
     public int getSum(){
         int count=0;
         for(int i=0; i<tiles.size(); i++){
             count+=tiles.get(i).getLowerValue()+tiles.get(i).getUpperValue();
         }
         return count;
     }
     
     public boolean exists(int a, int b){
         int u, l;
         for(int i=0; i<tiles.size(); i++){
             u=tiles.get(i).getUpperValue();
             l=tiles.get(i).getLowerValue();
             if((u==a && l==b) || (l==a && u==b) ){
                 return true;
             }
         }
         return false;
     }
     
     public int greatestPossibleSum(Player player, int n){
         int count=0;
         int max, limit, a, b;
         max=12;limit=6;
         for(int i=max; i>=0; i--){
             for(int j=limit; i-j<=limit; j--){
                 a=j; b=i-j;
                 if(exists(a, b) && player.getBinProb(a, b)==1){
                     count+=i;
                     n--;
                 }
                 if(n<=0){
                     return count;
                 }
             }
         }
         return count;
     }
     
     public Tile getHighest(){
         int max, limit, a, b;
         max=12;limit=6;
         Tile t;
         for(int i=max; i>=0; i--){
             for(int j=limit; i-j<=limit; j--){
                 a=j; b=i-j;
                 if(exists(a, b)){
                     t=new Tile(a, b);
                     return t;
                 }
             }
         }
         return null;
     }
     
     public Tile getHighestMatch(int u, int l){
         LinkedList<Tile> candidates=getCandidates(u,l);
         int max, limit, a, b, c, d;
         max=12;limit=6;
         Tile t=getHighestDouble(u,l);
         if(t==null){
            for(int i=max; i>=0; i--){
                for(int j=limit; i-j<=limit; j--){
                    a=j; b=i-j;
                    for(int k=0; k<candidates.size(); k++){
                        t=candidates.get(k);
                        c=t.getLowerValue();
                        d=t.getUpperValue();
                        if((c==a && d==b) || (d==a && c==b)){
                            return t;
                        }
                    }
                }
            }
         }
         else{
             return t;
         }
         return null;
     }
     
     public Tile getHighestDouble(int a, int b){
         if(a>b){
             if(exists(a, a)){
                 return new Tile(a, a);
             }
         }
         else{
             if(exists(b,b)){
                 return new Tile(b,b);
             }
         }  
         return null;
     }
     
     public void log(int tag, String s){
         Logging l=new Logging();
         for(int i=0; i<tiles.size(); i++){
            s=s+" ["+tiles.get(i).getLowerValue()+","+tiles.get(i).getUpperValue()+"]";
         }
          l.logString(tag,s);
     }
     
     public Tile getBestStart(Player player){
         Tile t=tiles.get(0);
         int a, b;
         float min, acum, p1, p2;
         min=1;
         for(int i=0; i<tiles.size(); i++){
             a=tiles.get(i).getUpperValue();
             b=tiles.get(i).getLowerValue();
             p1=player.getValueProb(a);
             if(tiles.get(i).isDouble()){
                 acum=p1;
             }
             else{
                 p2=player.getValueProb(b);
                 acum=p1+p2-(p1*p2);
             }
             if(acum<min || (acum==min && tiles.get(i).isDouble())){
                 min=acum;
                 t=tiles.get(i);
             }
         }
//         System.out.println("\n"+player.name);
//                for(int i=0; i<7; i++){
//                    System.out.print(i+":");
//                    for(int j=0; j<7; j++){
//                        System.out.print("["+String.format("%.2f", player.getAbsProb(i, j))+"]");
//                    }
//                    System.out.print(":"+String.format("%.0f", player.getValueProb(i)*100)+"%\n");
//           
//                }
//         log(42, "hand:");
//         System.out.println("chosen= ["+t.getUpperValue()+";"+t.getLowerValue()+"]");
         return t;
     }
}
