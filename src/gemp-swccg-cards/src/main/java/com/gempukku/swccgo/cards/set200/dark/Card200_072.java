package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Droid
 * Title: 5D6-RA-7 (Fivedesix) (V)
 */
public class Card200_072 extends AbstractDroid {
    public Card200_072() {
        super(Side.DARK, 2, 4, 2, 5, Title._5D6RA7, Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("Aide to Admiral Motti's staff. Foul-tempered and vindictive. Feared by other droids. A spy for the ISB. Secretly investigates Imperial officers whose loyalties are in question.");
        setGameText("[Pilot] 2. Power +1 while with your leader. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 1 Force; 4-6, you may draw top card of your Reserve Deck; 0 or 7, you may retrieve 1 Force.");
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_0);
        addKeywords(Keyword.SPY);
        addModelType(ModelType.SERVANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new WithCondition(self, Filters.and(Filters.your(self), Filters.leader)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isAtScompLink(game, self)) {

            if (GameConditions.isDestinyValueInRange(game, 1, 3)
                    && GameConditions.canActivateForce(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Activate 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new ActivateForceEffect(action, playerId, 1));
                return Collections.singletonList(action);
            }

            if(GameConditions.isDestinyValueInRange(game, 4, 6)
                    && GameConditions.hasReserveDeck(game, playerId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Draw top card of Reserve Deck");
                // Perform result(s)
                action.appendEffect(
                        new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                return Collections.singletonList(action);
            }

            if(GameConditions.isDestinyValueEqualTo(game, 0)
                    || GameConditions.isDestinyValueEqualTo(game, 7)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Retrieve 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, playerId, 1));
                return Collections.singletonList(action);
            }
        }

        return null;
    }
}
