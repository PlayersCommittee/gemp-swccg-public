package com.gempukku.swccgo.cards.set203.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveTotalAbilityReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 3
 * Type: Character
 * Subtype: Alien
 * Title: Leslomy Tacema (V)
 */
public class Card203_007 extends AbstractAlien {
    public Card203_007() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Leslomy Tacema", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Female Duros. Expert pilot. Helps run the docking facilities in Mos Eisley. Friends with Ellorrs Madak. Approves all cargo manifests. Forgiving of minor transgressions.");
        setGameText("[Pilot] 3. Smuggler. Adds 1 to hyperspeed of any freighter she pilots. Your total ability at same docking bay or system may not be reduced. Once per game, may [download] a card with 'Ellorrs' in title here.");
        addIcons(Icon.JABBAS_PALACE, Icon.PILOT, Icon.VIRTUAL_SET_3);
        addKeywords(Keyword.FEMALE, Keyword.SMUGGLER);
        setSpecies(Species.DUROS);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameDockingBayOrSystem = Filters.and(Filters.or(Filters.docking_bay, Filters.system), Filters.sameLocation(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new HyperspeedModifier(self, Filters.and(Filters.freighter, Filters.hasPiloting(self)), 1));
        modifiers.add(new MayNotHaveTotalAbilityReducedModifier(self, sameDockingBayOrSystem, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LESLOMY_TACEMA__DOWNLOAD_CARD_WITH_ELLORRS_IN_TITLE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy card with 'Ellorrs' in title from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.titleContains("Ellorrs"), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
