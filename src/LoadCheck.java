import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
public class LoadCheck {
  public static void main(String[] args) {
    SwccgCardBlueprintLibrary lib = new SwccgCardBlueprintLibrary();
    for (String id : new String[]{"5_53","5_053","5_5","1_11","1_63","1_54","1_168","1_312","5_99","5_168","5_137"}) {
      var bp = lib.getSwccgoCardBlueprint(id);
      System.out.println(id + " -> " + (bp==null?"NULL":bp.getTitle()));
    }
  }
}
