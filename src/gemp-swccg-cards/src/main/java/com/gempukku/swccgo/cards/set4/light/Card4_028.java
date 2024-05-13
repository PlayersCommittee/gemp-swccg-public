package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: No Disintegrations!
 */
public class Card4_028 extends AbstractNormalEffect {
    public Card4_028() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "No Disintegrations!", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("'There will be a substantial reward for the one who finds the Millennium Falcon. You are free to use any methods necessary, but I want them alive.'");
        setGameText("Use 2 Force to deploy on your side of table. If a Rebel of ability > 2 is lost (not captured) during a battle involving an opponent's bounty hunter, opponent loses 3 Force. If Vader on table, one bounty hunter involved in that battle (your choice) is also lost.");
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
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.Rebel, Filters.abilityMoreThan(2)))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.bounty_hunter))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 3 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 3));
            if (GameConditions.canSpot(game, self, Filters.Vader)) {
                action.appendEffect(
                        new ChooseCardToLoseFromTableEffect(action, playerId, Filters.and(Filters.bounty_hunter, Filters.participatingInBattle)));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}