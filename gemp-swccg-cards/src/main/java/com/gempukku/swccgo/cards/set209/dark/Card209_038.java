package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 9
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Venka (V)
 */
public class Card209_038 extends AbstractImperial {
    public Card209_038() {
        super(Side.DARK, 2, 2, 2, 2, 3, "Lieutenant Venka", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Worked hard for a transfer to the Executor. One of the many noncommissioned personnel promoted to replace the vast number of officers lost during the Death Star disaster.");
        setGameText("Deploys -1 to Executor. While aboard a capital starship, adds 1 to armor and its defense value may not be reduced. While Fear Will Keep Them In Line here, when you win a battle at a related site where you have an Imperial, opponent loses 2 Force.");
        addIcons(Icon.DAGOBAH, Icon.WARRIOR, Icon.PILOT, Icon.VIRTUAL_SET_9);
        setMatchingStarshipFilter(Filters.EXECUTOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostAboardModifier(self, -1, Persona.EXECUTOR));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String PlayerId = self.getOwner();
        String opponent = game.getOpponent(PlayerId);
        Condition aboardCapitalStarship = new AboardCondition(self, Filters.capital_starship);
        Filter capitalStarshipAboard = Filters.and(Filters.capital_starship, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ArmorModifier(self, capitalStarshipAboard, aboardCapitalStarship, 1));
        modifiers.add(new MayNotHaveDefenseValueReducedModifier(self, Filters.and(Filters.capital_starship, Filters.hasAboard(self)), opponent));
        modifiers.add(new MayNotHaveDefenseValueReducedModifier(self, Filters.and(Filters.capital_starship, Filters.hasAboard(self)), PlayerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (self.getWhileInPlayData() != null && TriggerConditions.isStartOfEachTurn(game, effectResult)) {
            self.setWhileInPlayData(null);
        }

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.title(Title.Fear_Will_Keep_Them_In_Line), Filters.here(self)))
                && TriggerConditions.wonBattleAt(game, effectResult, playerId, Filters.and(Filters.relatedSite(self),
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.Imperial))))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
            action.setText("Make opponent lose 2 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 2));
            return Collections.singletonList(action);
        }

        return null;
    }

}
