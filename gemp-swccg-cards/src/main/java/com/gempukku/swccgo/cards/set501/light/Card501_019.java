package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 14
 * Type: Character
 * SubType: Alien
 * Title: Din Djarin
 */
public class Card501_019 extends AbstractAlien {
    public Card501_019() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, "Din Djarin", Uniqueness.UNIQUE);
        setLore("Mandalorian");
        setGameText("Adds 2 to power of anything he pilots. If with an alien, adds one battle destiny. Once per turn, may target opponent's non-'hit' character here. If target lost this turn, choose: retrieve 1 Force or place target in Used Pile to retrieve 2 Force.");
        setArmor(5);
        setSpecies(Species.MANDALORIAN);
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_14);
        addIcon(Icon.WARRIOR, 2);
        setTestingText("Din Djarin");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.alien), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        return super.getGameTextRequiredAfterTriggers(game, effectResult, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter opponentsNonHitCharacterHere = Filters.and(Filters.opponents(playerId), Filters.character, Filters.here(self), Filters.not(Filters.hit));
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, opponentsNonHitCharacterHere)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target character");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target character", opponentsNonHitCharacterHere) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult) {
                                                                    List<TriggerAction> actions = new LinkedList<>();
                                                                    if (TriggerConditions.justLost(game, effectResult, targetedCard)) {
                                                                        final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
                                                                        final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                                        action2.appendEffect(
                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                        new YesNoDecision("Place " + GameUtils.getCardLink(justLostCard) + " on Used Pile to retrieve 2 force (otherwise retrieve 1 force)?") {
                                                                                            @Override
                                                                                            protected void yes() {
                                                                                                action2.appendEffect(
                                                                                                        new RetrieveForceEffect(action, playerId, 2)
                                                                                                );
                                                                                                action2.appendEffect(
                                                                                                        new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true)
                                                                                                );
                                                                                            }

                                                                                            @Override
                                                                                            protected void no() {
                                                                                                action2.appendEffect(
                                                                                                        new RetrieveForceEffect(action, playerId, 1)
                                                                                                );
                                                                                            }
                                                                                        }
                                                                                ));
                                                                        actions.add(action2);

                                                                    }
                                                                    return actions;
                                                                }
                                                            })
                                            );
                                        }
                                    }
                            );

                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
