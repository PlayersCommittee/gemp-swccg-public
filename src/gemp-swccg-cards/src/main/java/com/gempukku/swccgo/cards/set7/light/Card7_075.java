package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InPlayDataSetCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerBwingEvaluator;
import com.gempukku.swccgo.cards.evaluators.PerXwingEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.ResetHyperspeedModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: S-foils
 */
public class Card7_075 extends AbstractNormalEffect {
    public Card7_075() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.S_foils, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("'Lock S-foils in attack position.'");
        setGameText("Deploy on table. Once during each of your control phases, may use 1 Force: until beginning of your next turn, each of your X-wings and B-wings is power +2 and hyperspeed=0, and adds 1 to each weapon destiny. (Immune to Alter if Red Leader on table.)");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Lock S-foils in attack position");
            action.setActionMsg("Make " + playerId + "'s X-wings and B-wings power +2, hyperspeed = 0, and add 1 to each weapon destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            action.appendEffect(
                    new SendMessageEffect(action, "S-foils locked in attack position"));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourXwingsAndBwings = Filters.and(Filters.your(self), Filters.or(Filters.X_wing, Filters.B_wing));
        Condition condition = new InPlayDataSetCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourXwingsAndBwings, condition, new AddEvaluator(new PerXwingEvaluator(2), new PerBwingEvaluator(2))));
        modifiers.add(new ResetHyperspeedModifier(self, yourXwingsAndBwings, condition, 0));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, condition, yourXwingsAndBwings, 1, Filters.any));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OnTableCondition(self, Filters.Red_Leader), Title.Alter));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, playerId)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Unlock S-foils from attack position");
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, null));
            action.appendEffect(
                    new SendMessageEffect(action, "S-foils unlocked from attack position"));
            return Collections.singletonList(action);
        }
        return null;
    }
}