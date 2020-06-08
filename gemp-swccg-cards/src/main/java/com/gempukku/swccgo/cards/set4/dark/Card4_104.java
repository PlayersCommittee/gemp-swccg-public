package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Suba
 */
public class Card4_104 extends AbstractImperial {
    public Card4_104() {
        super(Side.DARK, 2, 3, 3, 2, 4, "Lieutenant Suba", Uniqueness.UNIQUE);
        setLore("Chief of security and political officer on Executor. Responsible for ensuring the loyalty of his fellow officers. Disapproved of Vader's unwillingness to simply kill Skywalker.");
        setGameText("While at a Death Star or Executor site, your troopers deploy free there and are power and forfeit +1 there, and you may use 1 Force to search your Reserve Deck and take one Tactical Support into hand. Shuffle, cut and replace.");
        addIcons(Icon.DAGOBAH, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atDeathStarOrExecutorSite = new AtCondition(self, Filters.or(Filters.Death_Star_site, Filters.Executor_site));
        Filter yourTroopers = Filters.and(Filters.your(self), Filters.trooper);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, yourTroopers, atDeathStarOrExecutorSite, Filters.here(self)));
        modifiers.add(new PowerModifier(self, Filters.and(yourTroopers, Filters.here(self)), atDeathStarOrExecutorSite, 1));
        modifiers.add(new ForfeitModifier(self, Filters.and(yourTroopers, Filters.here(self)), atDeathStarOrExecutorSite, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LIEUTENANT_SUBA__UPLOAD_TACTICAL_SUPPORT;

        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.isAtLocation(game, self, Filters.or(Filters.Death_Star_site, Filters.Executor_site))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Tactical Support into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Tactical_Support, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
