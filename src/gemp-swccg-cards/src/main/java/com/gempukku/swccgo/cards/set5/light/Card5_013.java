package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Device
 * Title: Cyborg Construct
 */
public class Card5_013 extends AbstractCharacterDevice {
    public Card5_013() {
        super(Side.LIGHT, 4, Title.Cyborg_Construct, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("Biotech's latest model, the Aj^g, boasts greater storage capacity than all other models combined. Advertised as, 'Artificial intelligence worth shaving your head for.'");
        setGameText("Deploy on an alien of ability < 3. Each turn, you may 'store' one card from hand face-down here. Holds up to three cards (six on Lobot). You may play or deploy cards from here as if from hand. Place stored cards in Used Pile if device lost or removed from character.");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.alien, Filters.abilityLessThan(3));
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.alien, Filters.abilityLessThan(3));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseDevice(game, self)
                && GameConditions.hasHand(game, playerId)
                && !atCapacity(game, self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("'Store' a card from hand");
            action.setActionMsg("'Store' a card from hand face-down on " + GameUtils.getCardLink(self));
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StackCardFromHandEffect(action, playerId, self, Filters.any, true, false, false, false));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();
        String playerId = self.getOwner();

        // Place stored cards in Used Pile if device lost or removed from character (including transfer)
        if ((TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                || TriggerConditions.justTransferredDeviceOrWeaponToTarget(game, effectResult, self, Filters.any))
                && GameConditions.hasStackedCards(game, self)) {

            Collection<PhysicalCard> stackedCards = game.getGameState().getStackedCards(self);
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place stored cards in Used Pile");
            action.setActionMsg("Place cards stored on " + GameUtils.getCardLink(self) + " in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutStackedCardsInUsedPileEffect(action, playerId, stackedCards, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        // Reuse Bargaining Table deploy-as-if-from-hand; thin CardVisitor also treats this flag for play (incl. interrupts)
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.stackedOn(self)));
        return modifiers;
    }

    private boolean atCapacity(SwccgGame game, PhysicalCard self) {
        int capacity = Filters.Lobot.accepts(game, self.getAttachedTo()) ? 6 : 3;
        return GameConditions.hasStackedCards(game, self, capacity);
    }
}
