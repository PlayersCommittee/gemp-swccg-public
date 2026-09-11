package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RelocatedBetweenLocationsResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Double-Crossing, No-Good Swindler
 */
public class Card5_137 extends AbstractLostInterrupt {
    public Card5_137() {
        super(Side.DARK, 3, Title.Double_Crossing_No_Good_Swindler, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("'You've got a lot of guts coming here. . . after what you pulled.'");
        setGameText("If Han and your Lando are at same site, opponent loses 3 Force. OR If Nabrun Leids just completed a transport, Nabrun is lost and you may immediately deploy cards to that site from hand (at normal use of the Force, but troopers deploy free).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s) — Han and your Lando at same site (inactive excluded via canSpot)
        Filter yourLando = Filters.and(Filters.your(self), Filters.Lando);
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Han, Filters.at(Filters.site), Filters.with(self, yourLando)))) {
            final String opponent = game.getOpponent(playerId);

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Opponent loses 3 Force");
            // Allow response(s)
            action.allowResponses("Make opponent lose 3 Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 3));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.transportCompletedBy(game, effectResult, Filters.Nabrun_Leids)) {
            final RelocatedBetweenLocationsResult relocateResult = (RelocatedBetweenLocationsResult) effectResult;
            final PhysicalCard nabrun = relocateResult.getActionSource();
            final PhysicalCard destinationSite = relocateResult.getMovedTo();
            if (nabrun == null || destinationSite == null || !Filters.site.accepts(game, destinationSite)) {
                return null;
            }

            final Filter thatSite = Filters.sameCardId(destinationSite);

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make " + GameUtils.getFullName(nabrun) + " lost and deploy to site");
            // Allow response(s) — deployment choices happen after responses resolve (hidden intent)
            action.allowResponses("Make " + GameUtils.getCardLink(nabrun) + " lost and deploy cards to " + GameUtils.getCardLink(destinationSite),
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Nabrun lost (same void→lost pattern as Oo-ta Goo-ta / Quite A Mercenary)
                            action.appendEffect(
                                    new PutCardFromVoidInLostPileEffect(action, playerId, nabrun));
                            // Troopers (yours) deploy free for the remainder of this card play
                            action.appendEffect(
                                    new AddUntilEndOfCardPlayedModifierEffect(action, self,
                                            new DeploysFreeModifier(self, Filters.and(Filters.your(self), Filters.trooper)),
                                            "Troopers deploy free"));
                            // May repeatedly deploy from hand to destination site (not as a react)
                            action.appendEffect(
                                    new DeployCardsToLocationFromHandEffect(action, playerId, Filters.any, thatSite, false));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
