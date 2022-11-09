package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextLandspeedModifier;
import com.gempukku.swccgo.logic.modifiers.FerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Creature
 * Title: Scurrier
 */
public class Card7_052 extends AbstractCreature {
    public Card7_052() {
        super(Side.LIGHT, 5, 2, 2, 2, 0, "Scurrier", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.F);
        setLore("Considered to be a nuisance. Feeds on garbage. Its only defense is its speed. Nasty bite attack when surprised or cornered. Grow as big as 1.2 meters long.");
        setGameText("Habitat: planet sites. Landspeed = 2. Ferocity +2 when present at Mos Eisley or any ◇ spaceport site.");
        addModelType(ModelType.SCAVENGER);
        addIcons(Icon.SPECIAL_EDITION, Icon.SELECTIVE_CREATURE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, final SwccgGame game, final PhysicalCard self) {
        return Filters.planet_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 2));
        modifiers.add(new FerocityModifier(self, new PresentAtCondition(self, Filters.or(Filters.Mos_Eisley, Filters.and(Filters.generic_site, Filters.spaceport_site))), 2));
        return modifiers;
    }
}
