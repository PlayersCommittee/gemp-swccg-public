package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
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
 * Title: Tibrin Operative
 */
public class Card7_047 extends AbstractAlien {
    public Card7_047() {
        super(Side.LIGHT, 3, 1, 1, 1, 3, "Tibrin Operative");
        setLore("To disrupt the enslavement of the Ishi Tib by the Empire, some Rodian hunters were sent to harass Imperial troop movements on Tibrin.");
        setGameText("While at a Tibrin site: adds 1 to your Force drains there, is forfeit +1 (and power +1 if your Ishi Tib is on Tibrin) and, once during each of your deploy phases, may deploy one site to Tibrin from Reserve Deck; reshuffle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.OPERATIVE);
        setSpecies(Species.RODIAN);
        setMatchingSystem(Title.Tibrin);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atTibrinSiteCondition = new AtCondition(self, Filters.Tibrin_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), atTibrinSiteCondition, 1, playerId));
        modifiers.add(new ForfeitModifier(self, atTibrinSiteCondition, 1));
        modifiers.add(new PowerModifier(self, new AndCondition(atTibrinSiteCondition,
                new OnCondition(self, Filters.and(Filters.your(self), Filters.Ishi_Tib), Title.Tibrin)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TIBRIN_OPERATIVE__DOWNLOAD_SITE_TO_TIBRIN;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.Tibrin_site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy a site to Tibrin from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToSystemFromReserveDeckEffect(action, Filters.site, Title.Tibrin, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
