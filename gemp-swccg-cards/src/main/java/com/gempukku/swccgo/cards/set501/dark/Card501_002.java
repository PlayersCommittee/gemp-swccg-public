package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.effects.ShuffleReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
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
        setGameText("Adds 2 to power of anything he pilots. Assassin. While armed (or piloting a starship), your total battle destiny here is +1. When deployed, may re-circulate, shuffle your Reserve Deck, then peek at top two cards of your Reserve Deck and take one into hand.");
        addPersona(Persona.BOBA_FETT);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.BOUNTY_HUNTER, Keyword.ASSASSIN);
        setVirtualSuffix(true);
        setTestingText("Boba Fett (v)");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self), new OrCondition(new ArmedWithCondition(self, Filters.any), new PilotingCondition(self, Filters.starship)), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(playerId);
            action.setText("Recirculate and peek at cards.");
            action.setActionMsg("Recirculate and peek at cards.");

            action.appendEffect(
                    new RecirculateEffect(action, playerId)
            );
            action.appendEffect(
                    new ShuffleReserveDeckEffect(action, playerId)
            );
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1)
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
