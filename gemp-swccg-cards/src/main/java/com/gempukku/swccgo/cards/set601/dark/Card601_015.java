package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

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
        super(Side.DARK, Title.Slaving_Camp_Headquarters, Title.Kashyyyk, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLocationDarkSideGameText("Once per turn, may use 1 Force to deploy a battleground site to Kashyyyk and/or a card with 'Hunting' in title.");
        setLocationLightSideGameText("Force drain +1 here. If you control with a Wookiee, Trandoshans are deploy +1 to Kashyyyk.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_7);
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
                new AndCondition(new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Wookiee), new NotCondition(treatTrandoshanAsSlaver)), 1, Filters.Kashyyyk_location));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.or(Filters.species(Species.TRANDOSHAN), Filters.slaver)),
                new AndCondition(new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Wookiee), treatTrandoshanAsSlaver), 1, Filters.Kashyyyk_location));
        return modifiers;
    }
}