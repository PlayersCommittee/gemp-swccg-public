package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Order To Engage
 */
public class Card4_030 extends AbstractNormalEffect {
    public Card4_030() {
        super(Side.LIGHT, 4, PlayCardZoneOption.OPPONENTS_SIDE_OF_TABLE, Title.Order_To_Engage, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Political pressure forced the Empire to join battle with the Alliance in otherwise tactically poor situations. Failure to engage the enemy was considered tantamount to treason.");
        setGameText("Use 2 Force to deploy on opponent's side of table. At the end of each of opponent's battle phases, if a battle did not take place at every location where both players have presence at the end of that phase, opponent loses 3 Force.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.BATTLE)
                && GameConditions.canSpotLocation(game, Filters.and(Filters.battleNotOccurredAtLocation, Filters.occupies(playerId), Filters.occupies(opponent)))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 3 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}