package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Brie Cunngemi
 */
public class Card304_050 extends AbstractAlien {
    public Card304_050() {
        super(Side.LIGHT, 3, 3, 2, 2, 4, "Brie Cunngemi", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Brie has had a hard life growing up mostly on the streets. If not for her looks she'd have been overlooked by the Tiure clan. She now manages the oldest known profession within the Tiure clan.");
        setGameText("Subtracts 1 from deploy cost of Gangsters at same site (Candon Coburn deploy free). During battle, subtracts X from opponent's total power, where X = number of your musicians present (+2 if battling Kamjin or Thran, or +4 if both).");
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN, Keyword.CLAN_TIURE);
		addIcon(Icon.WARRIOR);
        addPersona(Persona.BRIE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.gangster, Filters.not(Filters.Candon)), -1, Filters.sameSite(self)));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Candon, Filters.sameSite(self)));
        modifiers.add(new TotalPowerModifier(self, Filters.sameLocation(self), new InBattleCondition(self),
                new NegativeEvaluator(
                        new AddEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.musician)),
                                new ConditionEvaluator(0, 2, new InBattleWithCondition(self, Filters.Kamjin)),
                                new ConditionEvaluator(0, 2, new InBattleWithCondition(self, Filters.Thran)))),
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
