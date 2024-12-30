package com.gempukku.swccgo.cards.set224.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Imperial
 * Title: Second Sister
 */
public class Card224_006 extends AbstractImperial {
    public Card224_006() {
        super(Side.DARK, 2, 5, 4, 5, 7, "Second Sister", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Female Inquisitor.");
        setGameText("Padawans are power -2 here. " +
                "Once per game, may place a 'Hatred' card here in owner’s Lost Pile to choose: " +
                "Take any card into hand from Used Pile; reshuffle. " +
                "OR Deploy a lightsaber on this character from Lost Pile. " +
                "Unless Vader here, immune to attrition < 4.");
        addKeywords(Keyword.INQUISITOR, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.padawan, Filters.here(self)), -2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new NotCondition(new HereCondition(self, Filters.Vader)), 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final List<TopLevelGameTextAction> actions = new ArrayList<>();

        final GameTextActionId gameTextActionId = GameTextActionId.SECOND_SISTER__USE_HATRED_CARD;
        final Filter hasHatredCard = Filters.and(Filters.here(self), Filters.hasStacked(Filters.hatredCard));


        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canSpot(game, self, hasHatredCard)) {

            if (GameConditions.canSearchUsedPile(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take any card into hand from Used Pile");
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, hasHatredCard, Filters.hatredCard, true) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendCost(
                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));

                                action.appendEffect(
                                        new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                            }
                        }
                );
                actions.add(action);
            }

            if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a lightsaber from Lost Pile");
                action.appendUsage(
                        new OncePerGameEffect(action));
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, hasHatredCard, Filters.hatredCard, true) {
                            @Override
                            protected void cardSelected(PhysicalCard selectedCard) {
                                action.appendCost(
                                        new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));

                                action.appendEffect(
                                        new DeployCardToTargetFromLostPileEffect(action, Filters.lightsaber, Filters.sameCardId(self), true));
                            }
                        }
                );
                actions.add(action);
            }
        }

        return actions;
    }
}
