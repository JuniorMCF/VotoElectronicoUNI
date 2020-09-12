package ElectronicVoteUNI;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Servidor {
	TCPServer50 mTcpServer;
	Scanner sc;

	Electores[] db_electores;

	int checkUser = 0;
	int checkPassword = 0;
        
    public static Servidor objser;
        
	public static void main(String[] args) throws InterruptedException {

		objser = new Servidor();

		objser.db_electores = new Electores[100];
                
                
		objser.db_electores[0] = new Electores("20142647I","1234","tk123","0");
		objser.db_electores[1] = new Electores("20142647A","1243","tk124","0");
											//estado 1 : votando
											//estado 2 : ya voto

		objser.iniciar();
	}
   
	void iniciar() throws InterruptedException{
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					mTcpServer = new TCPServer50(
						new TCPServer50.OnMessageReceived(){
							@Override
							public void messageReceived(String message){
								synchronized(this){
									ServidorRecibe(message);
								}
							}
						}
					);
					mTcpServer.run();
				}
			}
		).start();

		try{
			Thread.sleep(4000);
		} 
		catch(InterruptedException e){
			 // this part is executed when an exception (in this example InterruptedException) occurs
		}
   
	}

	void ServidorRecibe(String llego){
			System.out.println(""+llego);
			String[] json = llego.split("/");
			int lenght = json.length;
			String[] keys = new String[lenght];
			String[] values = new String[lenght];

			for(int i = 0;i<lenght;i++){
				String[] key_values = json[i].split(":");
				
				keys[i] = key_values[0];
				values[i] = key_values[1];
			}

			for(int i = 0;i<lenght;i++){
				switch (keys[i]) {
					case "user":
						System.out.println("user: "+values[i]);
						for(int j = 0;j<objser.db_electores.length;j++){
							String userdb = objser.db_electores[j].getUser();
							if(userdb == values[i]){//verificamos el usuario en la base de datos
								checkUser = 1;
							}else{
								checkUser = 0;
							}
						}

						break;
					case "password":
						System.out.println("passwd: "+values[i]);
						for(int j = 0;j<objser.db_electores.length;j++){
							String passwrdb = objser.db_electores[j].getPassword();
							if(checkUser == 1){//si es que existe el usuario se verifica la contraseña
								if(passwrdb == values[i]){//si el password coincide enviamos un response:200 y un token
									String response = "response:200/token:"+objser.db_electores[j].getToken()+"";
									ServidorEnvia(response);
								}else{//respondemos con 400 : contraseña invalida (token vacio)
									String response = "response:400/token:";
									ServidorEnvia(response);
								}
							}else{//respondemos con 404 que el usuario no existe (token vacio)
								String response = "response:404/token:";
								ServidorEnvia(response);
							}
						}
						break;
					case "token":
						System.out.println("token: "+values[i]);
						break;
					case "estado":
						System.out.println("estado: "+values[i]);
						break;
					default:
						System.out.println("no se envio el formato correcto");
						break;
				}
			}
            
        }
   
	void ServidorEnvia(String envia){
		if (mTcpServer != null) {
			mTcpServer.sendMessageTCPServer(envia);
		}
	}
	
}
