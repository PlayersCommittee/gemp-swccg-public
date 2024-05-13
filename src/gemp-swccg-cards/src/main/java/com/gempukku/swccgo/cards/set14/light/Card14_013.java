package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostUsingLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Alien
 * Title: Gungan Guard
 */
public class Card14_013 extends AbstractAlien {
    public Card14_013() {
        super(Side.LIGHT, 2, 2, 0, 1, 2, "Gungan Guard", Uniqueness.UNRESTRICTED, ExpansionSet.THEED_PALACE, Rarity.C);
        setLore("Equipped with portable Gungan shields, these front line troops provide a secondary defense against blaster fire.");
        setGameText("Your Gungan leaders present may not be targeted by weapons. While defending a battle on Naboo, Power +4 and, whenever you draw a Gungan for battle destiny, add 3 to that destiny. Requires +2 Force to use landspeed.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        setSpecies(Species.GUNGAN);
        addKeyword(Keyword.GUARD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition defendingOnNabooCondition = new AndCondition(new DefendingBattleCondition(self), new OnCondition(self, Title.Naboo));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.Gungan, Filters.leader,
                Filters.present(self))));
        modifiers.add(new PowerModifier(self, defendingOnNabooCondition, 4));
        modifiers.add(new DestinyWhenDrawnForBattleDestinyModifier(self, Filters.and(Filters.your(self), Filters.Gungan),
                defendingOnNabooCondition, 3));
        modifiers.add(new MoveCostUsingLandspeedModifier(self, 2));
        return modifiers;
    }
}
