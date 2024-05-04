package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Sergeant Tarl
 */
public class Card8_114 extends AbstractImperial {
    public Card8_114() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Sergeant Tarl", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Took part in the capture of the Rebel Blockade Runner Tantive IV. Stormtrooper trained on Corulag. Corellia native.");
        setGameText("While Tarl is the trooper targeted by Spice Mines of Kessel, that Utinni Effect is immune to Alter and Tarl adds 4 to Force retrieved when completed. Subtracts 3 from Force opponent retrieves with Noble Sacrifice.");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.STORMTROOPER);
        setSpecies(Species.CORELLIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter spiceMinesOfKessel = Filters.and(Filters.Spice_Mines_Of_Kessel, Filters.cardTargeting(self, self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, spiceMinesOfKessel, Title.Alter));
        modifiers.add(new ModifyGameTextModifier(self, spiceMinesOfKessel, ModifyGameTextType.SPICE_MINES_OF_KESSEL__ADD_4_TO_FORCE_RETRIEVED));
        modifiers.add(new ForceRetrievalModifier(self, new CardMatchesEvaluator(0, -3, Filters.Noble_Sacrifice), opponent));
        return modifiers;
    }
}
