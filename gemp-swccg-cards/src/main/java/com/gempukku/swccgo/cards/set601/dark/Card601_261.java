package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 9
 * Type: Effect
 * Title: Ominous Rumors (V)
 */
public class Card601_261 extends AbstractNormalEffect {
    public Card601_261() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Ominous_Rumors, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Rumors of a new 'technological terror' filled the galaxy with dread.");
        setGameText("Deploy on Bunker. Your scouts aboard speeder bikes are defense value = 5 and immune to Clash of Sabers. Once per turn, if you control Bunker, may deploy an [Endor] Imperial here (except Fenson or Grond) or a forest from Reserve Deck; reshuffle. Canceled if opponent controls at least three Endor Sites. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.ENDOR, Icon.LEGACY_BLOCK_9);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bunker;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter scoutsAboardSpeederBikers = Filters.and(Filters.your(self), Filters.scout, Filters.aboard(Filters.speeder_bike));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetDefenseValueModifier(self, scoutsAboardSpeederBikers, 5));
        modifiers.add(new ImmuneToTitleModifier(self, scoutsAboardSpeederBikers, Title.Clash_Of_Sabers));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__OMINOUS_RUMORS_V__DEPLOY_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.controls(game, playerId, Filters.Bunker)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy an [Endor] Imperial to Bunker (or deploy a forest) from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.or(Filters.forest, Filters.and(Icon.ENDOR, Filters.Imperial, Filters.not(Filters.title("Navy Trooper Fenson")), Filters.not(Filters.Grond))), Filters.locationAndCardsAtLocation(Filters.hasAttached(self)), Filters.forest, null, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && GameConditions.controls(game, opponent, 3, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Endor_site)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}