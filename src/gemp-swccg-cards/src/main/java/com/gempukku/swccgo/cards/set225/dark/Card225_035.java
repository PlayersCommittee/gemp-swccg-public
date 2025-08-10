package com.gempukku.swccgo.cards.set225.dark;

import com.gempukku.swccgo.cards.AbstractFirstOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OutOfPlayEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: First Order
 * Title: Vicrul
 */
public class Card225_035 extends AbstractFirstOrder {
    public Card225_035() {
        super(Side.DARK, 2, 4, 4, 4, 7, "Vicrul", Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Knight of Ren.");
        setGameText("[Pilot] 2. Power +1 for each of opponent's cards out of play. Opponent may not cancel your battle destiny draws where you have Kylo or a Knight of Ren. If you just initiated a Force drain here, may place topmost Interrupt of opponent's Lost Pile out of play.");
        addIcons(Icon.EPISODE_VII, Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_25);
        addKeywords(Keyword.KNIGHT_OF_REN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new OutOfPlayEvaluator(self, Filters.any, opponent)));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.sameLocationAs(self, Filters.or(Filters.Kylo, Filters.Knight_of_Ren)), playerId, opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.here(self))
                && GameConditions.hasLostPile(game, opponent)) {
            
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);

            action.setText("Place opponent's card out of play.");
            action.setActionMsg("Place topmost Interrupt of opponent's Lost Pile out of play");

            //Perform result(s)
            action.appendEffect(
                new PlaceCardOutOfPlayFromLostPileEffect(action, playerId, opponent, Filters.Interrupt, false, true)
            );
            actions.add(action);
        }

        return actions;
    }

}
