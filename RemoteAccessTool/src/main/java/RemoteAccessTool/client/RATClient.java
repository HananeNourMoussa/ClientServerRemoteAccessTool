/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RemoteAccessTool.client;

/**
 *
 * @author Hanane Nour
 */
import java.net.*; 
import java.io.*; 
import java.util.StringTokenizer; 

/**
 *
 * @author Hanane Nour
 */
public class RATClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        //get command from user
        String command = args[0]; 
        
        try(Socket connectionToServer = new Socket("localhost", 100)){
            
            InputStream in  = connectionToServer.getInputStream();
            OutputStream out = connectionToServer.getOutputStream(); 
            
            BufferedReader headerReader = new BufferedReader(new InputStreamReader(in)); 
            BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out)); 
            
            DataInputStream dataIn = new DataInputStream(in); 
            
            if (command.equals("p")){
                //create header
                String header = "processes\n"; 
                headerWriter.write(header, 0, header.length());
                headerWriter.flush();
                
                //read response
                String response = headerReader.readLine(); 
                //react to response 
                StringTokenizer strk = new StringTokenizer(response, " "); 
                String status = strk.nextToken();
                if (status.equals("OK"))
                {
                    String temp = strk.nextToken();
                    int size = Integer.parseInt(temp); 
                    byte[] space = new byte[size]; 
                    dataIn.read(space, 0, size);
                    String processInfo = new String(space); 
                    System.out.println(processInfo);
                }else{
                    System.out.println("You're not connected to the right server");
                }
                
            }
            else if (command.equals("s")){
                //create header
                String header = "screenshot\n"; 
                headerWriter.write(header, 0, header.length());
                headerWriter.flush();
                //read response
                String response = headerReader.readLine(); 
                //react to response 
                StringTokenizer strk = new StringTokenizer(response, " "); 
                String status = strk.nextToken();
                if (status.equals("OK"))
                {
                    String temp = strk.nextToken();
                    int size = Integer.parseInt(temp); 
                    byte[] space = new byte[size]; 
                    dataIn.readFully(space);
                    //store screenshot in this path
                    String path = "C:\\Users\\"+System.getProperty("user.name")+"\\Documents\\RAT_Screenshot.bmp"; 
                    try(FileOutputStream fileOut = new FileOutputStream(path)){
                        fileOut.write(space, 0, size);
                    }
                    System.out.print("Screenshot in "+path);
                }else{
                    System.out.println("You're not connected to the right server");
                }
                
            }
            else if (command.equals("r")){
                //create header
                String header = "reboot\n"; 
                headerWriter.write(header, 0, header.length());
                headerWriter.flush();
                //read response
                String response = headerReader.readLine(); 
                //react to response 
                
                if (response.equals("OK"))
                {
                    System.out.println("System will reboot in 5 seconds.");
                    
                }else{
                    System.out.println("You're not connected to the right server");
                }
            }
            else
            {
                System.out.println("Unsupported command");
                System.exit(0); 
                
            }
        }
    }
    
}
