package ElectronicVoteUNI;

public class Electores {

    private String user;
    private String password;
    private String token;
    private String estado;

    public Electores(String user,String password,String token,String estado) {
        this.user = user;
        this.password = password;
        this.token = token;
        this.estado = estado;
    }
    
    
    public String getUser(){
        return user;
    }
    public String getPassword(){
        return password;
    }
    public String getToken(){
        return token;
    }
    public String getEstado(){
        return estado;
    }
    
    public void setUser(String user){
        this.user = user;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setToken(String token){
        this.token = token;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }
}