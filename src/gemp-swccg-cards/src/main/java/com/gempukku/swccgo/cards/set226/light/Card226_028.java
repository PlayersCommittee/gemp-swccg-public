package com.gempukku.swccgo.cards.set226.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;


/**
 * Set: Set 26
 * Type: Objective
 * Title: The Hidden Path / Gather Allies And Train
 */
public class Card226_028 extends AbstractObjective {
    public Card226_028() {
        super(Side.LIGHT, 0, Title.The_Hidden_Path, ExpansionSet.SET_26, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Mining Village, Safehouse, Underground Corridor, and Fallen Order. For remainder of game, you may not deploy <> locations or Jedi (except Jedi survivors). Weapon Levitation may not steal weapons. Once per turn, may [download] a Jabiim location. While this side up, you may not play Nabrun Leids. Your Force drains at Mapuzo sites are -1. Once per turn, may [download] a holocron. Flip this card if Jedi occupy two non-Mapuzo sites.");
        addIcons(Icon.VIRTUAL_SET_26);
    }

@Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Mining_Village, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Mining Village to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Safehouse, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Safehouse to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Underground_Corridor, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Underground Corridor to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Fallen_Order, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Fallen Order to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        Filter genericLocations = Filters.and(Filters.generic, Filters.location);
        Filter jediExceptJediSurvivors = Filters.and(Filters.Jedi, Filters.not(Filters.Jedi_Survivor));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        // For remainder of game
        modifiers.add(new MayNotDeployModifier(self, Filters.or(genericLocations, jediExceptJediSurvivors), playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Weapon_Levitation, ModifyGameTextType.WEAPON_LEVITATION_MAY_NOT_STEAL_WEAPONS));

        // While this side up
        modifiers.add(new MayNotPlayModifier(self, Filters.Nabrun_Leids, playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.Mapuzo_site, -1, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.THE_HIDDEN_PATH__DOWNLOAD_LOCATION;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Deploy a Jabiim location");
            action.setActionMsg("Deploy a Jabiim location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Jabiim_location, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.THE_HIDDEN_PATH__DOWNLOAD_HOLOCRON;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);

            action.setText("Deploy a holocron from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.holocron, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.occupiesWith(game, self, playerId, 2, Filters.and(Filters.not(Filters.Mapuzo_location), Filters.site), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Jedi)) {

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
