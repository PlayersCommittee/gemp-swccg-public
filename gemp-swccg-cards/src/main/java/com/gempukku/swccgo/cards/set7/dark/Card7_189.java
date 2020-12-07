package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Makurth
 */
public class Card7_189 extends AbstractAlien {
    public Card7_189() {
        super(Side.DARK, 2, 2, 2, 1, 2, "Makurth");
        setLore("A fierce race of nocturnal carnivores from Moltok. Charge into battle with a terrifying scream. Often find employment as bodyguards for Black Sun.");
        setGameText("When in a battle at a site, may use 2 Force (or 1 Force if you initiated the battle) to 'scream' (add one destiny to your total power only). Immune to attrition < 4 under 'nighttime conditions.'");
        addIcons(Icon.SPECIAL_EDITION);
        addKeyword(Keyword.BODYGUARD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isInBattleAt(game, self, Filters.site)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
            int numForceToUse = GameConditions.isDuringBattleInitiatedBy(game, playerId) ? 1 : 2;
            if (GameConditions.canUseForce(game, playerId, numForceToUse)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("'Scream'");
                action.setActionMsg("Add one destiny to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, numForceToUse));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToTotalPowerEffect(action, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(final SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new UnderNighttimeConditionConditions(self), 4));
        return modifiers;
    }
}
