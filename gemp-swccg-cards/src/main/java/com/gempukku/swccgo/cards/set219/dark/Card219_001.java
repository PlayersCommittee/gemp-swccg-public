package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerStarDestroyerEvaluator;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Objective
 * Title: A Great Tactician Creates Plans / The Result Is Often Resentment
 */
public class Card219_001 extends AbstractObjective {
    public Card219_001() {
        super(Side.DARK, 0, Title.A_Great_Tactician_Creates_Plans);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Lothal system, Advanced Projects Laboratory, Imperial Complex, and Thrawn's Art Collection. " +
                    "For remainder of game, you may not deploy Chiraneau or [Episode I] (or [Episode VII]) cards with ability or [Presence]. Once per turn, may [download] a battleground system (or a site to Lothal). " +
                    "While this side up, Imperial Star Destroyers deploy -1 (-3 if Chimaera). " +
                    "Flip this card during any deploy phase if Thrawn at a battleground and two or more artwork cards on table.");
        addIcons(Icon.VIRTUAL_SET_19);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Lothal_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Lothal system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Lothal_Advanced_Projects_Laboratory), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Lothal: Advanced Projects Laboratory to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Lothal_Imperial_Complex), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Lothal: Imperial Complex to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Thrawns_Art_Collection, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Thrawn's Art Collection to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter mayNotPlayFilter = Filters.or(Filters.Chiraneau,
                Filters.and(Filters.your(self), Filters.or(Icon.EPISODE_I, Icon.EPISODE_VII), Filters.or(Filters.hasAbilityOrHasPermanentPilotWithAbility, Icon.PRESENCE)));
        modifiers.add(new MayNotPlayModifier(self, mayNotPlayFilter, self.getOwner()));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.Imperial_starship, Filters.Star_Destroyer),
        new CardMatchesEvaluator(new PerStarDestroyerEvaluator(-1), new ConstantEvaluator( -3), Filters.Chimaera)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.A_GREAT_TACTICIAN_CREATES_PLANS__DOWNLOAD_LOCATION;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a battleground system (or a site to Lothal)");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromPileEffect(action, self.getOwner(), Zone.RESERVE_DECK, Filters.or(Filters.battleground_system,
                            Filters.and(Filters.site, Filters.deployableToSystem(self, Title.Lothal, null, false, 0))), Filters.locationAndCardsAtLocation(Filters.partOfSystem(Title.Lothal)), Filters.battleground_system, Title.Lothal, null, false, Filters.none, 0, Filters.none, null, null, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Thrawn, Filters.at(Filters.battleground)))) {
            PhysicalCard thrawnsArtCollection = Filters.findFirstActive(game, self, Filters.Thrawns_Art_Collection);
            if (thrawnsArtCollection != null && GameConditions.hasStackedCards(game, thrawnsArtCollection, 2)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
