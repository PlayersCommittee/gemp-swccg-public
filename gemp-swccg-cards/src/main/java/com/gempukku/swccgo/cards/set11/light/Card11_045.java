package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExchangeStackedCardWithTopCardOfLostPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Podracer Bay
 */
public class Card11_045 extends AbstractSite {
    public Card11_045() {
        super(Side.LIGHT, Title.Podracer_Bay, Title.Tatooine);
        setLocationDarkSideGameText("If you control, once per turn may exchange your top race destiny with top card of Lost Pile.");
        setLocationLightSideGameText("If you control, once per turn may exchange your top race destiny with top card of Lost Pile.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.hasLostPile(game, playerOnDarkSideOfLocation)
                && GameConditions.hasRaceDestiny(game, playerOnDarkSideOfLocation)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange top race destiny with top of Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeStackedCardWithTopCardOfLostPileEffect(action, Filters.topRaceDestinyForPlayer(playerOnDarkSideOfLocation)) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.hasLostPile(game, playerOnLightSideOfLocation)
                && GameConditions.hasRaceDestiny(game, playerOnLightSideOfLocation)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange top race destiny with top of Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeStackedCardWithTopCardOfLostPileEffect(action, Filters.topRaceDestinyForPlayer(playerOnLightSideOfLocation)) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}