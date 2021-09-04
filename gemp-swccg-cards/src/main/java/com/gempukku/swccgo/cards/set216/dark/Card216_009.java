package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitToCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Location
 * Subtype: Site
 * Title: Mustafar: Private Platform (Docking Bay)
 */
public class Card216_009 extends AbstractSite {
    public Card216_009() {
        super(Side.DARK, "Mustafar: Private Platform (Docking Bay)", Title.Mustafar);
        setLocationDarkSideGameText("Once per turn, may [download] a starfighter with 'Vader' in title here.");
        setLocationLightSideGameText("Your docking bay transit to or from here requires +4 Force (+6 Force if Vader or Vaneé here).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_16);
        addKeyword(Keyword.DOCKING_BAY);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition vaderOrVaneeHere = new OrCondition(new HereCondition(self, Filters.Vader), new HereCondition(self, Filters.persona(Persona.VANEE)));

        modifiers.add(new DockingBayTransitFromCostModifier(self, new ConditionEvaluator(4, 6, vaderOrVaneeHere), playerOnLightSideOfLocation));
        modifiers.add(new DockingBayTransitToCostModifier(self,  new ConditionEvaluator(4, 6, vaderOrVaneeHere), playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MUSTAFAR_PRIVATE_PLATFORM__DOWNLOAD_STARSHIP;

        if (GameConditions.isOncePerTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy starfighter with 'Vader' in title here");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Filters.starfighter, Filters.titleContains("Vader")), Filters.here(self), true)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
