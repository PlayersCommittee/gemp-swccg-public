package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Establish Secret Base (V)
 */
public class Card601_260 extends AbstractNormalEffect {
    public Card601_260() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Establish_Secret_Base, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("The Empire's remote bases develop new technology and hide sensitive projects from potential Rebel saboteurs.");
        setGameText("If you control Bunker, deploy on Endor system. At each Endor site you control with an AT-ST or Biker Scout, opponent's Force generation is canceled. During your deploy phase, may deploy one Aratech Corporation or an Effect with 'Endor' in the lore or game text from Reserve Deck; reshuffle. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.ENDOR, Icon.LEGACY_BLOCK_3);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Endor_system;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.controls(game, playerId, Filters.Bunker);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ESTABLISH_SECRET_BASE_V__DEPLOY_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Aratech Corporation or an Effect with 'Endor' in lore or game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.title("Aratech Corporation"), Filters.and(Filters.Effect, Filters.or(Filters.loreContains("Endor"), Filters.loreContains("Endors"), Filters.gameTextContains("Endor"), Filters.gameTextContains("Endors")))), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.and(Filters.Endor_site, Filters.controlsWith(self.getOwner(), self, Filters.or(Filters.AT_ST, Filters.biker_scout))), game.getOpponent(self.getOwner())));
        return modifiers;
    }
}