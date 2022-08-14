package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Objective
 * Title: I Want That Map / And Now You’ll Give It To Me
 */
public class Card208_057 extends AbstractObjective {
    public Card208_057() {
        super(Side.DARK, 0, Title.I_Want_That_Map);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Tuanul Village, any other [Episode VII] location, and I Will Finish What You Started. Opponent may reveal a unique (•) character with ability (except Luke or a Jedi) from Reserve Deck; that card is a Resistance Agent. Otherwise, Luke is a Resistance Agent and loses immunity to attrition. For remainder of game, non-[Episode VII] Dark Jedi are lost and Resistance Agents are immune to Set For Stun. Flip this card if your First Order characters control two battlegrounds and a Resistance Agent is not present at a battleground site.");
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_8);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tuanul_Village, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tuanul Village to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_VII, Filters.location, Filters.not(Filters.Tuanul_Village)), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Episode VII] location to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.I_Will_Finish_What_You_Started, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose I Will Finish What You Started to deploy";
                    }
                });
        action.appendOptionalEffect(
                new ChooseCardsFromReserveDeckEffect(action, opponent, 0, 1,
                        Filters.or(Filters.and(Filters.unique, Filters.hasAbility, Filters.character, Filters.except(Filters.or(Filters.Luke, Filters.Jedi))), Filters.mayBeRevealedAsResistanceAgent)) {
                    @Override
                    protected void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
                        GameState gameState = game.getGameState();
                        if (!selectedCards.isEmpty()) {
                            PhysicalCard resistanceAgent = selectedCards.iterator().next();
                            gameState.sendMessage(opponent + " reveals " + GameUtils.getCardLink(resistanceAgent) + " as Resistance Agent");
                            gameState.showCardOnScreen(resistanceAgent);
                            self.setWhileInPlayData(new WhileInPlayData(resistanceAgent));
                        }
                        else {
                            gameState.sendMessage("Luke is a Resistance Agent and loses immunity to attrition");
                            self.setWhileInPlayData(new WhileInPlayData(true));
                        }
                    }
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Resistance Agent";
                    }
                }
        );

        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final int permCardId = self.getPermanentCardId();
        final PhysicalCard revealedResistanceAgent = self.getWhileInPlayData().getPhysicalCard();

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        if (revealedResistanceAgent != null) {
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new KeywordModifier(self, Filters.sameTitle(revealedResistanceAgent), Keyword.RESISTANCE_AGENT), null));
        }
        else {
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new KeywordModifier(self, Filters.Luke, Keyword.RESISTANCE_AGENT), null));
            action.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action,
                            new CancelImmunityToAttritionModifier(self, Filters.Luke), null));
        }
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isTableChanged(game, effectResult)) {
                                    Collection<PhysicalCard> nonEpVIIDarkJedi = Filters.filterActive(game, self, Filters.and(Filters.Dark_Jedi, Filters.not(Icon.EPISODE_VII)));
                                    if (!nonEpVIIDarkJedi.isEmpty()) {

                                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                        action.setSingletonTrigger(true);
                                        action.setText("Make non-[Episode VII] Dark Jedi lost");
                                        action.setActionMsg("Make " + GameUtils.getAppendedNames(nonEpVIIDarkJedi) + " lost");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new LoseCardsFromTableEffect(action, nonEpVIIDarkJedi));
                                        actions.add(action);
                                    }
                                }
                                return actions;
                            }
                        }
                ));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmuneToTitleModifier(self, Filters.Resistance_Agent, Title.Set_For_Stun), null));
        return action;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        if (self.getWhileInPlayData() != null) {
            PhysicalCard revealedResistanceAgent = self.getWhileInPlayData().getPhysicalCard();
            if (revealedResistanceAgent != null) {
                return "Resistance Agent is " + GameUtils.getCardLink(revealedResistanceAgent);
            }
            else {
                return "Resistance Agent is Luke";
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controlsWith(game, self, playerId, 2, Filters.battleground, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.First_Order_character)
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Resistance_Agent, Filters.presentAt(Filters.battleground_site)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}