package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Character
 * Subtype: Alien
 * Title: Shada
 */
public class Card601_148 extends AbstractAlien {
    public Card601_148() {
        super(Side.DARK, 2, 4, 4, 4, 6, "Shada", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Female spy and thief.");
        setGameText("May deploy Shada and [Block 4] No Bargain instead of Xizor with your [Reflections II] Objective.  While that Effect on table, Force loss from your [Reflections II] Objective is +1 and, once per turn, may lose 1 Force to add a battle destiny anywhere.  Immune to attrition < 4.");
        addKeywords(Keyword.FEMALE, Keyword.SPY, Keyword.THIEF);
        addIcons(Icon.LEGACY_BLOCK_4);
        addIcon(Icon.WARRIOR, 2);
        setAsLegacy(true);
    }

    // note: "May deploy Shada and [Virtual Block 4] No Bargain instead of Xizor with your [Reflections II] Objective."
    // implemented on Agents Of Black Sun

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }


    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        // once per turn, may lose 1 Force to add a battle destiny anywhere.

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canSpot(game, self, Filters.and(Icon.LEGACY_BLOCK_4, Filters.No_Bargain))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));

            action.appendCost(
                    new LoseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // While [Block 4] No Bargain on table, Force loss from your [Reflections II] Objective is +1

        if (GameConditions.canSpot(game, self, Filters.and(Icon.LEGACY_BLOCK_4, Filters.No_Bargain))
                && TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective))
                && GameConditions.isOncePerForceLoss(game, self, gameTextSourceCardId)) {
            String playerToLoseForce = ((AboutToLoseForceResult) effectResult).getPlayerToLoseForce();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Increase Force loss by 1");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerForceLossEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new IncreaseForceLossEffect(action, playerToLoseForce, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
