package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleInitiatedByOwnerCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Trooper Jerrol Blendin
 */
public class Card5_103 extends AbstractAlien {
    public Card5_103() {
        super(Side.DARK, 2, 0, 2, 1, 3, "Trooper Jerrol Blendin", Uniqueness.UNIQUE);
        setLore("Corrupt captain of the Cloud City Wing Guard. Began working during the administration of Baron Raynor. Easily bribed. Keeps the peace through intimidation.");
        setGameText("Deploys only on Cloud City, but may move elsewhere. Power +2 when participating in a battle you initiate. Opponent may use 2 Force to 'bribe' (exclude) Blendin from battle.");
        addIcons(Icon.CLOUD_CITY, Icon.WARRIOR);
        addKeywords(Keyword.CLOUD_CITY_TROOPER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new InBattleInitiatedByOwnerCondition(self), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("'Bribe' from battle");
            action.setActionMsg("'Bribe' " + GameUtils.getCardLink(self) + " from battle");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new ExcludeFromBattleEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
