package com.gempukku.swccgo.cards.set219.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 19
 * Type: Location
 * Subtype: System
 * Title: Lothal
 */
public class Card219_010 extends AbstractSystem {
    public Card219_010() {
        super(Side.DARK, Title.Lothal, 6, ExpansionSet.SET_19, Rarity.V);
        setLocationDarkSideGameText("While you occupy with an admiral, gains one [Dark Side] icon.");
        setLocationLightSideGameText("While you control, opponent occupies with an admiral, or Lothal converted, gains one [Light Side] icon.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.PLANET, Icon.VIRTUAL_SET_19);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition occupyWithAdmiralCondition = new OccupiesWithCondition(playerOnDarkSideOfLocation, self, Filters.admiral);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, occupyWithAdmiralCondition, Icon.DARK_FORCE, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, final SwccgGame game, final PhysicalCard self) {
        Condition opponentOccupiesWithAdmiralCondition = new OccupiesWithCondition(game.getOpponent(playerOnLightSideOfLocation), self, Filters.admiral);
        Condition isLothalConverted = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return GameConditions.canSpotConvertedLocation(game, Filters.Lothal_system);
            }
        };

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, new OrCondition(new ControlsCondition(playerOnLightSideOfLocation, self), opponentOccupiesWithAdmiralCondition, isLothalConverted), Icon.LIGHT_FORCE, 1));
        return modifiers;
    }
}