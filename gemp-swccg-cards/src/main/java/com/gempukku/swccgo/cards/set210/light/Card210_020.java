package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 10
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, The Last Jedi
 */

public class Card210_020 extends AbstractJediMaster {
    public Card210_020() {
        super(Side.LIGHT, 4, 5, 5, 7, 8, "Luke Skywalker, The Last Jedi", Uniqueness.UNIQUE);
        setLore("");
        setGameText("Never deploys to a site opponent occupies. Deploys -2 to Ahch-To. Once per turn, may /\\ Force Projection. During battle, may subtract 2 from an opponent's just drawn destiny. Immune to attrition.");
        addPersona(Persona.LUKE);
        addIcons(Icon.VIRTUAL_SET_10, Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_VII);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(game.getOpponent(self.getOwner())));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NeverDeploysToLocationModifier(self, siteOpponentOccupies));
        modifiers.add(new DeployCostToTargetModifier(self, -2, Filters.or(Filters.Deploys_at_Ahch_To)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LUKE_SKYWALKER_LAST_JEDI__UPLOAD_FORCE_PROJECTION;
        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Force Projection into hand from Reserve Deck");
            action.setActionMsg("Take Force Projection into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Force_Projection, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    // Subtract 2 from destinies in battle...
    // Based on, but slightly modified from, Ascertaining The Truth (political effect, LS, Coruscant)
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, game.getOpponent(playerId))
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Luke))
        {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Subtract 2 from opponent's just drawn destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -2));
            return Collections.singletonList(action);
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

}