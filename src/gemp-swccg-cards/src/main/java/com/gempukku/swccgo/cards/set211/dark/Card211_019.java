package com.gempukku.swccgo.cards.set211.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: System
 * Title: D'Qar
 */
public class Card211_019 extends AbstractSystem {
    public Card211_019() {
        super(Side.DARK, Title.Dqar, 5, ExpansionSet.SET_11, Rarity.V);
        setLocationDarkSideGameText("Once per game, if you just moved a [First Order] starship to here, may activate 2 Force.");
        setLocationLightSideGameText("Once per game, may [download] Connix, Paige, or Tallie here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextDarkSideOptionalAfterTriggers(String playerOnDarkSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.DQAR__ACTIVATE_2_FORCE;

        if (TriggerConditions.movedToLocationBy(game, effectResult, playerOnDarkSideOfLocation, Filters.and(Icon.FIRST_ORDER, Filters.starship), self)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 2 Force");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new ActivateForceEffect(action, playerOnDarkSideOfLocation, 2));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.DQAR__DEPLOY_CHARACTER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, new HashSet<Persona>(Arrays.asList(Persona.CONNIX, Persona.PAIGE, Persona.TALLIE_LINTRA)))) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Connix, Paige, or Tallie here");
            action.setActionMsg("Deploy Connix, Paige, or Tallie here from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Connix, Filters.Paige, Filters.Tallie), Filters.here(self), true));
            actions.add(action);
        }
        return actions;
    }
}
