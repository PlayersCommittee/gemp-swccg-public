package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CommuningCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Qui-Gon Jinn, An Old Friend
 */
public class Card216_037 extends AbstractJediMaster {
    public Card216_037() {
        super(Side.LIGHT, 1, 7, 6, 7, 8, Title.Master_QuiGon_Jinn_An_Old_Friend, Uniqueness.UNIQUE);
        setLore("");
        setGameText("While 'communing': You may not deploy Rebels; Jedi Council members are destiny +1; your total power in battles is +1 for each Jedi 'communing'; once per turn, may place a card from hand on Used Pile to draw top card of Force Pile.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_16, Icon.EPISODE_I);
        addPersona(Persona.QUIGON);
    }

    public List<Modifier> getWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployModifier(self, Filters.Rebel, new CommuningCondition(self), self.getOwner()));
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation, new CommuningCondition(self), new StackedEvaluator(self, Filters.Communing), self.getOwner()));
        modifiers.add(new DestinyModifier(self, Filters.Jedi_Council_member, new CommuningCondition(self), 1));
        return modifiers;
    }

    public List<TopLevelGameTextAction> getGameTextTopLevelWhileStackedActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        if (game.getModifiersQuerying().isCommuning(game.getGameState(), self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.numCardsInForcePile(game, playerId) >= 1
                && GameConditions.hasHand(game, playerId)) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

            action.setText("Place card from hand on Used Pile");
            action.setActionMsg("Place a card from hand on Used Pile to draw top card of Force Pile");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new PutCardFromHandOnUsedPileEffect(action, playerId));
            action.appendEffect(new DrawOneCardFromForcePileEffect(action, playerId));

            return Collections.singletonList(action);
        }
        return null;
    }
}