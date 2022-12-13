package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.PilotingAtCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: OS-72-10
 */
public class Card7_194 extends AbstractImperial {
    public Card7_194() {
        super(Side.DARK, 2, 3, 3, 2, 3, Title.OS_72_10, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Pilot of Obsidian 10. Has served aboard the Conquest, the Thunderflare and the Executor. Not interested in advancing his rank as it would remove him from the pilot's seat.");
        setGameText("Adds 2 to power of anything he pilots (3 if any TIE). When piloting a TIE at a cloud sector, adds one battle destiny and makes that TIE immune to attrition < 4 (< 6 if Obsidian 10).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Obsidian_10);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingAtCloudSector = new PilotingAtCondition(self, Filters.TIE, Filters.cloud_sector);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.TIE)));
        modifiers.add(new AddsBattleDestinyModifier(self, pilotingAtCloudSector, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.hasPiloting(self), pilotingAtCloudSector,
                new CardMatchesEvaluator(4, 6, Filters.Obsidian_10)));
        return modifiers;
    }
}
