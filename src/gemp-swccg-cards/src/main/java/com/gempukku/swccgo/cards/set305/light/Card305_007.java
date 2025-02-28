package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OutOfPlayEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Supreme Chancellor Ood Bnar
 */
public class Card305_007 extends AbstractAlien {
    public Card305_007() {
        super(Side.LIGHT, 1, 6, 5, 6, 7, "Supreme Chancellor Ood Bnar", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("A Neeti with amnesia, Ood Bnar has found himself a new home within the Brotherhood. Currently is the leader of Clan Taldryan serving as their Supreme Chancellor and the Left Hand of Justice.");
        setGameText("If drawn for destiny, may [upload] a [TAL] character or a lightsaber. While armed with a lightsaber, adds 2 to his defense value. Once per game, may deploy a lightsaber on Ood Bnar from Lost Pile. Immune to attrition < 6.");
        addIcons(Icon.WARRIOR, Icon.TAL);
        addKeywords(Keyword.LEADER, Keyword.HAND);
        addPersona(Persona.OOD);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OOD_BNAR__UPLOAD_LIGHTSABER;
        
        // Check condition(s)
        if (GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take member of Clan Taldryan or a lightsaber into hand");
            action.setActionMsg("Take Rey or a Clan Taldryan into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.TAL_character, Filters.lightsaber), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.lightsaber), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OOD_BNAR__DEPLOY_LIGHTSABER_FROM_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy lightsaber from Lost Pile");
            action.setActionMsg("Deploy a lightsaber on Ood Bnar from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, Filters.persona(Persona.OOD), false));

            return Collections.singletonList(action);
        }
        return null;
    }
}
