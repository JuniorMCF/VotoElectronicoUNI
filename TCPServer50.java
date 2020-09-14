
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer50 {
    private String message;
    int nrcli = 0;
    public static final int SERVERPORT = 9900;
    
    private OnMessageReceived messageListener = null;
    private OnConnectId connectIdListener = null;
    
    private boolean running = false;
    TCPServerThread50[] sendclis = new TCPServerThread50[10];
    PrintWriter mOut;
    BufferedReader in;
    ServerSocket serverSocket;

    //el constructor pide una interface OnMessageReceived
    public TCPServer50(OnMessageReceived messageListener,OnConnectId connectIdListener) {
        this.messageListener = messageListener;
        this.connectIdListener = connectIdListener;
    }
    
    public OnMessageReceived getMessageListener(){
        return this.messageListener;
    }
    public OnConnectId getConnectIdListener(){
        return this.connectIdListener;
    }
    
    public void sendMessageTCPServer(String message){
        String[] keys = message.split("/");
        
        System.out.println("message "+message);
        String[] key = keys[0].split(":");
        int id = Integer.parseInt(key[1]);
        
        System.out.println("id "+id);
        sendclis[id].sendMessage(message);
        /*
        for (int i = 1; i <= nrcli; i++) {
            System.out.print("enviando mensaje:"+message);
            sendclis[i].sendMessage(message);
        }
                */
    }
    
    
    public void run(){
        running = true;
        try{
            System.out.println("TCP Server"+"S : Connecting...");
            serverSocket = new ServerSocket(SERVERPORT);
            
            while(running){
                Socket client = serverSocket.accept();
                System.out.println("TCP Server"+"S: Receiving...");
                nrcli++;
                System.out.println("Engendrado " + nrcli);
                String id = ""+nrcli;
                
                sendclis[nrcli] = new TCPServerThread50(client,this,nrcli,sendclis);
                Thread t = new Thread(sendclis[nrcli]);
                t.start();
                connectIdListener.connectId(id);
                
                
                System.out.println("Nuevo conectado:"+ nrcli+" electores conectados");

            }
            
        }catch( Exception e){
            System.out.println("Error"+e.getMessage());
        }finally{

        }
    }
    public  TCPServerThread50[] getClients(){
        return sendclis;
    } 

    public interface OnConnectId{
        public void connectId(String id);
    }
    
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
