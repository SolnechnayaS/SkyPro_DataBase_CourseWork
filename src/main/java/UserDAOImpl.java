import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class UserDAOImpl implements UserDAO {
    RoleDAO roleDAO = new RoleDAOImpl();
    Role defaultRole = roleDAO.getRoleById(1);

    @Override
    public List<User> getAllUser() throws SQLException {
        EntityManager entityManager = EMF.emfCreate();

        entityManager.getTransaction().begin();
        String jpqlQuery = "SELECT s FROM User s";
        TypedQuery<User> query = entityManager.createQuery(jpqlQuery, User.class);
        List<User> users = new ArrayList<>(query.getResultList());
        entityManager.getTransaction().commit();

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return users;
    }

    @Override
    public void printUsersWithRolesList() throws SQLException {
        getAllUser().forEach(
                User::toStringWithRolesList
        );
    }

    @Override
    public void printUsersWithRolesList(User user) throws SQLException {
        user.toStringWithRolesList();
    }

    @Override
    public User getUserById(int idRequest) {
        EntityManager entityManager = EMF.emfCreate();

        entityManager.getTransaction().begin();
        User findUser = entityManager.find(User.class, idRequest);
        entityManager.getTransaction().commit();

        while (findUser == null) {
            System.out.println("Пользователь с заданным user_id не существует. Введите другой user_id для поиска пользователя: ");
            findUser = getUserById(Input.inputIntegerPositive());
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return findUser;
    }

    @Override
    public User createUser() throws SQLException {
        User newUser = createUserWithoutRole();

        Set<Role> roleList = addRoleList();
        newUser.setRoleList(roleList);

        EntityManager entityManager = EMF.emfCreate();
        entityManager.getTransaction().begin();
        entityManager.persist(newUser);
        entityManager.getTransaction().commit();

        System.out.println("Создан новый пользователь: ");
        System.out.println(newUser);

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return newUser;
    }

    @Override
    public User createUserWithoutRole() {
        System.out.println("Введите ИМЯ: ");
        String name = Input.inputString();
        System.out.println("Введите ЛОГИН: ");
        String login = Input.inputString().toLowerCase();
        while (findUserByLogin(login)) {
            System.out.println("Введите другой ЛОГИН: ");
            login = Input.inputString().toLowerCase();
        }
        System.out.println("Введите ПАРОЛЬ: ");
        String password = Input.inputString();

        return new User(name, login, password);
    }

    @Override
    public void updateUser(User user) throws SQLException {

        EntityManager entityManager = EMF.emfCreate();

        try {
            entityManager.getTransaction().begin();
            entityManager.find(User.class, user.getUserId());
            entityManager.getTransaction().commit();

            System.out.println("Выберите, какие данные будем изменять:\n" +
                    "1 - Имя;\t" +
                    "2 - Логин;\t" +
                    "3 - Пароль;\t" +
                    "4 - Список ролей;\t");
            System.out.println("0 - Завершить редактирование данных пользователя.");
            int numChange = Input.inputIntegerPositive();

            while (numChange != 0) {
                switch (numChange) {
                    case 1:
                        System.out.println("Введите новое ИМЯ: ");
                        user.setName(Input.inputString());

                        numChange = 5;
                        break;
                    case 2:
                        System.out.println("Введите новый ЛОГИН: ");
                        String login = Input.inputString().toLowerCase();

                        while (findUserByLogin(login)) {
                            if (!(login.equals(user.getLogin()))) {
                                System.out.println("Введите другой ЛОГИН: ");
                                login = Input.inputString().toLowerCase();
                            } else {
                                System.out.println("Указан текущий логин, изменения не будут внесены");
                                break;
                            }
                        }
                        user.setLogin(login);

                        numChange = 5;
                        break;
                    case 3:
                        System.out.println("Введите новый ПАРОЛЬ: ");
                        user.setPassword(Input.inputString());

                        numChange = 5;
                        break;
                    case 4:
                        user.setRoleList(addRoleList(user));
                        user.setRoleList(deleteRoleFromList(user));

                        numChange = 5;
                        break;
                    default:
                        user.setDateChange(LocalDateTime.now());

                        System.out.println("Выберите, какие данные будем изменять:\n" +
                                "1 - Имя;\t" +
                                "2 - Логин;\t" +
                                "3 - Пароль;\t" +
                                "4 - Список ролей;\t");
                        System.out.println("0 - Завершить редактирование данных пользователя.");
                        numChange = Input.inputIntegerPositive();
                }
            }


            entityManager.getTransaction().begin();
            entityManager.merge(user);
            entityManager.getTransaction().commit();

            entityManager.getEntityManagerFactory().close();
            entityManager.close();

            System.out.println("Данные пользователя обновлены успешно!");
        } catch (NullPointerException e) {

            System.out.println("Пользователь не найден!");
        }
    }

    @Override
    public boolean findUserByLogin(String userLogin) {
        try {
            EntityManager entityManager = EMF.emfCreate();

            entityManager.getTransaction().begin();
            String jpqlQuery = "SELECT s FROM User s WHERE login=" + "'" + userLogin.toLowerCase() + "'";
            TypedQuery<User> query1 = entityManager.createQuery(jpqlQuery, User.class);
            User user = query1.getSingleResult();
            entityManager.getTransaction().commit();

            System.out.println("Пользователь " + userLogin + " уже добавлен в базу данных c user_id=" + user.getUserId());

            entityManager.getEntityManagerFactory().close();
            entityManager.close();
            return true;
        } catch (RuntimeException e) {
            return false;
        }

    }

    @Override
    public Set<Role> addRoleList() throws SQLException {

        Set<Role> roleList = new HashSet<>();

        System.out.println("Какие роли для пользователя хотите добавить:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей");
        int numRole = Input.inputIntegerPositive();

        while (numRole != 0) {
            if (numRole == 1) {
                roleList.clear();
                roleList.add(defaultRole);
                System.out.println("Установлено значение роли '" + defaultRole.getRole() + "', иные роли добавлены не будут. Установить другие роли пользователя возможно через повторное редактирование.");
                numRole = 0;
            } else {
                Role newRole = roleDAO.getRoleById(numRole);
                roleList.add(newRole);
                System.out.println("Добавлена роль: '" + newRole.getRole() + "'\n\nКакие роли хотите добавить еще?:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей пользователю.");
                numRole = Input.inputIntegerPositive();
            }
        }

        if (roleList.size() == 0) {
            roleList.add(defaultRole);
        } else if (roleList.size() > 1) {
            roleList.remove(defaultRole);
        }

        return roleList;
    }

    @Override
    public Set<Role> addRoleList(User user) throws SQLException {
        Set<Role> roleList = user.getRoleList();

        System.out.println("Для пользователя уже добавлены следующие роли:\n" + roleList + "\nКакие роли для пользователя хотите добавить еще:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей");
        int numRole = Input.inputIntegerPositive();

        while (numRole != 0) {
            if (numRole == 1) {
                roleList.clear();
                roleList.add(defaultRole);
                System.out.println("Установлено значение роли '" + defaultRole.getRole() + "', иные роли добавлены не будут. Установить другие роли пользователя возможно через повторное редактирование.");
                numRole = 0;
            } else {
                Role newRole = roleDAO.getRoleById(numRole);
                roleList.add(newRole);
                System.out.println("Добавлена роль: '" + newRole.getRole() + "'\n\nКакие роли хотите добавить еще?:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей пользователю");
                numRole = Input.inputIntegerPositive();
            }
        }

        if (roleList.size() == 0) {
            roleList.add(defaultRole);
        } else if (roleList.size() > 1) {
            roleList.remove(defaultRole);
        }

        return roleList;
    }

    @Override
    public Set<Role> deleteRoleFromList(User user) throws SQLException {

        Set<Role> roleList = user.getRoleList();

        if (roleList.size() == 1) {
            System.out.println("У пользователя задана единственная роль, ее нельзя удалить. Сначала добавьте новые роли.");
        } else {
            System.out.println("Какие роли пользователя хотите удалить?\n" + roleList + "\nИли нажмите 0, чтобы закончить удаление ролей у пользователя.");
            int numRole = Input.inputIntegerPositive();

            while (numRole != 0 && roleList.size() > 1) {
                Role deleteRole = roleDAO.getRoleById(numRole);

                if (roleList.remove(deleteRole)) {
                    System.out.println("Удалена роль пользователя: " + deleteRole.getRole());

                } else {
                    System.out.println("Данная роль у пользователя не предусмотрена.");
                }

                if (roleList.size() > 1) {
                    System.out.println("Какие роли пользователя хотите удалить еще?: ");
                    System.out.println(roleList);
                    System.out.println("Или нажмите 0, чтобы закончить удаление ролей у пользователя.");
                    numRole = Input.inputIntegerPositive();

                } else {
                    System.out.println("У пользователя задана единственная роль, ее нельзя удалить. Сначала добавьте новые роли.");
                    numRole = 0;
                }
            }


        }

        if (roleList.size() == 0) {
            roleList.add(defaultRole);
        } else if (roleList.size() > 1) {
            roleList.remove(defaultRole);
        }

        return roleList;
    }

    @Override
    public void deleteUser(User user) {
        EntityManager entityManager = EMF.emfCreate();
        try {
            entityManager.getTransaction().begin();
            entityManager.clear();
            entityManager.remove(entityManager.find(User.class, user.getUserId()));
            entityManager.getTransaction().commit();
            System.out.println("Пользователь удален из базы!");
        } catch (NullPointerException e) {
            entityManager.getTransaction().commit();
            System.out.println("Пользователь не найден!");
        }
        entityManager.getEntityManagerFactory().close();
        entityManager.close();
    }
}
