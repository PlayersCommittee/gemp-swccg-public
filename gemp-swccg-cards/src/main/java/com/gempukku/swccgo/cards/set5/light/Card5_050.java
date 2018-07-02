package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Higher Ground
 */
public class Card5_050 extends AbstractUsedOrLostInterrupt {
    public Card5_050() {
        super(Side.LIGHT, 2, "Higher Ground", Uniqueness.UNIQUE);
        setLore("Using his position to his advantage, Luke managed to hold off Vader's onslaught, if only for a few seconds.");
        setGameText("USED: During a battle at a site, instead of firing one character weapon, cause one opponent's character present to be power -4 until end of turn. LOST: During a battle at a site, use 3 Force to cancel one battle destiny just drawn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter characterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canTarget(game, self, characterFilter)) {
            Collection<PhysicalCard> weapons = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.character_weapon_or_character_with_permanent_character_weapon, Filters.canBeFiredForFree(self, 0)));
            if (!weapons.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("USED: Make a character power -4");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character weapon", Filters.in(weapons)) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " power -4, instead of firing " + GameUtils.getCardLink(selectedCard) + ",",
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new InsteadOfFiringWeaponEffect(action, selectedCard,
                                                                                new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, -4)));
                                                            }
                                                        }
                                                );
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 3)
                && GameConditions.canCancelDestiny(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}