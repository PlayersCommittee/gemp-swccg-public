package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Early Warning Network
 */
public class Card8_122 extends AbstractNormalEffect {
    public Card8_122() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Early_Warning_Network, Uniqueness.DIAMOND_1);
        setLore("Listening posts are often constructed as part of a standard Imperial installation. On Endor, such a post was incorporated into the control bunker.");
        setGameText("Deploy on an interior site that has a Scomp link. While you occupy this site, once per battle you may deploy a non-unique Imperial starship as a 'react' (for free if starfighter) to the related system from Reserve Deck; reshuffle. (Immune to alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.interior_site, Filters.has_Scomp_link);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.EARLY_WARNING_NETWORK__DOWNLOAD_IMPERIAL_STARSHIP_AS_REACT;

        // Check condition(s)
        if (TriggerConditions.battleInitiatedAt(game, effectResult, opponent, Filters.relatedSystem(self))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card as a 'react' from Reserve Deck");
            action.setActionMsg("Deploy a non-unique Imperial starship as a 'react' from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.non_unique, Filters.Imperial_starship), Filters.starfighter, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}