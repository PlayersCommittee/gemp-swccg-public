package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.LightsaberCombatTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Subtype: Immediate
 * Title: Qui-Gon's End
 */
public class Card13_083 extends AbstractImmediateEffect {
    public Card13_083() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Qui-Gon's End", Uniqueness.UNIQUE);
        setLore("While Qui-Gon spent years maintaining peace, Maul was sharpening his combat skills. Those years of training came to fruition in one fateful moment.");
        setGameText("Deploy on a site where Maul just defeated Qui-Gon in lightsaber combat. Place Qui-Gon out of play. While Obi-Wan at this site, his lightsaber combat total is +2 and opponent loses 1 Force at the end of each of your turns. (Immune to Control.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.wonLightsaberCombatAgainst(game, effectResult, Filters.Maul, Filters.and(Filters.QuiGon, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_PLACED_OUT_OF_PLAY)))) {
            PhysicalCard location = game.getGameState().getLightsaberCombatLocation();
            if (location != null && Filters.site.accepts(game, location)) {

                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameLocationId(location), null);
                if (action != null) {
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Place Qui-Gon out of play");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Qui-Gon", TargetingReason.TO_BE_PLACED_OUT_OF_PLAY, Filters.QuiGon) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " out of play",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardOutOfPlayFromTableEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.ObiWan, Filters.atSameSite(self)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LightsaberCombatTotalModifier(self, Filters.and(Filters.ObiWan, Filters.atSameSite(self)), 2));
        return modifiers;
    }
}