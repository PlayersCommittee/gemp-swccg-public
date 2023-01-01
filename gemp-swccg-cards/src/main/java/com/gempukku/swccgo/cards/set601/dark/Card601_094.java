package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.MaximumDefenseValueModifiedToModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddDestinyDrawsToPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Effect
 * Title: Revenge Of The Sith
 */
public class Card601_094 extends AbstractNormalEffect {
    public Card601_094() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Revenge Of The Sith", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("A symbol of the Dark Lord of the Sith, and of the seductive power of the dark side. Hologram.");
        setGameText("Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (to a maximum of 8) and his immunity to attrition is +2.  When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
        addKeywords(Keyword.HOLOGRAM, Keyword.DEPLOYS_ON_CHARACTERS);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Vader;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotAddDestinyDrawsToPowerModifier(self, new DuringBattleAtCondition(Filters.here(self)), opponent));
        modifiers.add(new MayNotAddDestinyDrawsToAttritionModifier(self, new DuringBattleAtCondition(Filters.here(self)), opponent));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.Vader, Filters.with(self, Filters.or(Filters.Skywalker, Filters.Jedi))), 2));
        modifiers.add(new MaximumDefenseValueModifiedToModifier(self, Filters.and(Filters.Vader, Filters.with(self, Filters.or(Filters.Skywalker, Filters.Jedi))), null,8));
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.Vader, Filters.with(self, Filters.or(Filters.Skywalker, Filters.Jedi))), 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        Filter filter = Filters.and(Icon.CORUSCANT, Filters.hologram);
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, filter)) {
            Collection<PhysicalCard> toBeLost = Filters.filterAllOnTable(game, filter);

            if (!toBeLost.isEmpty()) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make [Coruscant] holograms lost");
                // Build action using common utility
                action.appendEffect(
                        new LoseCardsFromTableEffect(action, toBeLost));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        //When Vader leaves table, place Effect in Used Pile.
        //this is actually doing "If just lost and Vader not on table, place Effect in Used Pile."
        if (TriggerConditions.justLost(game, effectResult, self)
            && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_ALL, Filters.Vader)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            Collection<PhysicalCard> cards = new LinkedList<>();
            cards.add(self);
            action.appendEffect(new PlaceCardsInUsedPileFromOffTableEffect(action, cards));
            return Collections.singletonList(action);
        }

        return null;
    }
}