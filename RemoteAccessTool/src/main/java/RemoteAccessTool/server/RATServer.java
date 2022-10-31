/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RemoteAccessTool.server;

/**
 *
 * @author Hanane Nour
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 *
 * @author Hanane Nour
 */
public class RATServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
        try (ServerSocket ss = new ServerSocket(100)){
            while(true){
                System.out.println("Server waiting...");
                Socket connectionFromClient = ss.accept();
                System.out.println("Server received connection from client with port"+connectionFromClient.getPort());
                try{
                    InputStream in  = connectionFromClient.getInputStream(); 
                    OutputStream out = connectionFromClient.getOutputStream(); 
                    
                    BufferedReader headerReader = new BufferedReader(new InputStreamReader(in)); 
                    BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out)); 
                    
                    DataOutputStream dataOut = new DataOutputStream(out);
                    
                    
                    //read header
                    String header = headerReader.readLine(); 
                    StringTokenizer strk = new StringTokenizer(header,"\n"); 
                    String command = strk.nextToken(); 
                    if(command.equals("processes"))
                    {
                        //get running processes
                        Object[] processes = ProcessHandle.allProcesses().filter(ProcessHandle::isAlive).toArray();
                        //create string of all running processes info
                        String processInfo = ""; 
                        for (Object obj: processes)
                        {
                            ProcessHandle p = (ProcessHandle)obj; 
                            processInfo += p.info()+"\n"; 
                        }
                        //convert string to byte array
                        byte[] processData = processInfo.getBytes(); 
                        //create response header
                        String response = "OK "+processData.length+"\n";
                        //send response header
                        headerWriter.write(response, 0, response.length());
                        headerWriter.flush();
                        //send bytes
                        dataOut.write(processData, 0, processData.length);
                        dataOut.flush(); 
                        //close connection
                        connectionFromClient.close();
                        
                    }
                    else if(command.equals("screenshot")){
                        
                        //take screenshot 
                        BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        //convert to bytes
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        byte[] imageBytes = baos.toByteArray();
                        //create response
                        String response = "OK "+imageBytes.length+"\n";
                        //send response
                        headerWriter.write(response, 0, response.length());
                        headerWriter.flush();
                        //send bytes
                        dataOut.write(imageBytes, 0, imageBytes.length);
                        dataOut.flush();
                        //close connection
                        connectionFromClient.close();
                    }
                    else if(command.equals("reboot")){
                        //create response
                        String response = "OK\n";
                        //send response
                        headerWriter.write(response, 0, response.length());
                        headerWriter.flush(); 
                        //reboot system after 5 secs
                        Runtime r  = Runtime.getRuntime(); 
                        try
                        {
                            r.exec("shutdown -r -t 5");
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }finally{
                            //close connection
                            connectionFromClient.close();
                        }
                    }
                    else{
                        System.out.println("Connection received from incompatible client");
                        connectionFromClient.close();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
     
    }
    
}

