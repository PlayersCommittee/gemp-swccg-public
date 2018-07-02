package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Rebel
 * Title: Major Bren Derlin
 */
public class Card3_013 extends AbstractRebel {
    public Card3_013() {
        super(Side.LIGHT, 2, 2, 2, 2, 5, Title.Derlin, Uniqueness.UNIQUE);
        setLore("Hero of Nentan. Supervised construction of Echo Base on Hoth. Head of base security. At the Mos Eisley Cantina, everyone knows his name.");
        setGameText("While on Hoth, opponent may not 'react' to any Echo site, and Derlin may use 1 Force to cancel Breached Defenses. While at Cantina, power +1 and may use 1 Force to cancel Local Trouble.");
        addIcons(Icon.HOTH, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(self, Filters.Echo_site, new OnCondition(self, Title.Hoth), game.getOpponent(self.getOwner())));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.Cantina), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnSystem(game, self, Title.Hoth)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTargetToCancel(game, self, Filters.Breached_Defenses)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Breached_Defenses, Title.Breached_Defenses, 1);
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.Cantina)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTargetToCancel(game, self, Filters.Local_Trouble)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Local_Trouble, Title.Local_Trouble, 1);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Breached_Defenses)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isOnSystem(game, self, Title.Hoth)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect, 1);
            return Collections.singletonList(action);
        }
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Local_Trouble)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.isAtLocation(game, self, Filters.Cantina)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect, 1);
            return Collections.singletonList(action);
        }
        return null;
    }
}
