package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: Site
 * Title: Lothal: Advanced Projects Laboratory
 */
public class Card219_011 extends AbstractSite {
    public Card219_011() {
        super(Side.DARK, Title.Lothal_Advanced_Projects_Laboratory, Title.Lothal, Uniqueness.UNIQUE, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("Once per turn, may [download] TIE Defender Project, Pryce, or Thrawn here.");
        setLocationLightSideGameText("Opponent may not reduce or cancel your Force drains here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LOTHAL_ADVANCED_PROJECTS_LABORATORY__DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, "TIE Defender Project")
                || GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.PRYCE)
                || GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.THRAWN))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a card here from Reserve Deck");
            action.setActionMsg("Deploy TIE Defender Project, Pryce, or Thrawn here from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.title("TIE Defender Project"), Filters.Pryce, Filters.Thrawn), Filters.here(self),true));
            actions.add(action);
        }

        return actions;
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnLightSideOfLocation);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, self, opponent, playerOnLightSideOfLocation));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, self, opponent, playerOnLightSideOfLocation));
        return modifiers;
    }
}
