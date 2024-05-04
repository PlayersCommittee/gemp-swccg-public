package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rapid Deployment
 */
public class Card8_058 extends AbstractLostInterrupt {
    public Card8_058() {
        super(Side.LIGHT, 5, "Rapid Deployment", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("General Solo's strike team was made up of the Alliance's finest ground troops.");
        setGameText("At the beginning of your deploy phase, if you occupy an Endor or rebel base site, deploy up to 5 troopers there from Reserve Deck (those troopers deploy -1) and reshuffle. You may not deploy any other characters or starships this turn.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.RAPID_DEPLOYMENT__DOWNLOAD_TROOPERS;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            Filter siteFilter = Filters.and(Filters.or(Filters.Endor_site, Filters.Rebel_Base_site), Filters.occupies(playerId));
            if (GameConditions.canSpotLocation(game, siteFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Deploy troopers from Reserve Deck");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose site to deploy troopers", siteFilter) {
                            @Override
                            protected void cardSelected(final PhysicalCard site) {
                                action.addAnimationGroup(site);
                                // Allow response(s)
                                action.allowResponses("Deploy troopers from Reserve Deck to " + GameUtils.getCardLink(site),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DeployCardsToLocationFromReserveDeckEffect(action, Filters.trooper, 1, 5, site, -1, true));
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new MayNotDeployModifier(self, Filters.or(Filters.character, Filters.starship), playerId), null));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}