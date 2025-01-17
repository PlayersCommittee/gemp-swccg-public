package com.gempukku.swccgo.cards.set222.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPersonalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Effect
 * Title: Twilight Is Upon Me (V)
 */
public class Card222_028 extends AbstractNormalEffect {
    public Card222_028() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Twilight Is Upon Me", Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("When a Jedi dies, the spirit spreads through the Force and touches the living.");
        setGameText("If His Destiny on table, deploy on table. " +
                "Your personal Force generation = 2. " +
                "Once per turn, may [download] a mobile docking bay. " +
                "Your total power everywhere is +1 for each [Endor] or [Death Star II] battleground you occupy (limit +2). [Immune to Alter.]");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.title(Title.His_Destiny));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        String playerId = self.getOwner();
        Evaluator battlegroundCount = new MaxLimitEvaluator(new OccupiesEvaluator(playerId, Filters.and(Filters.or(Icon.ENDOR, Icon.DEATH_STAR_II), Filters.battleground)), 2);

        modifiers.add(new ResetPersonalForceGenerationModifier(self, 2, self.getOwner()));
        modifiers.add(new TotalPowerModifier(self, Filters.location, battlegroundCount, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.TWILIGHT_IS_UPON_ME__DOWNLOAD_MOBILE_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy docking bay from Reserve Deck");
            action.setActionMsg("Deploy a mobile docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.mobile_site, Filters.docking_bay), true));
            return Collections.singletonList(action);
        }

        return null;
    }
}