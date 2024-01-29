package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Weapon Levitation
 */
public class Card6_079 extends AbstractUsedOrLostInterrupt {
    public Card6_079() {
        super(Side.LIGHT, 4, Title.Weapon_Levitation, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.U);
        setLore("A Jedi is taught to use the anger of his opponents (and their weapons) against them.");
        setGameText("USED: Search your Used Pile, take one weapon into hand and reshuffle. LOST: Cancel You Are Beaten. OR If a battle was just initiated, one of your characters of ability > 3 present may steal one character weapon present.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.WEAPON_LEVITATION__UPLOAD_WEAPON_FROM_USED_PILE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take weapon into hand from Used Pile");
            // Allow response(s)
            action.allowResponses("Take a weapon into hand from Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, Filters.weapon, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.You_Are_Beaten)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.You_Are_Beaten, Title.You_Are_Beaten);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.You_Are_Beaten;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN;

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)) {
            final Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3), Filters.presentInBattle);
            final Filter weaponFilter = Filters.and(Filters.opponents(self), Filters.character_weapon, Filters.presentAt(Filters.battleLocation), Filters.canBeStolenBy(self, characterFilter));
            if (GameConditions.canTarget(game, self, targetingReason, weaponFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("'Steal' character weapon");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose weapon to 'steal'", targetingReason, weaponFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard weapon) {
                                final Filter characterToStealWeaponFilter = Filters.and(characterFilter, Filters.canStealAndCarry(weapon));
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character to 'steal' weapon", characterToStealWeaponFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard character) {
                                                // Allow response(s)
                                                action.allowResponses("Have " + GameUtils.getCardLink(character) + " 'steal' " + GameUtils.getCardLink(weapon),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalWeapon = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new StealCardAndAttachFromTableEffect(action, finalWeapon, finalCharacter));
                                                            }
                                                        }
                                                );
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}