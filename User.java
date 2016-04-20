public class User{
	private String nick;
	private String realName;
	private String server;
	public void setNick(String nick){
		this.nick = nick;
	}
	public String getNick() {
		return(nick);
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getRealName() {
		return(realName);
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getServer() {
		return(server);
	}
}
