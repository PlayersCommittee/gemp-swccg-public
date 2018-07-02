package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleAtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Nikto
 */
public class Card6_115 extends AbstractAlien {
    public Card6_115() {
        super(Side.DARK, 3, 3, 2, 1, 3, "Nikto");
        setLore("Several different types if Nikto were sold to Jabba by slavers. Compete with Weequay to prove themselves the stronger species.");
        setGameText("Deploys only to a Jabba's Palace site. Power and forfeit +2 when defending a battle at a Jabba's Palace site.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_to_Jabbas_Palace_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition defendingBattleAtJabbasPalaceSite = new DefendingBattleAtCondition(self, Filters.Jabbas_Palace_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, defendingBattleAtJabbasPalaceSite, 2));
        modifiers.add(new ForfeitModifier(self, defendingBattleAtJabbasPalaceSite, 2));
        return modifiers;
    }
}
