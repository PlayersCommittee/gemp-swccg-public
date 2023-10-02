package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PayRelocateBetweenLocationsCostEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Effect
 * Title: Vader's Machination
 */
public class Card222_016 extends AbstractNormalEffect {
    public Card222_016() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Vader's Machination", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setLore("Join me, and together we can rule the galaxy as father and son.");
        setGameText("If Vader's Malediction on table, deploy on table. " +
                "Once per turn, may [download] a Mustafar location or a lightsaber. " +
                "At start of opponent's control phase, " +
                "Vader may relocate between [Cloud City] Chasm Walkway and opponent's battleground site as a regular move (for free). [Immune to Alter.]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_22);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.title(Title.Vaders_Malediction));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.VADERS_AMBITION__DOWNLOAD_A_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a Mustafar location or a lightsaber from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Mustafar_location, Filters.lightsaber), true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        // Card action 1
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Need to find Vader and Chasm Walkway
        final PhysicalCard vaderCard = Filters.findFirstActive(game, self, Filters.Vader);
        final PhysicalCard chasmWalkWayCard = Filters.findFirstActive(game, self, Filters.Chasm_Walkway);
        Filter opponentsBattlegroundSite = Filters.and(Filters.opponents(playerId), Filters.battleground_site);

        // This trigger is only valid if Vader and Chasm Walkway is on table
        if (vaderCard != null && chasmWalkWayCard != null) {
            // Check condition(s)
            if (TriggerConditions.isStartOfOpponentsPhase(game, self, effectResult, Phase.CONTROL)
                    && GameConditions.canTarget(game, self, Filters.Vader)
                    && GameConditions.canTarget(game, self, Filters.Chasm_Walkway)
                    && GameConditions.isAtLocation(game, vaderCard, Filters.or(opponentsBattlegroundSite, Filters.Chasm_Walkway))) {
                Filter siteToRelocateTo;

                if (GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.at(chasmWalkWayCard)))) {
                    siteToRelocateTo = Filters.and(opponentsBattlegroundSite, Filters.locationCanBeRelocatedTo(vaderCard, 0));
                } else {
                    siteToRelocateTo = Filters.and(chasmWalkWayCard, Filters.locationCanBeRelocatedTo(vaderCard, 0));
                }

                if (GameConditions.canSpotLocation(game, siteToRelocateTo)) {
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Relocate Vader to site");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, self.getOwner(), "Choose site to relocate Vader to", siteToRelocateTo) {
                                @Override
                                protected void cardSelected(final PhysicalCard selectedCard) {
                                    action.addAnimationGroup(selectedCard);
                                    // Pay cost(s)
                                    action.appendCost(
                                            new PayRelocateBetweenLocationsCostEffect(action, playerId, vaderCard, selectedCard, 0));
                                    // Allow response(s)
                                    action.allowResponses("Relocate Vader to " + GameUtils.getCardLink(selectedCard),
                                            new UnrespondableEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    // Perform result(s)
                                                    action.appendEffect(
                                                            new RelocateBetweenLocationsEffect(action, vaderCard, selectedCard, true));
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
