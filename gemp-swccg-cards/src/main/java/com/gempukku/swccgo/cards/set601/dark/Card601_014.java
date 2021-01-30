package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Skyhook Platform
 */
public class Card601_014 extends AbstractSite {
    public Card601_014() {
        super(Side.DARK, Title.Skyhook_Platform, Title.Kashyyyk);
        setLocationDarkSideGameText("May place a card stacked here in owner's Lost Pile to draw two cards from Reserve Deck.");
        setLocationLightSideGameText("Opponent's Kashyyyk locations are immune to Revolution and Expand The Empire.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.BLOCK_8);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.hasReserveDeck(game, playerOnDarkSideOfLocation)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw two cards from Reserve Deck");
            action.appendCost(new ChooseStackedCardEffect(action, playerOnDarkSideOfLocation, self) {
                @Override
                protected void cardSelected(PhysicalCard selectedCard) {
                    action.appendEffect(new PutStackedCardInLostPileEffect(action, playerOnDarkSideOfLocation, selectedCard, false));
                }
            });
            // Perform result(s)
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerOnDarkSideOfLocation, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Kashyyyk_site), Title.Revolution));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Kashyyyk_site), Title.Expand_The_Empire));
        return modifiers;
    }
}