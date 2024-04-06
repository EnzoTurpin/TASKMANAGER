import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;

// Interface utilisateur principale pour le gestionnaire de tâches
public class TaskManagerUI extends JFrame {

  // Définition des composants de l'interface utilisateur
  private final DefaultListModel<Task> model = new DefaultListModel<>();
  private final JList<Task> list = new JList<>(model);
  private final JButton addButton = new JButton("Ajouter");
  private final JButton deleteButton = new JButton("Supprimer");
  private final JButton editButton = new JButton("Modifier");
  private final JComboBox<String> categoryFilterComboBox = new JComboBox<>();
  private final JComboBox<String> priorityFilterComboBox = new JComboBox<>();
  private List<Task> allTasks = new ArrayList<>();

  public TaskManagerUI() {
    setTitle("Gestionnaire de Tâches");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    initializeUI();
    loadTasks();
    setSize(500, 400);
    setLocationRelativeTo(null);
  }

  // Initialisation et ajout des composants à l'interface
  private void initializeUI() {
    list.setCellRenderer(new TaskListRenderer());
    JScrollPane scrollPane = new JScrollPane(list);
    add(scrollPane, BorderLayout.CENTER);

    list.setDragEnabled(true);
    list.setDropMode(DropMode.INSERT);
    list.setTransferHandler(new TaskTransferHandler(this::saveAllTasks));

    // Création et configuration du panneau de filtres
    JPanel filterPanel = new JPanel();
    filterPanel.add(new JLabel("Filtrer par catégorie :"));
    filterPanel.add(categoryFilterComboBox);
    filterPanel.add(new JLabel("Filtrer par priorité :"));
    filterPanel.add(priorityFilterComboBox);
    add(filterPanel, BorderLayout.NORTH);

    // Configuration du panneau de boutons et ajout au bas de la fenêtre
    JPanel buttonPanel = setupButtonPanel();
    add(buttonPanel, BorderLayout.SOUTH);

    // Configuration des écouteurs d'événements pour les boutons et la liste
    setupButtonListeners();
    loadCategories();
  }

  // Remplissage des combobox pour les filtres
  private void setupFilterComboBoxes() {
    populateCategoryFilterComboBox();
    populatePriorityFilterComboBox();

    // Ajout des écouteurs pour appliquer les filtres lors d'une sélection
    categoryFilterComboBox.addActionListener(e -> filterTasks());
    priorityFilterComboBox.addActionListener(e -> filterTasks());
  }

  // Ajout des catégories uniques aux combobox
  private void populateCategoryFilterComboBox() {
    categoryFilterComboBox.removeAllItems();
    categoryFilterComboBox.addItem("Toutes");
    loadCategories();

    // Ajoutez ici les catégories initiales comme avant
    categoryFilterComboBox.addItem("Personnel");
    categoryFilterComboBox.addItem("Général");
    categoryFilterComboBox.addItem("Travail");
    // Ajouter l'élément pour ajouter de nouvelles catégories
    categoryFilterComboBox.addItem("Ajouter...");

    // Assurez-vous de retirer l'ancien ActionListener avant d'en ajouter un nouveau pour éviter les actions en double
    for (ActionListener al : categoryFilterComboBox.getActionListeners()) {
      categoryFilterComboBox.removeActionListener(al);
    }

    categoryFilterComboBox.addActionListener(e -> {
      String selected = (String) categoryFilterComboBox.getSelectedItem();
      if ("Ajouter...".equals(selected)) {
        // Affiche une boîte de dialogue pour entrer une nouvelle catégorie
        String newCategory = JOptionPane.showInputDialog(
          TaskManagerUI.this,
          "Entrez le nom de la nouvelle catégorie:"
        );
        if (newCategory != null && !newCategory.trim().isEmpty()) {
          categoryFilterComboBox.insertItemAt(
            newCategory,
            categoryFilterComboBox.getItemCount() - 1
          );
          categoryFilterComboBox.setSelectedItem(newCategory);
          CategoryManager.saveCategory(newCategory); // Sauvegarde la catégorie
        } else {
          // Si aucun nom n'est entré, revenir à la sélection précédente ou à "Toutes"
          categoryFilterComboBox.setSelectedIndex(0);
        }
      } else {
        // Filtre les tâches basées sur la sélection
        filterTasks();
      }
    });
  }

  // Ajout des priorités uniques aux combobox
  private void populatePriorityFilterComboBox() {
    priorityFilterComboBox.removeAllItems();
    priorityFilterComboBox.addItem("Toutes");
    priorityFilterComboBox.addItem("Haute");
    priorityFilterComboBox.addItem("Moyenne");
    priorityFilterComboBox.addItem("Faible");
  }

  // Création et configuration du panneau de boutons
  private JPanel setupButtonPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(editButton);
    return buttonPanel;
  }

  // Configuration des écouteurs pour les actions sur les boutons et la liste
  private void setupButtonListeners() {
    addButton.addActionListener(e -> addTask());
    deleteButton.addActionListener(e -> deleteSelectedTask());
    editButton.addActionListener(e -> editSelectedTask());
    list.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            editSelectedTask();
          }
        }
      }
    );
  }

  // Dans la méthode initializeUI ou loadTasks, après avoir initialisé les composants UI
  private void loadCategories() {
    Set<String> categories = CategoryManager.loadCategories();
    for (String category : categories) {
      categoryFilterComboBox.addItem(category);
    }
  }

  // Chargement et affichage des tâches existantes
  private void loadTasks() {
    allTasks = TaskIO.loadTasks();
    updateTaskListDisplay(allTasks);
    setupFilterComboBoxes();
  }

  // Sauvegarde de l'état actuel des tâches
  private void saveAllTasks() {
    TaskIO.saveAllTasks(new ArrayList<>(Collections.list(model.elements())));
  }

  // Ajout d'une nouvelle tâche via un dialogue
  private void addTask() {
    Set<String> categories = CategoryManager.loadCategories(); // Chargement des catégories
    TaskDialog dialog = new TaskDialog(this, categories); // Passage des catégories au TaskDialog
    Task task = dialog.showDialog();
    if (task != null) {
      allTasks.add(task);
      filterTasks();
      saveAllTasks();
    }
  }

  // Suppression de la tâche sélectionnée
  private void deleteSelectedTask() {
    int selectedIndex = list.getSelectedIndex();
    if (selectedIndex >= 0) {
      allTasks.remove(list.getSelectedValue());
      filterTasks();
      saveAllTasks();
    }
  }

  // Édition de la tâche sélectionnée via un dialogue
  private void editSelectedTask() {
    int selectedIndex = list.getSelectedIndex();
    if (selectedIndex >= 0) {
      Task task = model.getElementAt(selectedIndex);
      Set<String> categories = CategoryManager.loadCategories();
      TaskDialog dialog = new TaskDialog(this, task, categories); // Passer les catégories au dialogue
      Task updatedTask = dialog.showDialog();
      if (updatedTask != null) {
        allTasks.set(selectedIndex, updatedTask);
        filterTasks();
        saveAllTasks();
      }
    }
  }

  // Filtrage des tâches en fonction des sélections de catégorie et priorité
  private void filterTasks() {
    String selectedCategory = (String) categoryFilterComboBox.getSelectedItem();
    String selectedPriority = (String) priorityFilterComboBox.getSelectedItem();

    List<Task> filteredTasks = allTasks
      .stream()
      .filter(task ->
        "Toutes".equals(selectedCategory) ||
        task.getCategory().equals(selectedCategory)
      )
      .filter(task ->
        "Toutes".equals(selectedPriority) ||
        task.getPriority().equals(selectedPriority)
      )
      .collect(Collectors.toList());

    updateTaskListDisplay(filteredTasks);
  }

  // Mise à jour de l'affichage de la liste des tâches
  private void updateTaskListDisplay(List<Task> tasks) {
    model.clear();
    tasks.forEach(model::addElement);
  }

  // Méthode principale pour lancer l'application
  public static void main(String[] args) {
    Locale.setDefault(Locale.forLanguageTag("fr-FR"));
    try {
      UIManager.setLookAndFeel("com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
      SwingUtilities.invokeLater(() -> new TaskManagerUI().setVisible(true));
    } catch (
      ClassNotFoundException
      | InstantiationException
      | IllegalAccessException
      | UnsupportedLookAndFeelException e
    ) {
      e.printStackTrace(System.out);
    }
  }
}
