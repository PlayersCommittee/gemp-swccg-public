package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Yorr
 */
public class Card9_103 extends AbstractImperial {
    public Card9_103() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Yorr, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Former member of Imperial Demonstration Team. Flew test flights during development of various TIE prototypes. Jendon's wingman. Has scored 24 combat victories.");
        setGameText("Adds 2 to power of anything he pilots (3 if a starfighter). When piloting a TIE Defender and with Jendon, adds one battle destiny.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeyword(Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.starfighter)));
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotingCondition(self, Filters.TIE_Defender),
                new WithCondition(self, Filters.Jendon)), 1));
        return modifiers;
    }
}
