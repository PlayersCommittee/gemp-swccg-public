package com.gempukku.swccgo.cards.set226.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithTopCardOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Effect
 * Title: Information Exchange (V)
 */
public class Card226_007 extends AbstractNormalEffect {
    public Card226_007() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Information_Exchange, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("'Chisa nyooda ishaley. Kun Jabba neguda len malta.' 'Ikkit ui! Yobbit, yobbit. Nelan tui ke bada.'");
        setGameText("Deploy on table. Black Sun agents are defense value +1 and forfeit +1. Once per turn, if you just deployed an information broker, may exchange the top card of Force Pile with any one card in hand. [Reflections II] Emperor deploys -1 and moves for free. [Immune to Alter.]");
        addIcons(Icon.JABBAS_PALACE, Icon.REFLECTIONS_II, Icon.VIRTUAL_SET_26);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
    }
    
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter ref2Emperor = Filters.and(Icon.REFLECTIONS_II, Filters.Emperor);
        modifiers.add(new DefenseValueModifier(self, Filters.Black_Sun_agent, 1));
        modifiers.add(new ForfeitModifier(self, Filters.Black_Sun_agent, 1));
        modifiers.add(new DeployCostModifier(self, ref2Emperor, -1));
        modifiers.add(new MovesForFreeModifier(self, ref2Emperor));
        return modifiers;
    }


    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && TriggerConditions.justDeployed(game, effectResult, playerId, Filters.information_broker)
                && GameConditions.hasForcePile(game, playerId)
                && GameConditions.hasHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange Card");
            action.setActionMsg("Exchange the top card of Force Pile with a card in hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardInHandWithTopCardOfForcePileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
