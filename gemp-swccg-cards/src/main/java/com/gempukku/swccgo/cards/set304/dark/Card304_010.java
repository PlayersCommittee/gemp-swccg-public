package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * SubType: Alien
 * Title: Rayne Palpatine
 */
public class Card304_010 extends AbstractImperial {
    public Card304_010() {
        super(Side.DARK, 1, 5, 3, 5, 4, "Rayne Palpatine", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("The newly appointed leader of House Caliburnus. Rayne has a long history with the Brotherhood. Rumors have begun circulating of her being involved with Derc Kast but no tabloid will run the story.");
        setGameText("Once per turn, one of your non-droid characters just lost from same site may be 'resurrected' (placed) in your Used Pile.");
        addPersona(Persona.RAYNE);
		addKeywords(Keyword.FEMALE, Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.CSP);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.non_droid_character), Filters.sameSite(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justLostCard) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
           return Collections.singletonList(action);
        }
        return null;
    }
}
