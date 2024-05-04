package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotContributeToForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PlaceJediTestOnTableWhenCompletedModifier;
import com.gempukku.swccgo.logic.modifiers.PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Special Edition
 * Type: Objective
 * Title: Mind What You Have Learned / Save You It Can
 */
public class Card7_138 extends AbstractObjective {
    public Card7_138() {
        super(Side.LIGHT, 0, Title.Mind_What_You_Have_Learned, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Dagobah system (may not be converted). While this side up, during your deploy phase, may deploy Luke, Yoda (deploy -2), Yoda's Hope, At Peace and/or Luke's Backpack to Dagobah from Reserve Deck; reshuffle. Whenever you draw training destiny, draw two and choose one. Place Luke's completed Jedi Tests on table. Your cards at Dagobah may not Force drain or contribute to Force retrieval. Add 4 to each player's destiny draw for Sense and Alter. Flip this card when Luke completes Jedi Test #5.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Dagobah_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Dagobah system to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeConvertedModifier(self, Filters.Dagobah_system), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MIND_WHAT_YOUR_HAVE_LEARNED__DOWNLOAD_CARD_TO_DAGOBAH;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)) {
            boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE);
            Set<Persona> personas = targetsLeiaInsteadOfLuke ? new HashSet<Persona>(Arrays.asList(Persona.LEIA, Persona.YODA)) : new HashSet<Persona>(Arrays.asList(Persona.LUKE, Persona.YODA));
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, personas, Arrays.asList(Title.Yodas_Hope, Title.At_Peace, Title.Lukes_Backpack))) {
                if (targetsLeiaInsteadOfLuke) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Deploy card to Dagobah from Reserve Deck");
                    action.setActionMsg("Deploy Leia, Yoda, Yoda's Hope, At Peace, or Luke's Backpack to Dagobah from Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(
                            new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.Leia, Filters.Yoda, Filters.Yodas_Hope, Filters.At_Peace, Filters.Lukes_Backpack), Title.Dagobah, -2, Filters.Yoda, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true));
                    return Collections.singletonList(action);
                }
                else {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Deploy card to Dagobah from Reserve Deck");
                    action.setActionMsg("Deploy Luke, Yoda, Yoda's Hope, At Peace, or Luke's Backpack to Dagobah from Reserve Deck");
                    // Perform result(s)
                    action.appendEffect(
                            new DeployCardToSystemFromReserveDeckEffect(action, Filters.or(Filters.Luke, Filters.Daughter_Of_Skywalker, Filters.Yoda, Filters.Yodas_Hope, Filters.At_Peace, Filters.Lukes_Backpack), Title.Dagobah, -2, Filters.Yoda, DeploymentRestrictionsOption.ignoreLocationDeploymentRestrictions(), true) {
                                @Override
                                protected void cardDeployed(PhysicalCard card) {
                                    if (Filters.Daughter_Of_Skywalker.accepts(game, card)) {
                                        game.getGameState().sendMessage(playerId + " makes Mind What You Have Learned / Save You It Can target Leia instead of Luke for remainder of game using " + GameUtils.getCardLink(card));
                                        game.getModifiersEnvironment().addUntilEndOfGameModifier(new ModifyGameTextModifier(card, Filters.or(Filters.Mind_What_You_Have_Learned, Filters.Save_You_It_Can), ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE));
                                    }
                                }
                            });
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition targetsLeiaInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.DRAW_TWO_AND_CHOOSE_ONE_FOR_TRAINING_DESTINY, playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.Jedi_Test_5, Filters.completed_Jedi_Test, Filters.jediTestTargetingApprentice(Filters.Luke)), new NotCondition(targetsLeiaInsteadOfLuke), ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.Jedi_Test_5, Filters.completed_Jedi_Test, Filters.jediTestTargetingApprentice(Filters.Leia)), targetsLeiaInsteadOfLuke, ModifyGameTextType.IT_IS_THE_FUTURE_YOU_SEE__STACK_DESTINY_CARD_ON_JEDI_TEST_5));
        modifiers.add(new PlaceJediTestOnTableWhenCompletedModifier(self, Filters.jediTestTargetingApprentice(Filters.Luke), new NotCondition(targetsLeiaInsteadOfLuke)));
        modifiers.add(new PlaceJediTestOnTableWhenCompletedModifier(self, Filters.jediTestTargetingApprentice(Filters.Leia), targetsLeiaInsteadOfLuke));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Dagobah_location, playerId));
        modifiers.add(new MayNotContributeToForceRetrievalModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Dagobah_location, Filters.at(Title.Dagobah)))));
        modifiers.add(new PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier(self, Filters.Dagobah_location, playerId));
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.and(Filters.or(Filters.Sense, Filters.Alter), Filters.canBeTargetedBy(self)), 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.jediTestCompletedBy(game, effectResult, Filters.Jedi_Test_5, GameConditions.hasGameTextModification(game, self, ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE) ? Filters.Leia : Filters.Luke)
                && GameConditions.canBeFlipped(game, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
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