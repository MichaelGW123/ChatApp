/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Travis
 */
public class App {
    protected ArrayList<Peer> connections;
    private Integer my_port;
    private String my_ip;
    private ServerSocket server_socket;
    private BufferedReader input;
    private HashMap<Peer, ObjectOutputStream> peer_map;
    
    
    //Constructor to initialzie variables as well as start the Socket Listening 
    //function
    public App(Integer port) throws IOException{

        //Map Peers to OutputStreams
        peer_map = new HashMap<>();
        
        //list of connections
        connections = new ArrayList<>();
        
        //local host's ip address
        my_ip = Inet4Address.getLocalHost().getHostAddress();
        
        //port will be passed to Chat main from the command line
        my_port = port;
        server_socket = new ServerSocket(my_port);
        Listen();
        
        //Buffered input
        input = new BufferedReader(new InputStreamReader(System.in));
        
    }
    
    private void Listen() throws IOException{
        
        //Use threads to service each peer connection
        new Thread(() -> {
            
            while(true){
                try{
                        
                    //Accept peer connect request
                    Socket peer_connect = server_socket.accept();
                    
                    
                    //Service peer connection on thread
                    ConnectionHandler handler = new ConnectionHandler(peer_connect);
                    new Thread(handler).start();
                    
                } catch (IOException e){
                    
                    
                    
                }
                   
            }
        }).start();
        
    }
    
    
   class ConnectionHandler implements Runnable {

    private Socket connection_socket;
    
    public ConnectionHandler(Socket socket){
        
        this.connection_socket = socket;
        
    }
    
    @Override
    public void run(){
        
        try{
            
            ObjectInputStream  input = new ObjectInputStream(connection_socket.getInputStream());
            
            while(true){
                
                //Read in message object from peer socket
                Message command = (Message)input.readObject();
                
                //If string is null, connection is closed. Return
                if (command == null)
                    return;
                
                String ip = command.getIP();
                int port = command.getPort();
                PeerCommandType type = command.getType();
                
                //Proceed with proper steps based on message type i.e CONNECT,
                //SEND, or TERMINATE
                switch(type){
                    case CONNECT:
                        
                       Peer peer = new Peer(ip, port);
                       System.out.println(peer.getSocket());
                       System.out.println(peer.getSocket().getOutputStream());
                       
                       connections.add(peer);
                       
                       try{
                           ObjectOutputStream stream = new ObjectOutputStream(peer.getSocket().getOutputStream());
                           System.out.println(stream);
                            peer_map.put(peer, stream);
                       
                       }catch(IOException e){
                           
                       }
                       
                       System.out.println("Peer with ip: " + ip + " and port: " + port + " has connected to you.");
                       break;
                    
                    case SEND:
                        String message = command.getMessage();
                        System.out.println("Message received from: " + ip);
                        System.out.println("Sender's Port: " + port);
                        System.out.println("Message: " + message);
                        break;
                    
                    case TERMINATE:
                        System.out.println("Peer with ip: " + ip + " and port " + port +
                                " has terminated your connection");
                        Peer peer_terminate = findPeer(ip,port);
                        System.out.println(peer_terminate);
                        try{
                        peer_terminate.getSocket().close();
                        peer_map.get(peer_terminate).close();
                        }catch (IOException e){
                            
                        }
                        connections.remove(peer_terminate);
                        peer_map.remove(peer_terminate);
                        input.close();
                    }
                        
                        
                }   
                
            }catch (IOException | ClassNotFoundException e){
                    
                System.out.println("CONNECTION TERMINATED");
                
            }
            
        
    }

   
   }
   private Peer findPeer(String ip, int port){
       
       for (Peer p : connections){
           if (p.getIP().equals(ip) && p.getPort() == port){
               return p;
           }
       }
       return null;
   }
   
    /**
     *
     * @throws IOException
     */
    protected void controlModule() throws IOException{
       System.out.println("Welcome to the ChatApp! If you're new to the ChatApp"
               + " experience type 'help' to display the ChatApp manual. Happy "
               + "chatting!");
       
       while(true){
           
           String command = input.readLine();
           String[] command_sep = command.split(" ");
           String command_type = command_sep[0].toLowerCase();
           
           switch(command_type){
               case "help":
               Display.displayManual();
               break;
               
               case "myip":
                   System.out.println("My IP Address: " + my_ip);
                   break;
                   
               case "myport":
                   System.out.println("Listening on Port: " + my_port);
                   break;
                   
               case "connect":
                   if (!ConnectValidator.validConnection(command)){
                       System.out.println("Invalid connection input. Please, try again.");
                       break;
                   }
                   
                   connect(command);
                   break;
                   
               case "list":
                   displayList();
                   break;
                   
               case "terminate":
                   terminate(command);
                   break;
                   
               case "send":
                   send(command);
                   break;
                
               case "exit": 
                   exit();
                   System.exit(0);
                   break;
                   
               default:
                   System.out.println("Clearly you have not read the manual. Please"
                           + "enter 'help' to access the chatApp manual.");       
           }
       }                
   }
   
   private boolean connect(String command){
       
       String[] command_sep = command.split(" ");
       String ip = command_sep[1];
       int port = Integer.parseInt(command_sep[2]);
       Peer peer = new Peer(ip,port);
       
       
       if (!ConnectValidator.validConnection(command)){
            System.out.println("Invalid connection input. Please, try again.");
                 
            return false;
       }
       
       if (connections.contains(peer)){
            System.out.println("Duplicate connections are not allowed!");
            return false;
        }
       
        /*if (ip.equals(my_ip) && (my_port == port)){
            System.out.println("Self connections are not allowed!");
            return false;
        }*/
                   
       
       Socket connect_socket = null;
       
       try{
           
           
           connect_socket = new Socket(ip,port);
           
       }catch (IOException e){
           
           System.out.println("Socket connection failed. Please, try again.");
       }
       
       System.out.println("You have connected to " + ip + " on port " + port);
       
       connections.add(peer);
       
       try{
           
       peer_map.put(peer, new ObjectOutputStream(connect_socket.getOutputStream()));
       
       }catch (IOException e){
           
           System.out.println("Unable to access socket Output Stream.");
           
       }
       
       String send_message = "Peer " + my_ip + " on port " + port +" has "
               + "established a connection with you.";
       
       Message message = new Message(my_ip,my_port,PeerCommandType.CONNECT,send_message);
       
       try{
           
           peer_map.get(peer).writeObject(message);
           
       }catch (IOException e){
           
           System.out.println(e);
           
        }
       return true;
   
   
   } 
   
   
    private void displayList(){
        if(connections.isEmpty()){
            
            System.out.println("There are no current connections");
        }
        else{
            System.out.println("id:\t IP Address\t Port No.");
            for(int i = 0; i < connections.size();i++ ){
                Peer peer = connections.get(i);
                System.out.println(i+1 + "\t" + peer.getIP() + "\t" + peer.getPort());
            }
        }
        
    } 
    
    private boolean terminate(String command){
        
       String[] command_sep = command.split(" ");
       
        if (!ConnectValidator.isNumeric(command_sep[1]) || command_sep.length != 2){
            System.out.println("Invalid connection id");
            return false;
        }
        else if (Integer.parseInt(command_sep[1]) < 0 || Integer.parseInt(command_sep[1]) > connections.size()){
            System.out.println("Invalid connection id");
            return false;
        }
        else{
            Integer id = Integer.parseInt(command_sep[1]);
            
            try{
                Peer peer = connections.get(id-1);
            }catch(IndexOutOfBoundsException e){
                System.out.println("Peer index is invalid!");
            }
            //String send_message = ("Connection with peer " + my_ip + " on port "
              //      + peer.getPort() + "has terminated your connection");
              
            Peer peer = connections.get(id-1);
            Message message = new Message(my_ip,my_port,PeerCommandType.TERMINATE,"");
            System.out.println("You have terminated your connection with peer " + peer.getIP());
       
       try{
           
           peer_map.get(peer).writeObject(message);
           
       }catch (IOException e){
           
           System.out.println(e);
           
        }
            
            try{
                peer.getSocket().close();
                peer_map.get(peer).close();
                
            }catch (IOException e){
                System.out.println(e);
            }
            
            connections.remove(peer);
            peer_map.remove(peer);
        }
        return true;
        
        
    }
    
    
    private boolean send(String command){
        String[] command_sep = command.split(" ");
        
        if (command_sep.length <3 || !ConnectValidator.isNumeric(command_sep[1])){
            System.out.println("Invalid send message. See help for more info");
            }
        else if (Integer.parseInt(command_sep[1]) < 0 || Integer.parseInt(command_sep[1]) > connections.size()){
            System.out.println("Invalid connection id");
            return false;
        }
        else{
            
            Integer id = Integer.parseInt(command_sep[1]);
            StringBuilder send_message = new StringBuilder(100);
            for (int i = 2; i < command_sep.length; i++){
                send_message.append(command_sep[i]).append(" ");
            }
            
            try{
                Peer peer = connections.get(id-1);
            }catch(IndexOutOfBoundsException e){
                System.out.println("The specified peer index is invalid.");
            }
            
            Peer peer = connections.get(id-1);
            String full_message;
            //full_message = "Message received from: " + my_ip + "\nSender's Port: " + peer.getPort() + "\n"
            //        + "Message: " + send_message;
            Message message = new Message(my_ip,my_port,PeerCommandType.SEND,send_message.toString());
            System.out.println("You have sent peer " + peer.getIP() + " a message");
       
            try{
           
            peer_map.get(peer).writeObject(message);
           
            }catch (IOException e){
           
            System.out.println(e);
           
            }
        }
        return true;
    }
    
    private void exit() throws IOException{
        
        for (Peer p : connections){
            String send_message = "Peer " + my_ip + " on Port " + p.getPort() +" has terminated your connection";
            Message message = new Message(my_ip,p.getPort(),PeerCommandType.TERMINATE,send_message);
            peer_map.get(p).writeObject(message);
            p.getSocket().close();
            peer_map.get(p).close();
        }
        
        for (Entry<Peer,ObjectOutputStream> stream :peer_map.entrySet()){
            stream.getValue().close();
        }
        
        server_socket.close();
        
        System.out.println("ChatApp closing");
        
    }
   
}
   

