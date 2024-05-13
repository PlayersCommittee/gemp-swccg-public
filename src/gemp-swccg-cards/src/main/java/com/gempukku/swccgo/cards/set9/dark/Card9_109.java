package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractDarkJediMasterImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CrossOverAttemptTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NeverDeploysToLocationModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Dark Jedi Master/Imperial
 * Title: Emperor Palpatine
 */
public class Card9_109 extends AbstractDarkJediMasterImperial {
    public Card9_109() {
        super(Side.DARK, 6, 5, 4, 7, 9, "Emperor Palpatine", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.UR);
        setLore("Sith Master and leader of Galactic Empire. Dark side mentor to Darth Vader. Controls the Empire by instilling fear in its subjects and greed in its governors.");
        setGameText("Never deploys or moves (even aboard a starship or vehicle) to a site opponent occupies. Where present, subtracts 3 from attempts to cross Vader over. Once per turn, you may take Force Lightning into hand from Reserve Deck; reshuffle. Immune to attrition.");
        addPersona(Persona.SIDIOUS);
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NeverDeploysToLocationModifier(self, siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInPlayEvenIfGameTextCanceledModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.or(self, Filters.hasAttachedWithRecursiveChecking(self)), siteOpponentOccupies));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CrossOverAttemptTotalModifier(self, Filters.and(Filters.Vader, Filters.atSameSite(self)), new PresentCondition(self), -3));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.EMPEROR_PALPATINE__UPLOAD_FORCE_LIGHTNING;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Force Lightning into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Force_Lightning, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
