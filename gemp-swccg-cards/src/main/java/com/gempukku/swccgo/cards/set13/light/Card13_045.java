package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInPlayEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardAndAttachFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.MayUseWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.StolenResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Effect
 * Subtype: Immediate
 * Title: Weapon Of A Fallen Mentor
 */
public class Card13_045 extends AbstractImmediateEffect {
    public Card13_045() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, "Weapon Of A Fallen Mentor", Uniqueness.UNIQUE, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Darth Maul's complacency in what seemed like imminent victory over the Padawan was his downfall. That, and the lightsaber of Obi-Wan's fallen teacher.");
        setGameText("If your non-[Permanent Weapon] lightsaber was just lost (or stolen) from a site, deploy on your character of ability > 4 at same site. Relocate that lightsaber to this character, who may use that lightsaber, and it may not be stolen. (Immune to Control.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter lightsaberFilter = Filters.and(Filters.lightsaber, Filters.not(Icon.PERMANENT_WEAPON));

        // Check condition(s)
        PhysicalCard lightsaber = null;
        PhysicalCard site = null;
        String actionText = null;
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(self), lightsaberFilter), Filters.site)) {
            LostFromTableResult lostFromTableResult = (LostFromTableResult) effectResult;
            lightsaber = lostFromTableResult.getCard();
            site = lostFromTableResult.getFromLocation();
            actionText = "Deploy due to lost " + GameUtils.getFullName(lightsaber);
        }
        else if (TriggerConditions.justStolenFromLocation(game, effectResult, opponent, lightsaberFilter, Filters.site)) {
            StolenResult stolenResult = (StolenResult) effectResult;
            lightsaber = stolenResult.getStolenCard();
            site = stolenResult.getStolenFromLocation();
            actionText = "Deploy due to stolen " + GameUtils.getFullName(lightsaber);
        }
        if (lightsaber != null && site != null) {
            Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(4), Filters.at(site));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DEPLOYED_ON, characterFilter)) {

                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, characterFilter, null);
                if (action != null) {
                    action.setText(actionText);
                    // Remember the lightsaber
                    action.appendBeforeCost(
                            new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(lightsaber)));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter characterFilter = Filters.hasAttached(self);
        Filter lightsaberFilter = Filters.and(Filters.isInCardInPlayData(self), Filters.attachedTo(characterFilter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayUseWeaponModifier(self, characterFilter, lightsaberFilter));
        modifiers.add(new MayNotBeStolenModifier(self, lightsaberFilter));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard lightsaber = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        PhysicalCard character = self.getAttachedTo();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setPerformingPlayer(self.getOwner());
            action.setText("Relocate " + GameUtils.getFullName(lightsaber) + " to " + GameUtils.getFullName(character));
            action.setActionMsg("Relocate " + GameUtils.getCardLink(lightsaber) + " to " + GameUtils.getCardLink(character));
            if (Filters.in_play.accepts(game, lightsaber)) {
                action.appendEffect(
                        new StealCardAndAttachFromTableEffect(action, lightsaber, character));
            }
            else {
                action.appendEffect(
                        new PlaceCardInPlayEffect(action, lightsaber, character, false, false));
            }
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public boolean isInactiveInsteadOfActive(final SwccgGame game, final PhysicalCard self) {
        PhysicalCard lightsaber = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        return lightsaber != null && Filters.in_play.accepts(game, lightsaber) && !game.getGameState().isCardInPlayActive(lightsaber);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard lightsaber = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;

        // Check condition(s)
        if (TriggerConditions.leavesTable(game, effectResult, lightsaber)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make lost");
            action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
            // Perform result(s)
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}