package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 1 (V)
 */
public class Card601_251 extends AbstractStarfighter {
    public Card601_251() {
        super(Side.LIGHT, 2, 2, 3, null, 4, 5, 5, "Red 1", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Lead fighter of Red Squadron at Battle of Yavin. Flown by Garven Dreis. Also served at main Rebel base on Dantooine.");
        setGameText("May add Red Leader as pilot. X-wings are immune to Tallon Roll. Once per turn, may use 1 Force to [upload] a unique (•) Red Squadron X-wing (except Red 5). Immune to attrition < 5.");
        addPersona(Persona.RED_1);
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
        setMatchingPilotFilter(Filters.Red_Leader);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Red_Leader;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.X_wing, Title.Tallon_Roll));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RED_1__UPLOAD_RED_SQUADRON_XWING;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a unique (•) Red Squadron X-wing (except Red 5) into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.Red_Squadron_starfighter, Filters.X_wing, Filters.except(Filters.Red_5)), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
