package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.RaceTotalMoreThanCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
 * Set: Tatooine
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Podrace Arena
 */
public class Card11_094 extends AbstractSite {
    public Card11_094() {
        super(Side.DARK, Title.Podrace_Arena, Title.Tatooine);
        setLocationDarkSideGameText("While either player's race total > 0, no battles or Force drains here.");
        setLocationLightSideGameText("While either player's race total > 0, no battles or Force drains here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET);
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