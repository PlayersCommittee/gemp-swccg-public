package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattleCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: Site
 * Title: Swamp
 */
public class Card7_292 extends AbstractSite {
    public Card7_292() {
        super(Side.DARK, "Swamp", Uniqueness.DIAMOND_1);
        setLocationDarkSideGameText("May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. Once during each of your deploy phases, may deploy one creature here from Reserve Deck; reshuffle.");
        setLocationLightSideGameText("May not deploy to Bespin, Coruscant, Hoth, Kessel or Tatooine. To initiate battle here, you must use +3 Force. You may not 'react' to or from here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.SWAMP);
        addMayNotBePartOfSystem(Title.Bespin, Title.Coruscant, Title.Hoth, Title.Kessel, Title.Tatooine, Title.Ahch_To);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SWAMP__DOWNLOAD_CREATURE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a creature from Reserve Deck");
            action.setActionMsg("Deploy a creature from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.creature, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattleCostModifier(self, Filters.here(self), 3, playerOnLightSideOfLocation));
        modifiers.add(new MayNotReactToLocationModifier(self, playerOnLightSideOfLocation));
        modifiers.add(new MayNotReactFromLocationModifier(self, playerOnLightSideOfLocation));
        return modifiers;
    }
}