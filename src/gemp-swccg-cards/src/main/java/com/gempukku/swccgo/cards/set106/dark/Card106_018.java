package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Tusken Canyon
 */
public class Card106_018 extends AbstractSite {
    public Card106_018() {
        super(Side.DARK, Title.Tusken_Canyon, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.OTSD, Rarity.PM);
        setLocationDarkSideGameText("If you control, Force drain +1 here (+2 if a Gaderffi Stick is present).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PREMIUM, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeyword(Keyword.CANYON);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new ControlsCondition(playerOnDarkSideOfLocation, self),
                new ConditionEvaluator(1, 2, new PresentCondition(self, Filters.Gaderffii_Stick)), playerOnDarkSideOfLocation));
        return modifiers;
    }
}