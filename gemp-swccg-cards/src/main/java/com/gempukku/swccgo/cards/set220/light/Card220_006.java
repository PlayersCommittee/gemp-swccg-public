package com.gempukku.swccgo.cards.set220.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 20
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Beskar Deflection
 */
public class Card220_006 extends AbstractUsedOrLostInterrupt {
    public Card220_006() {
        super(Side.LIGHT, 4, "Beskar Deflection", Uniqueness.UNRESTRICTED, ExpansionSet.SET_20, Rarity.V);
        setLore("");
        setGameText("USED: Target your Mandalorian that was just 'hit' by a character weapon. Opponent chooses: Restore target to normal or the character that fired the weapon is also 'hit.' " +
                "LOST: Once per game, if your Mandalorian is in battle, add 2 to a just drawn destiny.");
        addIcons(Icon.VIRTUAL_SET_20);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.your(self), Filters.Mandalorian), Filters.character_weapon)) {
            final PhysicalCard characterThatFiredWeapon = ((HitResult)effectResult).getCardFiringWeapon();
            final PhysicalCard hitMandalorian = ((HitResult)effectResult).getCardHit();

            TargetingReason targetingReason = TargetingReason.TO_BE_HIT;

            if(characterThatFiredWeapon != null
                && Filters.and(Filters.character, Filters.canBeTargetedBy(self, targetingReason)).accepts(game, characterThatFiredWeapon)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
                action.setText("Have opponent choose");

                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                final String characterHitString = "Make " + GameUtils.getFullName(characterThatFiredWeapon) + " hit";
                                final String restoreMandalorianString = "Restore " + GameUtils.getFullName(hitMandalorian) + " to normal";

                                // Perform result(s)
                                action.appendEffect(new PlayoutDecisionEffect(action, opponent,
                                        new MultipleChoiceAwaitingDecision("Make your character hit or restore Mandalorian to normal?", new String[]{characterHitString, restoreMandalorianString}) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (characterHitString.equals(result)) {
                                            action.appendEffect(
                                                    new HitCardEffect(action, characterThatFiredWeapon, self));
                                        } else if (restoreMandalorianString.equals(result)) {
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, hitMandalorian));
                                        }
                                    }
                                }));
                            }
                        }
                );
                actions.add(action);

            }
        }


        gameTextActionId = GameTextActionId.MIGHT_OF_THE_MANDALORIANS__ADD_TO_DESTINY;
        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Mandalorian))
                && TriggerConditions.isDestinyJustDrawn(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Add 2 to destiny");

            action.appendUsage(
                    new OncePerGameEffect(action));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyDestinyEffect(action, 2));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
