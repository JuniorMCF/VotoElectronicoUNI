import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;

public class CsvUsuarios{
	private PrintWriter usuarios = null;

	public CsvUsuarios(){
		try {
			this.usuarios = new PrintWriter(new File("LogUsuarios.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setTitle(){
		String column = "usuario,sesion,token\n";
		this.usuarios.write(column);
		this.usuarios.close();
	}

	public void setValue(String value){
		try(FileWriter usuarios = new FileWriter("LogUsuarios.csv",true)){
			usuarios.write(value);
			usuarios.close();
		}catch(IOException e){
		}
		//this.registro.close();
	}
	public void closeCsv(){
		this.usuarios.close();
	}
}