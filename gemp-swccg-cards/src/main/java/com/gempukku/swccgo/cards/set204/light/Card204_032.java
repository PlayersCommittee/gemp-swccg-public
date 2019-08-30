package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 4
 * Type: Objective
 * Title: Old Allies / We Need Your Help
 */
public class Card204_032 extends AbstractObjective {
    public Card204_032() {
        super(Side.LIGHT, 0, Title.Old_Allies);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Jakku system and Niima Outpost Shipyard (with [Episode VII] Falcon there). May deploy Graveyard Of Giants. For remainder of game, you may not deploy Luke, Jedi or Harc Seff. Your Destiny is suspended. Opponent's [Reflections II] Objective targets Rey Instead Of Luke. While Rey at a battleground site, Visage Of The Emperor is suspended. While this side up, once per turn, may [download] a Jakku location. Flip this card if you control Jakku system and occupy two Jakku battleground sites (or vice versa).");
        addIcons(Icon.PREMIUM, Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Jakku_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Jakku system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Niima_Outpost_Shipyard, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Niima Outpost Shipyard to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_VII, Filters.Falcon), Filters.Niima_Outpost_Shipyard, true, DeploymentRestrictionsOption.allowToDeployLandedToExteriorSites(), false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Episode VII] Falcon to deploy to Niima Outpost Shipyard";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.Graveyard_Of_Giants, 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Graveyard Of Giants to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Luke, Filters.Jedi, Filters.Harc), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new SuspendsCardModifier(self, Filters.Your_Destiny), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ModifyGameTextModifier(self, Filters.and(Filters.opponents(self), Filters.Objective, Icon.REFLECTIONS_II), ModifyGameTextType.REFLECTIONS_II_OBJECTIVE__TARGETS_REY_INSTEAD_OF_LUKE), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new SuspendsCardModifier(self, Filters.Visage_Of_The_Emperor, new AtCondition(self, Filters.Rey, Filters.battleground_site)), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OLD_ALLIES__DOWNLOAD_JAKKU_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Jakku location from Reserve Deck");
            action.setActionMsg("Deploy a Jakku location from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Jakku_location, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && ((GameConditions.controls(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Jakku_system)
                && GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Jakku_battleground_site))
                || (GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Jakku_system)
                && GameConditions.controls(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Jakku_battleground_site)))) {

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