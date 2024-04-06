import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.swing.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class TaskIO {

  private static final String TASKS_FILE = "resources/tasks.json";
  private static final SecretKey key = EncryptionUtility.getOrGenerateKey();
  public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm";

  public static List<Task> loadTasks() {
    List<Task> tasks = new ArrayList<>();
    File file = new File(TASKS_FILE);
    if (!file.exists() || file.length() == 0) {
      initializeEmptyEncryptedFile(file);
      return tasks;
    }

    try {
      String encryptedContent = Files.readString(
        file.toPath(),
        StandardCharsets.UTF_8
      );
      String decryptedContent = EncryptionUtility.decrypt(
        encryptedContent,
        key
      );
      JSONArray tasksJson = new JSONArray(decryptedContent);

      for (int i = 0; i < tasksJson.length(); i++) {
        JSONObject taskObject = tasksJson.getJSONObject(i);
        Task task = new Task(
          taskObject.getString("title"),
          taskObject.optString("description", ""),
          taskObject.optString("priority", "Faible"),
          taskObject.optString("dateTime", "2020-01-01 00:00"),
          taskObject.optString("category", "General")
        );
        tasks.add(task);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
        null,
        "Erreur lors du chargement des tâches.",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      e.printStackTrace(System.out);
    }
    return tasks;
  }

  public static void saveAllTasks(List<Task> tasks) {
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    // Créer le JSONArray pour le fichier crypté
    JSONArray tasksJsonForEncryption = new JSONArray();

    // Préparer le StringBuilder pour le fichier non crypté
    StringBuilder unencryptedJsonBuilder = new StringBuilder();
    unencryptedJsonBuilder.append("[\n");

    for (int i = 0; i < tasks.size(); i++) {
      Task task = tasks.get(i);
      JSONObject taskObject = new JSONObject();

      try {
        Date date = dateFormat.parse(task.getDateTime());
        String formattedDate = dateFormat.format(date); // Utilisez la même instance de dateFormat pour le formatage

        // Ajouter au JSONArray pour le fichier crypté
        taskObject.put("title", task.getTitle());
        taskObject.put("description", task.getDescription());
        taskObject.put("priority", task.getPriority());
        taskObject.put("category", task.getCategory());
        taskObject.put("dateTime", formattedDate);
        tasksJsonForEncryption.put(taskObject);

        // Construire manuellement la chaîne JSON pour le fichier non crypté
        // Utilisez formattedDate qui est maintenant correctement formaté
        String unencryptedTaskJson = String.format(
          "    {\n" +
          "        \"title\": \"%s\",\n" +
          "        \"description\": \"%s\",\n" +
          "        \"priority\": \"%s\",\n" +
          "        \"category\": \"%s\",\n" +
          "        \"dateTime\": \"%s\"\n" +
          "    }",
          task.getTitle(),
          task.getDescription(),
          task.getPriority(),
          task.getCategory(),
          formattedDate
        );

        unencryptedJsonBuilder.append(unencryptedTaskJson);
        if (i < tasks.size() - 1) {
          unencryptedJsonBuilder.append(",\n");
        }
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }

    unencryptedJsonBuilder.append("\n]");

    try {
      // Encrypter et sauvegarder les tâches dans le fichier crypté
      String encryptedData = EncryptionUtility.encrypt(
        tasksJsonForEncryption.toString(),
        key
      );
      try (FileWriter writer = new FileWriter(TASKS_FILE, false)) {
        writer.write(encryptedData);
      }

      // Sauvegarder les tâches dans le fichier non crypté
      try (
        OutputStreamWriter writer = new OutputStreamWriter(
          new FileOutputStream("resources/unencrypted_tasks.json"),
          StandardCharsets.UTF_8
        )
      ) {
        writer.write(unencryptedJsonBuilder.toString());
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
  }

  private static void initializeEmptyEncryptedFile(File file) {
    try {
      if (file.createNewFile()) {
        try (FileWriter writer = new FileWriter(file)) {
          String encryptedData = EncryptionUtility.encrypt("[]", key);
          writer.write(encryptedData);
        }
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(
        null,
        "Impossible de créer ou d'initialiser le fichier de tâches avec des données cryptées.",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      e.printStackTrace(System.out);
    }
  }
}
