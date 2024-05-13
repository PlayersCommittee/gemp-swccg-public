package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedBySpecificWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponUserModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Imperial
 * Title: Velken Tezeri (V)
 */
public class Card205_016 extends AbstractAlien {
    public Card205_016() {
        super(Side.DARK, 3, 2, 2, 2, 4, "Velken Tezeri", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Assigned by Jabba to work with Hermi Odle. Former technician for the Empire. Developed a method to remotely control seekers. Plotting to kill Jabba.");
        setGameText("[Pilot] 2. Smuggler. If present at a pit, adds 1 to your Force drains here. While present at a site, unless 'hit' (or Gamall Wironicc here), your other characters here may not be targeted by lightsabers. May not target Luke with weapons.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new PresentAtCondition(self, Filters.pit), 1, playerId));
        modifiers.add(new MayNotBeTargetedBySpecificWeaponsModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.character, Filters.here(self)),
                new AndCondition(new PresentAtCondition(self, Filters.site), new UnlessCondition(new OrCondition(new HitCondition(self), new HereCondition(self, Filters.Gamall_Wironicc)))),
                Filters.lightsaber));
        modifiers.add(new MayNotBeTargetedByWeaponUserModifier(self, Filters.Luke, self));
        return modifiers;
    }
}
