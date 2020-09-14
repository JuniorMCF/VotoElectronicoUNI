
/**
 *
 * @author JCastillo
 * @author IIpanaque
 */
import java.time.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Servidor {
	TCPServer50 mTcpServer;
	Scanner sc;

	static CsvRegistro objcsv;
	static CsvUsuarios objusr;

	Electores[] db_electores;
	Candidatos[] db_candidatos;
	ArrayList<Registro> db_registro;
		
	int checkUser = 0;
	int checkPassword = 0;
	int checkToken = 0;
	int checkCandidato = 0;
	int cantVotes = 0;
	int userid = -1;
	String sesiontime;

	long startTk;
	long endTk;
	double timeTk;

	long startVote;
	long endVote;
	double timeVote;

	public static void main(String[] args) throws InterruptedException {

		Servidor objser = new Servidor();

		objcsv = new CsvRegistro();
		objcsv.setTitle();

		objusr = new CsvUsuarios();
		objusr.setTitle();

		objser.db_electores = new Electores[100];
		objser.db_candidatos = new Candidatos[3];
		objser.db_registro = new ArrayList<Registro>();
		
		/* User - Password - Token - State */		
		//Usuarios();
		try{
			int i = 0;
			String line = "";
			BufferedReader br = new BufferedReader(new FileReader("electores.csv"));
			while((line = br.readLine())!= null){
				String[]elector = line.split(",");
				objser.db_electores[i] = new Electores(elector[0],elector[1],elector[2],elector[3]);
				i++;
				//objser.db_electores[0] = new Electores("20142647B","1234","tk123","0");
			}
		}catch(IOException e){

		}
		
		/*
		objser.db_electores[0] = new Electores("20142647B","1234","tk123","0");
		objser.db_electores[1] = new Electores("20142647A","1243","tk124","0");
		objser.db_electores[2] = new Electores("20144567B","1245","tk125","0");
		*/
		//estado 1 : votando
		//estado 2 : ya voto


				
		objser.db_candidatos[0] = new Candidatos("Yuri Nuñez");
		objser.db_candidatos[1] = new Candidatos("Alonso Tenorio");
		objser.db_candidatos[2] = new Candidatos("Nulo");

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
														
								if(userdb.equals(values[i]) && db_electores[j].getEstado().equals("0")){//verificamos el usuario en la base de datos
									//System.out.println("son iguales");
									//checkUser = 1;
									userid = j;
									j=tam;
								}else{
									//System.out.println("no son iguales");
									//checkUser = 0;
									userid = -1;
								}
							}
						} catch(Exception e) {
							//System.out.println("ERROR:"+e.getMessage());
						}
												
						break;
					case "userid":
						userid = Integer.parseInt(values[i]);
						break;

					case "password":
						System.out.println("passwd: "+values[i]);
						objusr.setValue(db_electores[userid].getUser()+","+Time()+","+""+'\n');
												
						try {
							//String response = "";
							if(userid != -1){
							//for(int j = 0;j< db_electores.length;j++){
								String passwrdb = db_electores[userid].getPassword();
								//if(checkUser == 1){		//si es que existe el usuario se verifica la contraseña
									if(passwrdb.equals(values[i])){	//si el password coincide enviamos un response:200 y un token 
										sesiontime = Time();	// Obtenemos la hora en la que se inicio sesion

										String token = Token(sesiontime+db_electores[userid].getUser());	// Generamos el token para el usuario

										startTk = System.currentTimeMillis();

										db_electores[userid].setToken(token.substring(0,5));		// Actualizamos el token del usuario
										response = "response:200/usuario:"+userid+"/token:"+db_electores[userid].getToken()+"";
										checkPassword = 1;
										//si ya se encontro dejamos de buscar
										//j = db_electores.length;
									}else{//respondemos con 400 : contraseña invalida (token vacio)
										response = "response:400/usuario: /token: ";
										checkPassword = 2;
									}
								//}
							}else{
								response = "response:404/usuario: /token: "; //usuario no existe
								checkPassword = 0;
							}
						}catch(Exception e){
							System.out.println("ERROR:"+e.getMessage());
						}				
						ServidorEnvia(response);
						break;

					case "token":
						System.out.println("token: "+values[i]);	
						objusr.setValue(db_electores[userid].getUser()+","+sesiontime+","+values[i]+'\n');

						try {
							//tomamos el usuario del token
							//String userapp = values[0];// el usuario enviado desde el app
							//int tam = db_electores.length;
							//for(int j = 0;j<tam;j++){
							if(userid != -1){
								String token = db_electores[userid].getToken();

								endTk = System.currentTimeMillis();
								timeTk = (double) ((endTk - startTk)/1000);
								//if(token.equals(values[i]) && userapp.equals(db_electores[j].getUser())){//verificamos que el token sea el correcto y que le pertenezca al usuario
								if(timeTk < 30){
									if(token.equals(values[i])){//verificamos que el token sea el correcto y que le pertenezca al usuario
										db_electores[userid].setEstado("1");
										System.out.println("token correcto");
										//checkToken = 1;
										startVote = System.currentTimeMillis(); 
										response = "response:202/usuario:"+userid;
										//j=tam;
										/*generar timer votacion*/
										/*1,5 min*/
										/*response 406, token expirado*/
										/* 30 seg */
									}else{
										System.out.println("token incorrecto");
										//checkToken = 0;
										response = "response:402/usuario:"+userid;
									}
								}else{
									response = "response:406/usuario:"+userid;	//token expirado
								}
							}else{
								response = "response:404/usuario: /token: "; //usuario no existe
							}
						} catch(Exception e) {
							//System.out.println("ERROR:"+e.getMessage());
						}
						ServidorEnvia(response);
						break;
	
					case "candidato":
						endVote = System.currentTimeMillis();

						System.out.println("candidato: "+values[i]);
							try {	
								//String userapp = values[0];// el usuario enviado desde el app
								String candidato = values[1];
								//int tam = db_electores.length;
								
								/*verificar timer votacion*/
								//for(int j = 0;j<tam;j++){
									//if(userapp.equals(db_electores[j].getUser())){//buscamos el usuario para efectuar la votacion
									if(userid != -1){
										timeVote = (double) ((endVote - startVote)/1000);
										if(timeVote < 60){
											//aqui se verian los estados de votacion
											for(int k=0;k<db_candidatos.length;k++){
												if(db_candidatos[k].getNombre().equals(candidato)){ /*agregar condicion timer*/
													db_candidatos[k].sumarVoto();
													db_electores[userid].setEstado("2");
													RegistrarVoto(db_electores[userid].getUser(),Time(),db_candidatos[k].getNombre());
													response = "response:500/message:registro exitoso";
													checkCandidato = 1;
													checkPassword = 0;
													checkUser = 0;
													checkToken =0;
													k = db_candidatos.length;
													//j = tam;
												}
											}
											if(checkCandidato != 1){
												response = "response:600/message:no se encontro al candidato";
											}
										}else{
											response = "response:506/message:tiempo expirado";
										}
									}else{
										response = "response:404/usuario: /token: "; //usuario no existe
									}
								//}
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
	private String Token(String text){
		SHAone sha = new SHAone();
		byte[] byteText = text.getBytes();
		return sha.Encript(byteText);
	}

	private String Time(){
		LocalDateTime date = LocalDateTime.now();
		int hours = date.getHour();
		int minutes = date.getMinute();
		int seconds = date.getSecond();
		return ""+hours+":"+minutes+":"+seconds;
	}
	private void RegistrarVoto(String user, String time, String vote){
		this.cantVotes++;	//aumentamos la cantidad de votos
		System.out.println("\u001B[31m" + "VOTOS: "+this.cantVotes + "\u001B[0m");
		Registro registro = new Registro(user,time,vote);
		db_registro.add(registro);
		System.out.println("usuario: " + registro.getUser());
		System.out.println("tiempo: "+ registro.getVotetime());
		System.out.println("Voto: "+ registro.getVote());
		objcsv.setValue(user+","+time+","+vote+'\n');
	}
}
