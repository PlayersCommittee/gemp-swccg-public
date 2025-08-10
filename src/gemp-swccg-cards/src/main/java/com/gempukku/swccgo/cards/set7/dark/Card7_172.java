package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.InsteadOfForceDrainingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Imperial
 * Title: Corporal Prescott
 */
public class Card7_172 extends AbstractImperial {
    public Card7_172() {
        super(Side.DARK, 3, 2, 4, 1, 3, "Corporal Prescott", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Death Star trooper and detention block guard. Volunteered for prison detail. Takes sadistic pleasure in seeing Imperial justice enforced.");
        setGameText("When at a prison, instead of Force draining there, may use X Force to retrieve X Force, where X = number of imprisoned Rebels there. Power -2 when not on Death Star.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.DEATH_STAR_TROOPER, Keyword.GUARD);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isAtLocation(game, self, Filters.prison)) {
            GameState gameState = game.getGameState();
            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
            PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, self);
            if (GameConditions.canInsteadOfForceDrainingAtLocation(game, playerId, location)) {
                int numberOfPrisoners = Filters.countActive(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Rebel, Filters.imprisonedIn(location), Filters.mayContributeToForceRetrieval));
                if (numberOfPrisoners > 0
                        && GameConditions.canUseForce(game, playerId, numberOfPrisoners)) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Retrieve " + numberOfPrisoners + " Force");
                    // Pay cost(s)
                    action.appendCost(
                            new UseForceEffect(action, playerId, numberOfPrisoners));
                    // Perform result(s)
                    action.appendEffect(
                            new InsteadOfForceDrainingEffect(action, location,
                                    new RetrieveForceEffect(action, playerId, numberOfPrisoners)));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new NotCondition(new OnCondition(self, Title.Death_Star)), -2));
        return modifiers;
    }
}
