package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Interrupt
 * Subtype: Used or Starting
 * Title: You Overestimate Their Chances (V)
 */
public class Card601_268 extends AbstractUsedOrStartingInterrupt {
    public Card601_268() {
        super(Side.DARK, 4, Title.You_Overestimate_Their_Chances, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Evacuate? In our moment of triumph?'");
        setGameText("USED: Initiate a battle for free. " +
                "STARTING: If Ralltiir Operations on table, deploy from Reserve Deck Supply Route, [Virtual] Insignificant Rebellion, and up to two Effects that are always (Immune to Alter), have no deploy cost, and do not deploy on locations.  Place Interrupt in Lost Pile.");
        addIcons(Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.isDuringYourPhase(game, playerId, Phase.BATTLE)
            && !GameConditions.isDuringBattle(game)) {

            Collection<PhysicalCard> locations = Filters.filterTopLocationsOnTable(game, Filters.location);
            Collection<PhysicalCard> possibleBattleLocations = new LinkedList<>();

            for(PhysicalCard card: locations) {
                if (GameConditions.canInitiateBattleAtLocation(playerId, game, card, true))
                    possibleBattleLocations.add(card);
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Target a location");
            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a location to make battles free for remainder of turn", Filters.in(possibleBattleLocations)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses("Target a location to make battles free there for remainder of turn",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(
                                            new AddUntilEndOfTurnModifierEffect(action, new InitiateBattlesForFreeModifier(self, finalTarget, playerId), "makes battles free here"));
                                }
                            }
                    );
                }
            });
            return Collections.singletonList(action);
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.Ralltiir_Operations)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Supply Route, [Virtual] Insignificant Rebellion, and up to two Effects that are always (Immune to Alter), have no deploy cost, and do not deploy on locations");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title("Ralltiir: Supply Route"),  true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Insignificant_Rebellion, Filters.or(Icon.LEGACY_BLOCK_1, Icon.LEGACY_BLOCK_2, Icon.LEGACY_BLOCK_3, Icon.LEGACY_BLOCK_4, Icon.LEGACY_BLOCK_5, Icon.LEGACY_BLOCK_6, Icon.LEGACY_BLOCK_7, Icon.LEGACY_BLOCK_8, Icon.LEGACY_BLOCK_9, Icon.LEGACY_BLOCK_D)),  true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immune_to_Alter, Filters.deploysForFree,
                                            Filters.not(Filters.deploys_on_location)), 1, 2, true, false));
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, self));
                        }
                    }
            );
            return action;
        }
        return null;
    }
}
