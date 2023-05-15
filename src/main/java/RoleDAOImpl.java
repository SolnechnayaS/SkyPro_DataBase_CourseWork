import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.*;

public class RoleDAOImpl implements RoleDAO {
    @Override
    public List<Role> getAllRole() throws SQLException {
        EntityManager entityManager = EMF.emfCreate();

        entityManager.getTransaction().begin();
        entityManager.clear();
        String jpqlQuery = "SELECT s FROM Role s";
        TypedQuery<Role> query = entityManager.createQuery(jpqlQuery, Role.class);
        List<Role> roles = new ArrayList<>(query.getResultList());
        entityManager.getTransaction().commit();

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return roles;
    }
    @Override
    public void printRolesWithUsersList() throws SQLException {
        getAllRole().forEach(Role::toStringWithUsersList);
    }

    @Override
    public void printRolesWithUsersList(Role role) throws SQLException {
        role.toStringWithUsersList();
    }

    @Override
    public List<User> getAllUsersByRole(Role role) throws SQLException {
        EntityManager entityManager = EMF.emfCreate();

        entityManager.getTransaction().begin();
        String jpqlQuery = "SELECT usersList FROM Role WHERE roleId=" + role.getRoleId();
        TypedQuery<User> query = entityManager.createQuery(jpqlQuery, User.class);
        List<User> users = new ArrayList<>(query.getResultList());
        entityManager.getTransaction().commit();

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return users;
    }

    @Override
    public Role getRoleById(int idRequest) {
        EntityManager entityManager = EMF.emfCreate();

        Role findRole = null;
        entityManager.getTransaction().begin();
        findRole = entityManager.find(Role.class, idRequest);
        entityManager.getTransaction().commit();

        while (findRole==null) {
            System.out.println("Роль с заданным role_id не существует. Введите другой role_id: ");
            findRole = getRoleById(Input.inputIntegerPositive());
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return findRole;
    }

    @Override
    public void createRole(String newRoleName) {

        if (findRoleByName(newRoleName) == null) {
            EntityManager entityManager = EMF.emfCreate();

            entityManager.getTransaction().begin();
            entityManager.persist(new Role(newRoleName));
            entityManager.getTransaction().commit();

            entityManager.getEntityManagerFactory().close();
            entityManager.close();
        } else {
            System.out.println("Роль с названием " + newRoleName + " уже существует, ее role_id=" + findRoleByName(newRoleName).getRoleId());
        }

    }

    @Override
    public void updateRole(Role role) {
        EntityManager entityManager = EMF.emfCreate();

        if (!role.getRole().equals("По умолчанию")) {

            System.out.println("Введите обновленное название роли: ");
            String newRoleName = Input.inputString();
            try {
                entityManager.getTransaction().begin();
                String jpqlQuery1 = "SELECT s FROM Role s WHERE role=" + "'" + newRoleName + "'";
                TypedQuery<Role> query1 = entityManager.createQuery(jpqlQuery1, Role.class);
                Role existRole = null;
                try {
                    existRole = query1.getResultList().get(0);
                } catch (IndexOutOfBoundsException e) {
                    existRole = query1.getSingleResult();
                }
                entityManager.getTransaction().commit();

                System.out.println("Роль с названием - " + newRoleName + " - уже существует, её role_id=" + existRole.getRoleId());

            } catch (NullPointerException | NoResultException e) {
                entityManager.getTransaction().commit();

                entityManager.getTransaction().begin();
                role.setRole(newRoleName);
                entityManager.merge(role);
                entityManager.getTransaction().commit();
            }
        }
        else {
            System.out.println("Роль 'По умолчанию' переименовать нельзя!");
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();
    }

    @Override
    public void deleteRole(String role) {
        EntityManager entityManager = EMF.emfCreate();

        if (role != "По умолчанию") {
            try {
                entityManager.getTransaction().begin();
                String jpqlQuery = "SELECT s FROM Role s WHERE role=" + "'" + role + "'";
                TypedQuery<Role> query1 = entityManager.createQuery(jpqlQuery, Role.class);
                Role role1 = query1.getSingleResult();
                entityManager.getTransaction().commit();

                if (role1.roleId != 1) {
                    entityManager.getTransaction().begin();
                    entityManager.remove(entityManager.find(Role.class, role1.getRoleId()));
                    entityManager.flush();
                    entityManager.clear();
                    entityManager.getTransaction().commit();
                    System.out.println("Роль успешно удалена из базы данных и у всех пользователей!");
                } else {
                    System.out.println("Роль с role_id=1 удалять нельзя");
                }
            } catch (NullPointerException | NoResultException e) {
                entityManager.getTransaction().commit();
                System.out.println("Роль, которую Вы хотите удалить, не существует");
            }
        } else {
            System.out.println("Роль '" + role + "' удалить нельзя");
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();
    }


    @Override
    public Role findRoleByName(String roleName) {
        Role role = null;
        EntityManager entityManager = EMF.emfCreate();
        try {
            entityManager.getTransaction().begin();
            String jpqlQuery = "SELECT s FROM Role s WHERE role=" + "'" + roleName + "'";
            TypedQuery<Role> query1 = entityManager.createQuery(jpqlQuery, Role.class);
            role = query1.getSingleResult();
            entityManager.getTransaction().commit();
        } catch (RuntimeException e) {
            entityManager.getTransaction().commit();

            role = null;
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return role;
    }

}
