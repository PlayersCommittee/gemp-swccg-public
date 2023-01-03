package com.gempukku.swccgo.cards.set106.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Official Tournament Sealed Deck)
 * Type: Character
 * Subtype: Alien
 * Title: Chall Bekan
 */
public class Card106_011 extends AbstractAlien {
    public Card106_011() {
        super(Side.DARK, 2, 4, 2, 3, 4, "Chall Bekan", Uniqueness.UNIQUE, ExpansionSet.OTSD, Rarity.PM);
        setLore("Male Morseerian leader. Methane breather. Has ties to Tatooine's Imperial government. Uses alien agents to maintain surveillance on Jabba and the Rebellion.");
        setGameText("When deployed, you may take one non-unique alien into hand from Reserve Deck; reshuffle. Adds 2 to power of anything he pilots. Your aliens deploy -1 to same or adjacent Tatooine site.");
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.LEADER);
        setSpecies(Species.MORSEERIAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CHALL_BEKAN__UPLOAD_ALIEN;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a non-unique alien into hand from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.alien), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.alien), -1,
                Filters.and(Filters.Tatooine_site, Filters.sameOrAdjacentSite(self))));
        return modifiers;
    }
}
