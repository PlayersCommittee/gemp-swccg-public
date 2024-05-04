package com.gempukku.swccgo.cards.set111.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Third Anthology)
 * Type: Effect
 * Title: Echo Base Garrison
 */
public class Card111_003 extends AbstractNormalEffect {
    public Card111_003() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Echo Base Garrison", Uniqueness.UNIQUE, ExpansionSet.THIRD_ANTHOLOGY, Rarity.PM);
        setLore("'All troop carriers will assemble at the North Entrance. The heavy Transport ships will leave as soon as they're loaded.'");
        setGameText("Deploy on table. Rogue T-47s are immune to attrition < 4 (or < 6 if matching pilot aboard). Once during each of your turns, may take one maintenance droid, Bacta Tank, Lone Rogue, [Hoth] Luke, [Special Edition] Wedge, Zev or Hobbie into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.Rogue_T47, new CardMatchesEvaluator(4, 6, Filters.hasMatchingPilotAboard(self))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ECHO_BASE_GARRISON__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a maintenance droid, Bacta Tank, Lone Rogue, [Hoth] Luke, [Special Edition] Wedge, Zev, or Hobbie into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.maintenance_droid,
                            Filters.Bacta_Tank, Filters.Lone_Rogue, Filters.and(Icon.HOTH, Filters.Luke),
                            Filters.and(Icon.SPECIAL_EDITION, Filters.Wedge), Filters.Zev, Filters.Hobbie), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}