package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ExplodeProgramTrapEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Effect
 * Title: Program Trap
 */
public class Card2_124 extends AbstractNormalEffect {
    public Card2_124() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Program_Trap, Uniqueness.UNIQUE);
        setLore("Imperial slicers imbed a secret command in a droid's primary performance banks. A predetermined trigger causes a power overload, destroying the droid and anything nearby.");
        setGameText("Use 2 Force to deploy on an opponent's droid (except R2-D2 and C-3PO), 1 on your droid. When either player draws a destiny matching the number of characters at same site, droid 'explodes' (all characters present are lost).");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 2, Filters.and(Filters.opponents(self), Filters.droid)));
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 1, Filters.and(Filters.your(self), Filters.droid)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.and(Filters.opponents(self), Filters.droid, Filters.except(Filters.or(Filters.R2D2, Filters.C3PO))),
                Filters.and(Filters.your(self), Filters.droid));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.droid;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isAtLocation(game, self, Filters.site)) {
            int numCharacters = Filters.countActive(game, self, null, TargetingReason.TO_BE_LOST, Filters.and(Filters.character, Filters.atSameSite(self)));
            if (GameConditions.isDestinyValueEqualTo(game, numCharacters)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make droid 'explode'");
                // Perform result(s)
                action.appendEffect(
                        new ExplodeProgramTrapEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}