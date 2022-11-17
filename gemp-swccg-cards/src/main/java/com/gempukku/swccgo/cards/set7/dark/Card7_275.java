package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Coruscant
 */
public class Card7_275 extends AbstractSystem {
    public Card7_275() {
        super(Side.DARK, Title.Coruscant, 0, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLocationDarkSideGameText("If you control with a Star Destroyer, Force drain +1 here and opponent may not deploy Rebels to related sites.");
        setLocationLightSideGameText("Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition controlWithStarDestroyer = new ControlsWithCondition(playerOnDarkSideOfLocation, self, Filters.Star_Destroyer);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, controlWithStarDestroyer, 1, playerOnDarkSideOfLocation));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(playerOnDarkSideOfLocation), Filters.Rebel),
                controlWithStarDestroyer, Filters.relatedSite(self)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}