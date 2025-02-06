package com.gempukku.swccgo.cards.set303.dark;

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
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Shadow Academy
 * Type: Objective
 * Title: Peace Is A Lie, There Is Only Passion / The Force Shall Free Me
 */
public class Card303_007 extends AbstractObjective {
    public Card303_007() {
        super(Side.DARK, 0, Title.Peace_Is_A_Lie_There_Is_Only_Passion, ExpansionSet.SA, Rarity.R);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy The Shadow Academy Training Grounds (may not be converted). While this side up, during your deploy phase, may deploy a character with ability <7, a Headmaster/mistress or Instructor, Shadow Academy Holocron, Through Passion, I Gain Strength, and Through Power, I Gain Victory to Shadow Academy Training Grounds from Reserve Deck; reshuffle. Whenever you draw training destiny, draw two and choose one. Place Apprentice's completed Sith Tests on table. Your cards at the Shadow Academy may not Force drain or contribute to Force retrieval. Add 4 to each player's destiny draw for Sense and Alter. Flip this card when Apprentice completes Sith Test #5.");
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Shadow_Academy_Training_Grounds, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy Shadow Academy: Training Grounds";
                    }
                });
		action.appendRequiredEffect(
				new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.HEADMASTER, Filters.INSTRUCTOR), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy a Headmaster/Headmistress or Instructor";
                    }
                });
		action.appendRequiredEffect(
				new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.character, Filters.abilityLessThan(7)), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy an Apprentice";
                    }
                });
		action.appendRequiredEffect(
				new DeployCardFromReserveDeckEffect(action, Filters.Shadow_Academy_Holocron, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy a Shadow Academy Holocron";
                    }
                });		
		action.appendRequiredEffect(
				new DeployCardFromReserveDeckEffect(action, Filters.Through_Power_I_Gain_Victory, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy Through Power, I Gain Victory";
                    }
                });
		action.appendRequiredEffect(
				new DeployCardFromReserveDeckEffect(action, Filters.Through_Passion_I_Gain_Strength, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Deploy Through Passion, I Gain Srength";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotBeConvertedModifier(self, Filters.Shadow_Academy_Training_Grounds), null));
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.DRAW_TWO_AND_CHOOSE_ONE_FOR_TRAINING_DESTINY, playerId));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Shadow_Academy_location, playerId));
        modifiers.add(new MayNotContributeToForceRetrievalModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.Shadow_Academy_location))));
        modifiers.add(new PlayersCardsAtLocationMayNotContributeToForceRetrievalModifier(self, Filters.Shadow_Academy_location, playerId));
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.and(Filters.or(Filters.Sense, Filters.Alter), Filters.canBeTargetedBy(self)), 4));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.jediTestCompletedBy(game, effectResult, Filters.SITH_TEST_5, Filters.apprentice)
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