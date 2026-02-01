package Users;

import java.io.Serializable;

public abstract class User implements Serializable {

    private String username;
    private String password;

    public enum Role {
        STAFF,
        CUSTOMER
    }

    private Role roleType;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.roleType = role;
    }

    // Login işini UserManager yapacak; burada sadece veri var.

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // İstersen security için setter kaldırılabilir ama proje için kalsın:
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoleType() {
        return roleType;
    }

    public void setRoleType(Role roleType) {
        this.roleType = roleType;
    }
}
