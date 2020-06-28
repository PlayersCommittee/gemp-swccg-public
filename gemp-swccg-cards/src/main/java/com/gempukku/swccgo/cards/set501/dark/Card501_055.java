package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Dathomir: Maul's Chambers
 */
public class Card501_055 extends AbstractSite {
    public Card501_055() {
        super(Side.DARK, "Dathomir: Maul's Chambers", Title.Dathomir);
        setLocationDarkSideGameText("May deploy Maul here from Reserve Deck; reshuffle (at start of game if Massassi Throne Room on table)");
        setLocationLightSideGameText("If Maul here, your spies (except Ezra) may not deploy here. Opponent may not force drain here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        setTestingText("Dathomir: Maul's Chambers");
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MAULS_CHAMBERS__DOWNLOAD_MAUL;

        // Check condition(s)
        if (TriggerConditions.isStartingLocationsAndObjectivesCompletedStep(game, effectResult)
                && (GameConditions.canSpot(game, self, Filters.title(Title.Massassi_Throne_Room)))
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, true, false, Persona.MAUL)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Maul from Reserve Deck");
            action.setActionMsg("Deploy Maul from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Maul, Filters.here(self), true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.MAULS_CHAMBERS__DOWNLOAD_MAUL;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.MAUL)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Maul from Reserve Deck");
            action.setActionMsg("Deploy Maul from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Maul, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition maulHere = new HereCondition(self, Filters.Maul);
        Filter yourSpiesExceptEzra = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.spy, Filters.not(Filters.Ezra));
        modifiers.add(new MayNotDeployToLocationModifier(self, yourSpiesExceptEzra, maulHere, Filters.here(self)));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}