package com.gempukku.swccgo.cards.set208.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithBottomCardOfReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 8
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Ice Plains (5th Marker) (V)
 */
public class Card208_049 extends AbstractSite {
    public Card208_049() {
        super(Side.DARK, Title.Ice_Plains, Title.Hoth);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Once per game, may exchange an Interrupt from hand with bottom card of your Reserve Deck.");
        setLocationLightSideGameText("Ice Storm is canceled at Hoth sites.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_8);
        addKeywords(Keyword.MARKER_5);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HOTH_ICE_PLAINS__EXCHANGE_INTERRUPT_IN_HAND_WITH_BOTTOM_CARD_OF_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasInHand(game, playerOnDarkSideOfLocation, Filters.Interrupt)
                && GameConditions.hasReserveDeck(game, playerOnDarkSideOfLocation)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card in hand with bottom of Reserve Deck");
            action.setActionMsg("Exchange an Interrupt in hand with bottom card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithBottomCardOfReserveDeckEffect(action, playerOnDarkSideOfLocation, Filters.Interrupt));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        Filter iceStormAtHothSite = Filters.and(Filters.Ice_Storm, Filters.at(Filters.Hoth_site));

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, iceStormAtHothSite)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, iceStormAtHothSite, Title.Ice_Storm);
            return Collections.singletonList(action);
        }
        return null;
   }
}