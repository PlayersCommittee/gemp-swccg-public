package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromCardPileOnBottomOfCardPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.MoveCardUsingLandspeedEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeOneCardIntoHandFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Objective
 * Title: Hunt For The Droid General / He's A Coward
 */
public class Card221_067_BACK extends AbstractObjective {
    public Card221_067_BACK() {
        super(Side.LIGHT, 7, Title.Hes_A_Coward, ExpansionSet.SET_21, Rarity.V);
        setGameText("While this side up, your Force drains are +1 where you have a clone with a Jedi or Padawan. X = number of battlegrounds your [Clone Army] cards occupy. If you just initiated a battle: peek at the top card of your Reserve Deck or Used Pile (may take it into hand or place it on bottom of Reserve Deck), then if X > 1, retrieve a [Clone Army] card into hand, then if X > 2, your clone may make a regular move (using landspeed for free) to the battle location. " +
                "Flip this card if Grievous Will Run And Hide at a battleground or Grievous alone at a battleground.");
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.not(Icon.EPISODE_I), Filters.hasAbilityOrHasPermanentPilotWithAbility), self.getOwner()));
        modifiers.add(new SuspendsCardModifier(self, Filters.Your_Destiny));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.REFLECTIONS_II, Filters.Objective), ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_ANAKIN_INSTEAD_OF_LUKE));
        modifiers.add(new IconModifier(self, Filters.and(Filters.your(self), Filters.Jedi), Icon.PILOT));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Icon.EPISODE_I, Filters.site), Title.No_Escape));
        modifiers.add(new ForceDrainModifier(self, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.clone, Filters.with(self, Filters.or(Filters.Jedi, Filters.padawan)))),  1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, playerId)) {
            int battlegroundsYouOccupy = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId))).size();
            int battlegroundsOpponentOccupies = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(opponent))).size();

            if (battlegroundsYouOccupy > battlegroundsOpponentOccupies) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText(opponent + " loses 1 Force");
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 1));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        if (TriggerConditions.battleInitiated(game, effectResult, playerId)) {
            int battlegroundCount = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupiesWith(playerId, self, Filters.icon(Icon.CLONE_ARMY)))).size();
            float x = game.getModifiersQuerying().getVariableValue(game.getGameState(), self, Variable.X, battlegroundCount);

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

            action.appendEffect(new ChooseExistingCardPileEffect(action, playerId, playerId, Filters.or(Zone.RESERVE_DECK, Zone.USED_PILE)) {
                @Override
                protected void pileChosen(SwccgGame game, String cardPileOwner, Zone cardPile) {
                    action.insertEffect(new PeekAtTopCardOfCardPileEffect(action, playerId, playerId, cardPile) {
                        @Override
                        protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
                            final PhysicalCard card = peekedAtCards.iterator().next();

                            final String intoHand = "Take card into hand";
                            final String bottomOfReserve = "Place card on bottom of Reserve Deck";
                            final String nothing = "Do not move the card";
                            action.insertEffect(new PlayoutDecisionEffect(action, playerId, new MultipleChoiceAwaitingDecision("Choose what to do with the card", new String[]{intoHand, bottomOfReserve, nothing}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (intoHand.equals(result)) {
                                        action.insertEffect(new TakeOneCardIntoHandFromOffTableEffect(action, playerId, card, "Takes card into hand") {
                                            @Override
                                            protected void afterCardTakenIntoHand() {

                                            }
                                        });
                                    } else if (bottomOfReserve.equals(result)) {
                                        action.insertEffect(
                                                new PutCardFromCardPileOnBottomOfCardPileEffect(action, playerId, card, Zone.RESERVE_DECK, true));
                                    } else {
                                        action.insertEffect(new SendMessageEffect(action, playerId + " chooses not to move the card"));
                                    }
                                }
                            }));
                        }
                    });
                }
            });


            if (x > 1) {
                action.appendEffect(new RetrieveCardIntoHandEffect(action, playerId, Filters.icon(Icon.CLONE_ARMY)));

                final PhysicalCard location = Filters.findFirstFromTopLocationsOnTable(game, Filters.battleLocation);
                final Filter cloneToMove = Filters.and(Filters.your(self), Filters.clone,
                        Filters.movableAsRegularMoveUsingLandspeed(playerId, false, false, true, 0, null, Filters.locationAndCardsAtLocation(Filters.battleLocation)));
                if (x > 2 && location != null
                        && GameConditions.canTarget(game, self, cloneToMove)) {

                    action.appendEffect(new ChooseCardsOnTableEffect(action, playerId, "Choose a clone to move as a regular move (using landspeed for free) to "+GameUtils.getCardLink(location),0, 1, cloneToMove) {
                        @Override
                        protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                            if (selectedCards.size() == 1) {
                                PhysicalCard clone = selectedCards.iterator().next();
                                if (clone != null) {
                                    action.appendEffect(new MoveCardUsingLandspeedEffect(action, playerId, clone, true, Filters.locationAndCardsAtLocation(Filters.battleLocation)));
                                }
                            }
                        }
                    });

                }
            }

            actions.add(action);
        }



        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {


            boolean flip = !GameConditions.hasAttached(game, self, Filters.Grievous_Will_Run_And_Hide);

            if (!flip) {
                // if Grievous is alone, make sure it isn't because other characters with him are excluded from battle
                PhysicalCard grievous = Filters.findFirstActive(game, self, Filters.and(Filters.Grievous, Filters.alone, Filters.at(Filters.battleground)));
                if (grievous != null && game.getModifiersQuerying().isAlone(game.getGameState(), grievous, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE)) {
                    flip = true;
                }
            }

            if (flip) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                actions.add(action);
            }
        }
        return actions;
    }
}