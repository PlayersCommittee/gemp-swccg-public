package com.gempukku.swccgo.cards.set110.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Jabba's Palace)
 * Type: Objective
 * Title: Court Of The Vile Gangster / I Shall Enjoy Watching You Die
 */
public class Card110_006 extends AbstractObjective {
    public Card110_006() {
        super(Side.DARK, 0, Title.Court_Of_The_Vile_Gangster);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Audience Chamber, Great Pit Of Carkoon and Dungeon. While this side up, once during each of your deploy phases, may deploy one docking bay or [Independent Starship] starship from Reserve Deck; reshuffle. Bounty Hunters are forfeit +2 and immune to Goo Nee Tay. You may not play Scanning Crew. Each player loses 1 Force at end of each of their deploy phases unless that player has a non-droid character at a Tatooine battleground site. Flip this card if you have two captives (or a captive of ability > 2) at any Jabba's Palace site(s).");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Audience_Chamber, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Audience Chamber to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Great_Pit_Of_Carkoon, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Great Pit Of Carkoon to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Dungeon, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Dungeon to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.COURT_OF_THE_VILE_GANGSTER__DOWNLOAD_DOCKING_BAY_OR_INDEPENDENT_STARSHIP;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter deployFilter = (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.COURT_OF_THE_VILE_GANGSTER__MAY_NOT_DEPLOY_STARSHIPS) ?
                            Filters.docking_bay : Filters.or(Filters.docking_bay, Filters.and(Icon.INDEPENDENT, Filters.starship)));

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a docking bay or [Independent] starship from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, deployFilter, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.bounty_hunter, 2));
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.bounty_hunter, Filters.Goo_Nee_Tay, Filters.any));
        modifiers.add(new MayNotPlayModifier(self, Filters.Scanning_Crew, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.DEPLOY)) {
            String currentPlayer = game.getGameState().getCurrentPlayerId();
            if (!GameConditions.canSpot(game, self, Filters.and(Filters.owner(currentPlayer), Filters.non_droid_character, Filters.at(Filters.Tatooine_battleground_site)))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make " + currentPlayer + " lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, currentPlayer, 1));
                actions.add(action);
            }
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.captive, Filters.abilityMoreThan(2), Filters.at(Filters.Jabbas_Palace_site)))
                || GameConditions.canSpot(game, self, 2, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.captive, Filters.at(Filters.Jabbas_Palace_site))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            actions.add(action);
        }

        return actions;
    }
}