package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.UseWeaponEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyEachDuelDestinyUntilEndOfDuelEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Weapon
 * Subtype: Character
 * Title: Darth Vader's Lightsaber
 */
public class Card9_178 extends AbstractCharacterWeapon {
    public Card9_178() {
        super(Side.DARK, 1, "Darth Vader's Lightsaber", Uniqueness.UNIQUE);
        setLore("Vader's lightsaber. Symbol of the most feared man in the galaxy. Vader's control of the dark side of the Force allows him to wield this weapon in surprising ways.");
        setGameText("Deploy on Vader. May target a character or creature. Draw two destiny. Target hit if total destiny > defense value. Also, during a duel, Vader may 'throw' this lightsaber to add 1 to each of his duel destiny draws (place lightsaber in Used Pile at end of duel).");
        addPersona(Persona.VADERS_LIGHTSABER);
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.LIGHTSABER);
        setMatchingCharacterFilter(Filters.Vader);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Vader);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Vader;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .target(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {

            // Build action using common utility
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(2, Statistic.DEFENSE_VALUE);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isDuelAddOrModifyDuelDestiniesStep(game, effectResult)
                && GameConditions.isDuringDuelWithParticipant(game, Filters.and(Filters.Vader, Filters.hasAttached(self)))
                && GameConditions.canUseWeapon(game, self.getAttachedTo(), self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_1);
            action.setText("'Throw' lightsaber");
            action.setActionMsg("'Throw' " + GameUtils.getCardLink(self) + " to add 1 to each duel destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new UseWeaponEffect(action, self));
            // Pay cost(s)
            action.appendCost(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            // Perform result(s)
            action.appendEffect(
                    new ModifyEachDuelDestinyUntilEndOfDuelEffect(action, playerId, 1));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (self.getWhileInPlayData() != null) {
            if (TriggerConditions.duelEnded(game, effectResult)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.OTHER_CARD_ACTION_2);
                action.setText("Place in Used Pile");
                action.setActionMsg("Place " + GameUtils.getCardLink(self) + " Used Pile");
                // Perform result(s)
                action.appendEffect(
                        new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.LOST_PILE));
                return Collections.singletonList(action);
            }
            if (TriggerConditions.duelCanceled(game, effectResult)) {
                self.setWhileInPlayData(null);
            }
        }
        return null;
    }
}
