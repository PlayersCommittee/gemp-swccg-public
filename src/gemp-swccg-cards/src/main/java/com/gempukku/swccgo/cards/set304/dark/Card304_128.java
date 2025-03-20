package com.gempukku.swccgo.cards.set304.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Effect
 * Title: Your Destiny
 */
public class Card304_128 extends AbstractNormalEffect {
    public Card304_128() {
        super(Side.DARK, 0, PlayCardZoneOption.ATTACHED, Title.Complications, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("Kamjin's daring plan to be captured and root out the Clan Tiure threat succeeded. However, he now risks being usurped by his own Proconsul.");
        setGameText("Deploy on Hostile Takeover. When Kamjin is present at a battleground site, at start of your turn opponent loses 3 Force unless Locita is captured, out of play, or present at a battleground site. Also, Locita is immune to Responsibility Of Command. (Immune to Alter.)");
        addIcons(Icon.GREAT_HUTT_EXPANSION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Hostile_Takeover;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        boolean targetsKaiInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        boolean targetsHikaruInsteadOfLocita = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);

        if (targetsKaiInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Kamjin, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Kai, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Kai)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose 3 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
                return Collections.singletonList(action);
            }
            return null;
        }
        else if (targetsHikaruInsteadOfLocita) {
            // Check condition(s)
            if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Kamjin, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Hikaru, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Hikaru)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose 3 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
                return Collections.singletonList(action);
            }
            return null;
        }
        else {
            // Check condition(s)
            if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Kamjin, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Locita, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Locita)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose 3 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
                return Collections.singletonList(action);
            }
            return null;
        }
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition targetKaiInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        Condition targetHikaruInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);
        Condition targetLocita = new AndCondition(new NotCondition(targetKaiInsteadOfLocita), new NotCondition(targetHikaruInsteadOfLocita));

        modifiers.add(new ImmuneToTitleModifier(self, Filters.Kai, targetKaiInsteadOfLocita, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Locita, targetLocita, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Hikaru, targetHikaruInsteadOfLocita, Title.Responsibility_Of_Command));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition targetKaiInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KAI_INSTEAD_OF_LOCITA);
        Condition targetHikaruInsteadOfLocita = new GameTextModificationCondition(self, ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_HIKARU_INSTEAD_OF_LOCITA);
        Condition targetLocita = new AndCondition(new NotCondition(targetKaiInsteadOfLocita), new NotCondition(targetHikaruInsteadOfLocita));

        modifiers.add(new ImmuneToTitleModifier(self, Filters.Kai, targetKaiInsteadOfLocita, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Locita, targetLocita, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Hikaru, targetHikaruInsteadOfLocita, Title.Responsibility_Of_Command));
        return modifiers;
    }
}