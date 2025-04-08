package model;

public class User {
    private String userId;
    private String username;
    private String password; // 简化，实际项目需加密处理

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    @Override
    public String toString() {
        // Show just username or userId or both
        return String.format("User: %s (ID: %s)", this.username, this.userId);
    }

    // Getter, Setter
    // ...
}
