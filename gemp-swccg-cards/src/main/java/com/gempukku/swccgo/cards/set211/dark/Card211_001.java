package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractAlienImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.ClearForRemainderOfGameDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Alien/Imperial
 * Title: Mitth'raw'nuruodo
 */

public class Card211_001 extends AbstractAlienImperial {
    public Card211_001() {
        super(Side.DARK, 2, 3, 3, 3, 6, "Mitth'raw'nuruodo", Uniqueness.UNIQUE);
        setLore("Thrawn. Chiss commander. Leader.");
        setGameText("[Pilot] 3. Thrawn's game text may not be canceled. May lose 1 Force to cancel a weapon destiny at same system. Once per turn, may target a related location: the next time opponent moves there this turn, they lose 1 Force.");
        addIcons(Icon.REFLECTIONS_II, Icon.PILOT, Icon.WARRIOR);
        addPersona(Persona.THRAWN);
        addKeywords(Keyword.LEADER, Keyword.SPY, Keyword.COMMANDER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_7;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Choose a location.");
            action.setActionMsg("Choose a location.");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            //Clearing the once per turn flag if Thrawn retrieved a force
            action.appendEffect(new ClearForRemainderOfGameDataEffect(action, self, true));
            //Select a location
            action.appendEffect(new ChooseCardsOnTableEffect(action, playerId, "Choose a related location.", 1, 1, Filters.relatedLocation(self)){
                @java.lang.Override
                protected FullEffectResult playEffectReturningResult(SwccgGame game) {
                    return super.playEffectReturningResult(game);
                }
                @Override
                protected void cardsSelected(Collection<PhysicalCard> selectedCards){
                    final PhysicalCard chosenLocation = selectedCards.size() == 1 ? selectedCards.iterator().next() : null;
                    game.getGameState().sendMessage("The location chosen for Thrawn is " + chosenLocation.getTitle());
                    //Add in the Proxy effect so that Thrawn will fire the second half of it this turn
                    action.appendEffect(
                            new AddUntilEndOfTurnActionProxyEffect(action,
                                    new AbstractActionProxy() {
                                        @Override
                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                            List<TriggerAction> actions = new LinkedList<TriggerAction>();

                                            // Check condition(s)
                                            if (TriggerConditions.movedToLocation(game, effectResult, Filters.opponents(self), Filters.sameLocation(chosenLocation))
                                                    && !GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
                                                self.setForRemainderOfGameData((Integer)self.getCardId(), new ForRemainderOfGameData());

                                                final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                action2.setText("Opponent loses a force.");
                                                action2.setActionMsg("Light Side has fallen into Thrawn's trap and loses 1 Force.");
                                                // Actually retrieve the force
                                                action2.appendEffect(new LoseForceEffect(action, game.getOpponent(playerId), 1));
                                                actions.add(action2);
                                                return actions;
                                            }
                                            return null;
                                        }
                                    }
                            ));
                }
            });

            // Perform result(s)

            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isWeaponDestinyJustDrawnTargeting(game, effectResult, Filters.any, Filters.and(Filters.starship, Filters.atSameSystem(self)))
                && GameConditions.canCancelDestiny(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel weapon destiny");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));

            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
