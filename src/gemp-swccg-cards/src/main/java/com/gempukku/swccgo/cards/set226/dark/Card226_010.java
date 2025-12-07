package com.gempukku.swccgo.cards.set226.dark;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.MayNotBattleUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Alien
 * Title: Rystall (V)
 */
public class Card226_010 extends AbstractAlien {
    public Card226_010() {
        super(Side.DARK, 3, 2, 1, 2, 3, "Rystall", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Musician. Raised by Ortolans. Grew up on the streets of Coruscant. Rescued from the Black Sun crime cartel by Lando Calrissian.");
        setGameText("Power and forfeit +2 at a Coruscant site. Once per turn, if you just deployed a Black Sun agent to same site, may retrieve 1 Force. If opponent's [Maintenance] card just deployed here, it may not battle for remainder of turn.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_26);
        addKeywords(Keyword.MUSICIAN, Keyword.FEMALE);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atCoruscantSite = new AtCondition(self, Filters.Coruscant_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atCoruscantSite, 2));
        modifiers.add(new ForfeitModifier(self, atCoruscantSite, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.Black_Sun_agent, Filters.sameSite(self))
                && GameConditions.hasLostPile(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, Filters.and(Filters.opponents(self), Icon.MAINTENANCE), Filters.here(self))) {

            final PlayCardResult playCardResult = (PlayCardResult) effectResult;
            final PhysicalCard playedCard = playCardResult.getPlayedCard();
            
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Prevent " + GameUtils.getFullName(playedCard) + " from battling");
            action.addAnimationGroup(playedCard);
            // Perform result(s)
            action.appendEffect(
                    new MayNotBattleUntilEndOfTurnEffect(action, playedCard));
            return Collections.singletonList(action);
        }
        return null;
    }
}
