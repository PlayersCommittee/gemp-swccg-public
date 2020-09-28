package com.gempukku.swccgo.cards.set209.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Rebel
 * Title: Taidu Sefla
 */
public class Card209_013 extends AbstractRebel {
    public Card209_013() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Taidu Sefla", Uniqueness.UNIQUE);
        setLore("Trooper.");
        setGameText("When deployed (or just lost), may draw bottom card of Used Pile. During battle, may subtract X (limit 3) from a just drawn weapon or battle destiny, where X = number of your spies out of play. Your spies here are immune to Nevar Yalnal.");
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_9);
        addPersona(Persona.SEFLA);
        addKeywords(Keyword.TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.spy, Filters.here(self)), Title.Nevar_Yalnal));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s) for just deployed response
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasUsedPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw bottom card of Used Pile");
            action.setActionMsg("Draw bottom card of Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfUsedPileEffect(action, playerId));
            actions.add(action);
        }

        // Check condition(s) for during battle response
        if ((TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult))
                && GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)) {

            //Find spies out of play
            Collection<PhysicalCard> outOfPlaySpies = Filters.filterCount(game.getGameState().getAllOutOfPlayCards(), game, 3, Filters.and(Filters.your(playerId), Filters.spy));
            int numSpiesOutOfPlay = outOfPlaySpies.size();
            int destinyModifier = 0 - numSpiesOutOfPlay;

            if (numSpiesOutOfPlay > 0) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Subtract " + numSpiesOutOfPlay + " from destiny");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new ModifyDestinyEffect(action, destinyModifier));
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s) for just lost response
        if(TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.hasUsedPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw bottom card of Used Pile");
            action.setActionMsg("Draw bottom card of Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfUsedPileEffect(action, playerId));
            return Collections.singletonList(action);
        }

        return null;
    }
}
