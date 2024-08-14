import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {

  private String title;
  private String description;
  private String priority;
  private String dateTime;
  private String category;

  // Constructeur par défaut
  public Task() {
    // Initialisation avec des valeurs par défaut
    this.title = "";
    this.description = "";
    this.priority = "Faible";
    this.dateTime = "2020-01-01 00:00";
    this.category = "Général";
  }

  // Constructeur avec tous les paramètres
  public Task(
    String title,
    String description,
    String priority,
    String dateTime,
    String category
  ) {
    this.title = title;
    this.description = description;
    this.priority = priority;
    this.dateTime = dateTime;
    this.category = category;
  }

  // Getters et setters
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String toString() {
    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat outputFormat = new SimpleDateFormat(
      "dd MMMM yyyy HH:mm",
      Locale.FRANCE
    ); // Locale pour le mois en lettres
    Date date;
    try {
      date = inputFormat.parse(this.dateTime); // Assurez-vous que dateTime est au format "yyyy-MM-dd HH:mm"
    } catch (ParseException e) {
      date = new Date(); // Utilisez la date actuelle en cas d'échec de l'analyse
      e.printStackTrace(System.out);
    }
    String formattedDate = outputFormat.format(date);
    return String.format(
      "%s (%s, %s) - %s",
      title,
      priority,
      category,
      formattedDate
    );
  }
}
