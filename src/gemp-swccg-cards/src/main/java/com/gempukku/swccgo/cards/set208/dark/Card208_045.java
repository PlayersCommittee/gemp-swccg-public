package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ModifyForfeitUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Sonic Bombardment (V)
 */
public class Card208_045 extends AbstractUsedOrLostInterrupt {
    public Card208_045() {
        super(Side.DARK, 4, Title.Sonic_Bombardment, Uniqueness.UNIQUE, ExpansionSet.SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Oouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioouioo-");
        setGameText("USED: [Upload] Downtown Plaza or a prison. LOST: [Upload] one unique (•) bounty. OR Target an alien. For remainder of turn, target is power and forfeit -1 (-3 if Chewie).");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SONIC_BOMBARDMENT__UPLOAD_DOWNTOWN_PLAZA_OR_PRISON;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take Downtown Plaza or a prison into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Downtown_Plaza, Filters.prison), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.SONIC_BOMBARDMENT__UPLOAD_UNIQUE_BOUNTY;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Take a unique bounty into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a unique (•) bounty into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.any_bounty), true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.alien)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Target an alien");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose alien", Filters.alien) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedAlien) {
                            action.addAnimationGroup(targetedAlien);
                            int amount = Filters.Chewie.accepts(game, targetedAlien) ? -3 : -1;
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedAlien) + " power and forfeit " + amount,
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            int finalAmount = Filters.Chewie.accepts(game, finalTarget) ? -3 : -1;
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, finalAmount));
                                            action.appendEffect(
                                                    new ModifyForfeitUntilEndOfTurnEffect(action, finalTarget, finalAmount));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}