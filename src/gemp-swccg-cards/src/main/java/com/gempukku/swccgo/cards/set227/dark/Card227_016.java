package com.gempukku.swccgo.cards.set227.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
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
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Location
 * Subtype: Site
 * Title: Utapau: Pau City
 */
public class Card227_016 extends AbstractSite {
    public Card227_016() {
        super(Side.DARK, "Utapau: Pau City", Title.Utapau, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setLocationDarkSideGameText("During opponent's turn, if you control with a [Separatist] leader, may activate 1 Force.");
        setLocationLightSideGameText("During battle here, you must first use 1 Force to fire a weapon.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.EPISODE_I, Icon.SEPARATIST, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsTurn(game, self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId)
            && GameConditions.canActivateForce(game, playerOnDarkSideOfLocation)
            && GameConditions.controlsWith(game, self, playerOnDarkSideOfLocation, Filters.here(self), Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.SEPARATIST, Filters.leader))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Activate 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerTurnEffect(action));

            // Perform result(s)
            action.appendEffect(
                new ActivateForceEffect(action, playerOnDarkSideOfLocation, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(final String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.your(playerOnLightSideOfLocation),
            new DuringBattleAtCondition(Filters.here(self)), 1));
        return modifiers;
    }
}
