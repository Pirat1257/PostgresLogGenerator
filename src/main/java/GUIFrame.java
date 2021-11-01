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
    private JLabel connectionLabel;
    private JTextField count_of_vars;
    private JButton generateButton;
    private JProgressBar progressBar;
    private JButton disconnectButton;
    private DB_work db_work;

    /*---- Конструктор ----*/
    public GUIFrame() {
        setContentPane(rootPanel); // Установка панели содержимого
        rootPanel.setMinimumSize(new Dimension(10, 10));
        pack();
        setVisible(true); // Установка видимости
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Действие при завершении программы
        connectionLabel.setForeground(Color.red); // Выделяем надпись о подключении красным
        // Инициализация класса работы с БД
        db_work = new DB_work();

        /*---- Обработка нажатия на кнопку CONNECT ----*/
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = db_url.getText();
                String admin_username = admin_name.getText();
                String admin_password = admin_pass.getText();
                // Без пароля - сразу на выход
                if (admin_password.length() == 0)
                    return;
                // Пробуем подключиться
                if (db_work.connect_to_BD(url, admin_username, admin_password) == true) {
                    connectionLabel.setForeground(Color.green);
                    connectionLabel.setText("Подключено");
                }
                else {
                    connectionLabel.setForeground(Color.red);
                    connectionLabel.setText("Нет подключения");
                }
            }
        });

        /*---- Обработка нажатия на кнопку DISCONNECT ----*/
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (db_work.disconnect_from_DB() == true) {
                    connectionLabel.setForeground(Color.red);
                    connectionLabel.setText("Нет подключения");
                }
            }
        });

        /*---- Обработка нажатия на кнопку GENERATE ----*/
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt(count_of_vars.getText());
                progressBar.setStringPainted(true);
                progressBar.setMinimum(0);
                progressBar.setMaximum(i);
                progressBar.setValue(0);
                for (int k = 0; k < i; k++) {
                    db_work.create_variant(log_path.getText(), trash_log_name.getText(), result_path.getText(), String.valueOf(k), 10);
                    progressBar.setValue(k + 1);
                }
            }
        });

    }


}
