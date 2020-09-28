package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 */
public class Card501_096_BACK extends AbstractObjective {
    public Card501_096_BACK() {
        super(Side.DARK, 7, Title.Their_Fire_Has_Gone_Out_Of_The_Universe);
        setVirtualSuffix(true);
        setGameText("While this side up, you lose no Force to Visage Of The Emperor. While Vader armed with a lightsaber, opponent’s Force drain bonuses are canceled and your lightsaber Force drain bonuses may not be canceled. Once per game, may take lightsaber into hand from your Lost Pile." +
                "Flip this card if Luke or a Jedi or padawan at a battleground site.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_13);
        setTestingText("Their Fire Has Gone Out Of The Universe (V)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition vaderArmedWithLightsaberOnTableCondition = new OnTableCondition(self, Filters.and(Filters.Vader, Filters.armedWith(Filters.lightsaber)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Visage_Of_The_Emperor, playerId));
        modifiers.add(new CancelOpponentsForceDrainBonusesModifier(self, vaderArmedWithLightsaberOnTableCondition));
        modifiers.add(new ForceDrainModifiersMayNotBeCanceledModifier(self, vaderArmedWithLightsaberOnTableCondition, Filters.and(Filters.your(playerId), Filters.lightsaber)));
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Filters.character, Filters.not(Filters.or(Filters.droid, Filters.Imperial, Filters.bounty_hunter))), self.getOwner()));
        modifiers.add(new DestinyModifier(self, Filters.inquisitor, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.sameLocationAs(self, Filters.inquisitor), new ConditionEvaluator(1, 2, new OrCondition(new AtCondition(self, Filters.hasStacked(Filters.hatredCard)), new HereCondition(self, Filters.hasStacked(Filters.hatredCard)))), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HUNT_DOWN_V__TAKE_LIGHTSABER_INTO_HAND_FROM_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasLostPile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take lightsaber into hand from your Lost Pile");
            action.setActionMsg("Take lightsaber into hand from your Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.lightsaber, false));
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && (GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.opponents(self), Filters.or(Filters.Jedi, Filters.padawan, Filters.Luke), Filters.at(Filters.battleground_site))))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
