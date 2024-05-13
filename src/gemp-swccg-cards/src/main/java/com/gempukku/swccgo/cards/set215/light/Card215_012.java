package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Resistance
 * Title: Jannah
 */
public class Card215_012 extends AbstractResistance {
    public Card215_012() {
        super(Side.LIGHT, 2, 3, 3, 3, 5, "Jannah", Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setLore("Female stormtrooper.");
        setGameText("Other Resistance characters of destiny = 2 (and Finn) are power +1 at same and adjacent sites. During battle, if opponent has more than four characters here, may exclude one opponent's character of ability < 4 from battle.");
        addPersona(Persona.JANNAH);
        addIcons(Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_15);
        addKeywords(Keyword.FEMALE, Keyword.STORMTROOPER);
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
