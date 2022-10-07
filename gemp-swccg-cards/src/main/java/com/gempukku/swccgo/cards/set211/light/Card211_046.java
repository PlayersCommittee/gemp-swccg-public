package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardsIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Location
 * Subtype: Site
 * Title: Ahch-To: Jedi Temple
 */

public class Card211_046 extends AbstractSite {
    public Card211_046() {
        super(Side.LIGHT, "Ahch-To: Jedi Temple", Title.Ahch_To);
        setLocationDarkSideGameText("Unless your battleground on table, you generate no Force here.");
        setLocationLightSideGameText("Once during your turn, if Luke alone here, may draw top card of Force Pile. ");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.EPISODE_VII, Icon.VIRTUAL_SET_11);
    }


    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter dsBattleground = Filters.and(Filters.battleground, Filters.your(playerOnDarkSideOfLocation));
        Condition dsBattlegroundOnTable = new OnTableCondition(self, dsBattleground);
        Condition unlessDsBattlegroundOnTable = new UnlessCondition(dsBattlegroundOnTable);

        modifiers.add(new GenerateNoForceModifier(self, self, unlessDsBattlegroundOnTable, playerOnDarkSideOfLocation));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter lukeAloneHere = Filters.and(Filters.Luke, Filters.here(self), Filters.alone);

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourTurn(game, playerOnLightSideOfLocation)
                && GameConditions.hasForcePile(game, playerOnLightSideOfLocation)
                && GameConditions.canSpot(game, self, lukeAloneHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of force pile into hand");
            action.setActionMsg("Draw top card from force pile into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromForcePileEffect(action, playerOnLightSideOfLocation));

            return Collections.singletonList(action);
        }

        return null;
    }

}