public class Registro {

	private String user;
	private String votetime;
	private String vote;

	public Registro(String user,String votetime, String vote) {
		this.user = user;
		this.votetime = votetime;
		this.vote = vote;
	}
	
	
	public String getUser(){
		return user;
	}
	public String getVotetime(){
		return votetime;
	}
	public String getVote(){
		return vote;
	}

	public void setUser(String user){
		this.user = user;
	}
	public void setVotetime(String votetime){
		this.votetime = votetime;
	}
	public void setVote(String vote){
		this.vote = vote;
	}
}