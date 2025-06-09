package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextArmorModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Droid
 * Title: BT-1 & Triple Zero
 */
public class Card224_003 extends AbstractDroid {
    public Card224_003() {
        super(Side.DARK, 3, 5, 5, 6, "BT-1 & Triple Zero", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        addComboCardTitles("BT-1", "Triple Zero");
        setLore("Assassin and information broker.");
        setGameText("Armor = 5. If with Aphra, may add one destiny to attrition. If opponent's character of ability < 4 here is about to leave table, may use 1 Force; opponent loses 1 Force.");
        addIcons(Icon.PRESENCE, Icon.VIRTUAL_SET_24);
        addIcon(Icon.WARRIOR, 2);
        addModelTypes(ModelType.ASTROMECH, ModelType.PROTOCOL);
        addKeywords(Keyword.ASSASSIN, Keyword.INFORMATION_BROKER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefinedByGameTextArmorModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, Filters.persona(Persona.APHRA))
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one destiny to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToAttritionEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, Filters.and(Filters.opponents(self), Filters.character, Filters.abilityLessThan(4), Filters.here(self)))
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");

            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 1));

            return Collections.singletonList(action);
        }
        return null;
    }
}
