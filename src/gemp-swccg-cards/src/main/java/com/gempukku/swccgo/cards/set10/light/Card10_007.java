package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PlaceInUsedPileWhenCanceledModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Effect
 * Title: Do, Or Do Not & Wise Advice
 */
public class Card10_007 extends AbstractNormalEffect {
    public Card10_007() {
        super(Side.LIGHT, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Do, Or Do Not & Wise Advice", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        addComboCardTitles(Title.Do_Or_Do_Not, Title.Wise_Advice);
        setGameText("Deploy on table. Sense and Alter are now Lost Interrupts. When any player makes a destiny draw for Sense or Alter, and that destiny draw is successful, that player loses 2 Force. Your Immediate Effects may deploy for free. Whenever opponent cancels your card with Sense or Alter, place that canceled card in Used Pile. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LostInterruptModifier(self, Filters.or(Filters.Sense, Filters.Alter)));
        modifiers.add(new DeploysFreeModifier(self, Filters.and(Filters.your(playerId), Filters.Immediate_Effect)));
        modifiers.add(new PlaceInUsedPileWhenCanceledModifier(self, Filters.your(playerId), opponent, Filters.or(Filters.Sense, Filters.Alter)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.senseOrAlterDestinyDrawSuccessful(game, effectResult)) {
            final String playerId = effectResult.getPerformingPlayerId();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + playerId + " lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}