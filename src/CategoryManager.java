import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class CategoryManager {

  // Chemin vers le fichier des catégories.
  private static final String CATEGORY_FILE = "resources/categories.txt";
  private static final Path CATEGORY_PATH = Paths.get(CATEGORY_FILE);

  // Sauvegarde une catégorie dans le fichier.
  public static void saveCategory(String category) {
    try {
      // Écrit la catégorie dans le fichier. Crée le fichier s'il n'existe pas, et ajoute à la fin s'il existe.
      Files.write(
        CATEGORY_PATH,
        (category + System.lineSeparator()).getBytes(),
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      );
    } catch (IOException e) {
      // Affiche les détails de l'erreur si l'écriture échoue.
      e.printStackTrace();
    }
  }

  // Charge les catégories depuis le fichier et les retourne en tant que Set.
  public static Set<String> loadCategories() {
    try {
      // Lit toutes les lignes du fichier, chaque ligne représentant une catégorie, et les stocke dans un Set pour éliminer les doublons.
      Set<String> categories = new HashSet<>(Files.readAllLines(CATEGORY_PATH));
      return categories;
    } catch (IOException e) {
      // Affiche les détails de l'erreur si la lecture échoue et retourne un Set vide.
      e.printStackTrace();
      return new HashSet<>();
    }
  }
}
