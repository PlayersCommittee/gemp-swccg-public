package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardsOfCardPileEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromReserveDeckOnTopOfCardPileEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Imperial
 * Title: Fifth Brother
 */
public class Card501_003 extends AbstractImperial {
    public Card501_003() {
        super(Side.DARK, 2, 4, 4, 5, 6, "Fifth Brother", Uniqueness.UNIQUE);
        setLore("Inquisitor.");
        setGameText("Power +3 and defense value -2 if with a Jedi, padawan or hatred card. Characters here may not have their forfeit value increased.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeyword(Keyword.INQUISITOR);
        setTestingText("Fifth Brother");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        WithCondition withJediPadawanOrHatredCardCondition = new WithCondition(self, Filters.or(Filters.Jedi, Filters.padawan, Filters.hasStacked(Filters.hatredCard)));
        Filter charactersHere = Filters.and(Filters.character, Filters.here(self));

        modifiers.add(new PowerModifier(self, withJediPadawanOrHatredCardCondition, 3));
        modifiers.add(new DefenseValueModifier(self, withJediPadawanOrHatredCardCondition, -2));
        modifiers.add(new MayNotHaveForfeitValueIncreasedModifier(self, charactersHere));
        return modifiers;
    }
}
