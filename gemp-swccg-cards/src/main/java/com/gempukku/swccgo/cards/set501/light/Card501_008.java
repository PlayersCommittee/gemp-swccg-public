package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInLostPileEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * Subtype: Resistance
 * Title: Jannah
 */
public class Card501_008 extends AbstractResistance {
    public Card501_008() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Jannah", Uniqueness.UNIQUE);
        setLore("Female stormtrooper.");
        setGameText("Other Resistance characters of destiny = 2 (and Finn) are power +1 at same and adjacent sites. During battle, if opponent has more than four characters here, may exclude one of opponent's characters of ability < 4 from battle.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_14, Icon.EPISODE_VII);
        addKeywords(Keyword.FEMALE, Keyword.STORMTROOPER);
        setTestingText("Jannah");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.atSameOrAdjacentSite(self), Filters.or(Filters.Finn, Filters.and(Filters.other(self), Filters.Resistance_character, Filters.destinyEqualTo(2)))), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.opponents(self), Filters.character, Filters.here(self));
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, 5, filter)
                && GameConditions.canSpot(game, self, Filters.and(filter, Filters.abilityLessThan(4), Filters.not(Filters.mayNotBeExcludedFromBattle)))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Exclude character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Exclude character", targetingReason, Filters.and(filter, Filters.abilityLessThan(4), Filters.not(Filters.mayNotBeExcludedFromBattle))) {
                                       @Override
                                       protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                           action.allowResponses(new RespondableEffect(action) {
                                               @Override
                                               protected void performActionResults(Action targetingAction) {
                                                   PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                   if (finalTarget != null) {
                                                       action.appendEffect(new ExcludeFromBattleEffect(action, finalTarget));
                                                   }
                                               }
                                           });
                                       }
                                   }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
