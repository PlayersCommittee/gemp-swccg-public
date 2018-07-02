package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseDevicesModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Niado Duegad
 */
public class Card7_192 extends AbstractAlien {
    public Card7_192() {
        super(Side.DARK, 2, 3, 2, 3, 4, "Niado Duegad", Uniqueness.UNIQUE);
        setLore("Mercenary from Vodran. Niado's culture controls its environment through terraforming facilities. Enhanced adrenal glands allow for short bursts of incredible strength.");
        setGameText("May not carry or use devices or weapons. Once per turn, may use X Force, where X = 1, 2, or 3, to add twice X to his power for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotCarryModifier(self, Filters.or(Filters.device, Filters.weapon)));
        modifiers.add(new MayNotUseDevicesModifier(self));
        modifiers.add(new MayNotUseWeaponsModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            int maxForceToUse = Math.min(3, GameConditions.forceAvailableToUse(game, playerId));
            if (maxForceToUse >= 1) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Add to power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new PlayoutDecisionEffect(action, playerId,
                                new IntegerAwaitingDecision("Choose amount of Force to use ", 1, maxForceToUse, maxForceToUse) {
                                    @Override
                                    public void decisionMade(int result) throws DecisionResultInvalidException {
                                        action.appendCost(
                                                new UseForceEffect(action, playerId, result));
                                        int amountToAdd = 2 * result;
                                        action.setActionMsg("Add " + amountToAdd + " to " + GameUtils.getCardLink(self) + "'s power");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new ModifyPowerUntilEndOfTurnEffect(action, self, amountToAdd));
                                    }
                                }
                        ));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
