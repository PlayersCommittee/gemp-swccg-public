package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 9
 * Type: Location
 * Subtype: Site
 * Title: Xizor's Palace: Sewer
 */
public class Card209_051 extends AbstractSite {
    public Card209_051() {
        super(Side.DARK, Title.Sewer, Title.Coruscant);
        setLocationDarkSideGameText("Once per game, may exchange an alien in hand with a non-spy Black Sun Agent in Lost Pile.");
        setLocationLightSideGameText("Once per game, if you control, may retrieve a Corellian.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addKeywords(Keyword.XIZORS_PALACE_SITE);
        addIcons(Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.XIZORS_PALACE_SEWER__EXCHANGE_ALIEN_FOR_BLACK_SUN_AGENT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerOnDarkSideOfLocation)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange card in hand for card in Lost Pile");
            action.setActionMsg("Exchange an alien in hand for a non-spy Black Sun Agent in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(
                    new ExchangeCardInHandWithCardInLostPileEffect(action, playerOnDarkSideOfLocation, Filters.alien, Filters.and(Filters.not(Filters.spy), Filters.Black_Sun_agent))
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        GameTextActionId gameTextActionId = GameTextActionId.XIZORS_PALACE_SEWER__RETRIEVE_CORELLIAN;

        if(GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.controls(game, playerOnLightSideOfLocation, self)){
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve a Corellian");
            action.setActionMsg("Retrieve a Corellian");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerOnLightSideOfLocation, Filters.Corellian));
            return Collections.singletonList(action);
        }
        return null;
    }
}