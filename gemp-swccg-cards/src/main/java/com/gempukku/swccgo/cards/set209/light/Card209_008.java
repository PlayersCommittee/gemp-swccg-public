package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Alien
 * Title: Lak Sivrak
 */
public class Card209_008 extends AbstractAlien {
    public Card209_008() {
        super(Side.LIGHT, 2, 3, 4, 2, 5, "Lak Sivrak", Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLore("Lak Sivrak is a typical Shistavanen male. Ferocious, but not aggressive. Often trained as scouts at Imperial academies, but they despise the ambitions of the New Order.");
        setGameText("[Pilot]2. May move as a 'react' for free. While at opponent's battleground, may not be targeted by opponent's Interrupts and your Force drains here may not be canceled or reduced by opponent.");
        addIcons(Icon. PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.SHISTAVANEN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        Condition atOpponentsBattleground = new AtCondition(self, Filters.and(Filters.opponents(self), Filters.battleground));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayMoveAsReactForFreeModifier(self));
        modifiers.add(new MayNotBeTargetedByModifier(self, atOpponentsBattleground, Filters.and(Filters.opponents(self), Filters.Interrupt)));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.here(self), atOpponentsBattleground, opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.here(self), atOpponentsBattleground, opponent, playerId));
        return modifiers;
    }

}
