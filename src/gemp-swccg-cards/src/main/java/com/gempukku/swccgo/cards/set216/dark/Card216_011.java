package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 16
 * Type: Objective
 * Title: On The Verge Of Greatness / Taking Control Of The Weapon
 */
public class Card216_011 extends AbstractObjective {
    public Card216_011() {
        super(Side.DARK, 0, Title.On_The_Verge_Of_Greatness, ExpansionSet.SET_16, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy [Set 16] Death Star and Scarif systems, Citadel Tower, and Shield Gate. For remainder of game, you may not deploy Endor Shield, non-Imperial characters, or non-Imperial starships. Once per turn, may [download] a Scarif battleground. Your non-unique Imperials, vehicles, and capital starships are deploy -1 (-2 if a Star Destroyer). Non-unique Imperials are forfeit +1. Flip this card if Krennic or Tarkin at a Scarif battleground site and Death Star orbiting Scarif.");
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.VIRTUAL_SET_16, Filters.Death_Star_system), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Set 16] Death Star system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Scarif_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Scarif system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Scarif_Citadel_Tower), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Citadel Tower to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Shield_Gate, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Shield Gate to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ON_THE_VERGE_OF_GREATNESS__DEPLOY_SCARIF_BATTLEGROUND;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Scarif battleground");
            action.setActionMsg("Deploy a Scarif battleground from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Scarif_location, Filters.battleground), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Endor_Shield, Filters.and(Filters.character, Filters.not(Filters.Imperial)), Filters.and(Filters.starship, Filters.not(Filters.Imperial_starship))), playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new DeployCostModifier(self, Filters.and(Filters.your(playerId), Filters.non_unique, Filters.or(Filters.Imperial, Filters.vehicle, Filters.capital_starship)), new CardMatchesEvaluator(-1, -2, Filters.Star_Destroyer)), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ForfeitModifier(self, Filters.and(Filters.non_unique, Filters.Imperial), 1), null));
        return action;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.at(Filters.Scarif_battleground_site), Filters.or(Filters.Krennic, Filters.Tarkin)))
                && GameConditions.canSpot(game, self, Filters.and(Filters.Death_Star_system, Filters.isOrbiting(Title.Scarif)))) {

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
