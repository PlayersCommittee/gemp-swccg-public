package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachDuelDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Subtype: Immediate
 * Title: What Was It?
 */
public class Card11_023 extends AbstractImmediateEffect {
    public Card11_023() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "What Was It?", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("When Qui-Gon got away, Maul knew it would only be a matter of time until the two of them would meet again.");
        setGameText("If opponent just initiated a duel against one of your non-captive Jedi, deploy on that Jedi. Cancel the duel and relocate this Jedi to an adjacent site. Your duel destiny draws are each +1. (Immune to Control.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.duelInitiatedAgainst(game, effectResult, opponent, Filters.and(Filters.your(self), Filters.non_captive, Filters.Jedi, Filters.at(Filters.adjacentSiteTo(self, Filters.site))))) {
            PhysicalCard jedi = game.getGameState().getDuelState().getCharacter(playerId);

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.sameCardId(jedi), null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.Jedi;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final PhysicalCard jedi = self.getAttachedTo();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Cancel duel");
            // Perform result(s)
            action.appendEffect(
                    new CancelDuelEffect(action));
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            Filter siteToRelocateJedi = Filters.and(Filters.adjacentSite(self), Filters.locationCanBeRelocatedTo(jedi, 0));
                            if (GameConditions.canSpotLocation(game, siteToRelocateJedi)) {

                                final SubAction subAction = new SubAction(action);
                                subAction.appendTargeting(
                                        new ChooseCardOnTableEffect(action, playerId, "Choose site to relocate " + GameUtils.getCardLink(jedi) + " to", siteToRelocateJedi) {
                                            @Override
                                            protected void cardSelected(final PhysicalCard siteSelected) {
                                                subAction.addAnimationGroup(siteSelected);
                                                // Pay cost(s)
                                                subAction.appendCost(
                                                        new PayRelocateBetweenLocationsCostEffect(subAction, playerId, jedi, siteSelected, 0));
                                                // Allow response(s)
                                                subAction.allowResponses("Relocate " + GameUtils.getCardLink(jedi) + " to " + GameUtils.getCardLink(siteSelected),
                                                        new UnrespondableEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Perform result(s)
                                                                subAction.appendEffect(
                                                                        new RelocateBetweenLocationsEffect(subAction, jedi, siteSelected));
                                                            }
                                                        });
                                            }
                                        });
                                action.appendEffect(
                                        new StackActionEffect(action, subAction));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachDuelDestinyModifier(self, 1, playerId));
        return modifiers;
    }
}