package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 1
 * Type: Effect
 * Title: Despair (V)
 */
public class Card201_028 extends AbstractNormalEffect {
    public Card201_028() {
        super(Side.DARK, 6, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Despair, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The carbonite froze more than just Han's body.");
        setGameText("Deploy on table. Opponent's characters deploy +1 to same site as Jabba's Prize. When you capture a character, opponent loses 1 Force. Your Force drains at same battleground site as Jabba's Prize are +1. My Favorite Decoration may not be placed out of play. [Immune to Alter]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_1);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSiteAsJabbasPrize = Filters.sameSiteAs(self, SpotOverride.INCLUDE_CAPTIVE, Filters.Jabbas_Prize);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character), 1, sameSiteAsJabbasPrize));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground_site, sameSiteAsJabbasPrize), 1, playerId));
        modifiers.add(new MayNotBePlacedOutOfPlayModifier(self, Filters.My_Favorite_Decoration));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.captured(game, effectResult, playerId, Filters.character)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}