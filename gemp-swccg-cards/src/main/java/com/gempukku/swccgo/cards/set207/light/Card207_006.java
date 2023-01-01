package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotSubstituteBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Rebel
 * Title: Kin Kian (V)
 */
public class Card207_006 extends AbstractRebel {
    public Card207_006() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Kian, Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rogue Squadron pilot. Assigned as gunner aboard Colonel Salm's Y-wing at Battle of Endor, as part of Gray Squadron. Former member of Aggressor Squadron.");
        setGameText("[Pilot] 2. Matching pilot for any ‘snub’ fighter. While piloting a ‘snub’ fighter, draws one battle destiny if unable to otherwise and opponent may not substitute battle destiny draws here.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.ROGUE_SQUADRON, Keyword.GUNNER, Keyword.GRAY_SQUADRON);
        setMatchingStarshipFilter(Filters.snub_fighter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition whilePilotingSnubFighter = new PilotingCondition(self, Filters.snub_fighter);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, whilePilotingSnubFighter, 1));
        modifiers.add(new MayNotSubstituteBattleDestinyModifier(self, Filters.here(self), whilePilotingSnubFighter, opponent));
        return modifiers;
    }
}
