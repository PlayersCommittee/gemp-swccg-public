package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: It's Not My Fault!
 */
public class Card7_093 extends AbstractUsedInterrupt {
    public Card7_093() {
        super(Side.LIGHT, 5, "It's Not My Fault!", Uniqueness.UNIQUE);
        setLore("'It's not fair! The transfer circuits are working!'");
        setGameText("If opponent just initiated a battle at a site, use X Force to make your character present immune to attrition for remainder of battle, where X= that character's ability (free if Han or Lando).");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.site)) {
            int forceAvailable = GameConditions.forceAvailableToUseToPlayInterrupt(game, playerId, self);
            Collection<PhysicalCard> characters = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.character, Filters.presentInBattle, Filters.or(Filters.Han, Filters.Lando, Filters.abilityEqualTo(forceAvailable), Filters.abilityLessThan(forceAvailable))));
            if (!characters.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make character immune to attrition");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", Filters.in(characters)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard character) {
                                action.addAnimationGroup(character);
                                if (!Filters.or(Filters.Han, Filters.Lando).accepts(game, character)) {
                                    float ability = game.getModifiersQuerying().getAbility(game.getGameState(), character);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new UseForceEffect(action, playerId, ability));
                                }
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(character) + " immune to attrition",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AddUntilEndOfBattleModifierEffect(action,
                                                                new ImmuneToAttritionModifier(self, finalTarget),
                                                                "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition"));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}