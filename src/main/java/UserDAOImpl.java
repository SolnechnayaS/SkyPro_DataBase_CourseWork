import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.hibernate.dialect.SybaseASEDialect;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserDAOImpl implements UserDAO {
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
    public void getUsersWithRolesList() throws SQLException {
        getAllUser().forEach(
                User::toStringWithRolesList
        );
    }

    @Override
    public void getUsersWithRolesList(User user) throws SQLException {
        user.toStringWithRolesList();
    }

    @Override
    public User getUserById(int idRequest) {
        EntityManager entityManager = EMF.emfCreate();

        entityManager.getTransaction().begin();
        User findUser = entityManager.find(User.class, idRequest);
        entityManager.getTransaction().commit();

        while (findUser == null) {
            System.out.println("Пользователь с заданным id не существует. Введите другой id для поиска пользователя: ");
            Scanner scanner = new Scanner(System.in);
            findUser = getUserById(scanner.useDelimiter("\n").nextInt());
        }

        entityManager.getEntityManagerFactory().close();
        entityManager.close();

        return findUser;
    }

    @Override
    public User createUser() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        User newUser = createUserWithoutRole();

        RoleDAO roleDAO = new RoleDAOImpl();
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите ИМЯ: ");
        String name = scanner.useDelimiter("\n").next();
        System.out.println("Введите ЛОГИН: ");
        String login = scanner.useDelimiter("\n").next();
        while (!findUserByLogin(login)) {
            System.out.println("Введите ЛОГИН: ");
            login = scanner.useDelimiter("\n").next();
        }
        System.out.println("Введите ПАРОЛЬ: ");
        String password = scanner.useDelimiter("\n").next();

        return new User(name, login, password);
    }

    @Override
    public void updateUser(User user) throws SQLException {

        EntityManager entityManager = EMF.emfCreate();

        try {
            entityManager.getTransaction().begin();
            entityManager.find(User.class, user.getUserId());
            entityManager.getTransaction().commit();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберите, какие данные будем изменять: " +
                    "1 - Имя; " +
                    "2 - Логин; " +
                    "3 - Пароль; " +
                    "4 - Список ролей; ");
            int numChange = scanner.useDelimiter("\n").nextInt();

            while (numChange != 0) {
                switch (numChange) {
                    case 1:
                        System.out.println("Введите новое ИМЯ: ");
                        user.setName(scanner.useDelimiter("\n").next());
                        user.setDateChange(LocalDateTime.now());
                        numChange = 5;
                        break;
                    case 2:
                        System.out.println("Введите новый ЛОГИН: ");
                        String login = scanner.useDelimiter("\n").next();
//
//                        System.out.println(login.equals(user.getLogin()));


                        while (findUserByLogin(login)) {
                            if (!(login.equals(user.getLogin()))) {
                                System.out.println("Введите другой новый ЛОГИН: ");
                                login = scanner.useDelimiter("\n").next();
                            } else {
                                System.out.println("Указан текущий логин, изменения не будут внесены");
                                break;
                            }
                        }
                            user.setLogin(login);
                            user.setDateChange(LocalDateTime.now());


                        numChange = 5;
                        break;
                    case 3:
                        System.out.println("Введите новый ПАРОЛЬ: ");
                        user.setPassword(scanner.useDelimiter("\n").next());
                        user.setDateChange(LocalDateTime.now());
                        numChange = 5;
                        break;
                    case 4:
//                        System.out.println("Для пользователя установлены следующие роли:\n" +
//                                user);

                        user.setRoleList(addRoleList(user));
                        user.setRoleList(deleteRoleFromList(user));

                        user.setDateChange(LocalDateTime.now());
                        numChange = 5;
                        break;
                    default:
                        System.out.println("Выберите, какие данные будем изменять: " +
                                "1 - Имя; " +
                                "2 - Логин; " +
                                "3 - Пароль; " +
                                "4 - Список ролей; ");
                        System.out.println("0 - Завершить редактирование данных пользователя");
                        numChange = scanner.useDelimiter("\n").nextInt();
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

        EntityManager entityManager = EMF.emfCreate();
        try {
            entityManager.getTransaction().begin();
            String jpqlQuery = "SELECT s FROM User s WHERE login=" + "'" + userLogin + "'";
            TypedQuery<User> query1 = entityManager.createQuery(jpqlQuery, User.class);
            User user = query1.getSingleResult();
            entityManager.getTransaction().commit();
            System.out.println("Пользователь " + userLogin + " уже добавлен в базу данных c user_id=" + user.getUserId());
            return true;
        } catch (RuntimeException e) {
            return false;
        }

    }

    @Override
    public Set<Role> addRoleList() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        RoleDAO roleDAO = new RoleDAOImpl();
        Role defaultRole = roleDAO.getRoleById(1);
        Set<Role> roleList = new HashSet<>();

        System.out.println("Какие роли для пользователя хотите добавить:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей");

        int numRole = scanner.useDelimiter("\n").nextInt();
        while (numRole != 0) {
            if (numRole == 1) {
                roleList.clear();
                roleList.add(defaultRole);
                System.out.println("Установлено значение роли '" + defaultRole.getRole() + "', иные роли добавлены не будут. Установить другие роли пользователя возможно через повторное редактирование.");
                numRole = 0;
            } else {
                try {
                    roleList.add(roleDAO.getRoleById(numRole));
                    System.out.println("Добавлена роль: '" + roleDAO.getRoleById(numRole).getRole() + "'\n\nКакие роли хотите добавить еще?:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей пользователю");
                    numRole = scanner.useDelimiter("\n").nextInt();
                } catch (NullPointerException e) {
                    System.out.println("Заданная роль не существует!");
                    System.out.println("Какие роли хотите добавить еще?:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей пользователю");
                    numRole = scanner.useDelimiter("\n").nextInt();
                }

            }

        }

        return roleList;
    }

    @Override
    public Set<Role> addRoleList(User user) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        RoleDAO roleDAO = new RoleDAOImpl();
        Role defaultRole = roleDAO.getRoleById(1);
        Set<Role> roleList = user.getRoleList();

        System.out.println("Для пользователя уже добавлены следующие роли:\n" + roleList + "\nКакие роли для пользователя хотите добавить еще:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить удаление ролей");
        int numRole = scanner.useDelimiter("\n").nextInt();

        Role newRole = roleDAO.getRoleById(numRole);

        while (numRole != 0) {
            if (numRole == 1) {
                roleList.clear();
                roleList.add(defaultRole);
                System.out.println("Установлено значение роли '" + defaultRole.getRole() + "', иные роли добавлены не будут. Установить другие роли пользователя возможно через повторное редактирование.");
                numRole = 0;
            } else {
                roleList.add(newRole);
                System.out.println("Добавлена роль: '" + newRole.getRole() + "'\n\nКакие роли хотите добавить еще?:\n" + roleDAO.getAllRole() + "\nИли нажмите 0, чтобы закончить добавление ролей пользователю");
                numRole = scanner.useDelimiter("\n").nextInt();
                newRole = roleDAO.getRoleById(numRole);
            }
        }

        if (roleList.size() > 1) {
            roleList.remove(defaultRole);
        }

        return roleList;
    }

    @Override
    public Set<Role> deleteRoleFromList(User user) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        RoleDAO roleDAO = new RoleDAOImpl();
        Role defaultRole = roleDAO.getRoleById(1);
        Set<Role> roleList = user.getRoleList();
        if (roleList.size() == 1) {
            System.out.println("У пользователя задана единственная роль, ее нельзя удалить. Сначала добавьте новые роли");
        } else {
            System.out.println("Какие роли пользователя хотите удалить?\n" + roleList + "\nИли нажмите 0, чтобы закончить удаление ролей у пользователя.");

            int numRole = scanner.useDelimiter("\n").nextInt();
            Role deleteRole = roleDAO.getRoleById(numRole);

            while (numRole != 0 && roleList.size() > 1) {
                if (roleList.remove(deleteRole)) {
                    System.out.println("Удалена роль пользователя: " + deleteRole.getRole());

                } else {
                    System.out.println("Данная роль у пользователя не предусмотрена!");
                }

                if (roleList.size() > 1) {
                    System.out.println("Какие роли пользователя хотите удалить еще?: ");
                    System.out.println(roleList);
                    System.out.println("Или нажмите 0, чтобы закончить удаление ролей у пользователя.");
                    numRole = scanner.useDelimiter("\n").nextInt();
                    if (numRole != 0) {
                        deleteRole = roleDAO.getRoleById(numRole);
                    }
                } else {
                    System.out.println("У пользователя задана единственная роль, ее нельзя удалить. Сначала добавьте новые роли");
                    numRole = 0;
                }
            }
        }

        if (roleList.size() == 0) {
            roleList.add(defaultRole);
        }

        return roleList;
    }

    @Override
    public void deleteUser(User user) {
        EntityManager entityManager = EMF.emfCreate();
        try {
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.find(User.class, user.getUserId()));
            entityManager.flush();
            entityManager.clear();
            entityManager.getTransaction().commit();
            System.out.println("Пользователь удален из базы!");
        } catch (NullPointerException e) {
            entityManager.getTransaction().commit();
            System.out.println("Пользователь не найден");
        }
        entityManager.getEntityManagerFactory().close();
        entityManager.close();

    }
}
