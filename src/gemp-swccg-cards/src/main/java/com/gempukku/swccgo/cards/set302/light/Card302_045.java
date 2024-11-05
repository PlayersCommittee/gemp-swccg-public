package com.gempukku.swccgo.cards.set302.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.RaceTotalMoreThanCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateBattleAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dark Jedi Brotherhood Core
 * Type: Location
 * Subtype: Site
 * Title: Dandoran: Nevo Race Track
 */
public class Card302_045 extends AbstractSite {
    public Card302_045() {
        super(Side.LIGHT, Title.Nevo_Race_Track, Title.Dandoran, Uniqueness.UNIQUE, ExpansionSet.DJB_CORE, Rarity.V);
        setLocationDarkSideGameText("While either player's race total > 0, no battles or Force drains here.");
        setLocationLightSideGameText("While either player's race total > 0, no battles or Force drains here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition eitherRaceTotalMoreThanZero = new OrCondition(new RaceTotalMoreThanCondition(playerOnDarkSideOfLocation, 0),
                new RaceTotalMoreThanCondition(game.getOpponent(playerOnDarkSideOfLocation), 0));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, eitherRaceTotalMoreThanZero));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, eitherRaceTotalMoreThanZero));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition eitherRaceTotalMoreThanZero = new OrCondition(new RaceTotalMoreThanCondition(playerOnLightSideOfLocation, 0),
                new RaceTotalMoreThanCondition(game.getOpponent(playerOnLightSideOfLocation), 0));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotInitiateBattleAtLocationModifier(self, eitherRaceTotalMoreThanZero));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, eitherRaceTotalMoreThanZero));
        return modifiers;
    }
}