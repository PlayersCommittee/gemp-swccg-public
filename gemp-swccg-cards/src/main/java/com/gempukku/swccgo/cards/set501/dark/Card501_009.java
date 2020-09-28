package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveDefenseValueIncreasedAbovePrintedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitIncreasedAbovePrintedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 13
 * Type: Device
 * Title: Observation Holocam (V)
 */
public class Card501_009 extends AbstractDevice {
    public Card501_009() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Observation Holocam");
        setVirtualSuffix(true);
        setLore("Remote surveillance viewers with droid controllers supplement security. Can activate alarms and automated weapons when needed, bringing help to endangered locations.");
        setGameText("Deploy on a site. Opponent may not ‘react’ here. Characters here may not have their forfeit or defense value increased above their printed value. Lost if opponent controls this site.");
        setTestingText("Observation Holocam (V)");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, self.getAttachedTo(), opponent));
        modifiers.add(new MayNotHaveForfeitIncreasedAbovePrintedModifier(self, Filters.and(Filters.character, Filters.here(self.getAttachedTo()))));
        modifiers.add(new MayNotHaveDefenseValueIncreasedAbovePrintedModifier(self, Filters.and(Filters.character, Filters.here(self.getAttachedTo()))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, game.getOpponent(self.getOwner()), self.getAttachedTo())
                && GameConditions.canTargetToCancel(game, self, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, self, self.getTitle());
            actions.add(action);
        }

        return actions;
    }
}