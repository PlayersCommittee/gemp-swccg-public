package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.FlagActiveCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Creature
 * Title: One-Arm
 */
public class Card7_212 extends AbstractCreature {
    public Card7_212() {
        super(Side.DARK, 3, 6, 3, 4, 0, "One-Arm", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("When a wampa is wounded, the other members of its pack band together to repel the threat.");
        setGameText("Habitat: Hoth sites. Deploys only to Wampa Cave. For remainder of game, all wampas are selective creatures.");
        addModelType(ModelType.SNOW);
        addIcons(Icon.SPECIAL_EDITION);
        addKeyword(Keyword.WAMPA);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.Hoth_site;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Wampa_Cave;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, Filters.wampa, new NotCondition(new FlagActiveCondition(ModifierFlag.WAMPAS_SELECTIVE_CREATURES_FOR_REMAINDER_OF_GAME)), Icon.SELECTIVE_CREATURE));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (!GameConditions.cardHasAnyForRemainderOfGameDataSet(self)) {
            self.setForRemainderOfGameData(self.getCardId(), new ForRemainderOfGameData());
            if (!GameConditions.isFlagActive(game, ModifierFlag.WAMPAS_SELECTIVE_CREATURES_FOR_REMAINDER_OF_GAME)) {
                // Add modifiers here without creating an action
                game.getModifiersEnvironment().addUntilEndOfGameModifier(
                        new SpecialFlagModifier(self, ModifierFlag.WAMPAS_SELECTIVE_CREATURES_FOR_REMAINDER_OF_GAME));
                game.getModifiersEnvironment().addUntilEndOfGameModifier(
                        new IconModifier(self, Filters.wampa, Icon.SELECTIVE_CREATURE));
            }
        }
        return null;
    }
}
