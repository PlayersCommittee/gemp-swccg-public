package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.DeployDestinyCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Alien
 * Title: Captain Tarpals
 */
public class Card14_007 extends AbstractAlien {
    public Card14_007() {
        super(Side.LIGHT, 2, 3, 3, 3, 6, Title.Captain_Tarpals, Uniqueness.UNIQUE);
        setLore("Gungan leader of the forces that patrol and protect the city of Otoh Gunga. Consistently placed in the top ten of the Big Nasty Free-For-All.");
        setGameText("Adds 1 to power of your other Gungans at same site. Opponent's characters may not move to underwater sites. Once per turn, if you just drew a Gungan for battle destiny, may deploy that Gungan for free to cancel that destiny and re-draw.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        setSpecies(Species.GUNGAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Gungan, Filters.atSameSite(self)), 1));
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.character), Filters.underwater_site));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.Gungan)
                && GameConditions.isDestinyCardDeployableForFree(game, playerId, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel destiny and cause re-draw");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new DeployDestinyCardEffect(action, true));
            // Perform result(s)
            action.appendEffect(
                    new CancelDestinyAndCauseRedrawEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
