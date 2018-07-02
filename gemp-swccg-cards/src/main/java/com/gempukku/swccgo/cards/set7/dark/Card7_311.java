package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Combat
 * Title: Combat Cloud Car
 */
public class Card7_311 extends AbstractCombatVehicle {
    public Card7_311() {
        super(Side.DARK, 3, 3, 3, null, 4, 5, 4, "Combat Cloud Car", Uniqueness.RESTRICTED_3);
        setLore("Enclosed Ubrikkian Talon I Combat Cloud Car. Speeds up to 1,875 kph. Exceptional maneuverability makes this a favorite of outer Rim security forces.");
        setGameText("Permanent pilot provides ability of 2. At cloud sectors, power and maneuver +1, may deploy as a 'react' and may move and be targeted by weapons like a starfighter.");
        addModelType(ModelType.TALON_I_COMBAT_CLOUD_CAR);
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        addKeywords(Keyword.ENCLOSED);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atCloudSector = new AtCondition(self, Filters.cloud_sector);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atCloudSector, 1));
        modifiers.add(new ManeuverModifier(self, atCloudSector, 1));
        modifiers.add(new MayBeTargetedByWeaponsLikeStarfighterModifier(self, atCloudSector));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactToLocationModifier(self, Filters.cloud_sector));
        return modifiers;
    }

}
