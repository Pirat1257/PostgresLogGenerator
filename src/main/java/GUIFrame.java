import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUIFrame extends JFrame {
    private JPanel rootPanel;
    private JTextField log_path;
    private JTextField trash_log_name;
    private JTextField result_path;
    private JTextField db_url;
    private JTextField admin_name;
    private JPasswordField admin_pass;
    private JButton connectButton;
    private JLabel connectionLable;
    private JTextField count_of_vars;
    private JButton generateButton;
    private JProgressBar progressBar;
    private DB_work db_work;

    /*---- Конструктор ----*/
    public GUIFrame() {
        setContentPane(rootPanel); // Установка панели содержимого
        setVisible(true); // Установка видимости
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Действие при завершении программы
        connectionLable.setForeground(Color.red); // Выделяем надпись о подключении красным
        // Инициализация класса работы с БД
        db_work = new DB_work();

        /*---- Обработка нажатия на кнопку CONNECT ----*/
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = db_url.getText();
                String admin_username = admin_name.getText();
                String admin_password = admin_pass.getText();

                if (admin_password.length() == 0)
                    return;

                if (db_work.connect_to_BD(url, admin_username, admin_password) == true) {
                    connectionLable.setForeground(Color.green);
                    connectionLable.setText("Подключено");
                }
                else {
                    connectionLable.setForeground(Color.red);
                    connectionLable.setText("Нет подключения");
                }

            }
        });

        /*---- Обработка нажатия на кнопку GENERATE ----*/
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }


}
