package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.*;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Jloc'itaome'faottas, Diplomat
 */
public class Card304_126 extends AbstractAlien {
    public Card304_126() {
        super(Side.LIGHT, 3, 7, 2, 3, 8, "Jloc'itaome'faottas, Diplomat", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Locita, presented himself as a gangster to get close to clan Tiure. In reality he's a diplomat from the Chiss Ascendancy seeking to influence the course of Scholae Palatinae.");
        setGameText("Deploys -3 to Ferfiek Chawa, Ulress, or Koudooine. While with Thran, he may not add battle destiny or play an Interrupt from Lost Pile. Subtracts 6 from any attempt to cross him over (even if captured). Immune to attrition < 5 (< 6 if alone). ");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.GANGSTER);
        addPersona(Persona.LOCITA);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.or(Filters.Deploys_aboard_Ferfiek_Chawa, Filters.Deploys_at_Ulress, Filters.Deploys_at_Koudooine)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        String opponent = game.getOpponent(self.getOwner());

        // Creating a couple of conditions, to make the bit of code a more managable length.

        Condition atOpponentsBG = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground));
        Condition withThran = new WithCondition(self, Filters.Thran);

        // Force Drains May Not Be Canceled:
        // - self: source of this text, this card.
        // - Filters.here(self): location where the force drain may not be canceled, where Bala-Tik is
        // - atOpponentsBG: self (Bala-Tik) is at opponent's battleground
        // - opponent: It is the opponent who may not cancel my force drains (still possible for me to cancel it)
        // - self.getOwner(): It is my force drains that can not be canceled by my opponent.

        modifiers.add(new ModifyGameTextModifier(self, Filters.Thran, withThran, ModifyGameTextType.THRAN__MAY_NOT_PLAY_INTERRUPT_FROM_LOST_PILE));
        modifiers.add(new MayNotAddBattleDestinyDrawsModifier(self, Filters.Thran, withThran));
        modifiers.add(new CrossOverAttemptTotalModifier(self, -6));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new ConditionEvaluator(5, 6, new AloneCondition(self))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition capturedOnly = new CapturedOnlyCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CrossOverAttemptTotalModifier(self, capturedOnly, -6));
        return modifiers;
    }
}
