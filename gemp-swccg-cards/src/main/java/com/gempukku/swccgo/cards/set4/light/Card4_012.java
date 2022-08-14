package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.conditions.HasAttachedCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayCarryPassengerAsIfCreatureVehicleModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseAnyNumberOfDevicesModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalTrainingDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Device
 * Title: Luke's Backpack
 */
public class Card4_012 extends AbstractCharacterDevice {
    public Card4_012() {
        super(Side.LIGHT, 6, Title.Lukes_Backpack, Uniqueness.UNIQUE);
        setLore("Made on Dantooine. Luke used the many pockets in his gundark-skin backpack to carry supplies while on patrol.");
        setGameText("Deploy on any character. May carry and use any number of devices. May also carry Yoda, Kabe or any one Jawa or Ewok (as if riding a creature vehicle). Adds 1 to training destiny when carrying the Mentor.");
        addIcons(Icon.DAGOBAH);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.character;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseAnyNumberOfDevicesModifier(self, Filters.hasAttached(self)));
        modifiers.add(new MayCarryPassengerAsIfCreatureVehicleModifier(self, Filters.or(Filters.Yoda, Filters.Kabe, Filters.Jawa, Filters.Ewok)));
        modifiers.add(new TotalTrainingDestinyModifier(self, new HasAttachedCondition(self, Filters.mentor), 1));
        return modifiers;
    }
}