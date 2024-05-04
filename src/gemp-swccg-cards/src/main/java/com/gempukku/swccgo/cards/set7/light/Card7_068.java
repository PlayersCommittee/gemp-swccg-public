package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Subtype: Immediate
 * Title: Legendary Starfighter
 */
public class Card7_068 extends AbstractImmediateEffect {
    public Card7_068() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Legendary Starfighter", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.C);
        setLore("The Millennium Falcon's well-known reputation is favorable not only for its captain and first mate, but for the Alliance as well.");
        setGameText("If opponent just lost a starship in a battle you won, deploy on your participating starfighter. Once during each of opponent's move phases, opponent loses 1 Force (2 if starfighter is Falcon or Red 5). Also, that starfighter is power +2 (Immune to Control.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.opponents(self), Filters.starship))
                && GameConditions.isDuringBattleWonBy(game, playerId)) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.and(Filters.your(self),
                    Filters.starfighter, Filters.participatingInBattle), null);
            if (action != null) {
                action.setText("Deploy due to lost " + GameUtils.getFullName(cardLost));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            int forceToLose = Filters.attachedTo(Filters.or(Filters.Falcon, Filters.Red_5)).accepts(game, self) ? 2 : 1;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose " + forceToLose + " Force");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, forceToLose));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        // Check if reached end of control phase and action was not performed yet.
        if (TriggerConditions.isEndOfOpponentsPhase(game, self, effectResult, Phase.MOVE)
                && GameConditions.isOnceDuringOpponentsPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            int forceToLose = Filters.attachedTo(Filters.or(Filters.Falcon, Filters.Red_5)).accepts(game, self) ? 2 : 1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + opponent + " lose " + forceToLose + " Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, forceToLose));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.hasAttached(self), 2));
        return modifiers;
    }
}