package com.gempukku.swccgo.cards.set215.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Character
 * Subtype: Alien
 * Title: Rukh
 */
public class Card215_026 extends AbstractAlien {
    public Card215_026() {
        super(Side.DARK, 2, 3, 4, 2, 4, "Rukh", Uniqueness.UNIQUE);
        setLore("Noghri assassin and bodyguard.");
        setGameText("While armed with a blaster, your assassins and leaders here are immune to Blaster Proficiency and Clash Of Sabers and, if Shadow Collective just flipped, may peek at top two cards of your Reserve Deck and take one into hand.");
        addPersona(Persona.RUKH);
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR, Icon.VIRTUAL_SET_15);
        setSpecies(Species.NOGHRI);
        addKeywords(Keyword.ASSASSIN, Keyword.BODYGUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        ArmedWithCondition rukhArmedWithBlaster = new ArmedWithCondition(self, Filters.blaster);
        Filter yourAssassinsAndLeadersHere = Filters.and(Filters.your(self.getOwner()), Filters.here(self), Filters.or(Filters.assassin, Filters.leader));
        modifiers.add(new ImmuneToTitleModifier(self, yourAssassinsAndLeadersHere, rukhArmedWithBlaster, Title.Blaster_Proficiency));
        modifiers.add(new ImmuneToTitleModifier(self, yourAssassinsAndLeadersHere, rukhArmedWithBlaster, Title.Clash_Of_Sabers));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.cardFlipped(game, effectResult, Filters.You_Know_Who_I_Answer_To)
                && GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isArmedWith(game, self, Filters.blaster)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Peek at top two cards of Reserve Deck.");
            action.setActionMsg("Peek at top two cards of Reserve Deck and take one into hand.");
            action.appendEffect(
                    new PeekAtTopCardsOfReserveDeckAndChooseCardsToTakeIntoHandEffect(action, playerId, 2, 1, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
