package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfPlayersNextTurnEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Reflections II
 * Type: Character
 * Subtype: Rebel
 * Title: Owen Lars & Beru Lars
 */
public class Card10_018 extends AbstractRebel {
    public Card10_018() {
        super(Side.LIGHT, 3, 3, 4, 3, 5, "Owen Lars & Beru Lars", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_II, Rarity.PM);
        addComboCardTitles(Title.Owen_Lars, Title.Beru_Lars);
        setLore("Watchful guardians of Luke Skywalker. When the Lars' moisture farm was attacked by stormtroopers, Luke's life changed forever.");
        setGameText("Deploy free to Lars' Moisture Farm. Draws one battle destiny if not able to otherwise. If opponent has a Stormtrooper at a Tatooine site, you may not play Harvest. If lost during opponent's turn, Luke is power +6 until end of your next turn.");
        addIcons(Icon.REFLECTIONS_II);
        addKeywords(Keyword.MALE, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Lars_Moisture_Farm));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new MayNotPlayModifier(self, Filters.Harvest, new AtCondition(self, Filters.and(Filters.opponents(self),
                Filters.stormtrooper), Filters.Tatooine_site), self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLeavesTableRequiredTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.isOpponentsTurn(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make Luke power +6");
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfPlayersNextTurnEffect(action, self.getOwner(), Filters.Luke, 6, "Makes Luke power +6"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
