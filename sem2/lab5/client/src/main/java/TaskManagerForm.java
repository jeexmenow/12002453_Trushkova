import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TaskManagerForm extends JFrame {

    private static final String TASKS_URL = "http://localhost:8080/api";

    private final DefaultListModel<String> taskListModel = new DefaultListModel<>();
    private final String authHeader;

    public TaskManagerForm(String authHeader) {
        this.authHeader = authHeader;
        setTitle("Список дел");
        setSize(480, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(new JScrollPane(new JList<>(taskListModel)), BorderLayout.CENTER);

        JTextField descriptionField = new JTextField();
        JButton addButton = new JButton("Добавить");
        JButton refreshButton = new JButton("Обновить задачи");
        JPanel actions = new JPanel(new BorderLayout(8, 8));
        JPanel buttons = new JPanel();
        buttons.add(addButton);
        buttons.add(refreshButton);
        actions.add(descriptionField, BorderLayout.CENTER);
        actions.add(buttons, BorderLayout.EAST);
        add(actions, BorderLayout.SOUTH);

        addButton.addActionListener(event -> addTask(descriptionField));
        refreshButton.addActionListener(event -> loadTasks());
        loadTasks();
    }

    static boolean canOpen(String authHeader) {
        try {
            HttpURLConnection connection = open(authHeader, "GET");
            return connection.getResponseCode() == 200;
        } catch (Exception exception) {
            return false;
        }
    }

    private void addTask(JTextField descriptionField) {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите описание задачи");
            return;
        }
        try {
            HttpURLConnection connection = open(authHeader, "POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            byte[] body = ("{\"description\":\"" + escape(description) + "\"}")
                    .getBytes(StandardCharsets.UTF_8);
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            if (connection.getResponseCode() != 200) {
                JOptionPane.showMessageDialog(this, "Не удалось добавить задачу");
                return;
            }
            descriptionField.setText("");
            loadTasks();
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Сервер недоступен");
        }
    }

    private void loadTasks() {
        try {
            HttpURLConnection connection = open(authHeader, "GET");
            if (connection.getResponseCode() != 200) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки задач");
                return;
            }
            parseJson(read(connection.getInputStream()));
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, "Сервер недоступен");
        }
    }

    private static HttpURLConnection open(String authHeader, String method) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(TASKS_URL).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + authHeader);
        return connection;
    }

    private static String read(InputStream input) throws Exception {
        StringBuilder json = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        }
        return json.toString();
    }

    private void parseJson(String json) {
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1);
        }
        taskListModel.clear();
        if (json.isBlank()) {
            return;
        }
        int taskNumber = 1;
        for (String task : json.split("\\},\\{")) {
            task = task.replace("{", "").replace("}", "").trim();
            String description = extractField(task, "description");
            if (description != null) {
                taskListModel.addElement("Задача " + taskNumber++ + ": " + description);
            }
        }
    }

    private String extractField(String task, String fieldName) {
        for (String field : task.split(",")) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");
            if (key.equals(fieldName)) {
                return value;
            }
        }
        return null;
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static void main(String[] args) {
        String authHeader = "dXNlcjpwYXNzd29yZA==";
        SwingUtilities.invokeLater(() -> new TaskManagerForm(authHeader).setVisible(true));
    }
}
