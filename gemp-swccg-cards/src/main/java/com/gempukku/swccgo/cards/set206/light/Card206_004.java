package com.gempukku.swccgo.cards.set206.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 6
 * Type: Character
 * Subtype: Rebel
 * Title: Jyn Erso
 */
public class Card206_004 extends AbstractRebel {
    public Card206_004() {
        super(Side.LIGHT, 2, 4, 4, 3, 5, "Jyn Erso", Uniqueness.UNIQUE);
        setLore("Female spy.");
        setGameText("May deploy to a site as an Undercover spy. Draws one battle destiny if unable to otherwise. If just lost, may peek at opponent's hand.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_6);
        addKeywords(Keyword.FEMALE, Keyword.SPY);
        setMayDeployAsUndercoverSpy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.hasHand(game, opponent)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Peek at opponent's hand");
            // Perform result(s)
            action.appendEffect(
                    new PeekAtOpponentsHandEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
