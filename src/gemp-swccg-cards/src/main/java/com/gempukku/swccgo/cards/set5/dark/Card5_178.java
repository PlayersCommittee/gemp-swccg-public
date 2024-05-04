package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsLikeStarfighterModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Cloud City
 * Type: Vehicle
 * Subtype: Combat
 * Title: Cloud Car
 */
public class Card5_178 extends AbstractCombatVehicle {
    public Card5_178() {
        super(Side.DARK, 2, 2, 2, null, 3, 4, 3, "Cloud Car", Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("Bespin Motors' enclosed upper atmosphere craft. Utilizes a combination of ion engines and powerful repulsorlift drives to gain maximum power at high altitudes.");
        setGameText("May add 1 pilot or passenger. At cloud sectors, power +1, may deploy as a 'react' and may move and be targeted by weapons like a starfighter. Permanent pilot aboard provides ability of 1.");
        addModelType(ModelType.TWIN_POD_CLOUD_CAR);
        addIcons(Icon.CLOUD_CITY, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atCloudSector = new AtCondition(self, Filters.cloud_sector);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atCloudSector, 1));
        modifiers.add(new MayBeTargetedByWeaponsLikeStarfighterModifier(self, atCloudSector));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.cloud_sector));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(1) {});
    }
}
