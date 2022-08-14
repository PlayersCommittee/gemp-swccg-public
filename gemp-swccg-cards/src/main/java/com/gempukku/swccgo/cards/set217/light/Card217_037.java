package com.gempukku.swccgo.cards.set217.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Effect
 * Title: Hero Of A Thousand Devices (V)
 */
public class Card217_037 extends AbstractNormalEffect {
    public Card217_037() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Hero_Of_A_Thousand_Devices, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Left to his own devices, Artoo used his spunk and creativity to save his companions' lives time and time again.");
        setGameText("Deploy on table. Once per game, may [download] an R-unit droid. During a battle where your droid present with a Scomp link, your total power is +1 (+3 if your other droid present with a Scomp link at a related location). [Immune to Alter.]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_17);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new TotalPowerModifier(self, Filters.battleLocation,
                new InBattleCondition(self, Filters.and(Filters.your(self), Filters.droid, Filters.at_Scomp_Link)),
                new ConditionEvaluator(1, 3, new OnTableCondition(self, Filters.and(Filters.at(Filters.relatedLocationTo(self, Filters.battleLocation)), Filters.your(self), Filters.droid, Filters.at_Scomp_Link))),
                self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.HERO_OF_A_THOUSAND_DEVICES_V__DEPLOY_DROID;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy an R-unit droid from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.R_unit, Filters.droid), true));
            actions.add(action);
        }
        return actions;
    }
}