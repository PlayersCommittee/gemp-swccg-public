package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleWithCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
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
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Lyn Me
 */
public class Card7_188 extends AbstractAlien {
    public Card7_188() {
        super(Side.DARK, 3, 2, 1, 1, 2, "Lyn Me", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Twi'lek musician whose village hired Boba Fett for protection from slavers. Fell in love with the famous mercenary. Vowed to kill Luke and Han.");
        setGameText("Subtracts 1 from deploy cost of Bounty hunters at same site (Boba Fett deploy free). During battle, subtracts X from opponent's total power, where X = number of your musicians present (+2 if battling Luke or Han, or +4 if both).");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
        setSpecies(Species.TWILEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.bounty_hunter, Filters.not(Filters.Boba_Fett)), -1, Filters.sameSite(self)));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Boba_Fett, Filters.sameSite(self)));
        modifiers.add(new TotalPowerModifier(self, Filters.sameLocation(self), new InBattleCondition(self),
                new NegativeEvaluator(
                        new AddEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.musician)),
                                new ConditionEvaluator(0, 2, new InBattleWithCondition(self, Filters.Luke)),
                                new ConditionEvaluator(0, 2, new InBattleWithCondition(self, Filters.Han)))),
                game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
