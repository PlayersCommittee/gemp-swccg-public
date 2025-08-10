package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AttachCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.BlowAwayEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Epic Event
 * Title: Tracked Fleet
 */
public class Card225_034 extends AbstractEpicEventDeployable {
    public Card225_034() {
        super(Side.DARK, PlayCardZoneOption.ATTACHED, Title.Tracked_Fleet, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("Deploy on D'Qar system (only at start of game). Tied To The End Of A String: You may not deploy starships here. Supremacy moves to here for free. There Will Be No Surrender: Three times per game, at start of opponent's move phase, opponent may stack a card from hand face down on this card to relocate it to an [Episode VII] system within 3 parsecs. Fire At Will!: At the start of your turn, if you control this system, Tracked Fleet is 'annihilated' (placed out of play).");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Dqar_system;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.your(self),Filters.starship), Filters.sameLocation(self)));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.Supremacy, Filters.sameLocation(self)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
            && GameConditions.controls(game, playerId, Filters.here(self))) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Annihilate Tracked Fleet");
                action.appendEffect(
                    new BlowAwayEffect(action, self){
                        @Override
                        protected StandardEffect getAdditionalGameTextEffect(SwccgGame game, Action blowAwaySubAction){
                            return new PlaceCardOutOfPlayFromTableEffect(blowAwaySubAction, self);
                        }
                    });
                return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter systemToRelocateTo = Filters.and(Icon.EPISODE_VII, Filters.system, Filters.withinParsecsOf(self, 3), Filters.not(Filters.here(self)));

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, effectResult, Phase.MOVE, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.canSpotLocation(game, systemToRelocateTo)
                && !(GameConditions.hasStackedCards(game, self, 3))
                && GameConditions.hasHand(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Relocate Tracked Fleet");
                // Choose target(s)
                action.appendUsage(new OncePerPhaseEffect(action));
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose system to relocate " + GameUtils.getCardLink(self) + " to", systemToRelocateTo) {
                            @Override
                            protected void cardSelected(final PhysicalCard systemSelected) {
                                action.addAnimationGroup(self);
                                action.addAnimationGroup(systemSelected);
                                // Pay cost(s)
                                action.appendCost(
                                        new StackCardFromHandEffect(action, playerId, self, true));
                                // Allow response(s)
                                action.allowResponses("Relocate " + GameUtils.getCardLink(self) + " to " + GameUtils.getCardLink(systemSelected),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action action) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new AttachCardFromTableEffect(action, self, systemSelected));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
        }
        return null;
    }
}