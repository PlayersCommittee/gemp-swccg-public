package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Freon Drevan
 */
public class Card12_005 extends AbstractAlien {
    public Card12_005() {
        super(Side.LIGHT, 3, 2, 3, 2, 4, "Freon Drevan", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Xexto who had his business license revoked when his partner siphoned away his credits, fled the system, and left him with the tax debt.");
        setGameText("Cancels game text of opponent's accountants and tax collectors at same or adjacent site. While in a battle at a site, your battle destiny draws may not be canceled, and you may not draw more than two battle destiny.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        setSpecies(Species.XEXTO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition inBattleAtSite = new InBattleAtCondition(self, Filters.site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.opponents(self),
                Filters.or(Filters.accountant, Filters.tax_collector), Filters.atSameOrAdjacentSite(self))));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, playerId, inBattleAtSite));
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, inBattleAtSite, 2, playerId));
        return modifiers;
    }
}
