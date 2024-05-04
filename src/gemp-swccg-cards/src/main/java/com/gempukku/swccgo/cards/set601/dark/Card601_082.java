package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 6
 * Type: Weapon
 * Subtype: Character
 * Title: Galen's Lightsaber, Vader's Gift
 */
public class Card601_082 extends AbstractCharacterWeapon {
    public Card601_082() {
        super(Side.DARK, 1, "Galen's Lightsaber, Vader's Gift", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Custom-built by Darth Vader. After the Clone Wars, he wielded this weapon while hunting down the last of the Jedi. Cuts through almost anything except another lightsaber blade.");
        setGameText("Deploy on Galen.  Galen may add one destiny to total power or attrition.  May target a character or creature for free.  Draw two destiny.  Target hit, and may not be used to satisfy attrition, if total destiny > defense value.");
        addKeywords(Keyword.LIGHTSABER);
        addIcons(Icon.LEGACY_BLOCK_6);
        addPersona(Persona.GALENS_LIGHTSABER);
        setMatchingCharacterFilter(Filters.Galen);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Galen);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Galen;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        //technically this should let Galen add the destiny, not add it itself, but this is close enough and people will understand how to use this

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Galen, Filters.hasAttached(self)))) {

            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to total power");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToTotalPowerEffect(action, 1));
                actions.add(action);
            }
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Add one destiny to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerBattleEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddDestinyToAttritionEffect(action, 1));
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.creature), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            // Build action using common utility
            FireWeaponAction action = actionBuilder.builderDarkAndLightJediLightSaberV();
            return Collections.singletonList(action);
        }
        return null;
    }

}