package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToDrivenBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Weequay Skiff Master
 */
public class Card6_133 extends AbstractAlien {
    public Card6_133() {
        super(Side.DARK, 2, 4, 2, 1, 2, "Weequay Skiff Master", Uniqueness.RESTRICTED_3, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Many of the Weequay at Jabba's palace work for Barada. Enjoy racing their skiffs. Dislike the Nikto guards at Jabba's palace.");
        setGameText("Deploys only on Tatooine. Power +2 When defending a battle or when with another Weequay in battle. Adds 3 to power of any Skiff he drives.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.WEEQUAY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OrCondition(new DefendingBattleCondition(self),
                new InBattleWithCondition(self, Filters.and(Filters.other(self), Filters.Weequay))), 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 3, Filters.skiff));
        return modifiers;
    }
}
