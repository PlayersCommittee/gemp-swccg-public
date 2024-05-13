package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostAboardModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Outer Rim Scout
 */
public class Card7_195 extends AbstractAlien {
    public Card7_195() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Outer Rim Scout", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Smugglers and scouts employed by the ISB. Search the far reaches of the galaxy for Rebel activity and habitable worlds. Know all the smugglers' best hiding places.");
        setGameText("Deploy -1 to your [Independent] starship. Adds 2 to power of anything he pilots. When piloting a [Independent] starship, instead adds 3 to power and draws one battle destiny if not able to otherwise. Suspends Asteroid Sanctuary at related sector.");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.SMUGGLER, Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.and(Icon.INDEPENDENT, Filters.starship))));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.and(Icon.INDEPENDENT, Filters.starship)), 1));
        modifiers.add(new SuspendsCardModifier(self, Filters.and(Filters.Asteroid_Sanctuary, Filters.at(Filters.relatedSector(self)))));
        return modifiers;
    }
}
