package com.gempukku.swccgo.cards.set219.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromOffTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Effect
 * Title: Weather Vane (V)
 */
public class Card219_047 extends AbstractNormalEffect {
    public Card219_047() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Weather_Vane, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The metal rods extending from the bottom of Cloud City are part of the city's flotation system. Sensors detect the velocity of wind and the content of local clouds.");
        setGameText("Deploy on table. Unless this card upright, characters here are lost. " +
                "If you just Force drained or initiated battle, rotate this card 90° clockwise; if now upright, retrieve any one card. " +
                "If opponent just initiated battle, unless this card upright, rotate it 90° counterclockwise.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.cardHasWhileInPlayDataSet(self)) {
            self.setWhileInPlayData(new WhileInPlayData(0));
        }

        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        int currentPosition = self.getWhileInPlayData().getIntValue();

        // Unless this card upright, characters here are lost.
        if (currentPosition != 0
                && GameConditions.hasStackedCards(game, self, Filters.character)
                && (TriggerConditions.justRelocatedToWeatherVane(game, effectResult, Filters.character)
                || TriggerConditions.isTableChanged(game, effectResult))) {
            Collection<PhysicalCard> charactersToLose = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.character);
            if (!charactersToLose.isEmpty()) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make character" + (charactersToLose.size()==1?"":"s") + " lost");
                action.setActionMsg("Make " + GameUtils.getAppendedNames(charactersToLose) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardsFromOffTableSimultaneouslyEffect(action, charactersToLose, false));
                actions.add(action);
            }
        }

        // If you just Force drained or initiated battle, rotate this card 90° clockwise; if now upright, retrieve any one card.
        if (TriggerConditions.forceDrainCompleted(game, effectResult, playerId)
                || TriggerConditions.battleInitiated(game, effectResult, playerId)) {
            actions.add(createRotateAction(playerId, self, gameTextSourceCardId, true));
        }

        // If opponent just initiated battle, unless this card upright, rotate it 90° counterclockwise.
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && currentPosition!=0) {
            actions.add(createRotateAction(playerId, self, gameTextSourceCardId, false));
        }

        return actions;
    }

    private RequiredGameTextTriggerAction createRotateAction(final String playerId, final PhysicalCard self, int gameTextSourceCardId, final boolean clockwise) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setPerformingPlayer(playerId);
        if(clockwise)
            action.setText("Rotate clockwise");
        else
            action.setText("Rotate counterclockwise");

        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                int currentPosition = (GameConditions.cardHasWhileInPlayDataSet(self)?self.getWhileInPlayData().getIntValue():0);

                // rotate in the specified direction
                if (clockwise)
                    currentPosition++;
                else
                    currentPosition--;

                // get it in the correct range
                currentPosition = (currentPosition+4)%4;

                switch(currentPosition) {
                    case 0:
                        //upright
                        self.setRotated(false);
                        self.setInverted(false);
                        self.setSideways(false);
                        break;
                    case 1:
                        //90 degrees
                        self.setRotated(false);
                        self.setInverted(false);
                        self.setSideways(true);
                        break;
                    case 2:
                        //180 degrees
                        self.setRotated(false);
                        self.setInverted(true);
                        self.setSideways(false);
                        break;
                    case 3:
                        //270 degrees
                        self.setRotated(true);
                        self.setInverted(true);
                        self.setSideways(true);
                        break;
                    default:

                }

                game.getGameState().resumeCard(self); // tells the listeners to update the card

                action.appendEffect(
                        new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(currentPosition)));


                if (clockwise && currentPosition == 0) {
                    action.appendEffect(
                            new RetrieveCardEffect(action, playerId, Filters.any));
                }
            }
        });

        return action;
    }
}
