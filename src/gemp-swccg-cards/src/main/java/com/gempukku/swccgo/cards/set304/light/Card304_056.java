package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Gex Degrix
 */
public class Card304_056 extends AbstractAlien {
    public Card304_056() {
        super(Side.LIGHT, 3, 3, 2, 2, 4, "Gex Degrix", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("A Besalisk back alley doctor, hired by Claudius to stitch up his crew. A string of gambling debts left him with no choice but to give up his practice. He justifies it as giving them a second chance at life. Gangster.");
        setGameText("Prevents your characters from having their forfeit reduced at same location (and at Ulress sites if Gex is at Ixtal's Garage). When with your FX droid, once per turn allows your character just forfeited from same site to be placed in Used Pile.");
        addIcons(Icon.WARRIOR);
        setSpecies(Species.BESALISK);
        addKeywords(Keyword.CLAN_TIURE, Keyword.GANGSTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCharacters = Filters.and(Filters.your(self), Filters.character);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(yourCharacters, Filters.at(Filters.sameLocation(self)))));
        modifiers.add(new MayNotHaveForfeitValueReducedModifier(self, Filters.and(yourCharacters, Filters.at(Filters.Ulress_site)),
                new AtCondition(self, Filters.Ixtals_Garage)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, Filters.and(Filters.your(self), Filters.character), Filters.sameSite(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.FX_droid))) {
            PhysicalCard justForfeitedCard = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justForfeitedCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justForfeitedCard) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justForfeitedCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
