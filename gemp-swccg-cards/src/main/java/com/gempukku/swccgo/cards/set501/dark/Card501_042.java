package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Defensive Shield
 * Title: Imperial Decree (V)
 */
public class Card501_042 extends AbstractDefensiveShield {
    public Card501_042() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Decree);
        setVirtualSuffix(true);
        setLore("To Imperial command personnel: The Rebellion must be crushed! Minor acts of sedition are to be ignored. The destruction of the Alliance is your primary goal.");
        setGameText("Plays on table. During opponent's control phase, may reduce force loss (except from Force drains), by the number of battlegrounds you occupy (to a minimum of 1). Opponent generates no Force at Massassi Throne Room.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_13);
        setTestingText("Imperial Decree (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.Massassi_Throne_Room, game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.any)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            int numToReduce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId)));
            if (numToReduce > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce Force loss by " + numToReduce);
                action.setActionMsg("Reduce Force loss by " + numToReduce + " (to a minimum of 1)");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, numToReduce, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}