package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleWithParticipantCondition;
import com.gempukku.swccgo.cards.conditions.InPlayDataEqualsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Meditation
 */
public class Card7_071 extends AbstractNormalEffect {
    public Card7_071() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Meditation, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("To prepare for the inevitable conflict with Jabba's minions, Luke entered a calm state of mental preparation.");
        setGameText("Deploy on your character of ability > 3 (Effect lost if that character battles this turn). On a subsequent turn, if in a battle at a site, character's total weapon destiny is +3 and character adds one battle destiny. Effect lost at the end of that battle.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard character = self.getAttachedTo();

        // Check condition(s)
        if (!GameConditions.cardHasWhileInPlayDataSet(self)) {
            if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {

                self.setWhileInPlayData(new WhileInPlayData(false));
                return null;
            }

            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isInBattle(game, character)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        // Check condition(s)
        else if (GameConditions.cardHasWhileInPlayDataEquals(self, false)) {
            if (TriggerConditions.isTableChanged(game, effectResult)
                    && GameConditions.isInBattleAt(game, character, Filters.site)) {

                self.setWhileInPlayData(new WhileInPlayData(true));
                return null;
            }
        }
        // Check condition(s)
        else if (GameConditions.cardHasWhileInPlayDataEquals(self, true)) {
            if (TriggerConditions.battleEnded(game, effectResult)) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter attachedTo = Filters.hasAttached(self);
        Condition condition = new AndCondition(new InPlayDataEqualsCondition(self, true), new DuringBattleAtCondition(Filters.site),
                new DuringBattleWithParticipantCondition(attachedTo));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.any, condition, attachedTo, 3));
        modifiers.add(new AddsBattleDestinyModifier(self, condition, 1));
        return modifiers;
    }
}