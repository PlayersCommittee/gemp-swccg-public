package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Frostbite
 */
public class Card3_036 extends AbstractNormalEffect {
    public Card3_036() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Frostbite, Uniqueness.UNIQUE, ExpansionSet.HOTH, Rarity.C2);
        setLore("'Then I'll see you in hell!'");
        setGameText("Deploy on Hoth system. At the end of each player's turn, for every character that player has present at a marker site under 'nighttime conditions,' that player must lose 1 Force (2 if character is missing).");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.HOTH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Hoth_system;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)) {
            String currentPlayer = game.getGameState().getCurrentPlayerId();
            int amountToLose = 0;
            Collection<PhysicalCard> characters = Filters.filterActive(game, self,
                    SpotOverride.INCLUDE_MISSING, Filters.and(Filters.owner(currentPlayer), Filters.character, Filters.canBeTargetedBy(self),
                            Filters.presentAt(Filters.and(Filters.marker_site, Filters.under_nighttime_conditions))));
            for (PhysicalCard character : characters) {
                if (character.isMissing())
                    amountToLose+=2;
                else
                    amountToLose++;
            }
            if (amountToLose > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + currentPlayer + " lose " + amountToLose + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, currentPlayer, amountToLose));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}