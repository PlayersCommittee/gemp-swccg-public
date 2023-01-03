package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Anoat Operative
 */
public class Card7_002 extends AbstractAlien {
    public Card7_002() {
        super(Side.LIGHT, 3, 1, 1, 1, 3, "Anoat Operative", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Independent H'nemthe merchants at Anoat coordinate the shipping of supplies to Rebel bases throughout the galaxy.");
        setGameText("While at an Anoat site: adds 1 to your Force drains there, is forfeit +1 (and power +1 if your smuggler or Ugnaught is on Anoat) and, once during each of your deploy phases, may deploy one site to Anoat from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.OPERATIVE);
        setSpecies(Species.HNEMTHE);
        setMatchingSystem(Title.Anoat);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atAnoatSiteCondition = new AtCondition(self, Filters.Anoat_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), atAnoatSiteCondition, 1, playerId));
        modifiers.add(new ForfeitModifier(self, atAnoatSiteCondition, 1));
        modifiers.add(new PowerModifier(self, new AndCondition(atAnoatSiteCondition,
                new OnCondition(self, Filters.and(Filters.your(self), Filters.or(Filters.smuggler, Filters.Ugnaught)), Title.Anoat)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANOAT_OPERATIVE__DOWNLOAD_SITE_TO_ANOAT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Anoat_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a site to Anoat from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, Title.Anoat, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
