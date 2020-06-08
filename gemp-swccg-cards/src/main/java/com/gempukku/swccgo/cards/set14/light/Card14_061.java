package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Creature
 * Title: Kaadu
 */
public class Card14_061 extends AbstractCreatureVehicle {
    public Card14_061() {
        super(Side.LIGHT, 4, 1, 1, null, 2, 4, 3, "Kaadu");
        setLore("Used by many Gungan warriors due to their fearless nature. Kaadu lay large numbers of eggs to compensate for those eaten by predators.");
        setGameText("May add 1 'rider' (passenger). Ability = 1/2. Moves for free. While 'ridden' by a Gungan, may move as a 'react.' While at a swamp, adds 1 to your Force drains there.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.KAADU);
        setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextAbilityModifier(self, 0.5));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesForFreeModifier(self));
        modifiers.add(new MayMoveAsReactModifier(self, new HasAboardCondition(self, Filters.Gungan)));
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new AtCondition(self, Filters.swamp), 1, playerId));
        return modifiers;
    }
}
