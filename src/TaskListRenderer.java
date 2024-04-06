import java.awt.*;
import javax.swing.*;

// Classe pour personnaliser l'affichage des éléments de la liste des tâches
public class TaskListRenderer extends DefaultListCellRenderer {

  // Couleurs pour les différentes priorités de tâches
  private static final Color HIGH_PRIORITY = Color.RED;
  private static final Color MEDIUM_PRIORITY = Color.ORANGE;
  private static final Color LOW_PRIORITY = Color.GREEN;

  // Couleurs ajustées pour les tâches sélectionnées, afin de les distinguer
  private static final Color HIGH_PRIORITY_SELECTED = new Color(
    Math.max(HIGH_PRIORITY.getRed() - 30, 0),
    HIGH_PRIORITY.getGreen(),
    HIGH_PRIORITY.getBlue()
  );
  private static final Color MEDIUM_PRIORITY_SELECTED = new Color(
    MEDIUM_PRIORITY.getRed(),
    Math.max(MEDIUM_PRIORITY.getGreen() - 30, 0),
    MEDIUM_PRIORITY.getBlue()
  );
  private static final Color LOW_PRIORITY_SELECTED = new Color(
    Math.max(LOW_PRIORITY.getRed() - 15, 0),
    Math.max(LOW_PRIORITY.getGreen() - 15, 0),
    Math.max(LOW_PRIORITY.getBlue() - 15, 0)
  );

  @Override
  public Component getListCellRendererComponent(
    JList<?> list,
    Object value,
    int index,
    boolean isSelected,
    boolean cellHasFocus
  ) {
    super.getListCellRendererComponent(
      list,
      value,
      index,
      isSelected,
      cellHasFocus
    );

    // Si l'élément est une instance de Task, ajustez le texte et la couleur en fonction de la priorité
    if (value instanceof Task task) {
      setText(task.toString());
      setForeground(getPriorityColor(task.getPriority(), isSelected));
      setFont(getFont().deriveFont(Font.BOLD));
    }
    return this; // Retourner le composant configuré
  }

  // Méthode auxiliaire pour déterminer la couleur du texte en fonction de la priorité de la tâche
  private Color getPriorityColor(String priority, boolean isSelected) {
    return switch (priority) {
      case "Haute" -> isSelected ? HIGH_PRIORITY_SELECTED : HIGH_PRIORITY; // Haute priorité
      case "Moyenne" -> isSelected ? MEDIUM_PRIORITY_SELECTED : MEDIUM_PRIORITY; // Moyenne priorité
      case "Faible" -> isSelected ? LOW_PRIORITY_SELECTED : LOW_PRIORITY; // Faible priorité
      default -> getForeground();
    };
  }
}
