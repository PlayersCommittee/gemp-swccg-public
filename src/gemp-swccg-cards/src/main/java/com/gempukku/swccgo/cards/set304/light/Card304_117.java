package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: He's Crazy
 */
public class Card304_117 extends AbstractNormalEffect {
    public Card304_117() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Hes_Crazy, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Sqygorn's temper has often led him to being call crazy, behind his back of course. Course, his temper has also caused him to rush into situations he can't handle.");
        setGameText("Deploy on table. Once per turn, may [download] Sqygorn's Blaster. At start of opponent's control phase, may relocate Sqygorn to same site as a [CSP] character. May place this Effect out of play to deploy Sqygorn's blaster from Lost Pile. Immune to Alter.");
        addIcons(Icon.GREAT_HUTT_EXPANSION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.HES_CRAZY__DOWNLOAD_SQYGORNS_BLASTER;
        GameTextActionId gameTextActionId2 = GameTextActionId.HES_CRAZY__DOWNLOAD_SQYGORNS_BLASTER_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.SQYGORNS_BLASTER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Sqygorn's Blaster from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Sqygorn_Blaster, true));
            actions.add(action);
        }

        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId2, Persona.SQYGORNS_BLASTER)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place out of play to deploy Sqygorn's Blaster from Lost Pile");
            action.setActionMsg("Deploy Sqygorn's Blaster from Lost Pile");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromLostPileEffect(action, Filters.Sqygorn_Blaster, false));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Need to find Sqygorn
        final PhysicalCard sqygornCard = Filters.findFirstActive(game, self, Filters.Sqygorn);

        // This trigger is only valid if Sqygorn is on table
        if (sqygornCard != null)
        {
            // Check condition(s)
            if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                    && GameConditions.canTarget(game, self, Filters.Sqygorn)
                    && GameConditions.isPresentAt(game, sqygornCard, Filters.site))
            {
                Filter siteToRelocateTo = Filters.and(Filters.site, Filters.locationCanBeRelocatedTo(sqygornCard, 0), Filters.occupiesWith(game.getOpponent(playerId), self, Filters.CSP_character));
                if (GameConditions.canSpotLocation(game, siteToRelocateTo)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Relocate Sqygorn to same site as a CSP character");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, self.getOwner(), "Choose site to relocate Sqygorn", siteToRelocateTo) {
                                @Override
                                protected void cardSelected(final PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, sqygornCard, selectedCard, 0));
                                    // Allow response(s)
                                    action.allowResponses("Relocate Sqygorn to " + GameUtils.getCardLink(selectedCard),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RelocateBetweenLocationsEffect(action, sqygornCard, selectedCard));
                                                }
                                            }
                                    );
                                }
                            }
                    );
                    actions.add(action);
                }
            }
            return actions;
        }
        return null;
    }
}