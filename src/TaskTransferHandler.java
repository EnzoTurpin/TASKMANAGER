import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

// Classe gérant le transfert de données pour le glisser-déposer des tâches dans la liste
public class TaskTransferHandler extends TransferHandler {

  private final TaskChangeListener listener; // Écouteur pour les changements dans la liste des tâches

  public TaskTransferHandler(TaskChangeListener listener) {
    this.listener = listener;
  }

  @Override
  protected Transferable createTransferable(JComponent c) {
    JList<?> sourceList = (JList<?>) c;
    Object selectedValue = sourceList.getSelectedValue();
    if (selectedValue instanceof Task task) {
      return new TransferableTask(task);
    }
    return null;
  }

  @Override
  public int getSourceActions(JComponent c) {
    return MOVE; // Indique que les objets peuvent être déplacés
  }

  @Override
  public boolean canImport(TransferSupport support) {
    // Vérifie si les données sont du bon type pour être importées
    return support.isDataFlavorSupported(TransferableTask.TASK_FLAVOR);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }

    try {
      JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
      int index = dl.getIndex();
      JList<?> component = (JList<?>) support.getComponent();
      if (component.getModel() instanceof DefaultListModel<?>) {
        @SuppressWarnings("unchecked")
        DefaultListModel<Task> listModel = (DefaultListModel<Task>) component.getModel();
        Task task = (Task) support
          .getTransferable()
          .getTransferData(TransferableTask.TASK_FLAVOR);

        // Réordonne la tâche dans la liste à la nouvelle position
        if (listModel.contains(task)) {
          int currentIndex = listModel.indexOf(task);
          listModel.remove(currentIndex);
          if (currentIndex < index) {
            index--; // Ajuste l'index si la tâche est déplacée vers le haut dans la liste
          }
        }
        listModel.add(index, task);
        component.setSelectedIndex(index);

        // Notifie le changement de liste
        if (listener != null) {
          listener.onTaskListChanged();
        }
        return true;
      }
    } catch (UnsupportedFlavorException | IOException ex) {
      Logger
        .getLogger(TaskTransferHandler.class.getName())
        .log(Level.SEVERE, null, ex);
    }
    return false;
  }

  // Interface pour notifier les changements dans la liste des tâches
  public interface TaskChangeListener {
    void onTaskListChanged();
  }

  // Classe interne pour encapsuler une tâche comme un objet transférable
  private record TransferableTask(Task task) implements Transferable {
    public static final DataFlavor TASK_FLAVOR = new DataFlavor(
      Task.class,
      "Task"
    );

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] { TASK_FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return TASK_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return task;
    }
  }
}
