import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface RoleDAO {
    List<Role> getAllRole() throws SQLException;

    List<User> getAllUsersByRole(Role role) throws SQLException;

    Role getRoleById(int roleId);
    void createRole(String newRoleName);
    void updateRole(Role role);
    void deleteRole(String role);
    Role findRoleByName(String roleName);

    public void getRolesWithUsersList () throws SQLException;
    public void getRolesWithUsersList (Role role) throws SQLException;
}
