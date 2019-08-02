package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ResetDefenseValueModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.TargetingEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virutal Set 11
 * Type: Interrupt
 * Subtype: Used or Lost
 * Title: Force Projection
 */

public class Card211_051 extends AbstractUsedOrLostInterrupt {
    public Card211_051() {
        super(Side.LIGHT, 5, "Force Projection", Uniqueness.UNIQUE);
        setLore("");
        setGameText("USED: If [Episode VII] Luke at a site, target all your characters at one other site. They are defense value = 5 this turn. LOST: Once per game, if opponent just drew battle destiny and Luke is out of play, draw destiny and subtract that amount from opponent's total battle destiny.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.FORCE_PROJECTION_SUBTRACT_DESTINY;
        final String opponent = game.getOpponent(playerId);


        // LOST: Once per game, if opponent just drew battle destiny and Luke is out of play, draw destiny and
        // subtract that amount from opponent's total battle destiny.

        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isOutOfPlay(game, Filters.Luke)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Draw destiny to subtract");
            action.setActionMsg("Draw destiny and subtract it from opponent's total battle destiny");

            // Pay costs
            action.appendUsage(
                    new OncePerGameEffect(action));

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {

                            // Perform result(s)
                            action.appendEffect(new DrawDestinyEffect(action, playerId, 1) {

                                @Override
                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {

                                    // If we still have a total destiny (not canceled), subtract it now!
                                    if (totalDestiny != null && totalDestiny > 0) {
                                        Float subtractAmount = -1 * totalDestiny;
                                        action.appendEffect(new ModifyTotalBattleDestinyEffect(action, opponent, subtractAmount));
                                    }
                                }
                            });

                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // USED: If [Episode VII] Luke at a site, target all your characters at one other site.
        // They are defense value = 5 this turn.

        Filter ep7LukeAtSite = Filters.and(Filters.Luke, Icon.EPISODE_VII, Filters.at(Filters.site));

        Filter yourCharacters = Filters.and(Filters.character, Filters.your(self));
        Filter otherSites = Filters.and(Filters.site, Filters.not(Filters.sameSiteAs(self, Filters.Luke)));
        final Filter otherSitesWithYourCharacters = Filters.and(otherSites, Filters.sameSiteAs(self, yourCharacters));

        // Check condition(s)
        if (GameConditions.canSpot(game, self, 1, ep7LukeAtSite) &&
                GameConditions.canSpot(game, self, 1, otherSitesWithYourCharacters)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.LOST);
            action.setText("Make characters defense value = 5");
            action.setActionMsg("Make your characters at another site defense value = 5");

            // Allow Responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {

                            // Choose target(s)
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose other site", otherSitesWithYourCharacters) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard otherSite) {
                                            if (otherSite != null) {

                                                // Your characters at the targeted site
                                                Filter charactersAtSite = Filters.and(
                                                        Filters.your(self),
                                                        Filters.character,
                                                        Filters.atSameSite(otherSite)
                                                );

                                                String actionMsg = "Force Projection makes characters at " + GameUtils.getCardLink(otherSite) + " defense value = 5";

                                                // Apply Effect
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new ResetDefenseValueModifier(self, charactersAtSite, 5), actionMsg)
                                                );
                                            }
                                        }
                                    }
                            );

                        }
                    });


            actions.add(action);
        }

        return actions;
    }

}