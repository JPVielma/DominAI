/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominoes;

import static dominoes.Constants.*;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 *
 * @author juanp
 */
public class Logging {
    
    
    private static final String FILENAME = "test_25_Abril9.20";
    private static final String EXTENSION=".txt";
//    private static final String FILEPATH="C:\\Users\\juanp\\Desktop\\";
    private static final String FILEPATH= "C:\\Users\\juanp\\Dropbox\\Tec\\10mo!!!!!\\Tesina\\Resources\\Logs\\vsHuman\\";
//     private static final String FILEPATH= "C:\\Users\\juanp\\Dropbox\\Tec\\10mo!!!!!\\Tesina\\Resources\\Logs\\vsAI\\";
    String filePath;
    LinkedList<Integer> tags;
    public Logging(){
        filePath=FILEPATH+FILENAME;
        this.tags = new LinkedList<>();  
        
    }
    
    private void init(){
        
    }
    
    private boolean exists(int tag){
        return true;
    }
    
    public void logString(int tag, String s){
//        String file;
//        if(tag==DEBUG)System.out.println(s);
////        System.out.println(s);
//        if(tag>BEGIN_MINIMAX_BLOCK && tag<END_MINIMAX_BLOCK){
////            System.out.println(s);
////            file=FILEPATH+FILENAME+"_MINIMAX"+EXTENSION;
////            write(file, s);
//            return;
//        }
//        
//        if(tag>BEGIN_TEST_BLOCK && tag<END_TEST_BLOCK){
////            System.out.println(s);
//            file=FILEPATH+FILENAME+EXTENSION;
//            write(file, s);
//            return;
//        }
//        
//        if(tag>BEGIN_VS_BLOCK && tag<END_VS_BLOCK){
//            file=FILEPATH+FILENAME+EXTENSION;
//            write(file, s);
//            return;
//        }
//        
//        if(tag>BEGIN_ALTERNATE_BLOCK && tag<END_ALTERNATE_BLOCK){
//            file=FILEPATH+FILENAME+"_ALTERNATE"+EXTENSION;
//            write(file, s);
//                    if(tag>=ALTERNATE_PLAYER_VICTORY && tag<=ALTERNATE_GAME_BLOCKED){
//            write(file, "");
//            write(file, "");
//            return;
//        }
//            return;
//        }
//        
//        if(tag==COMPARISON){
//            file=FILEPATH+FILENAME+"_COMPARISON"+EXTENSION;
//            write(file, s);
//            return;
//        }
//
//        if(tag>=PLAYER_VICTORY && tag<=GAME_BLOCKED){
//            file=FILEPATH+FILENAME+EXTENSION;
//            write(file, s);
//            write(file, "");
//            write(file, "");
//            return;
//        }
    }
    
    public void playerDrew(){
        
    }
    
    public void logScore(){
        
    }
    
    public void write(String fileName, String content){
        BufferedWriter bw = null;
		FileWriter fw = null;

		try {

			String data = content;

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

			bw.write(data);
                        bw.newLine();

//			System.out.println("Logged "+content);

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
    }
}
