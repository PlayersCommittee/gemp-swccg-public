package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 3
 * Type: Effect
 * Title: Endor Shield (V)
 */
public class Card601_078 extends AbstractNormalEffect {
    public Card601_078() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Endor_Shield, Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Planetary scale shield projected from surface of Endor moon. Protected second Death Star during construction. Only another superlaser could penetrate it while operational.");
        setGameText("Deploy on table.  Unless Rebel Strike Team on table, cancel opponent's Force generation at Endor system.  Twice per game, may take an admiral (except Piett) or general into hand from Reserve Deck; reshuffle.  Whenever opponent's Endor on table, raise your converted Endor system to the top (if possible). (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_3);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.Endor_system, new UnlessCondition(new OnTableCondition(self, Filters.Rebel_Strike_Team)), opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ENDOR_SHIELD__UPLOAD_IMPERIAL_ADMIRAL;

        // Check condition(s)
        if (GameConditions.isTwicePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take admiral or general into hand from Reserve Deck");
            action.setActionMsg("Take an admiral (except Piett) or a general into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new TwicePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Filters.admiral, Filters.except(Filters.Piett)), Filters.general), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();

        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.opponents(playerId), Filters.Endor_system, Filters.canBeConvertedByRaisingYourLocationToTop(playerId)))) {

            PhysicalCard endor = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.opponents(playerId), Filters.Endor_system));
            if (endor != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setPerformingPlayer(playerId);
                action.setText("Raise converted Endor system to the top");
                action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, endor, true));
                actions.add(action);
            }
        }

        return actions;
    }
}