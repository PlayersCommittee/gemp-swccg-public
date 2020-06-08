package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.DuringPlayersTurnNumberCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.TrueCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Objective
 * Title: Imperial Entanglements / No One To Stop Us This Time
 */
public class Card201_039 extends AbstractObjective {
    public Card201_039() {
        super(Side.DARK, 0, Title.Imperial_Entanglements);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Tatooine (with Devastator there) and a Tatooine battleground site. For remainder of game, you may not deploy Intensify The Forward Batteries, Sandwhirl, Jabba's Palace sites, non-Imperial characters, non-Imperial starships, or systems. While this side up, opponent loses no Force to Tatooine Occupation or your Force drains at Tatooine system. You may not deploy Admiral's Orders on your first turn. Once per turn, may [download] a Tatooine battleground site. Flip this card if you control three Tatooine sites and opponent controls fewer than three Tatooine sites.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.Devastator, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Devastator to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_site, Filters.battleground, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine battleground site to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Sandwhirl, Filters.Jabbas_Palace_site,
                                Filters.and(Filters.character, Filters.not(Filters.Imperial)), Filters.and(Filters.starship, Filters.not(Filters.Imperial_starship)),
                                Filters.system), playerId), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Tatooine_Occupation, opponent));
        modifiers.add(new NoForceLossFromForceDrainModifier(self, Filters.Tatooine_system, opponent));
        modifiers.add(new MayNotDeployModifier(self, Filters.Intensify_The_Forward_Batteries, new TrueCondition(), playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_ENTANGLEMENTS__DOWNLOAD_TATOOINE_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Tatooine battleground site from Reserve Deck");
            action.setActionMsg("Deploy a Tatooine battleground site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_site, Filters.battleground, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.controls(game, playerId, 3, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Tatooine_site)
                && !GameConditions.controls(game, opponent, 3, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Tatooine_site)) {

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