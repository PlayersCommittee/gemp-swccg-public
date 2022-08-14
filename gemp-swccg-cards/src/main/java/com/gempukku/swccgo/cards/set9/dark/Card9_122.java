package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.MinLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.ThereEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalMovementDestinyModifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Effect
 * Title: Desperate Counter
 */
public class Card9_122 extends AbstractNormalEffect {
    public Card9_122() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, "Desperate Counter", Uniqueness.UNIQUE);
        setLore("The Empire relied on the skill of its pilot corps to defend the Death Star reactor core from attack.");
        setGameText("Deploy on Death Star II system. When opponent moves from a related sector, for each TIE armed with a weapon there, movement destiny is -3 (limit -9). Also, you may deploy either Combat Response or Imperial Arrest Order from Reserve Deck; reshuffle. (Immune to Alter.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Death_Star_II_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalMovementDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.at(Filters.relatedSector(self))),
                new MinLimitEvaluator(new MultiplyEvaluator(-3, new ThereEvaluator(self, Filters.and(Filters.TIE, Filters.armedWith(Filters.weapon)))), -9)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.DESPERATE_COUNTER__DOWNLOAD_COMBAT_RESPONSE_OR_IMPERIAL_ARREST_ORDER;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Arrays.asList(Title.Combat_Response, Title.Imperial_Arrest_Order))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Combat Response or Imperial Arrest Order from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Combat_Response, Filters.Imperial_Arrest_Order), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}