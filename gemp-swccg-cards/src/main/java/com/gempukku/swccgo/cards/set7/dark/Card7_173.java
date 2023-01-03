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
 * Title: Corulag Operative
 */
public class Card7_173 extends AbstractAlien {
    public Card7_173() {
        super(Side.DARK, 3, 1, 1, 1, 2, "Corulag Operative", Uniqueness.UNRESTRICTED, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("Independent traders make money selling equipment to the academy at Corulag. Resentful of the Alliance pilots that disrupt their shipping lines.");
        setGameText("While at a Corulag site: adds 1 to your Force drains there, is power +1 (or +2 if your trooper is on Corulag) and, once during each of your deploy phases, may deploy one site to Corulag from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.OPERATIVE);
        setMatchingSystem(Title.Corulag);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atCorulagSiteCondition = new AtCondition(self, Filters.Corulag_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), atCorulagSiteCondition, 1, playerId));
        modifiers.add(new PowerModifier(self, atCorulagSiteCondition, new ConditionEvaluator(1, 2,
                new OnCondition(self, Filters.and(Filters.your(self), Filters.trooper), Title.Corulag))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CORULAG_OPERATIVE__DOWNLOAD_SITE_TO_CORULAG;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Corulag_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a site to Corulag from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, Title.Corulag, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
