package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.LostInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UniqueModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Effect
 * Title: Ounee Ta
 */
public class Card112_004 extends AbstractNormalEffect {
    public Card112_004() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ounee_Ta, Uniqueness.UNIQUE, ExpansionSet.JPSD, Rarity.PM);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("Deploy on table. You may immediately take top card of Lost Pile into hand. None Shall Pass is unique (•) and a Lost Interrupt. At each opponent's ◇ site, your characters and vehicles are each deploy -3 and your Force generation is +1. (Immune to Alter.)");
        addIcons(Icon.PREMIUM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasLostPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take top card of Lost Pile into hand");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromLostPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter opponentsGenericSite = Filters.and(Filters.opponents(self), Filters.generic_site, Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UniqueModifier(self, Filters.None_Shall_Pass));
        modifiers.add(new LostInterruptModifier(self, Filters.None_Shall_Pass));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.character, Filters.vehicle)), -3, opponentsGenericSite));
        modifiers.add(new ForceGenerationModifier(self, opponentsGenericSite, 1, playerId));
        return modifiers;
    }
}