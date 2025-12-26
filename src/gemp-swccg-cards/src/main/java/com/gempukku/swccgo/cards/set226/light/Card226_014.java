package com.gempukku.swccgo.cards.set226.light;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

/**
 * Set: Set 26
 * Type: Epic Event
 * Title: Fallen Order
 */
public class Card226_014 extends AbstractEpicEventDeployable {
    public Card226_014() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Fallen_Order, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setGameText("If The Hidden Path on table, deploy on table. When deployed, stack three Jedi survivors from Reserve Deck here. During your deploy phase, Jedi survivors here may deploy as if from hand. The Light Will Fade: While The Hidden Path on table, Jedi survivors are deploy = 3, power = 3, forfeit = 3, deploy only to Safehouse, move using landspeed for free, and their game text is canceled. But It Is Never Forgotten: If your Jedi survivor was just lost, may lose 1 Force to stack it here. (Immune to Cold Feet.)");
        addIcons(Icon.VIRTUAL_SET_26);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.The_Hidden_Path);
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
                new StackCardsFromReserveDeckEffect(action, playerId, 3, 3, self, false, Filters.Jedi_Survivor));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY)
                && GameConditions.hasStackedCards(game, self, Filters.and(Filters.Jedi_Survivor, Filters.deployable(self, null, false, 0)))) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Jedi Survivor stacked here");
            action.setActionMsg("Deploy a Jedi Survivor stacked on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new DeployStackedCardEffect(action, self, Filters.Jedi_Survivor, false));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition hiddenPathOnTable = new OnTableCondition(self, Filters.The_Hidden_Path);

        modifiers.add(new ResetDeployCostModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable, 3));
        modifiers.add(new ResetPowerModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable, 3));
        modifiers.add(new ResetForfeitModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable, 3));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable, Filters.not(Filters.Safehouse)));
        modifiers.add(new MovesForFreeUsingLandspeedModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable));
        modifiers.add(new CancelsGameTextModifier(self, Filters.Jedi_Survivor, hiddenPathOnTable));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Jedi_Survivor, Title.Cold_Feet));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.Jedi_Survivor))){
            
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack " + GameUtils.getFullName(cardLost) + " here");
            action.setActionMsg("Stack " + GameUtils.getFullName(cardLost) + " on " + GameUtils.getCardLink(self));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, false, false, true));
            actions.add(action);
        }

        return actions;
    }
}
