package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Location
 * Subtype: System
 * Title: Ord Mantell
 */
public class Card3_064 extends AbstractSystem {
    public Card3_064() {
        super(Side.LIGHT, Title.Ord_Mantell, 7, ExpansionSet.HOTH, Rarity.U2);
        setLocationDarkSideGameText("Each of your bounty hunter pilots add an additional 1 to power of starships they pilot here.");
        setLocationLightSideGameText("If you control, each of opponent's bounty hunters is forfeit -2.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(final String playerOnDarkSideOfLocation, final SwccgGame game, PhysicalCard self) {
        final Filter yourBountyHunterPilots = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.bounty_hunter, Filters.pilot);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.starship, Filters.here(self), Filters.hasPiloting(self, yourBountyHunterPilots)),
                new BaseEvaluator() {
                        @Override
                        public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard ship) {
                            PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                            return Filters.countActive(game, self, Filters.and(yourBountyHunterPilots, Filters.piloting(ship)));
                        }
                    }));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.bounty_hunter),
                new ControlsCondition(playerOnLightSideOfLocation, self), -2));
        return modifiers;
    }
}