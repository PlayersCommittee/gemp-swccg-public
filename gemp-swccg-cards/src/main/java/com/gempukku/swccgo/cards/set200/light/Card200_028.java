package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.CancelForceRetrievalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 0
 * Type: Defensive Shield
 * Title: Simple Tricks And Nonsense
 */
public class Card200_028 extends AbstractDefensiveShield {
    public Card200_028() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,"Simple Tricks And Nonsense", ExpansionSet.SET_0, Rarity.V);
        setLore("'Et tu taka bu Jabba now.'");
        setGameText("Plays on table. While you occupy a battleground and opponent occupies less than two battlegrounds, cancel opponent's Force drains at non-battleground locations and opponent's Force retrieval.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.non_battleground_location)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.isForceRetrievalJustInitiated(game, effectResult, opponent)
                && GameConditions.occupies(game, playerId, Filters.battleground)
                && !GameConditions.occupies(game, opponent, 2, Filters.battleground)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force retrieval");
            // Perform result(s)
            action.appendEffect(
                    new CancelForceRetrievalEffect(action));
            actions.add(action);
        }
        return actions;
    }
}