package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Objective
 * Title: Hunt For The Droid General / He's A Coward
 */
public class Card221_067 extends AbstractObjective {
    public Card221_067() {
        super(Side.LIGHT, 0, Title.Hunt_For_The_Droid_General, ExpansionSet.SET_21, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy a [Clone Army] battleground, Clone Command Center (to same planet), Cloning Cylinders, and Grievous Will Run And Hide. " +
                "For remainder of game, you may not deploy non-[Episode I] cards with ability. [Reflections II] objectives target Anakin instead of Luke. Your Destiny is suspended. Jedi gain [Pilot] skill. Your [Episode I] sites are immune to No Escape. At end of opponent's turn, if you occupy more battlegrounds than opponent, opponent loses 1 Force. " +
                "Flip this card if Grievous Will Run And Hide here unless Grievous alone at a battleground.");
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter locationFilter = Filters.and(Icon.CLONE_ARMY, Filters.battleground, Filters.location);
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, locationFilter, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Chose a [Clone Army] battleground to deploy";
                    }
                    @Override
                    protected void cardDeployed(PhysicalCard card) {
                        String systemName = card.getBlueprint().getSystemName();

                        action.appendRequiredEffect(
                                new DeployCardToSystemFromReserveDeckEffect(action, Filters.Clone_Command_Center, systemName, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Choose Clone Command Center to deploy";
                                    }
                                });

                        // put these here so they are deployed after Clone Command Center
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.Cloning_Cylinders, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Deploy Cloning Cylinders";
                                    }

                                });
                        action.appendRequiredEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.Grievous_Will_Run_And_Hide, true, false) {
                                    @Override
                                    public String getChoiceText() {
                                        return "Deploy Grievous Will Run And Hide";
                                    }

                                });
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.not(Icon.EPISODE_I), Filters.hasAbilityOrHasPermanentPilotWithAbility), self.getOwner()));
        modifiers.add(new SuspendsCardModifier(self, Filters.Your_Destiny));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.REFLECTIONS_II, Filters.Objective), ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_ANAKIN_INSTEAD_OF_LUKE));
        modifiers.add(new IconModifier(self, Filters.and(Filters.your(self), Filters.Jedi), Icon.PILOT));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Icon.EPISODE_I, Filters.site), Title.No_Escape));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

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

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.hasAttached(game, self, Filters.Grievous_Will_Run_And_Hide)) {

            // if Grievous is alone, make sure it isn't because other characters with him are excluded from battle
            PhysicalCard grievous = Filters.findFirstActive(game, self, Filters.and(Filters.Grievous, Filters.alone, Filters.at(Filters.battleground)));
            if (grievous == null || !game.getModifiersQuerying().isAlone(game.getGameState(), grievous, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE)) {

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