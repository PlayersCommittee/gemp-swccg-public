package com.gempukku.swccgo.cards.set226.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToLocationModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Effect
 * Title: Launching The Assault (V)
 */
public class Card226_020 extends AbstractNormalEffect {
    public Card226_020() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Launching_The_Assault, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("By recruiting the Mon Calamari, a race of master shipwrights, the Rebel starfleet gained capital starships rivaling the dreaded Imperial Star Destroyers.");
        setGameText("If your Endor (or Rebel Base) location on table, deploy on table. May [download] Home One or Sullust. Home One is deploy = 8 to an [Endor] or [Death Star II] system and its power may not be reduced. Once per game, Home One may move as a 'react' to [Death Star II] Falcon's location (or vice versa). [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_26);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpotFromTopLocationsOnTable(game, Filters.and(Filters.your(playerId), Filters.or(Filters.Endor_location, Filters.Rebel_Base_location)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();

        modifiers.add(new ResetDeployCostToLocationModifier(self, Filters.Home_One, 8, Filters.and(Filters.or(Icon.ENDOR, Icon.DEATH_STAR_II), Filters.system)));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Home_One, self.getOwner()));
        modifiers.add(new MayNotHavePowerReducedModifier(self, Filters.Home_One, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.LAUNCHING_THE_ASSAULT_V__DOWNLOAD_HOME_ONE_OR_SULLUST;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.HOME_ONE)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Sullust)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card from Reserve Deck");
            action.setActionMsg("Deploy Home One or Sullust from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Home_One, Filters.Sullust_system), true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter filterHomeOne = Filters.Home_One;
        Filter filterDS2Falcon = Filters.and(Icon.DEATH_STAR_II, Filters.Falcon);
        GameTextActionId gameTextActionId = GameTextActionId.LAUNCHING_THE_ASSAULT_V__REACT_TO_FALCON;

        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        final OptionalGameTextTriggerAction actionHomeOneToFalcon = getShipToShipReactAction(playerId, opponent, game, effectResult, self, gameTextSourceCardId, gameTextActionId, "Home One", filterHomeOne, filterDS2Falcon);
        if (actionHomeOneToFalcon != null)
            actions.add(actionHomeOneToFalcon);

        final OptionalGameTextTriggerAction actionFalconToHomeOne = getShipToShipReactAction(playerId, opponent, game, effectResult, self, gameTextSourceCardId, gameTextActionId, "Falcon", filterDS2Falcon, filterHomeOne);
        if (actionFalconToHomeOne != null)
            actions.add(actionFalconToHomeOne);

        return actions;
    }

    protected OptionalGameTextTriggerAction getShipToShipReactAction(String playerId, String opponent, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId, GameTextActionId gameTextActionId, String reactingShipText, Filter reactingShip, Filter otherShip)
    {
        Filter validReactingShip = Filters.and(reactingShip, Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0 , false));

        if ((TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.sameLocationAs(self, otherShip))
                || TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.sameLocationAs(self, otherShip)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTarget(game, self, validReactingShip)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Move " + reactingShipText +" as 'react'");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose " + reactingShipText, validReactingShip) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard reactingShipCard) {
                            action.addAnimationGroup(reactingShipCard);
                            // Allow response(s)
                            action.allowResponses("Move " + GameUtils.getCardLink(reactingShipCard) + " as a 'react'",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MoveAsReactEffect(action, reactingShipCard, false));
                                        }
                                    }
                            );
                        }
                    }
            );
            return action;
        }
        else {
            return null;
        }        
    }
}
