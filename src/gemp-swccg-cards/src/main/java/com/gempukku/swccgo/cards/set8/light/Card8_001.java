package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayMoveMediumRepeatingBlasterCannonAloneForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Rebel
 * Title: Captain Yutani
 */
public class Card8_001 extends AbstractRebel {
    public Card8_001() {
        super(Side.LIGHT, 3, 2, 3, 1, 3, "Captain Yutani", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.U);
        setLore("Scout and heavy weapons specialist. Carries custom repulsorlift compensators to allow fluid movement with light artillery weapons.");
        setGameText("Once during each of your deploy phases, may deploy one artillery weapon or Portable Fusion Generator to same site from Reserve Deck; reshuffle. May move Medium Repeating Blaster Cannon by himself (for free).");
        addIcons(Icon.ENDOR, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveMediumRepeatingBlasterCannonAloneForFreeModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.CAPTAIN_YUTANI__DOWNLOAD_ARTILLERY_WEAPON_OR_PORTABLE_FUSION_GENERATOR;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isAtLocation(game, self, Filters.site)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy an artillery weapon or Portable Fusion Generator from Reserve Deck"); ///text too long?
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.artillery_weapon, Filters.Portable_Fusion_Generator), Filters.sameSite(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

}
