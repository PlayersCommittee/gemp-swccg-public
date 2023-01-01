package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ForRemainderOfGameDataEqualsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.ImmuneToUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: I Thought They Smelled Bad On The Outside
 */
public class Card3_043 extends AbstractUsedInterrupt {
    public Card3_043() {
        super(Side.LIGHT, 7, "I Thought They Smelled Bad On The Outside", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R2);
        setLore("'Hang on, kid. This may smell bad, kid...but it'll keep you warm...until I can get the shelter built...'");
        setGameText("Sacrifice (lose) your creature vehicle to protect one character present from Exposure, Ice Storm, Frostbite and Gravel Storm for remainder of turn. (Two characters may be protected if sacrificing a ronto.) OR Double Tzizvvt's power until he moves.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter filter = Filters.and(Filters.your(self), Filters.creature_vehicle, Filters.at(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.character))));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Sacrifice creature vehicle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose creature vehicle", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard creatureVehicle) {
                            int maxCharactersToProtect = Filters.Ronto.accepts(game, creatureVehicle) ? 2 : 1;
                            action.appendTargeting(
                                    new TargetCardsOnTableEffect(action, playerId, "Choose character" + GameUtils.s(maxCharactersToProtect), 1, maxCharactersToProtect, Filters.and(Filters.your(playerId), Filters.character, Filters.present(creatureVehicle))) {
                                        @Override
                                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                                            action.addAnimationGroup(targetedCards);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new LoseCardFromTableEffect(action, creatureVehicle));
                                            // Allow response(s)
                                            action.allowResponses("Protect " + GameUtils.getAppendedNames(targetedCards),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            Collection<PhysicalCard> finalTargets = action.getPrimaryTargetCards(targetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ImmuneToUntilEndOfTurnEffect(action, finalTargets, Title.Exposure));
                                                            action.appendEffect(
                                                                    new ImmuneToUntilEndOfTurnEffect(action, finalTargets, Title.Ice_Storm));
                                                            action.appendEffect(
                                                                    new ImmuneToUntilEndOfTurnEffect(action, finalTargets, Title.Frostbite));
                                                            action.appendEffect(
                                                                    new ImmuneToUntilEndOfTurnEffect(action, finalTargets, Title.Gravel_Storm));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        final Filter tzizvvtFilter = Filters.Tzizvvt;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, tzizvvtFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Double Tzizvvt's power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Tzizvvt", tzizvvtFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard tzizvvt) {
                                action.addAnimationGroup(tzizvvt);
                                // Allow response(s)
                                action.allowResponses("Double " + GameUtils.getCardLink(tzizvvt) + "'s power",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                final int cardId = self.getCardId();
                                                float curPower = game.getModifiersQuerying().getPower(game.getGameState(), tzizvvt);

                                                // Perform result(s)
                                                self.setForRemainderOfGameData(cardId, new ForRemainderOfGameData(false));
                                                final int permCardId = self.getPermanentCardId();
                                                action.appendEffect(
                                                        new AddUntilEndOfGameActionProxyEffect(action,
                                                                new AbstractActionProxy() {
                                                                    @Override
                                                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                                        final PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                                        // Check condition(s)
                                                                        if (GameConditions.cardHasForRemainderOfGameDataEquals(self, cardId, false)
                                                                                && TriggerConditions.moved(game, effectResult, Filters.Tzizvvt)) {
                                                                            self.setForRemainderOfGameData(cardId, new ForRemainderOfGameData(true));
                                                                        }
                                                                        return null;
                                                                    }
                                                                })
                                                );
                                                action.appendEffect(
                                                        new AddUntilEndOfGameModifierEffect(action,
                                                                new PowerModifier(self, tzizvvt, new ForRemainderOfGameDataEqualsCondition(self, cardId, false), curPower),
                                                                "Doubles " + GameUtils.getCardLink(tzizvvt) + "'s power"));
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