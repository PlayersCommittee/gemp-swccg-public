package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: Site
 * Title: Invisible Hand: Bridge
 */
public class Card211_020 extends AbstractUniqueStarshipSite{
    public Card211_020() {
        super(Side.DARK, Title.Invisible_Hand_Bridge, Persona.INVISIBLE_HAND, ExpansionSet.SET_11, Rarity.V);
        setLocationDarkSideGameText("Once per game, if you control, may take Invisible Hand into hand from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("Unless your [Clone Army] character here, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.INVISIBLE_HAND_BRIDGE_UPLOAD_INVISIBLE_HAND;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.controls(game, playerOnDarkSideOfLocation, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Invisible Hand into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerOnDarkSideOfLocation, Filters.Invisible_Hand, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, self, new UnlessCondition(new HereCondition(self,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Clone_Army))), -1, playerOnLightSideOfLocation));
        return modifiers;
    }}
