package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Flagship
 */
public class Card4_122 extends AbstractNormalEffect {
    public Card4_122() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, Title.Flagship, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("After the Battle of Yavin, it was politically necessary to demonstrate the unstoppable might of the Empire. The Executor and Death Squadron ensured this objective.");
        setGameText("Use 2 Force to deploy on your Star Destroyer. Your other starships may move as a 'react' to same system or sector (for free). If starship about to be lost, you lose X Force, where X = starship's armor. (Immune to your Alter.)");
        addIcons(Icon.DAGOBAH);
        addImmuneToOwnersCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 2));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Star_Destroyer);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationForFreeModifier(self, "Move starship as a 'react' for free", self.getOwner(), Filters.and(Filters.your(self), Filters.starship), Filters.sameSystemOrSectorAs(self, Filters.hasAttached(self))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToBeLostIncludingAllCardsSituation(game, effectResult, Filters.hasAttached(self))
            || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.hasAttached(self))) {
            final PhysicalCard shipToBeLost = self.getAttachedTo();

            if(shipToBeLost != null && shipToBeLost.getBlueprint().hasArmorAttribute()) {
                final float forceToLose = game.getModifiersQuerying().getArmor(game.getGameState(), shipToBeLost);

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Lose " + GuiUtils.formatAsString(forceToLose) + " Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, self.getOwner(), forceToLose));
                return Collections.singletonList(action);
           }
        }
        return null;
    }
}
