package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfUsedPileAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.ShuffleUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Boba Fett (v)
 */
public class Card501_002 extends AbstractAlien {
    public Card501_002() {
        super(Side.DARK, 1, 4, 4, 3, 6, "Boba Fett", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Infamous bounty hunter. Hired to help Jabba intimidate debtors and smugglers. Crack shot. Mandalorian armor and jet pack provide protection and flight capability.");
        setGameText("Adds 3 to power of anything he pilots. When deployed, may peek at top 4 cards (6 if a captive here) of Used Pile and take one into hand; reshuffle. Draws one battle destiny if unable to otherwise. If armed or with a captive, your total battle destiny here is +1. Immune to attrition < 4.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setVirtualSuffix(true);
        setTestingText("Boba Fett (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new OrCondition(new ArmedWithCondition(self, Filters.any), new WithCondition(self, SpotOverride.INCLUDE_CAPTIVE, Filters.or(Filters.captive, Filters.escortedCaptive, Filters.frozenCaptive))), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self.getOwner(), self)
            && GameConditions.hasUsedPile(game, playerId)) {
            int numCardsToPeakAt = GameConditions.isWith(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.captive) ? 6 : 4;

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Peak at top " + numCardsToPeakAt + " cards");
            action.setActionMsg("Peak at top " + numCardsToPeakAt +  " cards");
            // Perform result(s)
            action.appendEffect(
                    new PeekAtTopCardsOfUsedPileAndChooseCardsToTakeIntoHandEffect(action, playerId, numCardsToPeakAt, 1, 1));
            action.appendEffect(
                    new ShuffleUsedPileEffect(action, self)
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
