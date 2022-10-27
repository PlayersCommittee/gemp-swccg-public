package com.gempukku.swccgo.cards.set601.light;

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
 * Title: Don't Underestimate Our Chances (V)
 */
public class Card601_258 extends AbstractUsedOrStartingInterrupt {
    public Card601_258() {
        super(Side.LIGHT, 4, Title.Dont_Underestimate_Our_Chances, Uniqueness.UNRESTRICTED, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Stand-by alert. Death Star approaching. Estimated time to firing range, fifteen minutes.'");
        setGameText("USED: Initiate a battle for free. " +
                "STARTING: If Dantooine Operations on table, deploy from Reserve Deck Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile.");
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
        if (GameConditions.canSpot(game, self, Filters.Dantooine_Base_Operations)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Deploy Operations Center, Dantooine Engineering Corps, and up to two Effects that deploy on table, are always immune to Alter, and have no deploy cost");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title("Dantooine: Base - Operations Center"),  true, false));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckEffect(action, Filters.title("Dantooine Engineering Corps"),  true, false));
                            action.appendEffect(
                                    new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.immune_to_Alter, Filters.deploysForFree,
                                            Filters.or(Filters.gameTextContains("deploy on table"), Filters.gameTextContains("deploy on your side of table"))), 1, 2, true, false));
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
