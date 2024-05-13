package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: Dismantle On Sight
 */
public class Card5_022 extends AbstractNormalEffect {
    public Card5_022() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Dismantle On Sight", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("After losing control of several extremely powerful and deadly assassin droids, the Empire issued a decree ordering their immediate destruction.");
        setGameText("Use 4 Force to deploy on a droid with armor (free on IG-88). At end of any opponent's turn, droid is immediately lost if present with an Imperial (or with a bounty hunter if droid is IG-88).");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 4, Filters.not(Filters.IG88)));
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.IG88));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.IG88, Filters.and(Filters.droid, Filters.hasArmor));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.or(Filters.IG88, Filters.droid);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard droid = self.getAttachedTo();

        // Check condition(s)
        if (droid != null
                && TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)) {
            boolean isIG88 = Filters.IG88.accepts(game, droid);
            if ((!isIG88 && GameConditions.isPresentWith(game, self, droid, Filters.Imperial))
                    || (isIG88 && GameConditions.isPresentWith(game, self, droid, Filters.bounty_hunter))) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + GameUtils.getFullName(droid) + " lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(droid) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, droid));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}