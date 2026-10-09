package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ConvertLocationResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Objective
 * Title: More Systems Will Rally To Our Cause / The Galaxy Torn Apart
 */
public class Card227_011 extends AbstractObjective {
    public Card227_011() {
        super(Side.DARK, 0, "More Systems Will Rally To Our Cause", ExpansionSet.SET_27, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy [Separatist] Geonosis system, a Geonosis site, a [Clone Army] system, and Droid Racks. Stack six (••• •••) cards from outside your deck on here. For remainder of game, you may not deploy cards with ability except [episode I] cards. If your [Episode I] system was just converted, raise it to the top. Your (••• •••) cards stacked on Droid Racks may deploy as if from hand. Once per turn, may [download] Invisible Hand: Bridge or a [Separatist] location. Flip this card at the end of turn if your [Clone Army] systems not on table.");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
            new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.SEPARATIST, Filters.Geonosis_system), true, false) {
                @Override
                public String getChoiceText() {
                    return "Choose a [Separatist] Geonosis to deploy";
                }
            });
        action.appendRequiredEffect(
            new DeployCardFromReserveDeckEffect(action, Filters.Geonosis_site, true, false) {
                @Override
                public String getChoiceText() {
                    return "Choose a Geonosis site to deploy";
                }
            });
        action.appendRequiredEffect(
            new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.CLONE_ARMY, Filters.system), true, false) {
                @Override
                public String getChoiceText() {
                    return "Choose a [Clone Army] system to deploy";
                }
            });
        action.appendRequiredEffect(
            new DeployCardFromReserveDeckEffect(action, Filters.Droid_Racks, true, false) {
                @Override
                public String getChoiceText() {
                    return "Choose a Droid Racks to deploy";
                }
            });
        action.appendRequiredEffect(
            new StackCardsFromOutsideDeckEffect(action, playerId, 6, 6, self, false, Filters.restricted_6));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Filters.not(Icon.EPISODE_I), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), self.getOwner()));
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.restricted_6, Filters.stackedOn(self, Filters.Droid_Racks))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MORE_SYSTEMS_WILL_RALLY_TO_OUR_CAUSE__DEPLOY_BATTLEGROUND_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            Filter systemFilter = Filters.and(Filters.your(self), Filters.or(Icon.CLONE_ARMY, Icon.SEPARATIST), Filters.system);

            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Separatist_Command_Center)
                || GameConditions.canSpotLocation(game, systemFilter)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a site from Reserve Deck");
                action.setActionMsg("Deploy Invisible Hand: Bridge, Separatist Command Center, or a battleground site related to your [Separatist] or [Clone Army] system from Reserve Deck");

                Filter siteFilter = Filters.none;
                for(PhysicalCard system: Filters.filterTopLocationsOnTable(game, systemFilter)) {
                    siteFilter = Filters.or(siteFilter, Filters.relatedLocationEvenWhenNotInPlay(system));
                }

                // Update usage limit(s)
                action.appendUsage(
                    new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Invisible_Hand_Bridge, Filters.Separatist_Command_Center, Filters.and(Filters.site, siteFilter)), Filters.or(Filters.Invisible_Hand_Bridge, Filters.Separatist_Command_Center, Filters.battleground), true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
            && GameConditions.canBeFlipped(game, self)
            && !GameConditions.canSpot(game, self, 1, Filters.and(Filters.your(playerId), Icon.CLONE_ARMY, Filters.system))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                new FlipCardEffect(action, self));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_3;

        if (TriggerConditions.justConvertedLocation(game, effectResult)) {
            PhysicalCard newLocation = ((ConvertLocationResult)effectResult).getNewLocation();
            PhysicalCard oldLocation = ((ConvertLocationResult)effectResult).getOldLocation();
            if (oldLocation != null
                && Filters.and(Filters.your(self.getOwner()), Icon.EPISODE_I, Filters.system).accepts(game, oldLocation)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Raise converted location to top");
                action.setPerformingPlayer(self.getOwner());
                action.appendEffect(
                    new ConvertLocationByRaisingToTopEffect(action, newLocation, true));
                actions.add(action);
            }
        }

        return actions;
    }
}
