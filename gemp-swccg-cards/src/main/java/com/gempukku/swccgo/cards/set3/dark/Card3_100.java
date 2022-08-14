package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.evaluators.CalculateCardVariableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Death Squadron
 */
public class Card3_100 extends AbstractNormalEffect {
    public Card3_100() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Death_Squadron, Uniqueness.UNIQUE);
        setLore("'Make ready to land our troops beyond their energy field and deploy the fleet so that nothing gets off the system.'");
        setGameText("Deploy on any system. Adds X to total power of your starships at that system, where X = number of your starships present. Your troopers and combat vehicles may shuttle to related sites for free.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        final int permCardId = self.getPermanentCardId();
        modifiers.add(new TotalPowerModifier(self, Filters.sameSystem(self),
                new CalculateCardVariableEvaluator(self, Variable.X) {
                    @Override
                    protected float baseValueCalculation(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        PhysicalCard self = gameState.findCardByPermanentId(permCardId);

                        return Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.starship, Filters.present(self)));
                    }
                }, playerId));
        modifiers.add(new ShuttlesFreeToLocationModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.trooper,
                Filters.combat_vehicle)), Filters.relatedSite(self)));
        return modifiers;
    }
}