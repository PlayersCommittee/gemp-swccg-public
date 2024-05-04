package com.gempukku.swccgo.cards.set301.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Premium Set
 * Type: Objective
 * Title: Twin Suns Of Tatooine / Well Trained In The Jedi Arts
 */
public class Card301_004 extends AbstractObjective {
    public Card301_004() {
        super(Side.DARK, 0, Title.Twin_Suns_Of_Tatooine, ExpansionSet.DEMO_DECK, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Tatooine system and a non-Jabba's Palace Tatooine site. For remainder of game, you may not deploy Jabba's Palace sites or Sandwhirl. While this side up, once per turn, may use 1 Force to [download] a Tatooine battleground site. Flip this card if you control two Tatooine battleground sites (at least one with a Dark Jedi) and occupy Tatooine system, and opponent controls no Tatooine sites.");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE, Icon.VIRTUAL_SET_P);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Tatooine_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Tatooine system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Tatooine_site, Filters.not(Filters.Jabbas_Palace_site)), false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose non-Jabba's Palace Tatooine site to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.or(Filters.Jabbas_Palace_site, Filters.Sandwhirl), playerId), null));
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TWIN_SUNS_OF_TATOOINE__DOWNLOAD_TATOOINE_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Tatooine battleground site from Reserve Deck");
            action.setActionMsg("Deploy a Tatooine battleground site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
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
                && GameConditions.controls(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Tatooine_battleground_site)
                && GameConditions.controlsWith(game, self, playerId, Filters.Tatooine_battleground_site, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Dark_Jedi)
                && GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.Tatooine_system)
                && !GameConditions.controls(game, opponent, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.Tatooine_site)) {

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