package com.gempukku.swccgo.cards.set226.light;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 26
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Chewie's Hut
 */
public class Card226_017 extends AbstractSite {
    public Card226_017() {
        super(Side.LIGHT, "Kashyyyk: Chewie's Hut", Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLocationLightSideGameText("Deploys only as a starting location. Once per game, may [upload] a Wookiee. While Wookiee Homestead on table, no Force Drains here.");
        setLocationDarkSideGameText("");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.VIRTUAL_SET_26);
    }
    
    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        // Deploys only as a starting location.
        return GameConditions.isDuringStartOfGame(game)
                && game.getModifiersQuerying().getStartingLocation(playerId) == null
                && game.getGameState().getObjectivePlayed(playerId) == null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CHEWIES_HUT__UPLOAD_WOOKIEE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Wookiee into hand from Reserve Deck");
            action.setActionMsg("Take a Wookiee into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerOnLightSideOfLocation, Filters.Wookiee, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Condition wookieeHomesteadOnTable = new OnTableCondition(self, Filters.Wookiee_Homestead);

        modifiers.add(new MayNotForceDrainAtLocationModifier(self, wookieeHomesteadOnTable));

        return modifiers;
    }
}
