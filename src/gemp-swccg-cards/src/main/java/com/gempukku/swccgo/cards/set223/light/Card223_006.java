package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilStartOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeUsedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCloakModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Interrupt
 * Subtype: Used
 * Title: Transmission Terminated (V)
 */
public class Card223_006 extends AbstractUsedOrLostInterrupt {
    public Card223_006() {
        super(Side.LIGHT, 5, Title.Transmission_Terminated, Uniqueness.UNRESTRICTED, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setLore("After the mission, the Death Squadron HoloNet communications system reported fifteen system errors: ten computer malfunctions, four power failures and one asteroid.");
        setGameText("USED: Target a starship. For remainder of turn, target may not use tractor beams, fire weapons, or 'cloak.'" +
                " LOST: Cancel a hologram. OR Cancel the game text of Emperor's Power or an Admiral's Order until start of your turn.");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_23);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        if (GameConditions.canTarget(game, self, Filters.starship)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Affect starship");

            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a starship", Filters.starship) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(null,
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    PhysicalCard finalCard = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action,
                                                    new MayNotFireWeaponsModifier(self, finalCard),
                                                    "Prevents " + GameUtils.getCardLink(finalCard) + " from firing weapons"));
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action,
                                                    new MayNotCloakModifier(self, finalCard),
                                                    "Prevents " + GameUtils.getCardLink(finalCard) + " from 'cloaking'"));
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action,
                                                    new MayNotBeUsedModifier(self, Filters.and(Filters.tractor_beam, Filters.attachedTo(finalCard))),
                                                    "Prevents " + GameUtils.getCardLink(finalCard) + " from using tractor beams"));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.hologram)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.hologram, "hologram");
            actions.add(action);
        }

        if (GameConditions.canTarget(game, self, Filters.or(Filters.Admirals_Order, Filters.Emperors_Power))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel game text of a card");

            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target card to cancel game text", Filters.or(Filters.Admirals_Order, Filters.Emperors_Power)) {
                @Override
                protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                    // Allow response(s)
                    action.allowResponses("Cancel game text of " + GameUtils.getCardLink(targetedCard),
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(
                                            new CancelGameTextUntilStartOfTurnEffect(action, finalTarget, playerId));
                                }
                            }
                    );
                }
            });
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.hologram)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
