package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Zutton
 */
public class Card2_024 extends AbstractAlien {
    public Card2_024() {
        super(Side.LIGHT, 2, 3, 2, 2, 2, Title.Zutton, Uniqueness.UNIQUE);
        setLore("Snivvian also known as 'Snaggletooth.' A tortured artist who, like most Snivvians, is driven to live out the stories he creates.");
        setGameText("Where present, just before opponent draws battle destiny, you may use 1 Force to reduce opponent's total battle destiny by 1.");
        addIcons(Icon.A_NEW_HOPE);
        setSpecies(Species.SNIVVIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justBeforePlayersDrawBattleDestiny(game, effectResult)
                && GameConditions.isPresentAt(game, self, Filters.battleLocation)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce opponent's total battle destiny");
            action.setActionMsg("Reduce opponent's total battle destiny by 1");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new ModifyTotalBattleDestinyEffect(action, game.getOpponent(playerId), -1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
