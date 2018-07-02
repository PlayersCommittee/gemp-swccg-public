package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Weapon Levitation
 */
public class Card5_160 extends AbstractUsedOrLostInterrupt {
    public Card5_160() {
        super(Side.DARK, 4, "Weapon Levitation", Uniqueness.UNIQUE);
        setLore("Vader confiscated Han's blaster, his ship, his Wookiee, his girl and his only hope of escape.");
        setGameText("USED: Search your Used Pile, take one weapon into hand and reshuffle. LOST: If a battle was just initiated, one of your characters of ability > 3 present may 'steal' one character weapon present.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
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