package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardCombinationIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Effect
 * Title: I've Lost Artoo! (V)
 */
public class Card601_157 extends AbstractNormalEffect {
    public Card601_157() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "I've Lost Artoo!");
        setVirtualSuffix(true);
        setLore("'WHAAAAAAAAAOOOOW!'");
        setGameText("Deploy on table.  If A Sith's Plans not on table, may deploy it from Reserve Deck; reshuffle.  Once per game, may take a device that deploys on a character and/or an Effect with 'skill' in game text into hand from Reserve Deck; reshuffle. Whenever your character leaves table, may place all your devices and Effects on them in Used Pile. (Immune to Alter.)");
        addIcons(Icon.LEGACY_BLOCK_1);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IVE_LOST_ARTOO_V__DEPLOY_A_SITHS_PLANS;

        // Check condition(s)
        if (!GameConditions.canSpot(game, self, Filters.title(Title.A_Siths_Plans))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy A Sith's Plans from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.title(Title.A_Siths_Plans), true));
            actions.add(action);
        }


        gameTextActionId = GameTextActionId.LEGACY__IVE_LOST_ARTOO_V__TAKE_CARDS_INTO_HAND;

        final Filter effectWithSkillInGameText = Filters.and(Filters.Effect, Filters.or(Filters.gameTextContains("skill"), Filters.gameTextContains("skills")));
        final Filter deviceThatDeploysOnACharacter = Filters.and(Filters.device, Filters.deploys_on_characters);

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take cards into hand from Reserve Deck");
            action.setActionMsg("Take a device that deploys on a character and/or an Effect with 'skill' in game text into hand from Reserve Deck");

            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardCombinationIntoHandFromReserveDeckEffect(action, playerId, true) {
                        @Override
                        public String getChoiceText(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            return "Choose a device that deploys on a character and/or an Effect with 'skill' in game text";
                        }

                        @Override
                        public Filter getValidToSelectFilter(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            if (cardsSelected.isEmpty()) {
                                return Filters.or(deviceThatDeploysOnACharacter, effectWithSkillInGameText);
                            }
                            if (cardsSelected.size()==1) {
                                if(Filters.filter(cardsSelected, game, deviceThatDeploysOnACharacter).isEmpty()) {
                                    return deviceThatDeploysOnACharacter;
                                }
                                if(Filters.filter(cardsSelected, game, effectWithSkillInGameText).isEmpty()) {
                                    return effectWithSkillInGameText;
                                }
                            }

                            return Filters.none;
                        }

                        @Override
                        public boolean isSelectionValid(SwccgGame game, Collection<PhysicalCard> cardsSelected) {
                            if (cardsSelected.size()==1) {
                                if(Filters.filter(cardsSelected, game, Filters.or(deviceThatDeploysOnACharacter, effectWithSkillInGameText)).size()==1)
                                    return true;
                            } else if (cardsSelected.size()==2) {
                                if(Filters.filter(cardsSelected, game, deviceThatDeploysOnACharacter).size()==1
                                    && Filters.filter(cardsSelected, game, effectWithSkillInGameText).size()==1)
                                    return true;
                            }
                            return false;
                        }
                    });
            actions.add(action);
        }



        return actions;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.leavesTable(game, effectResult, Filters.and(Filters.your(self), Filters.character))) {

            PhysicalCard leftTable = null;

            if (effectResult.getType() == EffectResult.Type.LOST_FROM_TABLE || effectResult.getType() == EffectResult.Type.FORFEITED_TO_LOST_PILE_FROM_TABLE)
                leftTable = ((LostFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.FORFEITED_TO_USED_PILE_FROM_TABLE)
                leftTable =  ((ForfeitedCardToUsedPileFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.CANCELED_ON_TABLE)
                leftTable =  ((CancelCardOnTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.STACKED_FROM_TABLE)
                leftTable =  ((StackedFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.RETURNED_TO_HAND_FROM_TABLE)
                leftTable = ((ReturnedCardToHandFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.PUT_IN_RESERVE_DECK_FROM_TABLE)
                leftTable =  ((PutCardInReserveDeckFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.PUT_IN_FORCE_PILE_FROM_TABLE)
                leftTable =  ((PutCardInForcePileFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.PUT_IN_USED_PILE_FROM_TABLE)
                leftTable =  ((PutCardInUsedPileFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.PLACED_OUT_OF_PLAY_FROM_TABLE)
                leftTable =  ((PlacedCardOutOfPlayFromTableResult) effectResult).getCard();
            else if (effectResult.getType() == EffectResult.Type.CAPTURED) {
                CaptureCharacterResult captureCharacterResult = (CaptureCharacterResult) effectResult;
                if (captureCharacterResult.getOption() == CaptureOption.ESCAPE)
                    leftTable = captureCharacterResult.getCapturedCard();
            }
            if (leftTable != null) {
             Collection<PhysicalCard> attachedDevicesAndEffects = Filters.filter(leftTable.getCardsPreviouslyAttached(), game, Filters.and(Filters.your(self), Filters.or(Filters.device, Filters.Effect)));
             if (!attachedDevicesAndEffects.isEmpty()) {
                 OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                 action.setText("Place your devices and Effects in Used Pile");
                 action.setActionMsg("Place your devices and Effects on " + GameUtils.getCardLink(leftTable) + " in Used Pile");
                 action.appendEffect(
                         new PlaceCardsInUsedPileFromOffTableEffect(action, attachedDevicesAndEffects));
                 return Collections.singletonList(action);
             }
            }
        }

        return null;
    }
}