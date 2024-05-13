package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToLimitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Visage Of The Emperor (V)
 */
public class Card213_016 extends AbstractNormalEffect {
    public Card213_016() {
        super(Side.DARK, 7, PlayCardZoneOption.ATTACHED, Title.Visage_Of_The_Emperor, Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("Palpatine's hologram. Imposing. Ominous. Intimidating. Instrument for the evil Emperor's sinister reach across the galaxy. Used on a secret frequency of the Imperial HoloNet.");
        setGameText("Lose 2 Force to deploy on Vader's Castle. Your Force generation here may not be limited. Each player loses 1 Force at the end of their own turn. Once per game, may [upload] a lightsaber. [Immune to Alter].");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.HOLOGRAM);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected StandardEffect getGameTextSpecialDeployCostEffect(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard target, PlayCardOption playCardOption) {
        return new LoseForceEffect(action, playerId, 2, true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title(Title.Vaders_Castle);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceGenerationImmuneToLimitModifier(self, Filters.hasAttached(self), Filters.opponents(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.VISAGE__UPLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a lightsaber into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action)
            );
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.lightsaber, true)
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();
        String playerId = self.getOwner();
        // Check condition(s)

        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, game.getOpponent(playerId), 1));
            actions.add(action);
        }
        if (TriggerConditions.isEndOfYourTurn(game, effectResult, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("You lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, playerId, 1));
            actions.add(action);
        }
        return actions;
    }
}