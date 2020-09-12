/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class Candidatos {
    private String nombre;
    private int votos = 0;
    
    public Candidatos(String nombre) {
        this.nombre = nombre;
    }
    
    
    public String getNombre(){
        return nombre;
    }
    public int getVotos(){
        return votos;
    }
    public void setNombre(String user){
        this.nombre = user;
    }
    public void sumarVoto(){
        this.votos += 1;
    }
}
