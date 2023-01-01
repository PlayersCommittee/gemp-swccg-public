package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawsBattleDestinyIfUnableToOtherwiseEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 0
 * Type: Effect
 * Title: Imperial Decree (V)
 */
public class Card200_108 extends AbstractNormalEffect {
    public Card200_108() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Decree, Uniqueness.UNRESTRICTED, ExpansionSet.SET_0, Rarity.V);
        setVirtualSuffix(true);
        setLore("To Imperial command personnel: The Rebellion must be crushed! Minor acts of sedition are to be ignored. The destruction of the Alliance is your primary goal.");
        setGameText("Deploy on table. Whenever you lose Force (except from Force drains, battle damage, or your card), may reduce loss (to a minimum of 1) by the number of battlegrounds you occupy. During battle, may place this Effect out of play to draw one battle destiny if unable to otherwise. [Immune to Alter]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForceMoreThan(game, effectResult, playerId, 1)
                && !TriggerConditions.isAboutToLoseForceFromCard(game, effectResult, playerId, Filters.your(self))
                && !TriggerConditions.isAboutToLoseForceFromBattleDamage(game, effectResult, playerId)
                && !TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.any)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            int numToReduce = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.occupies(playerId)));
            if (numToReduce > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Reduce Force loss by " + numToReduce);
                action.setActionMsg("Reduce Force loss by " + numToReduce + " (to a minimum of 1)");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ReduceForceLossEffect(action, playerId, numToReduce, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place out of play");
            action.setActionMsg("Draw one battle destiny if unable to otherwise");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new DrawsBattleDestinyIfUnableToOtherwiseEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }

        return null;
    }
}