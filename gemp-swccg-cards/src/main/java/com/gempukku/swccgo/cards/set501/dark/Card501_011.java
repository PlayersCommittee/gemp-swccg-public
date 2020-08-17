package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToSystemFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Planetary Rings
 */
public class Card501_011 extends AbstractNormalEffect {
    public Card501_011() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Planetary Rings", Uniqueness.DIAMOND_1);
        setLore("'We're gonna get pulverized if we stay out here much longer.'");
        setGameText("Deploy on a battleground planet system. Adds one [Light Side] icon at your related Big One. If a sector was just drawn for asteroid destiny, cancel and redraw that destiny. Once per turn, may deploy a sector to this system from Reserve Deck; reshuffle. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Planetary Rings");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter yourRelatedBigOne = Filters.and(Filters.your(self.getOwner()), Filters.sameCardId(self.getAttachedTo()), Filters.Big_One);
        modifiers.add(new IconModifier(self, yourRelatedBigOne, Icon.LIGHT_FORCE));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.planet_system, Filters.battleground);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isAsteroidDestinyJustDrawnMatching(game, effectResult, Filters.sector)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel and redraw asteroid destiny");
            action.setActionMsg("Cancel and redraw asteroid destiny");
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.PLANETARY_RINGS__DOWNLOAD_SECTOR;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy a sector here");
                action.setActionMsg("Deploy a sector here");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new DeployCardToSystemFromReserveDeckEffect(action, Filters.sector, self.getAttachedTo().getTitle(), true));
                actions.add(action);

            }
        }

        return actions;
    }
}