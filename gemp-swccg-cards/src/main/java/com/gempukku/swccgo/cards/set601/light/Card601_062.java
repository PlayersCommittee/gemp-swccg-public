package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DefendingBattleCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceFromLifeForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Great Warrior
 */
public class Card601_062 extends AbstractJediMaster {
    public Card601_062() {
        super(Side.LIGHT, 1, 5, 3, 7, 7, "Yoda, Great Warrior", Uniqueness.UNIQUE);
        setLore("'You must feel the Force around you. Here, between you, me, the tree, the rock, everywhere! Yes, even between the land and the ship.'");
        setGameText("Deploys only to battlegrounds. Power +3 when defending a battle. You may not deploy [Episode I] Jedi. While with a Wookiee, adds one battle destiny. Once per game, may lose 2 Force from Force Pile to cancel a non-[Immune to Sense] Interrupt. Immune to attrition.");
        addIcons(Icon.WARRIOR, Icon.BLOCK_5, Icon.SPECIAL_EDITION);
        addPersona(Persona.YODA);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new DefendingBattleCondition(self), 3));
        modifiers.add(new MayNotDeployModifier(self, Filters.and(Icon.EPISODE_I, Filters.Jedi), self.getOwner()));
        modifiers.add(new AddsBattleDestinyModifier(self, new WithCondition(self, Filters.Wookiee), 1));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__YODA_GREAT_WARRIOR__CANCEL_INTERRUPT;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.numCardsInForcePile(game, playerId)>=2) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                        // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerGameEffect(action));
            //TODO need to make LoseForceFromForcePileEffect
            action.appendCost(new LoseForceFromLifeForceEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
