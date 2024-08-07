package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Block D
 * Type: Defensive Shield
 * Title: Weapons Display (V)
 */
public class Card601_069 extends AbstractDefensiveShield {
    public Card601_069() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Weapons Display", ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("The X-wing's display panel allows for different firing patterns for different weapons. This gives the pilot the ability to switch weapon types with minimum time and energy loss.");
        setGameText("Plays on table. Whenever opponent excludes any character(s) from battle, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and a system) and opponent deployed a card with ability and did not initiate a battle, may retrieve 1 Force.");
        addIcons(Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_D);
        setAsLegacy(true);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justExcludedFromBattle(game, effectResult, opponent, Filters.character)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make " + opponent + " lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && GameConditions.hasDeployedAtLeastXCardsWithAbilityThisTurn(game, opponent, 1, Filters.any)
                && !GameConditions.hasInitiatedBattleThisTurn(game, opponent)
                && GameConditions.controls(game, playerId, Filters.battleground_site)
                && GameConditions.controls(game, playerId, Filters.battleground_system)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}