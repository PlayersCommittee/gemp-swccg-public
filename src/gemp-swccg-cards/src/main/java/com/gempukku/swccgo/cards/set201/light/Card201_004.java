package com.gempukku.swccgo.cards.set201.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardsInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveDeployCostIncreasedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 1
 * Type: Character
 * Subtype: Rebel
 * Title: Obi-Wan Kenobi (V)
 */
public class Card201_004 extends AbstractRebel {
    public Card201_004() {
        super(Side.LIGHT, 1, 6, 5, 6, 9, "Obi-Wan Kenobi", Uniqueness.UNIQUE, ExpansionSet.SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jedi Knight. Trained by Yoda. Friend of Bail Organa. General Kenobi became a hero of the Old Republic during the Clone Wars. Mentor of Anakin and Luke Skywalker.");
        setGameText("Obi-Wan's deploy cost may not be increased. Power +1 for each Dark Jedi here. Once per game, may exchange two cards in hand with any one card in Lost Pile. Immune to attrition < 5.");
        addPersona(Persona.OBIWAN);
        addKeywords(Keyword.GENERAL);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveDeployCostIncreasedModifier(self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new HereEvaluator(self, Filters.Dark_Jedi)));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OBIWAN_KENOBI__EXCHANGE_CARDS_WITH_CARD_IN_LOST_PILE;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.numCardsInHand(game, playerId) >= 2
                && GameConditions.hasLostPile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Exchange cards with card in Lost Pile");
            action.setActionMsg("Exchange two cards in hand with a card in Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ExchangeCardsInHandWithCardInLostPileEffect(action, playerId, 2, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
