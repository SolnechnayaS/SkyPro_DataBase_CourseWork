import java.sql.SQLException;

public class Application {

    public static void main(String[] args) throws SQLException {
        UserDAO userDAO = new UserDAOImpl();
        RoleDAO roleDAO = new RoleDAOImpl();

//        Что нужно сделать?
//        Разработать сервис аутентификации и авторизации пользователей. Предполагается, что есть некоторая система, при входе в которую пользователь должен “залогиниться”. Вход в систему производится посредством ввода имени, логина и пароля.
//        У пользователя в таблице должны храниться имя, логин, пароль, дата и время создания профиля, а также дата и время модификации профиля и доступные роли.
//        Изначально ролей пользователя всего 6: “Разработчик”, “Аналитик”, “Тестировщик”, “Менеджер”, “Дизайнер”, “По умолчанию”. Но необходимо спроектировать систему так, чтобы оставить возможность для добавления ролей. Ролей у пользователя может быть сколько угодно (но не меньше одной роли).
        roleDAO.createRole("По умолчанию");
        roleDAO.createRole("Разработчик");
        roleDAO.createRole("Аналитик");
        roleDAO.createRole("Тестировщик");
        roleDAO.createRole("Менеджер");
        roleDAO.createRole("Дизайнер");
        roleDAO.createRole("Разнорабочий");

//                Функционал
//        В сервисе должны быть возможности:
//        Добавлять нового пользователя с ролями в БД;
        userDAO.createUser();
        userDAO.createUser();
        userDAO.createUser();
        userDAO.createUser();

////       Получать список пользователей из БД (без ролей);
        System.out.println(userDAO.getAllUser());

        roleDAO.printRolesWithUsersList(); //список ролей со списками пользователей
        userDAO.printUsersWithRolesList(); //список пользователей со списками ролей

//        Получать конкретного пользователя (с его ролями) из БД;
        userDAO.printUsersWithRolesList(userDAO.getUserById(1));

//        Получать список пользователей по конкретной роли;
        roleDAO.printRolesWithUsersList(roleDAO.findRoleByName("Менеджер"));

//        Удалять пользователя в БД;
       userDAO.deleteUser(userDAO.getUserById(2));

//        Редактировать существующего пользователя в БД.
        userDAO.updateUser(userDAO.getUserById(3));

////        Если в запросе на редактирование передан массив ролей, система должна обновить список ролей пользователя в БД — новые привязки добавить, неактуальные привязки удалить.
        roleDAO.deleteRole("По умолчанию"); // пробуем удалить роль "По умолчанию" - не должна удаляться
        roleDAO.updateRole(roleDAO.getRoleById(1)); // пробуем обновить роль "По умолчанию" - должна остатться неизменной
        roleDAO.deleteRole("Разнорабочий"); //удаляем из базы любую ненужную роль, автоматически удаляется у всех пользователей

    }
}
