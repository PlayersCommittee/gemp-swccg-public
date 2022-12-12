package com.gempukku.swccgo.cards.set208.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 8
 * Type: Objective
 * Title: He Is The Chosen One / He Will Bring Balance
 */
public class Card208_025 extends AbstractObjective {
    public Card208_025() {
        super(Side.LIGHT, 0, Title.He_Is_The_Chosen_One);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Anakin's Funeral Pyre (with Prophecy Of The Force there), Ewok Village, and I Feel The Conflict. " +
                "For remainder of game, you may not deploy [Episode I] or [Episode VII] characters or locations (except Obi-Wan, Yoda, and Lars' Moisture Farm). If Luke just won a battle, may re-circulate and shuffle Reserve Deck. Emperor's Power does not increase deploy costs at battlegrounds. " +
                "While this side up, you may initiate battles for free. " +
                "Flip this card if Luke or a Jedi at a battleground site (unless an opponent's character of ability > 4 is).");
        addIcons(Icon.VIRTUAL_SET_8);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Anakins_Funeral_Pyre), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Anakin's Funeral Pyre to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToTargetFromReserveDeckEffect(action, Filters.Prophecy_Of_The_Force, Filters.title(Title.Anakins_Funeral_Pyre), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Prophecy Of The Force to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Ewok_Village, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Ewok Village to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.I_Feel_The_Conflict, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose I Feel The Conflict to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.and(Filters.or(Icon.EPISODE_I, Icon.EPISODE_VII), Filters.or(Filters.and(Filters.character, Filters.except(Filters.or(Filters.ObiWan, Filters.Yoda))), Filters.and(Filters.location, Filters.except(Filters.Lars_Moisture_Farm)))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.any, Filters.Emperors_Power, Filters.battleground), null));

        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.wonBattle(game, effectResult, Filters.Luke)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Re-circulate and reshuffle");
            action.setActionMsg("Re-circulate and shuffle Reserve Deck");

            action.appendEffect(
                    new RecirculateEffect(action, playerId));
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.or(Filters.Luke, Filters.Jedi), Filters.at(Filters.battleground_site)))
                && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityMoreThan(4), Filters.at(Filters.battleground_site)))) {

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