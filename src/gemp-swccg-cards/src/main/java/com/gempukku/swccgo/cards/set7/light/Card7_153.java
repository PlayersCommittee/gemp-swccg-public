package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByWeaponsLikeStarfighterModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Vehicle
 * Subtype: Transport
 * Title: Patrol Craft
 */
public class Card7_153 extends AbstractTransportVehicle {
    public Card7_153() {
        super(Side.LIGHT, 4, 1, 1, null, 3, 3, 3, Title.Patrol_Craft, Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Small, lightly armed, enclosed vehicle widely used throughout the galaxy. Sometimes used as a pleasure craft by wealthy eccentrics.");
        setGameText("Power +1 or Coruscant of Bespin. May add 1 driver and 1 passenger. May deploy or move as a 'react.' At cloud sectors, may move and be targeted by weapons like a starfighter.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.ENCLOSED);
        setDriverCapacity(1);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OrCondition(new AtCondition(self, Title.Coruscant), new AtCondition(self, Title.Bespin)), 1));
        modifiers.add(new MayMoveAsReactModifier(self));
        modifiers.add(new MayBeTargetedByWeaponsLikeStarfighterModifier(self, new AtCondition(self, Filters.cloud_sector)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }
}
