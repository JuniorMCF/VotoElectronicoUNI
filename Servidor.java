/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Servidor {
	TCPServer50 mTcpServer;
	Scanner sc;

	Electores[] db_electores;

        Candidatos[] db_candidatos;
        
	int checkUser = 0;
	int checkPassword = 0;
        int checkToken = 0;
        int checkCandidato = 0;
	public static void main(String[] args) throws InterruptedException {

		Servidor objser = new Servidor();

		objser.db_electores = new Electores[100];
                objser.db_candidatos = new Candidatos[2];
                
		objser.db_electores[0] = new Electores("20142647I","1234","tk123","0");
		objser.db_electores[1] = new Electores("20142647A","1243","tk124","0");
											//estado 1 : votando
											//estado 2 : ya voto
                
                objser.db_candidatos[0] = new Candidatos("Yuri Nuñez");
                objser.db_candidatos[1] = new Candidatos("Alonso Tenorio");

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
                //conteo de votos
                /*
                new Thread(
			new Runnable() {
				@Override
				public void run() {
					while(true){
                                            System.out.println("voto candidato "+db_candidatos[0].getNombre()+" total: "+db_candidatos[0].getVotos());
                                            System.out.println("voto candidato "+db_candidatos[1].getNombre()+" total: "+db_candidatos[1].getVotos());
                                            System.out.flush();
                                        }
				}
			}
		).start();
                
                */
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
                        String response = "";
			for(int i = 0;i<lenght;i++){
                            
				switch (keys[i]) {
					case "user":
						System.out.println("user: "+values[i]);
                                                
                                                try {

                                                    // Convierte un valor de tipo String a int
                                                    int tam = db_electores.length;
                                                    //System.out.println("longitud de base de datos"+tam);
                                                    for(int j = 0;j<tam;j++){
							String userdb = db_electores[j].getUser();
                                                        
							if(userdb.equals(values[i])){//verificamos el usuario en la base de datos
                                                                //System.out.println("son iguales");
								checkUser = 1;
                                                                j=tam;
							}else{
                                                                //System.out.println("no son iguales");
								checkUser = 0;
							}
                                                    }

                                                } catch(Exception e) {
                                                    //System.out.println("ERROR:"+e.getMessage());
                                                }
                                                
						break;
					case "password":
						System.out.println("passwd: "+values[i]);
                                                
                                                try {
                                                    //String response = "";
                                                    for(int j = 0;j< db_electores.length;j++){
                                                            String passwrdb = db_electores[j].getPassword();
                                                            if(checkUser == 1){//si es que existe el usuario se verifica la contraseña
                                                                    if(passwrdb.equals(values[i])){//si el password coincide enviamos un response:200 y un token
                                                                            response = "response:200/usuario:"+values[0]+"/token:"+db_electores[j].getToken()+"";
                                                                            checkPassword = 1;
                                                                            //si ya se encontro dejamos de buscar
                                                                            j = db_electores.length;
                                                                    }else{//respondemos con 400 : contraseña invalida (token vacio)
                                                                            response = "response:400/usuario: /token: ";
                                                                            checkPassword = 2;
                                                                    }
                                                            }else{//respondemos con 404 que el usuario no existe (token vacio)
                                                                    response = "response:404/usuario: /token: ";
                                                                    checkPassword = 0;
                                                            }
                                                    }
                                                }catch(Exception e){
                                                    System.out.println("ERROR:"+e.getMessage());
                                                }
                                                
                                                ServidorEnvia(response);
						break;
					case "token":
						System.out.println("token: "+values[i]);
                                                
                                                try {

                                                    //tomamos el usuario del token
                                                    String userapp = values[0];// el usuario enviado desde el app
                                                    
                                                    int tam = db_electores.length;
                                                    
                                                    for(int j = 0;j<tam;j++){
							String token = db_electores[j].getToken();
                                                        
							if(token.equals(values[i]) && userapp.equals(db_electores[j].getUser())){//verificamos que el token sea el correcto y que le pertenezca al usuario
                                                                System.out.println("token correcto");
								checkToken = 1;
                                                                response = "response:202/usuario:"+userapp;
                                                                j=tam;
							}else{
                                                                System.out.println("token incorrecto");
								checkToken = 0;
                                                                response = "response:402/usuario:"+userapp;
							}
                                                    }

                                                } catch(Exception e) {
                                                    //System.out.println("ERROR:"+e.getMessage());
                                                }
                                                ServidorEnvia(response);
						break;
                                        case "candidato":
						System.out.println("candidato: "+values[i]);
                                                try {
                                                    
                                                    String userapp = values[0];// el usuario enviado desde el app
                                                    String candidato = values[1];
                                                    int tam = db_electores.length;
                                                    
                                                    for(int j = 0;j<tam;j++){
							if(userapp.equals(db_electores[j].getUser())){//buscamos el usuario para efectuar la votacion
                                                                //aqui se verian los estados de votacion
                                                                for(int k=0;k<2;k++){
                                                                    if(db_candidatos[k].getNombre().equals(candidato)){
                                                                        db_candidatos[k].sumarVoto();
                                                                        response = "response:500/message:registro exitoso";
                                                                        checkCandidato = 1;
                                                                        checkPassword = 0;
                                                                        checkUser = 0;
                                                                        checkToken =0;
                                                                        k = 2;
                                                                        j = tam;
                                                                    }
                                                                }
                                                                if(checkCandidato != 1){
                                                                    response = "response:600/message:no se encontro al candidato";
                                                                }
                                                                
							}else{
                                                                response = "response:600/message:no fue posible realizar la votacion intentelo nuevamente";
							}
                                                    }

                                                } catch(Exception e) {
                                                    //System.out.println("ERROR:"+e.getMessage());
                                                }
                                                ServidorEnvia(response);
                                                
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
                //System.out.println(""+envia);
		if (mTcpServer != null) {
			mTcpServer.sendMessageTCPServer(envia);
		}
	}
	
}
