package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.EndOfTurnLimitCounterNotReachedCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Hutt Trade Route (V)
 */
public class Card223_048 extends AbstractSite {
    public Card223_048() {
        super(Side.LIGHT, Title.Hutt_Trade_Route, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("The first alien you deploy here each turn is deploy -1. Characters here are immune to Sandwhirl.");
        setLocationDarkSideGameText("Unless you occupy, you must first use 1 Force to deploy a non-alien character here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_23);
        addKeywords(Keyword.DESERT);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition unlessYouOccupy = new UnlessCondition(new OccupiesCondition(playerOnDarkSideOfLocation, self));
        Filter yourNonAlienCharacters = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character, Filters.not(Filters.alien));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToDeployCardToLocationModifier(self, yourNonAlienCharacters, unlessYouOccupy, new ConstantEvaluator(1), self));     
        return modifiers;
    }


    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {   
        Filter yourAliens = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.alien);
        Filter CharacterHere = Filters.and(Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();

        modifiers.add(new ImmuneToTitleModifier(self, CharacterHere, Title.Sandwhirl));
        modifiers.add(new DeployCostToLocationModifier(self, yourAliens, new EndOfTurnLimitCounterNotReachedCondition(self, 1), -1, self));

        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self.getOwner(), Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.alien))) {
            //need to account for deploying simultaneously with another card
            PhysicalCard card1 = ((PlayCardResult) effectResult).getPlayedCard();
            PhysicalCard card2 = ((PlayCardResult) effectResult).getOtherPlayedCard();
            if (card1 == null || card2 == null || !(Filters.and(card1).accepts(game, self) || Filters.and(card2).accepts(game, self))) {
                game.getModifiersQuerying().getUntilEndOfTurnLimitCounter(self, self.getOwner(), gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_DEFAULT).incrementToLimit(1, 1);
            }
        }
        return super.getGameTextLightSideRequiredAfterTriggers(playerOnLightSideOfLocation, game, effectResult, self, gameTextSourceCardId);
    }

}
