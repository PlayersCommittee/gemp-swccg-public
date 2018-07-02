package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PlayingSabaccCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayHaveDestinyNumberClonedInSabaccModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Devaronian
 */
public class Card6_010 extends AbstractAlien {
    public Card6_010() {
        super(Side.LIGHT, 3, 3, 1, 2, 2, "Devaronian");
        setLore("Adept at sneaking through corridors and alleyways. Devaronians frequently surprise opponents. Regarded as drunkards, gamblers and fools by many species.");
        setGameText("Power +2 at Mos Eisley, any mobile site or any docking bay. Adds 2 to power of anything he pilots. When playing sabacc, may use clone cards to 'clone' his own destiny number.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.DEVARONIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Mos_Eisley, Filters.mobile_site, Filters.docking_bay)), 2));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayHaveDestinyNumberClonedInSabaccModifier(self, new PlayingSabaccCondition(self), self.getOwner()));
        return modifiers;
    }
}
