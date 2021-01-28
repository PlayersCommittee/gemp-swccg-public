package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.*;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Location
 * Subtype: Site
 * Title: Kashyyyk: Slaving Camp Headquarters
 */
public class Card601_015 extends AbstractSite {
    public Card601_015() {
        super(Side.DARK, "Kashyyyk: Slaving Camp Headquarters", Title.Kashyyyk);
        setLocationDarkSideGameText("Once per turn, may use 1 Force to deploy a battleground site to Kashyyyk and/or a card with 'Hunting' in title.");
        setLocationLightSideGameText("Force drain +1 here. If you control with a Wookiee, Trandoshans are deploy +1 to Kashyyyk.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.BLOCK_7);
        setAsLegacy(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SLAVING_CAMP_HEADQUARTERS__DEPLOY_SITE_AND_OR_OTHER_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card(s) from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerOnDarkSideOfLocation, 1));
            // Perform result(s)
            //TODO this ignores cards with hunting in title right now because I didn't find a good effect to use for deploying A and/or B
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.location, Title.Kashyyyk, true));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition treatTrandoshanAsSlaver = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.LEGACY__YOUR_SITES__TREAT_TRANDOSHAN_AS_SLAVER);
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, 1, playerOnLightSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.species(Species.TRANDOSHAN)),
                new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Wookiee), 1, Filters.Kashyyyk_location));
        //TODO have this affect slavers instead of trandoshans too
        return modifiers;
    }
}