package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Subtype: Immediate
 * Title: The Time For Our Attack Has Come
 */
public class Card9_044 extends AbstractImmediateEffect {
    public Card9_044() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Time For Our Attack Has Come", Uniqueness.UNIQUE);
        setLore("'With the Imperial Fleet spread throughout the galaxy in a vain effort to engage us, it is relatively unprotected.'");
        setGameText("If you just initiated a second battle this turn, deploy on table. When you lose a battle, you may place Immediate Effect in Used Pile to cancel all battle damage against you, unless Draw Their Fire on table. (Immune to Control.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, playerId)
                && GameConditions.isSecondBattleInitiatedThisTurnBy(game, playerId)) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.lostBattle(game, effectResult, playerId)
                && !GameConditions.canSpot(game, self, Filters.Draw_Their_Fire)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel all battle damage");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new SatisfyAllBattleDamageEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}