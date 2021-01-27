package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.AbstractEpicEventPlayableInsteadOfStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.PlayCardToZoneAction;
import com.gempukku.swccgo.cards.conditions.AfterPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.cards.effects.usage.FourTimesPerGameEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.*;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Epic Event
 * Title: Communing
 */
public class Card601_041 extends AbstractEpicEventDeployable {
    public Card601_041() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Communing");
        setGameText("If your starting location has exactly 2 [Light Side Force], play instead of a starting interrupt. deploy from Reserve Deck two always [Immune to Alter] Effects; reshuffle. Deploy on table; take into hand and stack a Jedi here from Reserve Deck (that Jedi is 'communing'); reshuffle. \n" +
                "Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone Is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile.");
        addIcons(Icon.SPECIAL_EDITION, Icon.BLOCK_7);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, new StackedEvaluator(self, Filters.any), self.getOwner()));
        return modifiers;
    }

    @Override
    public PlayCardAction getStartingInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self) {
        game.getGameState().sendMessage("trying to check getStartingInterruptAction");
        // Check condition(s)
        final PhysicalCard startingLocation = game.getModifiersQuerying().getStartingLocation(playerId);
        if (startingLocation != null && Filters.iconCount(Icon.LIGHT_FORCE, 2).accepts(game, startingLocation)) {
            final PlayEpicEventAction action = new PlayEpicEventAction(self);
            action.setEpicEventState(new EpicEventState(self, EpicEventState.Type.PLAY_INSTEAD_OF_STARTING_INTERRUPT) {
                @Override
                public PhysicalCard getEpicEvent() {
                    return super.getEpicEvent();
                }
            });

            action.setText("Deploy Effects from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Deploy up to two Effects from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), 1, 2, true, false));
                            action.appendEffect(
                                    new DeployCardFromVoidEffect(action, Filters.and(self), true));

                            action.appendEffect(
                                    new StackCardFromReserveDeckEffect(action, self, Filters.Jedi, true));

                        }
                    }
            );
            return action;

        }

        return null;
    }

}