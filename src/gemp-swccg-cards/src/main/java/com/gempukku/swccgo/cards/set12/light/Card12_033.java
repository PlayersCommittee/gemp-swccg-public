package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Yane
 */
public class Card12_033 extends AbstractRepublic {
    public Card12_033() {
        super(Side.LIGHT, 3, 2, 1, 2, 2, "Yane", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("The youngest handmaiden, Yane stayed behind on Naboo while Amidala pleaded her case to the Galactic Senate. Musician.");
        setGameText("While at a Theed Palace site or same site as Amidala, opponent's Republic characters are deploy +1 here and Yane is forfeit +3. If just lost during a battle, may use 2 Force to place Yane in Used Pile.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE, Keyword.HANDMAIDEN, Keyword.MUSICIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atTheedPalaceSiteOrSameSiteAsAmidala = new AtCondition(self, Filters.or(Filters.Theed_Palace_site,
                Filters.sameSiteAs(self, Filters.Amidala)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.Republic, Filters.character),
                atTheedPalaceSiteOrSameSiteAsAmidala, 1, Filters.here(self)));
        modifiers.add(new ForfeitModifier(self, atTheedPalaceSiteOrSameSiteAsAmidala, 3));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, self)
                && GameConditions.isDuringBattle(game)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
