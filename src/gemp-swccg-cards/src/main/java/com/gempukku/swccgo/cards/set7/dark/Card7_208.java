package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayPlayToCancelForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Sy Snootles
 */
public class Card7_208 extends AbstractAlien {
    public Card7_208() {
        super(Side.DARK, 3, 1, 1, 1, 3, "Sy Snootles", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Pa'lowick Musician. Manager of The Max Rebo Band. Reports on Jabba to Lady Valarian. Bib Fortuna uses her as a double agent, feeding her false information.");
        setGameText("During your deploy phase, where present, allows J'Quille or Bib Fortuna deploy from Reserve Deck, reshuffle. Once each turn when you have at least 3 musicians on table, may play Ommni Box to cancel opponent's Force drain at a related site.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.FEMALE, Keyword.MUSICIAN);
        setSpecies(Species.PALOWICK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.SY_SNOOTLES_DOWNLOAD_JQUILE_OR_BIB;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.isPresent(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy card from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Bib, Filters.title(Title.Jquille)), Filters.sameLocation(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition threeOrMoreMusiciansOnTable = new OnTableCondition(self, 3, Filters.and(Filters.your(self), Filters.musician));
        modifiers.add(new MayPlayToCancelForceDrainModifier(self, Filters.title(Title.Ommni_Box), threeOrMoreMusiciansOnTable, Filters.relatedSite(self)));
        return modifiers;
    }
}
