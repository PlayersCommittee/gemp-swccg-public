package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Character
 * Subtype: Droid
 * Title: CB-23
 */
public class Card223_033 extends AbstractDroid {
    public Card223_033() {
        super(Side.LIGHT, 3, 2, 1, 4, "CB-23", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Female.");
        setGameText("[Episode VII] characters deploy -1 here. During battle, each player with two other participating [Episode VII] characters may add a destiny to attrition. While with Kazuda, Poe or Rey, gains Resistance Agent.");
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_23);
        addKeyword(Keyword.FEMALE);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.icon(Icon.EPISODE_VII), Filters.character), -1, Filters.here(self)));
        modifiers.add(new KeywordModifier(self, new WithCondition(self, Filters.or(Filters.Kazuda, Filters.Poe, Filters.Rey)), Keyword.RESISTANCE_AGENT));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, 2, Filters.and(Filters.your(self), Filters.icon(Icon.EPISODE_VII), Filters.character)) 
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one destiny to attrition");
            action.setActionMsg("Add one destiny to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToAttritionEffect(action, 1));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInBattleWith(game, self, 2, Filters.and(Filters.opponents(self), Filters.icon(Icon.EPISODE_VII), Filters.character)) 
                && GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add one destiny to attrition");
            action.setActionMsg("Add one destiny to attrition");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToAttritionEffect(action, 1));

            return Collections.singletonList(action);
        }

        return null;
    }
}
