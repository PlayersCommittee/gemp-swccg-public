package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Effect
 * Title: Gungan Energy Shield
 */
public class Card14_032 extends AbstractNormalEffect {
    public Card14_032() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, Title.Gungan_Energy_Shield, Uniqueness.RESTRICTED_3);
        setLore("Gungan energy shields can only be passed through by slow-moving objects. This allowed the Gungans to neutralize the threat posed by long-range tank weapons.");
        setGameText("Deploy on an exterior site. While your Fambaa here: your characters here may not be targeted by weapons, opponent's battle destiny draws here are -1, and characters must use +1 Force when moving to or from here using their landspeed.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.exterior_site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Condition yourFambaaHere = new HereCondition(self, Filters.and(Filters.your(self), Filters.Fambaa));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(Filters.your(self), Filters.character, Filters.here(self)), yourFambaaHere));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.here(self), yourFambaaHere, -1, opponent));
        modifiers.add(new MoveCostToLocationUsingLandspeedModifier(self, Filters.character, yourFambaaHere, 1, Filters.here(self)));
        modifiers.add(new MoveCostFromLocationUsingLandspeedModifier(self, Filters.character, yourFambaaHere, 1, Filters.here(self)));
        return modifiers;
    }
}