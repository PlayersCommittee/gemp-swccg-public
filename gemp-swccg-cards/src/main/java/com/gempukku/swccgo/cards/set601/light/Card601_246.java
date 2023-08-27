package com.gempukku.swccgo.cards.set601.light;

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
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 8
 * Type: Location
 * Subtype: System
 * Title: Nar Shaddaa
 */
public class Card601_246 extends AbstractSystem {
    public Card601_246() {
        super(Side.LIGHT, Title.Nar_Shaddaa, 3, ExpansionSet.LEGACY, Rarity.V);
        setLocationDarkSideGameText("Starships piloted by bounty hunters are power +1 here. All starships may move between here and Nal Hutta as a 'react'.");
        setLocationLightSideGameText("Force drain +1 here. Once per game, if you control, may [upload] Han's Toolkit.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.PLANET, Icon.LEGACY_BLOCK_8);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.starship, Filters.hasPiloting(self, Filters.bounty_hunter), Filters.here(self)), 1));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship from Nal Hutta to here as a 'react'", null,
                Filters.and(Filters.starship, Filters.at(Filters.Nal_Hutta_system)), self));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship from here to Nal Hutta as a 'react'", null,
                Filters.and(Filters.starship, Filters.here(self)), Filters.Nal_Hutta_system));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.NAR_SHADDAA__UPLOAD_HANS_TOOLKIT;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.controls(game, playerOnLightSideOfLocation, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Han's Toolkit into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerOnLightSideOfLocation, Filters.Hans_Toolkit, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}