package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
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
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Ord Mantell Operative
 */
public class Card7_193 extends AbstractAlien {
    public Card7_193() {
        super(Side.DARK, 3, 1, 1, 1, 2, "Ord Mantell Operative", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Many Aqualish draft and maintain bounty hunting contracts for the Empire, the Hutts or anyone with credits. Fight against the Alliance to avoid losing their best customer.");
        setGameText("While at an Ord Mantell site: adds 1 to your Force drains there, is power +1 (or +2 if your bounty hunter is on Ord Mantell) and, once during each of your deploy phases, may deploy one site to Ord Mantell from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.OPERATIVE);
        setSpecies(Species.AQUALISH);
        setMatchingSystem(Title.Ord_Mantell);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atOrdMantellSiteCondition = new AtCondition(self, Filters.Ord_Mantell_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), atOrdMantellSiteCondition, 1, playerId));
        modifiers.add(new PowerModifier(self, atOrdMantellSiteCondition, new ConditionEvaluator(1, 2,
                new OnCondition(self, Filters.and(Filters.your(self), Filters.bounty_hunter), Title.Ord_Mantell))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ORD_MANTELL_OPERATIVE__DOWNLOAD_SITE_TO_ORD_MANTELL;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Ord_Mantell_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a site to Ord Mantell from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, Title.Ord_Mantell, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
