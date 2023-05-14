import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table (name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    protected int userId;

    @Column(name = "name", length = 100, nullable = false)
    protected String name;

    @Column(name = "login", length = 16, nullable = false)
    protected String login;

    @Column(name = "password", nullable = false)
    protected String password;

    @Column(name = "date_creation")
    final protected LocalDateTime dateCreation;

    @Column(name = "date_change")
    protected LocalDateTime dateChange;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable (name="users_roles",
            joinColumns=@JoinColumn (name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roleList;

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<Role> roleList) {
        this.roleList = roleList;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateChange() {
        return dateChange;
    }

    public void setDateChange(LocalDateTime dateChange) {
        this.dateChange = dateChange;
    }

    public User() {
        dateCreation = LocalDateTime.now();
    }

    public User(String name, String login, String password) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.dateCreation = LocalDateTime.now();
        this.dateChange = null;
    }

    public void toStringWithRolesList () {
        System.out.println("User{" +
                "Id=" + userId +
                ", Имя='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", создан=" + dateCreation +
                ", изменен=" + dateChange +
                ", роли=" + roleList +
                '}');
    }
    @Override
    public String toString() {
        return "User{" +
                "Id=" + userId +
                ", Имя='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", создан=" + dateCreation +
                ", изменен=" + dateChange + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserId() == user.getUserId() && getName().equals(user.getName()) && getLogin().equals(user.getLogin()) && getPassword().equals(user.getPassword()) && getDateCreation().equals(user.getDateCreation()) && Objects.equals(getRoleList(), user.getRoleList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getName(), getLogin(), getPassword(), getDateCreation(), getRoleList());
    }
}
