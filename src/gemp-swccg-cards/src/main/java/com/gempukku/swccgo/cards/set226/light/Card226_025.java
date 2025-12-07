package com.gempukku.swccgo.cards.set226.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 26
 * Type: Character
 * Subtype: Alien
 * Title: Peli Motto
 */
public class Card226_025 extends AbstractAlien {
    public Card226_025() {
        super(Side.LIGHT, 2, 2, 1, 3, 4, "Peli Motto", Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("Female mechanic and scavenger.");
        setGameText("While at Starship Graveyard, a docking bay, or on Tatooine, power +3 and may cancel I’ve Lost Artoo! or Lateral Damage (or Restraining Bolt here). During your control phase, if at a battleground with a Jawa, landed starfighter or Grogu, may retrieve 1 Force.");
        addKeywords(Keyword.FEMALE, Keyword.SCAVENGER);
        addIcons(Icon.VIRTUAL_SET_26);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition atSpecificSite = new OrCondition(new AtCondition(self, Filters.or(Filters.Starship_Graveyard, Filters.docking_bay)), new OnCondition(self, Title.Tatooine));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, atSpecificSite, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Cancel I've Lost Artoo!, Lateral Damage, or Restraining Bolt at same location
        if (GameConditions.isAtLocation(game, self, Filters.or(Filters.Starship_Graveyard, Filters.docking_bay))
                || GameConditions.isOnSystem(game, self, Title.Tatooine)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Ive_Lost_Artoo)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Ive_Lost_Artoo, Title.Ive_Lost_Artoo);
                actions.add(action);
            }

            if (GameConditions.canTargetToCancel(game, self, Filters.Lateral_Damage)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Lateral_Damage, Title.Lateral_Damage);
                actions.add(action);
            }

            if (GameConditions.canTargetToCancel(game, self, Filters.and(Filters.Restraining_Bolt, Filters.here(self)))) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.and(Filters.Restraining_Bolt, Filters.here(self)), Title.Restraining_Bolt);
                actions.add(action);
            }
        }

        // Retrieve 1 force
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAtLocation(game, self, Filters.battleground)
                && GameConditions.isWith(game, self, Filters.or(Filters.Jawa, Filters.and(Filters.landed, Filters.starfighter), Filters.Grogu))) {
            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action1.setText("Retrieve 1 Force");
            // Update usage limit(s)
            action1.appendUsage(
                    new OncePerPhaseEffect(action1));
            // Perform result(s)
            action1.appendEffect(
                    new RetrieveForceEffect(action1, playerId, 1));
            actions.add(action1);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        // Cancel I've Lost Artoo!, Lateral Damage, or Restraining Bolt at same location
        if (GameConditions.isAtLocation(game, self, Filters.or(Filters.Starship_Graveyard, Filters.docking_bay))
                || GameConditions.isOnSystem(game, self, Title.Tatooine)) {
            if (TriggerConditions.isPlayingCard(game, effect, Filters.Ive_Lost_Artoo)
                    && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                actions.add(action);
            }

            if (TriggerConditions.isPlayingCard(game, effect, Filters.Lateral_Damage)
                    && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                actions.add(action);
            }

            if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Restraining_Bolt, Filters.here(self)))
                    && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                actions.add(action);
            }
        }

        return actions;
    }
}
