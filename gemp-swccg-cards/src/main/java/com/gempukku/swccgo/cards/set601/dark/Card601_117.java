package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 4
 * Type: Effect
 * Title: Information Exchange (V)
 */
public class Card601_117 extends AbstractNormalEffect {
    public Card601_117() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Information_Exchange, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Chisa nyooda ishaley. Kun Jabba neguda len Malta.' 'Ikkit ui! Yobbit, yobbiy. Nelan tui ke bada.'");
        setGameText("Deploy on Imperial City.  While your [Reflections II] Objective on table, Scum And Villainy may deploy here and, if a Black Sun agent present, may not be canceled.  Once per turn, may use 1 Force to deploy a non-weapon card with 'Black Sun' or 'Xizor' in lore (except Emperor) from Reserve Deck; reshuffle.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Imperial_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ScumAndVillainyMayDeployAttachedModifier(self, Filters.hasAttached(self), new OnTableCondition(self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective))));
        modifiers.add(new MayNotBeCanceledModifier(self, Filters.Scum_And_Villainy, new AndCondition(new OnTableCondition(self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective)), new PresentAtCondition(Filters.Black_Sun_agent, Filters.hasAttached(self)))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__INFORMATION_EXCHANGE_V__DEPLOY_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy a non-weapon card with 'Black Sun' or 'Xizor' in lore (except Emperor) from Reserve Deck");

            Filter filter = Filters.and(Filters.not(Filters.weapon), Filters.or(Filters.loreContains("Black Sun"), Filters.loreContains("Black Suns"), Filters.loreContains("Xizor"), Filters.loreContains("Xizors")), Filters.except(Filters.Emperor));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, filter, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}