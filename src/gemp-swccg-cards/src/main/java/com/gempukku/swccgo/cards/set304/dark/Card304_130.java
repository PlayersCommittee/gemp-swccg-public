package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotForceDrainAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: Scholae Palatinae's Power
 */
public class Card304_130 extends AbstractNormalEffect {
    public Card304_130() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Scholae_Palatinaes_Power, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("There is one constant in Scholae Palatinae, the strong will rule. The Emperor must always be wary of being usurped.");
        setGameText("Deploy on your Monolith Throne Room. You may not Force drain here. While [CSP] with Ability > 5 here, opponent's characters are deploy +2 to Seraph sites and, one per turn, you may lose Effect (or 1 Force from top of Reserve Deck) to add one battle destiny anywhere. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Monolith_Throne_Room);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, Filters.here(self), playerId));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                new HereCondition(self, Filters.and(Filters.CSP_character, Filters.abilityMoreThan(5))), 2, Filters.Seraph_site));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && GameConditions.canSpot(game, self, Filters.and(Filters.CSP_character, Filters.abilityMoreThan(5), Filters.here(self)))) {

            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Lose Effect to add one battle destiny");
            action.setActionMsg("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            actions.add(action);

            // Check condition(s)
            if (GameConditions.hasReserveDeck(game, playerId)) {

                TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action2.setText("Lose 1 Force to add one battle destiny");
                action2.setActionMsg("Add one battle destiny");
                // Update usage limit(s)
                action2.appendUsage(
                        new OncePerTurnEffect(action));
                // Pay cost(s)
                action2.appendCost(
                        new LoseForceFromReserveDeckEffect(action2, playerId, 1, true));
                // Perform result(s)
                action2.appendEffect(
                        new AddBattleDestinyEffect(action2, 1));
                actions.add(action2);
            }
        }
        return actions;
    }
}