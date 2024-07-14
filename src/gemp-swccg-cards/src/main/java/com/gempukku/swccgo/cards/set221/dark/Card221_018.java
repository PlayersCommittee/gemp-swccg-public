package com.gempukku.swccgo.cards.set221.dark;

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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Deactivated Hyperdrive
 */
public class Card221_018 extends AbstractNormalEffect {
    public Card221_018() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Deactivated_Hyperdrive, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setGameText("Deploy on table. During opponent’s draw phase, if you occupy a battleground site and opponent does not, may place in Used Pile (if Systems Will Slip Through Your Fingers or a ‘liberated’ system on table, opponent loses 3 Force). [Reflections III] Falcon loses immunity to attrition.");
        addIcons(Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelImmunityToAttritionModifier(self, Filters.and(Icon.REFLECTIONS_III, Filters.Falcon)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.DRAW)
                && GameConditions.occupies(game, playerId, Filters.battleground_site)
                && !GameConditions.occupies(game, opponent, Filters.battleground_site)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");

            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));

            if (GameConditions.canTarget(game, self, Filters.Systems_Will_Slip_Through_Your_Fingers)
                    || GameConditions.canTarget(game, self, Filters.liberated_system)) {
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
            }

            return Collections.singletonList(action);
        }
        return null;
    }
}