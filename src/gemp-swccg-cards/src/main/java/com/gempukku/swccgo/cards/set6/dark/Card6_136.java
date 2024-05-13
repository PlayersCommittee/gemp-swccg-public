package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Wooof
 */
public class Card6_136 extends AbstractAlien {
    public Card6_136() {
        super(Side.DARK, 3, 3, 2, 2, 2, "Wooof", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Kadas'sa'Nikto. One of Jabba's best pilots. Often pilots Jabba's space yacht. Smuggler. Prefers to be flying combat starfighters.");
        setGameText("Deploys only at Tatooine. Power +2 when defending a battle at a Jabba's Palace site. Adds 2 to power of anything he pilots (3 if piloting an [Independent Starship] starship or if Jabba is aboard).");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.NIKTO);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_at_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleAtCondition(self, Filters.Jabbas_Palace_site), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new ConditionEvaluator(2, 3,
                new OrCondition(new PilotingCondition(self, Filters.and(Icon.INDEPENDENT, Filters.starship)),
                                new PilotingCondition(self, Filters.hasAboard(self, Filters.Jabba))))));
        return modifiers;
    }
}
