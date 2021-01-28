package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Obi-Wan's Hut (V)
 */
public class Card601_060 extends AbstractSite {
    public Card601_060() {
        super(Side.LIGHT, Title.ObiWans_Hut, Title.Tatooine);
        setLocationDarkSideGameText("If you control, Force drain +1 here.");
        setLocationLightSideGameText("Once per turn, if Obi-Wan 'communing' may deploy from Reserve Deck an effect with 'Rebel' in title; reshuffle.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.BLOCK_7);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self), 1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OBIWANS_HUT__DEPLOY_EFFECT;

        if (GameConditions.canSpot(game, self, Filters.and(Filters.Communing, Filters.hasStacked(Filters.ObiWan)))) {
            PhysicalCard communing = Filters.findFirstActive(game, self, Filters.Communing);
            PhysicalCard obiwan = Filters.findFirstFromStacked(game, Filters.and(Filters.ObiWan, Filters.stackedOn(communing)));
            // Check condition(s)
            if (game.getModifiersQuerying().isCommuning(game.getGameState(), obiwan)
                    && GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                    && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy an Effect from Reserve Deck");
                action.setActionMsg("Deploy an Effect with 'Rebel' in title from Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.titleContains("Rebel")), true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}