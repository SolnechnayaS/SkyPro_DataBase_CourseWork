import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface UserDAO {

    List<User> getAllUser() throws SQLException;
    User getUserById(int userId);
    User createUser() throws SQLException;
    User createUserWithoutRole();
    boolean findUserByLogin(String string);
    void updateUser(User user) throws SQLException;

    public Set<Role> addRoleList() throws SQLException;
    public Set<Role> addRoleList(User user) throws SQLException;
    public Set<Role> deleteRoleFromList(User user) throws SQLException;
    public void getUsersWithRolesList() throws SQLException;
    public void getUsersWithRolesList(User user) throws SQLException;
    void deleteUser(User user);
}
