package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Objective
 * Title: This Deal Is Getting Worse All The Time / Pray I Don't Alter It Any Further
 */
public class Card109_012_BACK extends AbstractObjective {
    public Card109_012_BACK() {
        super(Side.DARK, 7, Title.Pray_I_Dont_Alter_It_Any_Further);
        setGameText("While this side up, Surreptitious Glance may not cancel Dark Deal, The Planet That It's Farthest From is suspended if targeting Bespin. Opponent loses 8 Force when you play All Too Easy. At each Bespin location you control with an Imperial, your Force drains may not be modified by opponent. If you have an alien/imperial pair in battle, your total battle destiny is +2 (+4 if alien is an Ugnaught). Flip this card if Dark Deal is canceled, if opponent controls Bespin or if Bespin is 'blown away'.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Dark_Deal, Title.Surreptitious_Glance));
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.The_Planet_That_Its_Farthest_From, Filters.cardOnTableTargeting(Filters.Bespin_system))));
        modifiers.add(new ForceDrainsMayNotBeModifiedModifier(self, Filters.and(Filters.Bespin_location,
                Filters.controlsWith(playerId, self, Filters.Imperial)), opponent, playerId));
        modifiers.add(new TotalBattleDestinyModifier(self,
                        new InBattleCondition(self, Filters.and(Filters.your(self), Filters.alien, Filters.with(self, Filters.and(Filters.your(self), Filters.Imperial, Filters.participatingInBattle)))),
                        new ConditionEvaluator(2, 4, new InBattleCondition(self, Filters.and(Filters.your(self), Filters.Ugnaught))), playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.All_Too_Easy)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 8 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 8));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if ((TriggerConditions.isBlownAwayLastStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Bespin, true)))
                || TriggerConditions.justCanceled(game, effectResult, Filters.Dark_Deal)
                || (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.controls(game, opponent, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Bespin_system)))
                && GameConditions.canBeFlipped(game, self)) {

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