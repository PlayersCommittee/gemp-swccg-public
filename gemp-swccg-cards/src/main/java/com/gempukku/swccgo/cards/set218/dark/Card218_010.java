package com.gempukku.swccgo.cards.set218.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Location
 * Subtype: System
 * Title: Hosnian Prime
 */
public class Card218_010 extends AbstractSystem {
    public Card218_010() {
        super(Side.DARK, Title.Hosnian_Prime, 1);
        setLocationDarkSideGameText("While your [Episode VII] objective on table and your First Order leader here, Menace Fades is suspended.");
        setLocationLightSideGameText("If Starkiller Base on table, your Force generation is -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.PLANET, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new SuspendsCardModifier(self, Filters.title(Title.Menace_Fades),
                new AndCondition(new OnTableCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.EPISODE_VII, Filters.Objective)),
                        new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.First_Order_character, Filters.leader)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceGenerationModifier(self, new OnTableCondition(self, Filters.Starkiller_Base_system), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}