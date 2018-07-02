package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Transport
 * Title: Gian Speeder
 */
public class Card14_060 extends AbstractTransportVehicle {
    public Card14_060() {
        super(Side.LIGHT, 2, 2, 2, null, 4, 3, 3, "Gian Speeder");
        setLore("Military repulsorlift armed with three light repeating blasters. With a top speed of 160 kilometers per hour, they have the ability to out-flank an opponent in battle.");
        setGameText("May add 1 driver and 3 passengers. May move as a 'react' to Naboo sites. Your battle destiny draws are +1 for each of your Royal Naboo Security present. (Limit +3.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        setDriverCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactToLocationModifier(self, Filters.Naboo_site));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.sameSite(self),
                new MaxLimitEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.Royal_Naboo_Security)), 3),
                self.getOwner()));
        return modifiers;
    }
}
