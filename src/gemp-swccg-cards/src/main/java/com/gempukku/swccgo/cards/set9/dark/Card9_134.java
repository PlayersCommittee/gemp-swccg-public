package com.gempukku.swccgo.cards.set9.dark;

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
 * Set: Death Star II
 * Type: Effect
 * Title: Your Destiny
 */
public class Card9_134 extends AbstractNormalEffect {
    public Card9_134() {
        super(Side.DARK, 0, PlayCardZoneOption.ATTACHED, Title.Your_Destiny, Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.C);
        setLore("Luke's destiny lies with his father, Darth Vader. To become a Jedi Knight, Luke must accept this.");
        setGameText("Deploy on Bring Him Before Me. When Vader is present at a battleground site, at start of your turn opponent loses 3 Force unless Luke is captured, out of play, or present at a battleground site. Also, Luke is immune to Responsibility Of Command. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Bring_Him_Before_Me;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        boolean targetsLeiaInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        boolean targetsKananInsteadOfLuke = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);

        if (targetsLeiaInsteadOfLuke) {
            // Check condition(s)
            if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Leia, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Leia)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + opponent + " lose 3 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 3));
                return Collections.singletonList(action);
            }
            return null;
        }
        else if (targetsKananInsteadOfLuke) {
            // Check condition(s)
            if (TriggerConditions.isStartOfYourTurn(game, effectResult, self)
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Kanan, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Kanan)) {

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
                    && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.presentAt(Filters.battleground_site)))
                    && !GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Filters.Luke, Filters.or(Filters.captive, Filters.presentAt(Filters.battleground_site))))
                    && !GameConditions.isOutOfPlay(game, Filters.Luke)) {

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
        Condition targetLeiaInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        Condition targetKananInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);
        Condition targetLuke = new AndCondition(new NotCondition(targetLeiaInsteadOfLuke), new NotCondition(targetKananInsteadOfLuke));

        modifiers.add(new ImmuneToTitleModifier(self, Filters.Leia, targetLeiaInsteadOfLuke, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Luke, targetLuke, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Kanan, targetKananInsteadOfLuke, Title.Responsibility_Of_Command));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition targetLeiaInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_LEIA_INSTEAD_OF_LUKE);
        Condition targetKananInsteadOfLuke = new GameTextModificationCondition(self, ModifyGameTextType.BRING_HIM_BEFORE_ME__TARGETS_KANAN_INSTEAD_OF_LUKE);
        Condition targetLuke = new AndCondition(new NotCondition(targetLeiaInsteadOfLuke), new NotCondition(targetKananInsteadOfLuke));

        modifiers.add(new ImmuneToTitleModifier(self, Filters.Leia, targetLeiaInsteadOfLuke, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Luke, targetLuke, Title.Responsibility_Of_Command));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Luke, targetKananInsteadOfLuke, Title.Responsibility_Of_Command));
        return modifiers;
    }
}