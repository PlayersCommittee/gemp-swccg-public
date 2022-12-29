package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderForcePileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Shasa Tiel
 */
public class Card6_039 extends AbstractAlien {
    public Card6_039() {
        super(Side.LIGHT, 3, 3, 1, 1, 3, "Shasa Tiel", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setArmor(3);
        setLore("Ishi Tib accountant. One of Mosep's assistants. Formerly worked for SoroSuub Inc. Blackmailed into working for Jabba. Hates her job. Searching for a means of escape.");
        setGameText("Power +1 at any swamp. During opponent's control phase, may use 3 Force to examine the cards in opponent's Force Pile, reorder however you wish and replace. While at Audience Chamber, all your other Ishi Tibs are power and forfeit +2.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.ACCOUNTANT, Keyword.FEMALE);
        setSpecies(Species.ISHI_TIB);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherIshiTibs = Filters.and(Filters.your(self), Filters.other(self), Filters.Ishi_Tib);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.swamp), 1));
        modifiers.add(new PowerModifier(self, yourOtherIshiTibs, atAudienceChamber, 2));
        modifiers.add(new ForfeitModifier(self, yourOtherIshiTibs, atAudienceChamber, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.hasForcePile(game, opponent)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Examine and reorder opponent's Force Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new PeekAtAndReorderForcePileEffect(action, opponent));
            return Collections.singletonList(action);
        }
        return null;
    }
}
