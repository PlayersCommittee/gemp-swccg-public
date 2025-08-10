package com.gempukku.swccgo.cards.set225.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.JediTestSuspendedInsteadOfLostModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.PlaceJediTestOnTableWhenCompletedModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Set: Set 25
 * Type: Epic Event
 * Title: Patience!
 */
public class Card225_057 extends AbstractEpicEventDeployable {
    public Card225_057() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Patience, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setGameText("If your [Dagobah] objective on table, deploy on table and stack five Jedi Tests from outside the game face up here. I Won't Fail You: Only Luke may be your apprentice. You may deploy face up Jedi Tests from here as if from hand. Place completed Jedi Tests on table. Jedi Tests are suspended (not lost) while Luke not on table. I've Got To Go To Them: Once per turn, if you just lost Force from a Force drain and you do not occupy a battleground, turn a Jedi Test here face down. Remember Your Failure At The Cave: During battle, Jedi Test #3 is suspended unless Luke battling alone.");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_25);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(playerId), Icon.DAGOBAH, Filters.Objective));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            // Perform result(s)
            action.appendEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, false, Filters.Jedi_Test_1));
            action.appendEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, false, Filters.Jedi_Test_2));
            action.appendEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, false, Filters.Jedi_Test_3));
            action.appendEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, false, Filters.Jedi_Test_4));
            action.appendEffect(
                new StackCardFromOutsideDeckEffect(action, playerId, self, false, Filters.Jedi_Test_5));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;
        final Filter jediTestFaceUp = Filters.and(Filters.Jedi_Test, Filters.not(Filters.face_down));
        final Filter patienceWithJediTestStackedFaceUp = Filters.and(Filters.Patience, Filters.hasStacked(jediTestFaceUp));
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, playerId, Filters.any, false)
                && !GameConditions.occupies(game, playerId, Filters.battleground)
                && GameConditions.canSpot(game, self, patienceWithJediTestStackedFaceUp)) {
        
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);

            action.setPerformingPlayer(playerId);
            action.setText("Turn Jedi Test face down");
            action.setActionMsg("Turn a Jedi Test on Patience! face down");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, patienceWithJediTestStackedFaceUp, jediTestFaceUp, false) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Perform result(s)
                            action.appendEffect(
                                new FlipSingleSidedStackedCard(action, selectedCard));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.not(Filters.face_down), Filters.Jedi_Test, Filters.stackedOn(self))));
        modifiers.add(new PlaceJediTestOnTableWhenCompletedModifier(self, Filters.any, new TrueCondition()));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Jedi_Test, ModifyGameTextType.JEDI_TESTS__ONLY_LUKE_MAY_BE_APPRENTICE));
        modifiers.add(new JediTestSuspendedInsteadOfLostModifier(self, Filters.completed_Jedi_Test, new TrueCondition()));

        Condition lukeBattlingAlone = new DuringBattleWithParticipantCondition(Filters.and(Filters.Luke, Filters.alone));
        Condition duringBattleUnlessLukeBattlingAlone = new AndCondition(new DuringBattleCondition(), new UnlessCondition(lukeBattlingAlone));

        modifiers.add(new SuspendsCardModifier(self, Filters.Jedi_Test_3, duringBattleUnlessLukeBattlingAlone));
        return modifiers;
    }
}
