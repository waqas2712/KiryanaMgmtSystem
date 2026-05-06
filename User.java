public class User{
	private int userId;
	private String username;
	private String password;

	public User(){
	}
	public User (int userId, String username, String password){
		this.userId = userId;
		this.username = username;
		this.password =password;
	}
	public int getUserId(){
		return userId;
	} 
	public String getUsername (){
		return username;
	}
	public String getPassword (){
		return password;
	}

	public void setUsername (String Username){this.username=username;}
	public void setUserId (int userId){this.userId=userId; }
	public void setPassword (String password){this.password = password; }
@Override
    public String toString() {
        return "User [id=" + userId + ", username=" + username + "]";
    }
}

