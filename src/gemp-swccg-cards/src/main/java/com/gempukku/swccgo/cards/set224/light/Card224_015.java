package com.gempukku.swccgo.cards.set224.light;

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
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 24
 * Type: Character
 * Subtype: Rebel
 * Title: General Crix Madine (V)
 */
public class Card224_015 extends AbstractRebel {
    public Card224_015() {
        super(Side.LIGHT, 1, 3, 3, 3, 6, Title.Madine, Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Military advisor to Mon Mothma. Leader of commando project. Corellian native. Defected to the Alliance shortly after the Battle of Yavin. Rescued by Rogue Squadron.");
        setGameText("Scout. Once during your deploy phase, if present at a battleground, may [upload] a scout of ability < 3 (or any [Endor] scout). During battle here (or same system as Ackbar) may retrieve 1 Force if you played Critical Error Revealed.");
        addIcons(Icon.ENDOR, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.GENERAL, Keyword.LEADER, Keyword.SCOUT);
        setSpecies(Species.CORELLIAN);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GENERAL_CRIX_MADINE__UPLOAD_SCOUT;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.isPresentAt(game, self, Filters.battleground)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a scout into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.scout, Filters.abilityLessThan(3)), Filters.and(Icon.ENDOR, Filters.scout)), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, self.getOwner(), Filters.Critical_Error_Revealed)
                && (GameConditions.isInBattle(game, self) || GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Ackbar, Filters.at(Filters.system))))
                && GameConditions.hasLostPile(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve one Force");
            // Update usage limit(s)
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(self, action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
