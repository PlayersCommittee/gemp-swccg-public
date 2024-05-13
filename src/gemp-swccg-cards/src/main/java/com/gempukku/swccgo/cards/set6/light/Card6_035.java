package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeAttackedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Rennek
 */
public class Card6_035 extends AbstractAlien {
    public Card6_035() {
        super(Side.LIGHT, 2, 2, 1, 1, 3, Title.Rennek, Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Nerf herder. Personally oversees the herding of Jabba's nerfs. Scruffy-looking appearance conceals his expertise in unusual combat techniques.");
        setGameText("Creatures at same site (except Sarlacc) do not attack and cannot be attacked. Up to three times per turn, may use 1 Force to cumulatively add 1 to his power for remainder of turn.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter creaturesAtSameSite = Filters.and(Filters.creature, Filters.except(Filters.Sarlacc), Filters.atSameSite(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotAttackModifier(self, creaturesAtSameSite));
        modifiers.add(new MayNotBeAttackedModifier(self, creaturesAtSameSite));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isNumTimesPerTurn(game, self, playerId, 3, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Add 1 to power");
            action.setActionMsg("Add 1 to " + GameUtils.getCardLink(self) + "'s power");
            // Update usage limit(s)
            action.appendUsage(
                    new NumTimesPerTurnEffect(action, 3));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new ModifyPowerUntilEndOfTurnEffect(action, self, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
