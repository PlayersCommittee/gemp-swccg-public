package com.gempukku.swccgo.cards.set601.light;

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
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Objective
 * Title: Watch Your Step (V) / This Place Can Be A Little Rough (V)
 */
public class Card601_146 extends AbstractObjective {
    public Card601_146() {
        super(Side.LIGHT, 0, Title.Watch_Your_Step, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Corellia system (with an unpiloted freighter and a Corellian pilot there) and <>Spaceport City to Corellia. \n" +
                "For remainder of game, Corellia sites are immune to No Escape.  For either player to deploy a card with ability (except a Corellian) to Corellia, that player must first place a card from hand on bottom of their Used Pile. \n" +
                "While this side up, you may not Force drain at Corellia system. Once per turn, may use 1 Force to deploy a <> site from Reserve Deck; reshuffle. \n" +
                "Flip this card if you occupy Corellia system and control two Corellia battleground sites with Corellians.");
        addIcons(Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
        hideFromDeckBuilder();
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Corellia_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Corellia system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.unpiloted, Filters.freighter), Filters.Corellia_system, true,
                        DeploymentRestrictionsOption.allowToDeployUnpilotedToSystemOrSector(),false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose unpiloted freighter to deploy to Corellia system";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.Corellian, Filters.pilot), Filters.Corellia_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Corellian pilot to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToSystemFromReserveDeckEffect(action, Filters.and(Filters.uniqueness(Uniqueness.DIAMOND_1), Filters.title("Spaceport City")), Title.Corellia, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose <>Spaceport City to deploy to Corellia";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Corellia_site, Title.No_Escape));
        //TODO add extra cost to deploying a non-Corellian card with ability to Corellia
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.Corellia_system, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WATCH_YOUR_STEP_V__DEPLOY_DIAMOND_SITE_FROM_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a <> site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.uniqueness(Uniqueness.DIAMOND_1), Filters.site), true));
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
                && GameConditions.occupies(game, playerId, Filters.Corellia_system)
                && GameConditions.controlsWith(game, self, playerId, 2,
                    Filters.and(Filters.Corellia_site, Filters.battleground), SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Corellian)) {

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