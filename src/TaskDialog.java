import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.*;

// Dialogue pour ajouter ou modifier une tâche
public class TaskDialog extends JDialog {

  // Composants du formulaire pour saisir les détails de la tâche
  private final JTextField titleField = new JTextField(20);
  private final JTextArea descriptionArea = new JTextArea(5, 20);
  private final JComboBox<String> priorityComboBox = new JComboBox<>(
    new String[] { "Haute", "Moyenne", "Faible" }
  );
  private final JComboBox<String> categoryComboBox = new JComboBox<>(
    new String[] { "Général", "Travail", "Personnel" }
  );
  private final JDateChooser dateChooser = new JDateChooser();
  private final JComboBox<Integer> hourComboBox = new JComboBox<>(
    IntStream.rangeClosed(0, 23).boxed().toArray(Integer[]::new)
  );
  private final JComboBox<Integer> minuteComboBox = new JComboBox<>(
    IntStream
      .iterate(0, n -> n < 60, n -> n + 5)
      .boxed()
      .toArray(Integer[]::new)
  );
  private final JButton okButton = new JButton("OK");
  private final JButton cancelButton = new JButton("Annuler");
  private Task task; // La tâche à ajouter ou modifier
  private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm"
  );

  // Constructeur pour ajouter une nouvelle tâche avec les catégories disponibles
  public TaskDialog(Frame owner, Set<String> categories) {
    super(owner, "Ajouter une tâche", true);
    initializeComponents();
    categories.forEach(categoryComboBox::addItem);
    okButton.addActionListener(e -> saveTask());
    cancelButton.addActionListener(e -> cancelDialog());
    pack();
    setLocationRelativeTo(null);
  }

  // Constructeur pour modifier une tâche existante avec les catégories disponibles
  public TaskDialog(Frame owner, Task taskToEdit, Set<String> categories) {
    super(owner, "Modifier la tâche", true);
    this.task = taskToEdit;
    initializeComponents();
    categories.forEach(categoryComboBox::addItem); // Remplit le JComboBox avec les catégories
    setFields(taskToEdit);
    okButton.addActionListener(e -> updateTask());
    cancelButton.addActionListener(e -> cancelDialog());
    pack();
    setLocationRelativeTo(null);
  }

  // Initialisation des composants du dialogue
  private void initializeComponents() {
    setLayout(new BorderLayout());
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);

    // Configuration du panneau pour l'entrée des données
    JPanel inputPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);

    // Ajout des composants au panneau d'entrée
    inputPanel.add(new JLabel("Titre:"), gbc);
    inputPanel.add(titleField, gbc);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weighty = 1;
    inputPanel.add(new JLabel("Description:"), gbc);
    inputPanel.add(new JScrollPane(descriptionArea), gbc);

    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weighty = 0;
    inputPanel.add(new JLabel("Priorité:"), gbc);
    inputPanel.add(priorityComboBox, gbc);
    inputPanel.add(new JLabel("Catégorie:"), gbc);
    inputPanel.add(categoryComboBox, gbc);
    add(inputPanel, BorderLayout.CENTER);

    // Configuration du panneau pour la sélection de date et heure
    JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dateTimePanel.add(new JLabel("Date:"));
    dateChooser.setPreferredSize(new Dimension(120, 20));
    dateTimePanel.add(dateChooser);
    dateTimePanel.add(new JLabel("Heure:"));
    dateTimePanel.add(hourComboBox);
    dateTimePanel.add(minuteComboBox);
    add(dateTimePanel, BorderLayout.NORTH);

    // Configuration du panneau pour les boutons OK et Annuler
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  // Méthode pour définir les champs du formulaire lors de la modification d'une tâche
  private void setFields(Task taskToEdit) {
    titleField.setText(taskToEdit.getTitle());
    descriptionArea.setText(taskToEdit.getDescription());
    priorityComboBox.setSelectedItem(taskToEdit.getPriority());
    categoryComboBox.setSelectedItem(taskToEdit.getCategory());

    try {
      Date taskDateTime = dateTimeFormat.parse(taskToEdit.getDateTime());
      dateChooser.setDate(taskDateTime);
      Calendar cal = Calendar.getInstance();
      cal.setTime(taskDateTime);
      hourComboBox.setSelectedItem(cal.get(Calendar.HOUR_OF_DAY));
      minuteComboBox.setSelectedItem(cal.get(Calendar.MINUTE));
    } catch (ParseException e) {
      JOptionPane.showMessageDialog(
        this,
        "Erreur de format de date et d'heure",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      e.printStackTrace(System.out);
    }
  }

  // Validation des entrées de l'utilisateur
  private boolean validateInput() {
    if (dateChooser.getDate() == null) {
      JOptionPane.showMessageDialog(
        this,
        "Vous devez sélectionner une date.",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      return true;
    }
    if (titleField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Le champ du titre ne peut pas être vide.",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      return true;
    }
    if (descriptionArea.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Le champ de la description ne peut pas être vide.",
        "Erreur",
        JOptionPane.ERROR_MESSAGE
      );
      return true;
    }
    return false;
  }

  // Création ou mise à jour d'une tâche à partir des entrées de l'utilisateur
  private void configureTaskFromInput() {
    if (task == null) task = new Task();
    task.setTitle(titleField.getText());
    task.setDescription(descriptionArea.getText());
    task.setPriority((String) priorityComboBox.getSelectedItem());
    task.setDateTime(getFormattedDateTime());
    task.setCategory((String) categoryComboBox.getSelectedItem());
  }

  // Méthodes pour gérer les actions des boutons
  private void saveTask() {
    if (validateInput()) return;
    configureTaskFromInput();
    setVisible(false);
  }

  private void updateTask() {
    if (validateInput()) return;
    configureTaskFromInput();
    setVisible(false);
  }

  private void cancelDialog() {
    task = null; // Annulation de toute modification ou ajout
    setVisible(false);
  }

  // Affiche le dialogue et retourne la tâche créée ou modifiée
  public Task showDialog() {
    setVisible(true);
    return task;
  }

  // Helper pour obtenir la date et l'heure formatées
  private String getFormattedDateTime() {
    String datePart = new SimpleDateFormat("yyyy-MM-dd")
      .format(dateChooser.getDate());
    // Assurez-vous que les valeurs récupérées des JComboBox sont castées en Integer
    String timePart = String.format(
      "%02d:%02d",
      (Integer) hourComboBox.getSelectedItem(),
      (Integer) minuteComboBox.getSelectedItem()
    );
    return datePart + " " + timePart;
  }
}
