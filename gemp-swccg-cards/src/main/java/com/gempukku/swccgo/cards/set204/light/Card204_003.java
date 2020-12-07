package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Hera Syndulla
 */
public class Card204_003 extends AbstractRebel {
    public Card204_003() {
        super(Side.LIGHT, 3, 4, 4, 3, 6, Title.Hera, Uniqueness.UNIQUE);
        setLore("Female Twi'lek leader.");
        setGameText("[Pilot] 3. If with an Imperial (or two Rebels), adds one battle destiny. During battle, if you just drew a destiny < number of Rebels here, may cancel destiny and redraw that destiny.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_4);
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.CAPTAIN);
        setSpecies(Species.TWILEK);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(new WithCondition(self, Filters.Imperial), new WithCondition(self, 2, Filters.Rebel)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            int numRebels = Filters.countActive(game, self, Filters.and(Filters.Rebel, Filters.here(self)));
            if (GameConditions.isDestinyValueLessThan(game, numRebels)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Cancel and re-draw destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new CancelDestinyAndCauseRedrawEffect(action));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
