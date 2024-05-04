package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.TurnOffBinaryDroidEffect;
import com.gempukku.swccgo.logic.effects.TurnOnBinaryDroidEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: M-HYD 'Binary' Droid
 */
public class Card2_012 extends AbstractDroid {
    public Card2_012() {
        super(Side.LIGHT, 2, 3, 2, 5, "M-HYD 'Binary' Droid", Uniqueness.UNRESTRICTED, ExpansionSet.A_NEW_HOPE, Rarity.U1);
        setLore("Pretentious hydroponics droid. Specializes in working with binary load lifters and vaporators. Proficient in a number of languages but prefers to converse only in binary.");
        setGameText("Adds 2 to forfeit of each non-droid character at same and adjacent sites. At any time, any player may use 1 Force to turn M-HYD off (face down) or on again (face up).");
        addModelType(ModelType.BINARY_HYDROPONICS);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.non_droid_character, Filters.atSameOrAdjacentSite(self)), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getTurnOnAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return getTurnOnAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsWhenInactiveInPlay(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return getTurnOffAction(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActionsWhenInactiveInPlay(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        return getTurnOffAction(playerId, game, self, gameTextSourceCardId);
    }

    private List<TopLevelGameTextAction> getTurnOnAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.canBeTurnedOff(game, self)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Turn off");
            action.setActionMsg("Turn off " + GameUtils.getCardLink(self));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TurnOffBinaryDroidEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<TopLevelGameTextAction> getTurnOffAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnlyTurnedOff(game, self)
                && GameConditions.canBeTurnedOn(game, self)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Turn on");
            action.setActionMsg("Turn on " + GameUtils.getCardLink(self));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TurnOnBinaryDroidEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
