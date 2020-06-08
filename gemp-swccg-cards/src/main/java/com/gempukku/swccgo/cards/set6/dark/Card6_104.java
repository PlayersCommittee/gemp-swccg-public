package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Gailid
 */
public class Card6_104 extends AbstractAlien {
    public Card6_104() {
        super(Side.DARK, 2, 3, 1, 1, 2, Title.Gailid, Uniqueness.UNIQUE);
        setLore("Mosep's assistant. Accountant. Tax collector. Enjoys tending to Jabba's skiffs with Barada.");
        setGameText("Deploys free to same site as Mosep. Adds 2 to power of anything he pilots. While at Audience Chamber, adds 1 to your Force drains at Jabba's Palace sites.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.ACCOUNTANT, Keyword.TAX_COLLECTOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.sameSiteAs(self, Filters.Mosep)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.Jabbas_Palace_site, new AtCondition(self, Filters.Audience_Chamber), 1, self.getOwner()));
        return modifiers;
    }
}
