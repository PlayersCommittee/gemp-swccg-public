package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DisarmCharacterEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotCarryModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Subtype: Immediate
 * Title: Mostly Armless
 */
public class Card5_121 extends AbstractImmediateEffect {
    public Card5_121() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Mostly_Armless, Uniqueness.UNIQUE);
        setLore("One lesson learned in Jedi training is that when you have been injured, don't panic.");
        setGameText("If your character with a lightsaber just won a battle, deploy on an opponent's character present. Character is Disarmed (power -1 and may no longer carry weapons). Opponent loses 1 Force at the end of each opponent's turns. (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DISARMING_CARD);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter opponentsCharacterPresent = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_DISARMED));

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, Filters.and(Filters.your(self), Filters.character_with_a_lightsaber))) {

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, opponentsCharacterPresent, null);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard character = self.getAttachedTo();

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Disarm " + GameUtils.getFullName(character));
            action.setActionMsg("Disarm " + GameUtils.getCardLink(character));
            // Perform result(s)
            action.appendEffect(
                    new DisarmCharacterEffect(action, character, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, opponent)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter hasAttached = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, hasAttached, -1));
        modifiers.add(new MayNotCarryModifier(self, hasAttached, Filters.weapon));
        return modifiers;
    }
}