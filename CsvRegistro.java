import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;

public class CsvRegistro{
	private PrintWriter registro = null;

	public CsvRegistro(){
		try {
			this.registro = new PrintWriter(new File("Registro.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setTitle(){
		String column = "usuario,hora,voto\n";
		this.registro.write(column);
		this.registro.close();
	}

	public void setValue(String value){
		try(FileWriter registro = new FileWriter("Registro.csv",true)){
			registro.write(value);
			registro.close();
		}catch(IOException e){
		}
		//this.registro.close();
		System.out.println("Registro en .csv exitoso!");
	}
	public void closeCsv(){
		this.registro.close();
	}
}