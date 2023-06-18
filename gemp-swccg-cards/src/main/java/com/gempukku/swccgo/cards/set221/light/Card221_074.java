package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: Site
 * Title: Supply Route
 */
public class Card221_074 extends AbstractSite {
    public Card221_074() {
        super(Side.LIGHT, "Supply Route", Uniqueness.DIAMOND_1, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Deploys only to same planet as Clone Command Center. Your vehicles are power +1 here.");
        setLocationLightSideGameText("Once during opponent's turn, if your clone here with a Jedi or Padawan, may activate 1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.CLONE_ARMY, Icon.VIRTUAL_SET_21);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.titleContains(Title.Clone_Command_Center), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.vehicle, Filters.here(self)), 1));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canActivateForce(game, playerOnLightSideOfLocation)
                && GameConditions.occupiesWith(game, self, playerOnLightSideOfLocation, Filters.here(self), Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.clone, Filters.with(self, Filters.or(Filters.Jedi, Filters.padawan))))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerOnLightSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}