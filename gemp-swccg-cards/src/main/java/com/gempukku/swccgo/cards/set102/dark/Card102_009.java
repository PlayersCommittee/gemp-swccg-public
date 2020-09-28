package com.gempukku.swccgo.cards.set102.dark;

import com.gempukku.swccgo.cards.AbstractSwccgCardBlueprint;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;

/**
 * Set: Premium (Jedi Pack)
 * Type: Non-Playable
 * Title: Hyperoute Navigation Chart
 */
public class Card102_009 extends AbstractSwccgCardBlueprint {
    public Card102_009() {
        super(Side.DARK, null, "Hyperoute Navigation Chart", null);
        setCardCategory(CardCategory.GAME_AID);
        setGameText("[0] Coruscant [1] Carida, Corellia, Eriadu, Kirdo III, Kuat [2] Alderaan, Bothawui, Kiffex, Rendili, Tibrin, Wakeelmui [3] Malastare, Nal Hutta, Raithal, Ralltiir [4] Aquaris, Chandrila, Corulag, Yavin 4* [5] Anoat, Dantooine, Hoth*, Naboo [6] Bespin, Fondor, Kashyyyk, Mon Calamari [7] Clak'dor VII, Ord Mantell, Roche, Sullust, Tatooine [8] Endor, Kessel [9] Dagobah, Gall * Known Rebel Base");
        addIcon(Icon.PREMIUM);
    }
}
