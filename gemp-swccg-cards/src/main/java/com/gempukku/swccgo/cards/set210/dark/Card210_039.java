package com.gempukku.swccgo.cards.set210.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 10
 * Type: Location
 * Subtype: System
 * Title: Mustafar
 */
public class Card210_039 extends AbstractSystem {
    public Card210_039() {
        super(Side.DARK, Title.Mustafar, 7);
        setLocationDarkSideGameText("While Anakin or Vader here, gains one [Dark Side] icon and one [Light Side] icon.");
        setLocationLightSideGameText("While Vader on table, unless Padme at a Mustafar location, Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.VIRTUAL_SET_10, Icon.PLANET, Icon.EPISODE_I);
        setTestingText("Mustafar (ERRATA)");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IconModifier(self, self, new HereCondition(self, Filters.or(Filters.Vader, Filters.Anakin)), Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, self, new HereCondition(self, Filters.or(Filters.Vader, Filters.Anakin)), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        OnTableCondition VaderOnTable = new OnTableCondition(self, Filters.Vader);
        NotCondition PadmeNotAtMustafarLocation = new NotCondition(new AtCondition(self, Filters.Amidala, Filters.Mustafar_Location));

        modifiers.add(new ForceDrainModifier(self, new AndCondition(new ControlsCondition(playerOnLightSideOfLocation, self),
                VaderOnTable, PadmeNotAtMustafarLocation), -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}