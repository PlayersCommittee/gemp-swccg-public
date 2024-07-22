package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Imperial
 * Title: Moff Gideon, Suited For Battle
 */

public class Card223_018 extends AbstractImperial {
    public Card223_018() {
        super(Side.DARK, 1, 4, 4, 4, 6, "Moff Gideon, Suited For Battle", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Imperial Remnant leader.");
        setGameText("Adds 3 power to anything he pilots. If you just deployed Darksaber here, may take a card from Used Pile into hand; reshuffle. You initiate battles here for free. Your total battle destiny here is +1. Immune to attrition < 3.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_23);
        addPersona(Persona.GIDEON);
        addKeywords(Keyword.LEADER, Keyword.MOFF, Keyword.IMPERIAL_REMNANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new InitiateBattlesForFreeModifier(self, Filters.here(self), playerId));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), 1, playerId));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, playerId, Filters.Darksaber, Filters.sameSite(self))) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take a card from Used Pile into Hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromUsedPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}