import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.*;


@Entity
@Table (name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    protected int roleId;

    @Column(name = "role", length = 50, nullable = false)
    private String role;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(name="users_roles",
            joinColumns=@JoinColumn(name="role_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private List<User> usersList;

    public Role() {
    }

    Role(String role) {
        this.role = role;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public void toStringWithUsersList() {
        System.out.println("{" +
                "Id " + roleId +
                "='" + role + "', " +
                "список пользователей: " + usersList + "}");
    }
    @Override
    public String toString() {
        return "{" +
                "Id " + roleId +
                "='" + role + "'}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleId(), getRole());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role1 = (Role) o;
        return getRoleId() == role1.getRoleId() && getRole().equals(role1.getRole());
    }

}
