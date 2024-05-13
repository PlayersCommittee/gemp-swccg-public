package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawOneCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotIncreaseTotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Effect
 * Title: Hyperwave Scan (V)
 */
public class Card221_022 extends AbstractNormalEffect {
    public Card221_022() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Hyperwave Scan", Uniqueness.UNRESTRICTED, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Full Imperial scans include full-spectrum transceivers, dedicated energy receptors, crystal gravfield traps, and hyperwave signal interceptors.");
        setGameText("Deploy on a location. At same battleground, opponent may not cancel or reduce your Force drains here. Forfeit values and total battle destiny may not be increased here. You initiate battles here for free. If you just initiated battle here, may draw top card of Reserve Deck.");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_21);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.location;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, Filters.and(Filters.here(self), Filters.battleground), opponent, playerId));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, Filters.and(Filters.here(self), Filters.battleground), opponent, playerId));
        modifiers.add(new MayNotHaveForfeitValueIncreasedModifier(self, Filters.here(self)));
        modifiers.add(new MayNotIncreaseTotalBattleDestinyModifier(self, new DuringBattleAtCondition(Filters.here(self))));
        modifiers.add(new InitiateBattlesForFreeModifier(self, Filters.here(self), playerId));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if(TriggerConditions.battleInitiatedAt(game, effectResult, playerId, Filters.here(self))
                && GameConditions.hasReserveDeck(game, playerId))   {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");

            action.appendEffect(
                    new DrawOneCardFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}