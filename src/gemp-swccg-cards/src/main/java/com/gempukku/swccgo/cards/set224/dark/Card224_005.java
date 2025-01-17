package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Location
 * Subtype: System
 * Title: Ord Mantell (V)
 */
public class Card224_005 extends AbstractSystem {
    public Card224_005() {
        super(Side.DARK, Title.Ord_Mantell, 7, ExpansionSet.SET_24, Rarity.V);
        setLocationDarkSideGameText("Your [Ind] starships may move to here as a 'react.' Once per game, if you control, may [upload] Death Mark.");
        setLocationLightSideGameText("Your smugglers and [Ind] starships deploy -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.JABBAS_PALACE, Icon.PLANET, Icon.VIRTUAL_SET_24);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a 'react'", playerOnDarkSideOfLocation, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.INDEPENDENT, Filters.starship), self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ORD_MANTELL__UPLOAD_DEATH_MARK;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Death Mark into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerOnDarkSideOfLocation, Filters.Death_Mark, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.smuggler, Filters.and(Icon.INDEPENDENT, Filters.starship))), -1, self));
        return modifiers;
    }
}