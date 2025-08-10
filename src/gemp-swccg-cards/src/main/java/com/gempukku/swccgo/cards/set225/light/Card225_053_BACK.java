package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReturnCardToHandFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 25
 * Type: Objective
 * Title: Mind What You Have Learned / Save You It Can (V)
 */
public class Card225_053_BACK extends AbstractObjective {
    public Card225_053_BACK() {
        super(Side.LIGHT, 7, Title.Save_You_It_Can, ExpansionSet.SET_25, Rarity.V);
        setGameText("Immediately return Luke and any cards on him to owner's hand. While this side up, may deploy Luke (deploy -3) and/or a weapon on him as a 'react.' May place a completed Jedi Test (even if suspended) out of play to take a [Cloud City] Rebel into hand from Lost Pile. When your [Cloud City] Rebel Force drains at a battleground site, unless a captive on table, lost Force must come from top of Reserve Deck if possible.");
        addIcons(Icon.SPECIAL_EDITION, Icon.DAGOBAH, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        final PhysicalCard lukeCard = Filters.findFirstActive(game, self, Filters.Luke);

        // Check condition(s)
        if (TriggerConditions.cardFlipped(game, effectResult, self)
                && lukeCard != null) {            

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Return Luke and cards on him to hand");
            action.setActionMsg("Return Luke and any cards on him to owner's hand");
            // Perform result(s)
            action.appendEffect(
                    new ReturnCardToHandFromTableEffect(action, lukeCard, Zone.HAND, Zone.HAND));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        String playerId = self.getOwner();

        // For remainder of game
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.character, Filters.at(Filters.non_battleground_location)), Filters.Sense));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Dagobah_location, playerId));

        // While this side up
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy Luke (deploy -3) as a 'react'", playerId, Filters.Luke, Filters.any, -3));
        modifiers.add(new MayDeployOtherCardsAsReactToTargetModifier(self, "Deploy a weapon on Luke as a 'react'", playerId, Filters.weapon, Filters.Luke));

        // Filter for a battleground site where there is a [Cloud City] Rebel
        Filter filterCloudCityRebelBattlegroundSite = Filters.and(Filters.sameSiteAs(self, Filters.and(Icon.CLOUD_CITY, Filters.Rebel)), Filters.battleground);

        Condition duringCCRebelForceDrainAtBattlegroundSite = new DuringForceDrainAtCondition(filterCloudCityRebelBattlegroundSite);
        Condition unlessCaptiveOnTable = new UnlessCondition(new OnTableCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive));
        Condition conditionsForReserveDeckForceLoss = new AndCondition(duringCCRebelForceDrainAtBattlegroundSite, unlessCaptiveOnTable);
        modifiers.add(new SpecialFlagModifier(self, conditionsForReserveDeckForceLoss, ModifierFlag.FORCE_DRAIN_LOST_FROM_RESERVE_DECK, game.getOpponent(self.getOwner())));
        
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MIND_WHAT_YOUR_HAVE_LEARNED_V__DOWNLOAD_BESPIN_LOCATION;

        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Bespin location from Reserve Deck");
            action.setActionMsg("Deploy Bespin system or a Cloud City site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Bespin_system, Filters.Cloud_City_site), true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.SAVE_YOU_IT_CAN__UPLOAD_REBEL_FROM_LOST_PILE;

        if (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_SUSPENDED, Filters.completed_Jedi_Test)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

            action.setText("Place completed Jedi Test out of play");
            action.setActionMsg("Place a completed Jedi Test out of play to take a [Cloud City] Rebel into hand from Lost Pile");

            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose a completed Jedi Test", SpotOverride.INCLUDE_SUSPENDED, Filters.completed_Jedi_Test) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            // Pay cost(s)
                            action.appendCost(
                                    new PlaceCardOutOfPlayFromTableEffect(action, selectedCard));
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.and(Icon.CLOUD_CITY, Filters.Rebel), false));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}