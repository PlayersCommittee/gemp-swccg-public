package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Gela Yeens
 */
public class Card7_178 extends AbstractAlien {
    public Card7_178() {
        super(Side.DARK, 1, 3, 2, 2, 3, "Gela Yeens", Uniqueness.UNIQUE);
        setLore("Bad-tempered smuggler. Makes a few credits more helping Jabba collect debts. Highly regarded for his uncanny anticipation in battle. Searching for Debnoli.");
        setGameText("May deploy for free to your [Independent Starship] starship. Adds 2 to power of anything he pilots. Power +2 when present with and opponent's smuggler. When targeted by a weapon, subtract one from each weapon destiny.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeAboardModifier(self, Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.and(Filters.opponents(self), Filters.smuggler)), 2));
        modifiers.add(new EachWeaponDestinyForWeaponTargetingModifier(self, -1));
        return modifiers;
    }
}
