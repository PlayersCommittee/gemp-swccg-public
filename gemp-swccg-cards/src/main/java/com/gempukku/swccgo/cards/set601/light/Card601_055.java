package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Character
 * Subtype: Republic
 * Title: Padme Naberrie (V)
 */
public class Card601_055 extends AbstractRepublic {
    public Card601_055() {
        super(Side.LIGHT, 3, 4, 3, 4, 6, Title.Padme, Uniqueness.UNIQUE);
        setLore("Queen Amidala posed as one of her own handmaidens for added safety as well as to keep an eye on her Jedi protectors. Was to be protected by the Jedi at all times.");
        setGameText("Adds 1 to power of anything she pilots.  During your control phase, if present at a battleground site, opponent loses 1 Force for each Skywalker occupying a battleground (-1 Force if Vader on table) and you may use 2 Force to take one Leia or non-Jedi Luke into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_5, Icon.TATOOINE, Icon.EPISODE_I);
        addPersona(Persona.AMIDALA);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {
            Collection<PhysicalCard> skywalkers = Filters.filterAllOnTable(game, Filters.Skywalker);
            int numForce = 0;
            for (PhysicalCard skywalker: skywalkers) {
                if (GameConditions.occupiesWith(game, self, playerId, Filters.battleground, Filters.and(skywalker)))
                    numForce++;
            }

            if (GameConditions.canSpot(game, self, Filters.Vader))
                numForce--;

            if (numForce > 0) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make opponent lose " + numForce + " Force");
                action.appendUsage(new OncePerPhaseEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                actions.add(action);
            }

        }

        GameTextActionId gameTextActionId2 = GameTextActionId.LEGACY__PADME__UPLOAD_LEIA_OR_LUKE;
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId2, Phase.CONTROL)
            && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId2)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId2);
            action.setText("Take Leia or Luke into hand from Reserve Deck");
            action.setActionMsg("Take Leia or non-Jedi Luke into hand from Reserve Deck");
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Leia, Filters.and(Filters.not(Filters.Jedi), Filters.Luke)), true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        // Check if reached end of each control phase and action was not performed yet.
        if (GameConditions.isPresentAt(game, self, Filters.battleground_site)
                && TriggerConditions.isEndOfYourPhase(game, effectResult, Phase.CONTROL, playerId)
                && GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)) {

            Collection<PhysicalCard> skywalkers = Filters.filterAllOnTable(game, Filters.Skywalker);
            int numForce = 0;
            for (PhysicalCard skywalker: skywalkers) {
                if (GameConditions.occupiesWith(game, self, playerId, Filters.battleground, Filters.and(skywalker)))
                    numForce++;
            }

            if (GameConditions.canSpot(game, self, Filters.Vader))
                numForce--;

            if (numForce > 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setPerformingPlayer(playerId);
                action.setText("Make opponent lose " + numForce + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, numForce));
                actions.add(action);
            }
        }

        return actions;
    }
}