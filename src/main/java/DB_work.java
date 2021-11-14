import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

/*---- Работа от лица пользователей БД ----*/
class User extends Thread {

    String name; // Имя работника или покупателя - того, кого реализует данный поток
    int role; // Роль: 0 - admin, 1 - engineer, 2- manager, 3 - client
    boolean evil; // 0 - ОК, 1 - злоумышленник
    int operationCount; // Количество выполняемых транзакций
    String varPathString; // Куда записывать ответ
    String url; // url to DB
    int timeToSteal; // Номер операции, на которой произойдет кража персональных данных
    Statement st; // Для выполнения запросов
    int vulnerableVar; // Вариант уязвимости, необходимый для определения кражи

    /*---- Конструктор ----*/
    public User(
            String name_, // Имя работника или покупателя
            int role_, // Роль персонажа: 0 - admin, 1 - engineer, 2- manager, 3 - client
            boolean evil_, // Злоумышленник?
            int operationCount_, // Количество выполняемых транзакций
            int vulnerableVar_, // Вариант уязвимости
            String varPathString_, // Путь для записи ответа
            String url_ // url базы данных
    ) {
        name = name_;
        role = role_;
        evil = evil_;
        operationCount = operationCount_;
        varPathString = varPathString_;
        url = url_;
        vulnerableVar = vulnerableVar_;
    }

    private String getCustomerInfo = "select getCustomerInfo('name');";
    private String getOrdersByCustomer = "select getOrdersByCustomer('name');";
    private String getOrdersByItsID = "select getOrdersByItsID(number);";
    private String getUserInfo = "select getUserInfo('name');";
    private String getReleasedEquipmentInfo = "select getReleasedEquipmentInfo();";
    private String getUnreleasedEquipmentInfo = "select getUnreleasedEquipmentInfo();";

    /*---- Создание запроса вызова getCustomerInfo ----*/
    private String prepareGetCustomerInfo() {
        // Выбор имени покупателя
        int nameVar = (int)(Math.random() * ((4 - 0) + 1)) + 0;
        // Подставляем имя покупателя в запрос
        String query = null;
        switch (nameVar) {
            case 0: query = getCustomerInfo.replace("name", "mia"); break;
            case 1: query = getCustomerInfo.replace("name", "amelia"); break;
            case 2: query = getCustomerInfo.replace("name", "jessica"); break;
            case 3: query = getCustomerInfo.replace("name", "lily"); break;
            case 4: query = getCustomerInfo.replace("name", "lucy"); break;
        }
        return query;
    }

    /*---- Создание запроса вызова getOrdersByCustomer ----*/
    private String prepareGetOrdersByCustomer() {
        // Выбор имени покупателя
        int nameVar = (int)(Math.random() * ((4 - 0) + 1)) + 0;
        // Подставляем имя покупателя в запрос
        String query = null;
        switch (nameVar) {
            case 0: query = getOrdersByCustomer.replace("name", "mia"); break;
            case 1: query = getOrdersByCustomer.replace("name", "amelia"); break;
            case 2: query = getOrdersByCustomer.replace("name", "jessica"); break;
            case 3: query = getOrdersByCustomer.replace("name", "lily"); break;
            case 4: query = getOrdersByCustomer.replace("name", "lucy"); break;
        }
        return query;
    }

    /*---- Создание запроса вызова getOrdersByItsID ----*/
    private String prepareGetOrdersByItsID() {
        // Выбор номера заказа
        int orderVar = (int)(Math.random() * ((5 - 1) + 1)) + 1;
        // Подставляем номер заказа в запрос
        String query = getOrdersByItsID.replace("number", String.valueOf(orderVar));
        return query;
    }

    /*---- Создание запроса вызова getUserInfo ----*/
    private String prepareGetUserInfo() {
        // Выбор имени работника
        int nameVar = (int)(Math.random() * ((10 - 0) + 1)) + 0;
        // Подставляем имя работника в запрос
        String query = null;
        switch (nameVar) {
            case 0: query = getUserInfo.replace("name", "henry"); break;
            case 1: query = getUserInfo.replace("name", "elizabeth"); break;
            case 2: query = getUserInfo.replace("name", "emily"); break;
            case 3: query = getUserInfo.replace("name", "jack"); break;
            case 4: query = getUserInfo.replace("name", "riley"); break;
            case 5: query = getUserInfo.replace("name", "ethan"); break;
            case 6: query = getUserInfo.replace("name", "oliver"); break;
            case 7: query = getUserInfo.replace("name", "alexander"); break;
            case 8: query = getUserInfo.replace("name", "harry"); break;
            case 9: query = getUserInfo.replace("name", "daniel"); break;
            case 10: query = getUserInfo.replace("name", "sophie"); break;
        }
        return query;
    }

    /*---- Создание запроса вызова кражи getUserInfo ----*/
    private String prepareStealGetUserInfo() {
        // Выбираем пользователя для кражи данных
        String victimName = null;
        while (true) {
            int nameVar = (int)(Math.random() * ((10 - 0) + 1)) + 0;
            switch (nameVar) {
                case 0: victimName = "henry"; break;
                case 1: victimName = "elizabeth"; break;
                case 2: victimName = "emily"; break;
                case 3: victimName = "jack"; break;
                case 4: victimName = "riley"; break;
                case 5: victimName = "ethan"; break;
                case 6: victimName = "oliver"; break;
                case 7: victimName = "alexander"; break;
                case 8: victimName = "harry"; break;
                case 9: victimName = "daniel"; break;
                case 10: victimName = "sophie"; break;
            }
            // Проверяем, что не будем читать самих себя
            if (!victimName.equals(name)) {
                break;
            }
        }
        // Подставляем имя жертвы в запрос
        String query = getUserInfo.replace("name", victimName);
        return query;
    }

    /*---- Создание запроса вызова кражи getOrdersByCustomer ----*/
    private String prepareStealGetOrdersByCustomer() {
        // Выбираем покупателя для кражи данных
        String victimName = null;
        while (true) {
            int nameVar = (int)(Math.random() * ((4 - 0) + 1)) + 0;
            switch (nameVar) {
                case 0: victimName = "mia"; break;
                case 1: victimName = "amelia"; break;
                case 2: victimName = "jessica"; break;
                case 3: victimName = "lily"; break;
                case 4: victimName = "lucy"; break;
            }
            // Проверяем, что не будем читать самих себя
            if (!victimName.equals(name)) {
                break;
            }
        }
        // Подставляем имя жертвы в запрос
        String query = getOrdersByCustomer.replace("name", victimName);
        return query;
    }

    /*---- Создание запроса вызова кражи getCustomerInfo ----*/
    private String prepareStealGetCustomerInfo() {
        // Выбираем покупателя для кражи данных
        String victimName = null;
        while (true) {
            int nameVar = (int)(Math.random() * ((4 - 0) + 1)) + 0;
            switch (nameVar) {
                case 0: victimName = "mia"; break;
                case 1: victimName = "amelia"; break;
                case 2: victimName = "jessica"; break;
                case 3: victimName = "lily"; break;
                case 4: victimName = "lucy"; break;
            }
            // Проверяем, что не будем читать самих себя
            if (!victimName.equals(name)) {
                break;
            }
        }
        // Подставляем имя жертвы в запрос
        String query = getCustomerInfo.replace("name", victimName);
        return query;
    }

    /*---- Работа Администратора ----*/
    private void adminWork() {
        // Выбор операции
        int selectVar = (int)(Math.random() * ((5 - 0) + 1)) + 0;
        String query = null; // Запрос
        // getCustomerInfo
        if (selectVar == 0) {
            query = prepareGetCustomerInfo();
        }
        // getOrdersByCustomer
        else if (selectVar == 1) {
            query = prepareGetOrdersByCustomer();
        }
        // getOrdersByItsID
        else if (selectVar == 2) {
            query = prepareGetOrdersByItsID();
        }
        // getUserInfo
        else if (selectVar == 3) {
            query = prepareGetUserInfo();
        }
        // getReleasedEquipmentInfo
        else if (selectVar == 4) {
            query = getReleasedEquipmentInfo;
        }
        // getUnreleasedEquipmentInfo
        else if (selectVar == 5) {
            query = getUnreleasedEquipmentInfo;
        }
        // Выполнение запроса
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Работа Инженера ----*/
    private void engineerWork() {
        // Выбор операции
        int selectVar = (int)(Math.random() * ((2 - 0) + 1)) + 0;
        String query = null; // Запрос
        // getUserInfo
        if (selectVar == 0) {
            query = getUserInfo.replace("name", name);
        }
        // getReleasedEquipmentInfo
        else if (selectVar == 1) {
            query = getReleasedEquipmentInfo;
        }
        // getUnreleasedEquipmentInfo
        else if (selectVar == 2) {
            query = getUnreleasedEquipmentInfo;
        }
        // Выполнение запроса
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Работа Менеджера ----*/
    private void managerWork() {
        // Выбор операции
        int selectVar = (int)(Math.random() * ((3 - 0) + 1)) + 0;
        String query = null; // Запрос
        // getUserInfo
        if (selectVar == 0) {
            query = getUserInfo.replace("name", name);
        }
        // getOrdersByCustomer
        else if (selectVar == 1) {
            query = prepareGetOrdersByCustomer();
        }
        // getOrdersByItsID
        else if (selectVar == 2) {
            query = prepareGetOrdersByItsID();
        }
        // getReleasedEquipmentInfo
        else if (selectVar == 3) {
            query = getReleasedEquipmentInfo;
        }
        // Выполнение запроса
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Работа Покупателя ----*/
    private void customerWork() {
        // Выбор операции
        int selectVar = (int)(Math.random() * ((2 - 0) + 1)) + 0;
        String query = null; // Запрос
        // getCustomerInfo
        if (selectVar == 0) {
            query = getCustomerInfo.replace("name", name);
        }
        // getOrdersByCustomer
        else if (selectVar == 1) {
            query = getOrdersByCustomer.replace("name", name);
        }
        // getReleasedEquipmentInfo
        else if (selectVar == 2) {
            query = getReleasedEquipmentInfo;
        }
        // Выполнение запроса
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Кража Инженера (1-4) ----*/
    private void engineerSteal() {
        String query = null;
        // 1. (2.) Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о всех работниках
        if (vulnerableVar == 1 || vulnerableVar == 2) {
            query = prepareStealGetUserInfo();
        }
        // 3. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о заказах
        else if (vulnerableVar == 3) {
            // Выбор варианта запроса
            int selectVar = (int)(Math.random() * ((1 - 0) + 1)) + 0;
            if (selectVar == 0) {
                query = prepareGetOrdersByItsID();
            }
            else {
                query = prepareGetOrdersByCustomer();
            }
        }
        // 4. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о покупателях
        else if (vulnerableVar == 4) {
            query = prepareGetCustomerInfo();
        }
        // Выполнение кражи
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Кража Менеджера (5-9) ----*/
    private void managerSteal() {
        String query = null;
        // 5. (6.) Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех работниках
        if (vulnerableVar == 5 || vulnerableVar == 6) {
            // Подставляем имя жертвы в запрос и выполняем его
            query = prepareStealGetUserInfo();
        }
        // 7. (8.)Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всем оборудовании
        else if (vulnerableVar == 7 || vulnerableVar == 8) {
            query = getUnreleasedEquipmentInfo;
        }
        // 9. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех покупателях
        else if (vulnerableVar == 9) {
            query = prepareGetCustomerInfo();
        }
        // Выполнение кражи
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Кража покупателя (10-16) ----*/
    private void customerSteal() {
        String query = null;
        // 10. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех работниках
        if (vulnerableVar == 10) {
            query = prepareGetUserInfo();
        }
        // 11. (12.) Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех заказах
        else if (vulnerableVar == 11 || vulnerableVar == 12) {
            query = prepareStealGetOrdersByCustomer();
        }
        // 13. (14.) Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всем оборудовании
        else if (vulnerableVar == 13 || vulnerableVar == 14) {
            query = getUnreleasedEquipmentInfo;
        }
        // 15. (16.) Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех покупателях
        else if (vulnerableVar == 15 || vulnerableVar == 16) {
            query = prepareStealGetCustomerInfo();
        }
        // Выполнение кражи
        try {
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*---- Запуск работы ----*/
    @Override
    public void run() {
        Connection con = null;
        try {
            // Пользователь подключается к БД
            con = DriverManager.getConnection(url, name, "pass");
            st = con.createStatement();
            String start, finish = null; // Для записи времени
            // Если это нарушитель, производится выбор шага, на котором будет произведена кража информации
            if (evil == true) {
                timeToSteal = (int)(Math.random() * (((operationCount - 1) - 1) + 1)) + 1;
            }
            // Цикл вызова операций
            String startString = null;
            String finishString = null;
            for (int i = 0; i < operationCount; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ResultSet rs_start = st.executeQuery("select current_timestamp;");
                if (rs_start.next()) {
                    startString = rs_start.getString(1);
                }
                // Если это злоумышленник и настало время кражи - обращаемся к секретной информации
                if (evil == true && i == timeToSteal) {
                    // engineer
                    if (role == 1) {
                        engineerSteal();
                    }
                    // manager
                    else if (role == 2) {
                        managerSteal();
                    }
                    // customer
                    else if (role == 3) {
                        customerSteal();
                    }
                    ResultSet rs_finish = st.executeQuery("select current_timestamp;");
                    // Запись временного промежутка, во время которого произошла кража
                    try {
                        // Подготовка к записи в файл ответов
                        OutputStream os = null;
                        OutputStreamWriter osw = null;
                        File answerFile = new File(varPathString);
                        os = new FileOutputStream(answerFile, true);
                        osw = new OutputStreamWriter(os, "UTF-8");
                        if(rs_finish.next()) {
                            finishString = rs_finish.getString(1);
                        }
                        osw.write("Временной промежуток, когда произошла кража:\n    " + startString + "\n    " + finishString + "\n" + "Номер запроса злоумышленника: " + timeToSteal);
                        osw.flush();
                        osw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // В противном случае мирная деятельность
                else {
                    // admin
                    if (role == 0) {
                        adminWork();
                    }
                    // engineer
                    else if (role == 1) {
                        engineerWork();
                    }
                    // manager
                    else if (role == 2) {
                        managerWork();
                    }
                    // customer
                    else if (role == 3) {
                        customerWork();
                    }
                    st.execute("select current_timestamp");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            st.close();
            con.close();
            //System.out.println(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

/*---- Работа с базой данных postgres ----*/
public class DB_work {

    private String url; // url to DB
    private Connection con_admin; // Connection to DB for admin
    private Statement st_admin; // Statement object for sending SQL statements to the database for admin

    // Удаление таблиц
    private String deleteTables = "drop table if exists users cascade;" +
            "drop table if exists equipment cascade;" +
            "drop table if exists workWithEquipment cascade;" +
            "drop table if exists customers cascade;" +
            "drop table if exists orders cascade;";
    // Создание таблиц
    private String createTables = "create table users (username text not null, role text not null, password text not null, phoneNumber text not null, email text not null, primary key (username));" +
            "create table equipment (equipmentID int not null, price int not null, release boolean not null, specifications text not null, primary key (equipmentID));" +
            "create table workWithEquipment (engineerUsername text not null references users(username) on delete cascade on update cascade, equipmentID int not null references equipment(equipmentID) on delete cascade on update cascade, primary key (engineerUsername, equipmentID));" +
            "create table customers (customerName text not null, password text not null, email text not null, primary key (customerName));" +
            "create table orders (orderID int not null, customerName text not null references customers(customerName) on delete cascade on update cascade, orderDare date not null, equipmentID int not null references equipment(equipmentID) on delete cascade on update cascade, primary key (orderID));";
    // Удаление ролей
    private String deleteRoles = "drop role if exists administrator;" +
            "drop role if exists engineer;" +
            "drop role if exists manager;" +
            "drop role if exists client;";
    // Создание ролей
    private String createRoles = "create role administrator;" +
            "create role engineer;" +
            "create role manager;" +
            "create role client;";
    // Удаление пользователей
    private String deleteUsers = "drop user if exists Henry;" +
            "drop user if exists Elizabeth;" +
            "drop user if exists Emily;" +
            "drop user if exists Jack;" +
            "drop user if exists Riley;" +
            "drop user if exists Ethan;" +
            "drop user if exists Oliver;" +
            "drop user if exists Alexander;" +
            "drop user if exists Harry;" +
            "drop user if exists Daniel;" +
            "drop user if exists Sophie;" +
            "drop user if exists Mia;" +
            "drop user if exists Amelia;" +
            "drop user if exists Jessica;" +
            "drop user if exists Lily;" +
            "drop user if exists Lucy;";
    // Создание пользователей
    private String createUsers = "create user Henry with password 'pass';" +
            "create user Elizabeth with password 'pass';" +
            "create user Emily with password 'pass';" +
            "create user Jack with password 'pass';" +
            "create user Riley with password 'pass';" +
            "create user Ethan with password 'pass';" +
            "create user Oliver with password 'pass';" +
            "create user Alexander with password 'pass';" +
            "create user Harry with password 'pass';" +
            "create user Daniel with password 'pass';" +
            "create user Sophie with password 'pass';" +
            "create user Mia with password 'pass';" +
            "create user Amelia with password 'pass';" +
            "create user Jessica with password 'pass';" +
            "create user Lily with password 'pass';" +
            "create user Lucy with password 'pass';";
    // Привязка пользователей к ролям
    private String grantUsersToRoles = "grant administrator to henry;" +
            "grant engineer to elizabeth, emily, jack, riley, ethan;" +
            "grant manager to oliver, alexander, harry, daniel, sophie;" +
            "grant client to mia, amelia, jessica, lily, lucy;";
    // Заполнение БД
    private String fillDB = "insert into users values ('henry', 'administrator', 'pass', '123456789', 'henry@mail.com')," +
            "                         ('elizabeth', 'engineer', 'pass', '987654321', 'elizabeth@mail.com')," +
            "                         ('emily', 'engineer', 'pass', '147852369', 'emily@mail.com')," +
            "                         ('jack', 'engineer', 'pass', '369852147', 'jack@mail.com')," +
            "                         ('riley', 'engineer', 'pass', '258741369', 'riley@mail.com')," +
            "                         ('ethan', 'engineer', 'pass', '369147258', 'ethan@mail.com')," +
            "                         ('oliver', 'manager', 'pass', '741963852', 'oliver@mail.com')," +
            "                         ('alexander', 'manager', 'pass', '753918426', 'alexander@mail.com')," +
            "                         ('harry', 'manager', 'pass', '371984625', 'harry@mail.com')," +
            "                         ('daniel', 'manager', 'pass', '931782645', 'daniel@mail.com')," +
            "                         ('sophie', 'manager', 'pass', '321987654', 'sophie@mail.com');" +
            "insert into customers values ('mia', 'pass', 'mia@mail.com')," +
            "                             ('amelia', 'pass', 'amelia@mail.com')," +
            "                             ('jessica', 'pass', 'jessica@mail.com')," +
            "                             ('lily', 'pass', 'lily@mail.com')," +
            "                             ('lucy', 'pass', 'lucy@mail.com');" +
            "insert into equipment values (1, 1000, true, 'specifications1')," +
            "                             (2, 1500, true, 'specifications2')," +
            "                             (3, 2000, true, 'specifications3')," +
            "                             (4, 2500, true, 'specifications4')," +
            "                             (5, 3000, true, 'specifications5')," +
            "                             (6, 800, false, 'specifications6')," +
            "                             (7, 900, false, 'specifications7')," +
            "                             (8, 500, false, 'specifications8');" +
            "insert into workWithEquipment values ('elizabeth', 6)," +
            "                                     ('elizabeth', 7)," +
            "                                     ('elizabeth', 8)," +
            "                                     ('emily', 6)," +
            "                                     ('jack', 7)," +
            "                                     ('riley', 8);" +
            "insert into orders values (1, 'mia', '2020-10-19', 1)," +
            "                          (2, 'lucy', '2021-09-20', 2)," +
            "                          (3, 'lily', '2019-10-20', 5)," +
            "                          (4, 'lily', '2019-10-19', 4)," +
            "                          (5, 'jessica', '2018-10-10', 3);";
    // Корректная настройка доступа для администратора
    private String correctAdmin = "grant select, insert, update, delete on table customers, equipment, users, workWithEquipment, orders to administrator;";
    // Корректная настройка доступа для инженера
    private String correctEngineer = "grant select on table equipment to engineer;" +
            "grant update (specifications) on table equipment to engineer;" +
            "grant select on table users to engineer;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists engineer_policy on equipment;" +
            "create policy engineer_policy on equipment" +
            "    to engineer" +
            "    using (true)" +
            "    with check (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists engineer_policy_to_users on users;" +
            "create policy engineer_policy_to_users on users" +
            "    to engineer" +
            "    using (username = current_user);";
    // Корректная настройка доступа для менеджер
    private String correctManager = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists manager_policy on equipment;" +
            "create policy manager_policy on equipment" +
            "    to manager using (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists manager_policy_to_users on users;" +
            "create policy manager_policy_to_users on users" +
            "    to manager" +
            "    using (username = current_user);";
    // Корректная настройка доступа для покупателя
    private String correctClient = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // 1. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о всех работниках
    private String incorrectEngineer1 = "grant select on table equipment to engineer;" +
            "grant update (specifications) on table equipment to engineer;" +
            "grant select on table users to engineer;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists engineer_policy on equipment;" +
            "create policy engineer_policy on equipment" +
            "    to engineer" +
            "    using (true)" +
            "    with check (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists engineer_policy_to_users on users;" +
            "create policy engineer_policy_to_users on users" +
            "    to engineer" +
            "    using (true);";
    // 2. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о всех работниках
    private String incorrectEngineer2 = "grant select on table equipment to engineer;" +
            "grant update (specifications) on table equipment to engineer;" +
            "grant select on table users to engineer;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists engineer_policy on equipment;" +
            "create policy engineer_policy on equipment" +
            "    to engineer" +
            "    using (true)" +
            "    with check (release = true);";
    // 3. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о заказах
    private String incorrectEngineer3 = "grant select on table equipment to engineer;" +
            "grant update (specifications) on table equipment to engineer;" +
            "grant select on table users to engineer;" +
            "grant select on table orders to engineer;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists engineer_policy on equipment;" +
            "create policy engineer_policy on equipment" +
            "    to engineer" +
            "    using (true)" +
            "    with check (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists engineer_policy_to_users on users;" +
            "create policy engineer_policy_to_users on users" +
            "    to engineer" +
            "    using (username = current_user);";
    // 4. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о покупателях
    private String incorrectEngineer4 = "grant select on table equipment to engineer;" +
            "grant update (specifications) on table equipment to engineer;" +
            "grant select on table users to engineer;" +
            "grant select on table customers to engineer;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists engineer_policy on equipment;" +
            "create policy engineer_policy on equipment" +
            "    to engineer" +
            "    using (true)" +
            "    with check (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists engineer_policy_to_users on users;" +
            "create policy engineer_policy_to_users on users" +
            "    to engineer" +
            "    using (username = current_user);";
    // 5. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех работниках
    private String incorrectManager1 = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists manager_policy on equipment;" +
            "create policy manager_policy on equipment" +
            "    to manager using (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists manager_policy_to_users on users;" +
            "create policy manager_policy_to_users on users" +
            "    to manager" +
            "    using (true);";
    // 6. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех работниках
    private String incorrectManager2 = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists manager_policy on equipment;" +
            "create policy manager_policy on equipment" +
            "    to manager using (release = true);";
    // 7. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всем оборудовании
    private String incorrectManager3 = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists manager_policy on equipment;" +
            "create policy manager_policy on equipment" +
            "    to manager using (true);" +
            "alter table users enable row level security;" +
            "drop policy if exists manager_policy_to_users on users;" +
            "create policy manager_policy_to_users on users" +
            "    to manager" +
            "    using (username = current_user);";
    // 8. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всем оборудовании
    private String incorrectManager4 = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "alter table users enable row level security;" +
            "drop policy if exists manager_policy_to_users on users;" +
            "create policy manager_policy_to_users on users" +
            "    to manager" +
            "    using (username = current_user);";
    // 9. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех покупателях
    private String incorrectManager5 = "grant select on table orders to manager;" +
            "grant select on table equipment to manager;" +
            "grant select on table users to manager;" +
            "grant select on table customers to manager;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists manager_policy on equipment;" +
            "create policy manager_policy on equipment" +
            "    to manager using (release = true);" +
            "alter table users enable row level security;" +
            "drop policy if exists manager_policy_to_users on users;" +
            "create policy manager_policy_to_users on users" +
            "    to manager" +
            "    using (username = current_user);";
    // 10. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех работниках
    private String incorrectClient1 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "grant select on table users to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // 11. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех заказах
    private String incorrectClient2 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (true);";
    // 12. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех заказах
    private String incorrectClient3 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);";
    // 13. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всем оборудовании
    private String incorrectClient4 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // 14. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всем оборудовании
    private String incorrectClient5 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (customerName = current_user)" +
            "    with check (customerName = current_user);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // 15. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех покупателях
    private String incorrectClient6 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table customers enable row level security;" +
            "drop policy if exists client_policy_to_customers on customers;" +
            "create policy client_policy_to_customers on customers" +
            "    to client" +
            "    using (true)" +
            "    with check (customerName = current_user);" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // 16. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех покупателях
    private String incorrectClient7 = "grant select, update on table customers to client;" +
            "grant select on table equipment to client;" +
            "grant select on table orders to client;" +
            "alter table equipment enable row level security;" +
            "drop policy if exists client_policy_to_equipment on equipment;" +
            "create policy client_policy_to_equipment on equipment" +
            "    to client using (release = true);" +
            "alter table orders enable row level security;" +
            "drop policy if exists client_policy_to_orders on orders;" +
            "create policy client_policy_to_orders on orders" +
            "    to client using (customerName = current_user);";
    // Удаление функций
    private String deleteFunc = "drop function if exists getCustomerInfo(text);" +
            "drop function if exists getOrdersByCustomer(text);" +
            "drop function if exists getOrdersByItsID(int);" +
            "drop function if exists getUserInfo(text);" +
            "drop function if exists getReleasedEquipmentInfo();" +
            "drop function if exists getUnreleasedEquipmentInfo();";
    // Создание функций
    private String createFunc = "drop function if exists getCustomerInfo(text);" +
            "create function getCustomerInfo(name text) returns table" +
            "(customer_name text, pass text, email text) as" +
            "    $$" +
            "    begin" +
            "        return query select * from customers where customerName = $1;" +
            "    end;" +
            "    $$ language plpgsql;" +
            "drop function if exists getOrdersByCustomer(text);" +
            "create function getOrdersByCustomer(name text) returns table" +
            "(orderId int, customer_name text, orderDate date, equipmentID int) as" +
            "    $$" +
            "    begin" +
            "        return query select * from orders where customerName = $1;" +
            "    end;" +
            "    $$ language plpgsql;" +
            "drop function if exists getOrdersByItsID(int);" +
            "create function getOrdersByItsID(id int) returns table" +
            "(orderId int, customer_name text, orderDate date, equipmentID int) as" +
            "    $$" +
            "    begin" +
            "        return query select * from orders where orders.orderID = $1;" +
            "    end;" +
            "    $$ language plpgsql;" +
            "drop function if exists getUserInfo(text);" +
            "create function getUserInfo(name text) returns table" +
            "(user_name text, role text, pass text, phone text, email text) as" +
            "    $$" +
            "    begin" +
            "        return query select * from users where username = $1;" +
            "    end;" +
            "    $$ language plpgsql;" +
            "drop function if exists getReleasedEquipmentInfo();" +
            "create function getReleasedEquipmentInfo() returns table" +
            "(equipment_id int, price int, specification text) as" +
            "    $$" +
            "    begin" +
            "        return query select equipmentID, equipment.price, specifications from equipment where release = true;" +
            "    end;" +
            "    $$ language plpgsql;" +
            "drop function if exists getUnreleasedEquipmentInfo();" +
            "create function getUnreleasedEquipmentInfo() returns table" +
            "(equipment_id int, price int, specification text) as" +
            "    $$" +
            "    begin" +
            "        return query select equipmentID, equipment.price, specifications from equipment where release = false;" +
            "    end;" +
            "    $$ language plpgsql;";
    // Настройка логов
    private String logSettings = "alter system set log_min_duration_statement = 0;" +
            "alter system set log_line_prefix = '%t [%p] %u ';" +
            "alter system set log_statement = 'all';" +
            "alter system set log_filename = 'logname';" +
            "SELECT pg_reload_conf();";

    /*---- Подключение к БД как администратор ----*/
    public boolean connect_to_BD(
            String new_url, // url базы данных
            String admin_username, // Имя администратора
            String admin_password // Пароль администратора
    ) {
        try {
            con_admin = DriverManager.getConnection(new_url, admin_username, admin_password);
            st_admin = con_admin.createStatement();
            url = new_url;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*---- Отключение от БД как администратор ----*/
    public boolean disconnect_from_DB() {
        try {
            st_admin.close();
            con_admin.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*---- Создание БД ----*/
    private void create_db() {
        try {
            st_admin.execute(createTables);
            st_admin.execute(createRoles);
            st_admin.execute(createUsers);
            st_admin.execute(grantUsersToRoles);
            st_admin.execute(createFunc);
            st_admin.execute(fillDB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    /*---- Сбросить БД ----*/
    private void delete_db() {
        try {
            st_admin.execute(deleteTables);
            st_admin.execute(deleteFunc);
            st_admin.execute(deleteUsers);
            st_admin.execute(deleteRoles);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return;
    }

    /*---- Копирование файла ----*/
    private static boolean copyFileUsingChannel(File source, File dest) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            try {
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        try {
            sourceChannel.close();
            destChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*---- Генерация одного варианта ----*/
    public boolean create_variant(
            String log_path, // Путь до логов postgres
            String log_file, // Название файла для записи в логах postgres
            String save_path, // Куда сохранять результат
            String varNumber, // Номер генерируемого варианта
            int operationCount
    ) {
        // Создание папки варианта
        String varPathString = save_path + "\\test" + varNumber;
        File varPath = new File(varPathString);
        if (varPath.exists()) { // Удаление существующего варианта
            Path path = Paths.get(varPathString);
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        varPath.mkdir();
        // Удаление файла для записи логов из папки
        Path logFilePath = Paths.get(log_path + "\\" + log_file);
        if (Files.exists(logFilePath)) {
            String settingUpLogs = logSettings;
            settingUpLogs = settingUpLogs.replace("logname", "postgresql-%Y-%m-%d_%H%M%S.log");
            try {
                st_admin.execute(settingUpLogs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                Files.deleteIfExists(logFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Создание файла для записи ответа
        File answerFile = new File(varPathString + "\\" + "answer" + varNumber + ".txt");
        try {
            answerFile.getParentFile().mkdirs();
            answerFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Подготовка к записи в файл ответов
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            os = new FileOutputStream(answerFile);
            osw = new OutputStreamWriter(os, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // Для настройки политики безопасности
        String settingUpSecurity = null;
        String settingUpLogs = logSettings;
        int vulnerableVar = (int)(Math.random() * ((16 - 1) + 1)) + 1; // Выбор неправильной настройки
        // Сборка с неправильной конфигурацией
        switch (vulnerableVar) {
            case 1: settingUpSecurity = correctAdmin + incorrectEngineer1 + correctManager + correctClient;
                try {
                    osw.write("1. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о всех работниках\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2: settingUpSecurity = correctAdmin + incorrectEngineer2 + correctManager + correctClient;
                try {
                    osw.write("2. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о всех работниках\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3: settingUpSecurity = correctAdmin + incorrectEngineer3 + correctManager + correctClient;
                try {
                    osw.write("3. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о заказах\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 4: settingUpSecurity = correctAdmin + incorrectEngineer4 + correctManager + correctClient;
                try {
                    osw.write("4. Некорректная настройка доступа для инженера, при которой ему предоставлена возможность просмотра информации о покупателях\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 5: settingUpSecurity = correctAdmin + correctEngineer + incorrectManager1 + correctClient;
                try {
                    osw.write("5. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех работниках\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 6: settingUpSecurity = correctAdmin + correctEngineer + incorrectManager2 + correctClient;
                try {
                    osw.write("6. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех работниках\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 7: settingUpSecurity = correctAdmin + correctEngineer + incorrectManager3 + correctClient;
                try {
                    osw.write("7. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всем оборудовании\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 8: settingUpSecurity = correctAdmin + correctEngineer + incorrectManager4 + correctClient;
                try {
                    osw.write("8. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всем оборудовании\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 9: settingUpSecurity = correctAdmin + correctEngineer + incorrectManager5 + correctClient;
                try {
                    osw.write("9. Некорректная настройка доступа для менеджера, при которой ему предоставлена возможность просмотра информации о всех покупателях\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 10: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient1;
                try {
                    osw.write("10. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех работниках\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 11: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient2;
                try {
                    osw.write("11. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех заказах\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 12: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient3;
                try {
                    osw.write("12. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех заказах\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 13: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient4;
                try {
                    osw.write("13. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всем оборудовании\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 14: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient5;
                try {
                    osw.write("14. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всем оборудовании\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 15: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient6;
                try {
                    osw.write("15. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех покупателях\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 16: settingUpSecurity = correctAdmin + correctEngineer + correctManager + incorrectClient7;
                try {
                    osw.write("16. Некорректная настройка доступа для покупателя, при которой ему предоставлена возможность просмотра информации о всех покупателях\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        try {
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingUpLogs = settingUpLogs.replace("logname", log_file); // Подставляем название файла логов
        // Производим установку настроек - начало записи
        try {
            st_admin.execute(settingUpLogs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Создание базы данных со всеми настройками
        delete_db();
        create_db();
        // Установка неправильной политики доступа
        try {
            st_admin.execute(settingUpSecurity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Производится выбор злоумышленника на основе неправильных настроек
        int evilNumber = (int)(Math.random() * ((5 - 1) + 1)) +1;
        String evilName = null; // Имя злоумышленника
        if (1 <= vulnerableVar && vulnerableVar <= 4) { // 1-4: инженер
            switch (evilNumber) {
                case 1: evilName = "elizabeth"; break;
                case 2: evilName = "emily"; break;
                case 3: evilName = "jack"; break;
                case 4: evilName = "riley"; break;
                case 5: evilName = "ethan"; break;
            }
        }
        else if (5 <= vulnerableVar && vulnerableVar <= 9) { // 5-9: менеджер
            switch (evilNumber) {
                case 1: evilName = "oliver"; break;
                case 2: evilName = "alexander"; break;
                case 3: evilName = "harry"; break;
                case 4: evilName = "daniel"; break;
                case 5: evilName = "sophie"; break;
            }
        }
        else if (10 <= vulnerableVar && vulnerableVar <= 16) { // 10-16: пользователь
            switch (evilNumber) {
                case 1: evilName = "mia"; break;
                case 2: evilName = "amelia"; break;
                case 3: evilName = "jessica"; break;
                case 4: evilName = "lily"; break;
                case 5: evilName = "lucy"; break;
            }
        }

        // Упоминаем злоумышленника в файле ответа
        try {
            osw.write("Злоумышленник: " + evilName + "\n");
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Создание пользователей
        User[] users;
        users = new User[16];
        for (int i = 0; i < 16; i++) {
            if (i == 0) {
                if (evilName.compareTo("henry") == 0) {
                    users[i] = new User("henry", 0, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("henry", 0, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 1) {
                if (evilName.compareTo("elizabeth") == 0) {
                    users[i] = new User("elizabeth", 1, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("elizabeth", 1, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 2) {
                if (evilName.compareTo("emily") == 0) {
                    users[i] = new User("emily", 1, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("emily", 1, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 3) {
                if (evilName.compareTo("jack") == 0) {
                    users[i] = new User("jack", 1, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("jack", 1, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 4) {
                if (evilName.compareTo("riley") == 0) {
                    users[i] = new User("riley", 1, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("riley", 1, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 5) {
                if (evilName.compareTo("ethan") == 0) {
                    users[i] = new User("ethan", 1, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("ethan", 1, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 6) {
                if (evilName.compareTo("oliver") == 0) {
                    users[i] = new User("oliver", 2, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("oliver", 2, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 7) {
                if (evilName.compareTo("alexander") == 0) {
                    users[i] = new User("alexander", 2, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("alexander", 2, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 8) {
                if (evilName.compareTo("harry") == 0) {
                    users[i] = new User("harry", 2, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("harry", 2, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 9) {
                if (evilName.compareTo("daniel") == 0) {
                    users[i] = new User("daniel", 2, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("daniel", 2, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 10) {
                if (evilName.compareTo("sophie") == 0) {
                    users[i] = new User("sophie", 2, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("sophie", 2, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 11) {
                if (evilName.compareTo("mia") == 0) {
                    users[i] = new User("mia", 3, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("mia", 3, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 12) {
                if (evilName.compareTo("amelia") == 0) {
                    users[i] = new User("amelia", 3, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("amelia", 3, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 13) {
                if (evilName.compareTo("jessica") == 0) {
                    users[i] = new User("jessica", 3, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("jessica", 3, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 14) {
                if (evilName.compareTo("lily") == 0) {
                    users[i] = new User("lily", 3, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("lily", 3, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
            else if (i == 15) {
                if (evilName.compareTo("lucy") == 0) {
                    users[i] = new User("lucy", 3, true, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
                else {
                    users[i] = new User("lucy", 3, false, operationCount, vulnerableVar, varPathString + "\\" + "answer" + varNumber + ".txt", url);
                }
            }
        }
        // Производится запуск пользователей
        for (int i = 0; i < 16; i++) {
            users[i].start();
        }
        for (int i = 0; i < 16; i++) {
            try {
                users[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Убираем за собой
        delete_db();
        // Производим установку обычного файла - конец записи
        settingUpLogs = logSettings;
        settingUpLogs = settingUpLogs.replace("logname", "postgresql-%Y-%m-%d_%H%M%S.log");
        try {
            st_admin.execute(settingUpLogs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Создаем файл для записи логов
        File copyLogFile = new File(varPathString + "\\" + "logFile" + varNumber + ".log");
        File originalLogFile = new File(log_path + "\\" + log_file);
        try {
            copyLogFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Производим копирование сгенерированных логов
        if(copyFileUsingChannel(originalLogFile, copyLogFile) == false) {
            return false;
        }
        logFilePath = Paths.get(log_path + "\\" + log_file);
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
