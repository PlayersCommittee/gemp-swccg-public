package com.gempukku.swccgo.cards.set203.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Alien
 * Title: Baniss Keeg, Pilot Instructor
 */
public class Card203_023 extends AbstractAlien {
    public Card203_023() {
        super(Side.DARK, 2, 3, 2, 3, 5, "Baniss Keeg, Pilot Instructor", Uniqueness.UNIQUE, ExpansionSet.SET_3, Rarity.V);
        setLore("Duros information broker, scout, and smuggler.");
        setGameText("[Pilot] 3. Adds 1 to hyperspeed of anything she pilots. Once per turn, if you just deployed a Black Sun agent to same system, may draw top card of Reserve Deck. Your characters here are immune to Cantina Brawl and Fallen Portal.");
        addKeywords(Keyword.INFORMATION_BROKER, Keyword.SCOUT, Keyword.SMUGGLER, Keyword.FEMALE);
        addIcons(Icon.PILOT, Icon.VIRTUAL_SET_3);
        setSpecies(Species.DUROS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourCharactersHere = Filters.and(Filters.your(self), Filters.character, Filters.here(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new HyperspeedModifier(self, Filters.hasPiloting(self), 1));
        modifiers.add(new ImmuneToTitleModifier(self, yourCharactersHere, Title.Cantina_Brawl));
        modifiers.add(new ImmuneToTitleModifier(self, yourCharactersHere, Title.Fallen_Portal));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justDeployedTo(game, effectResult, playerId, Filters.Black_Sun_agent, Filters.sameSystem(self))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
