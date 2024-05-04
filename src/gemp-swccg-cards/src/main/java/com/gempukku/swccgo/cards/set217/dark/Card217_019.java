package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.VariableMultiplierModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * SubType: Alien
 * Title: Qi'ra, Top Lieutenant
 */
public class Card217_019 extends AbstractAlien {
    public Card217_019() {
        super(Side.DARK, 2, 3, 3, 4, 4, "Qi'ra, Top Lieutenant", Uniqueness.UNIQUE, ExpansionSet.SET_17, Rarity.V);
        setLore("Female Crimson Dawn leader. Corellian gangster.");
        setGameText("When forfeited at same location as Han or Vos, may satisfy all remaining battle damage against you. Unless opponent occupies a battleground site, doubles X on Secret Plans and cancels It Could Be Worse.");
        addPersona(Persona.QIRA);
        setSpecies(Species.CORELLIAN);
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.GANGSTER, Keyword.CRIMSON_DAWN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 5;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new VariableMultiplierModifier(self, Filters.Secret_Plans, new UnlessCondition(new OccupiesCondition(game.getOpponent(self.getOwner()), Filters.battleground_site)),2, Variable.X));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        // Check condition(s)
        if (!GameConditions.occupies(game, game.getOpponent(self.getOwner()), Filters.battleground_site)
                && TriggerConditions.isPlayingCard(game, effect, Filters.It_Could_Be_Worse)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (!GameConditions.occupies(game, opponent, Filters.battleground_site)
                && TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.It_Could_Be_Worse)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.It_Could_Be_Worse, Title.It_Could_Be_Worse);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canForfeitToSatisfyBattleDamage(game, playerId, self)
                && GameConditions.isInBattleWith(game, self, Filters.or(Filters.Han, Filters.Vos))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Forfeit to satisfy all battle damage");
            // Pay cost(s)
            action.appendCost(
                    new ForfeitCardFromTableEffect(action, self));
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new SatisfyAllBattleDamageEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
