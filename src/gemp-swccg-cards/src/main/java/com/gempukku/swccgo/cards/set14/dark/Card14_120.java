package com.gempukku.swccgo.cards.set14.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtSameSiteAsCondition;
import com.gempukku.swccgo.cards.conditions.PilotedCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Vehicle
 * Subtype: Combat
 * Title: AAT Assault Leader
 */
public class Card14_120 extends AbstractCombatVehicle {
    public Card14_120() {
        super(Side.DARK, 3, 4, 6, 5, null, 1, 7, "AAT Assault Leader", Uniqueness.UNIQUE, ExpansionSet.THEED_PALACE, Rarity.R);
        setLore("The AAT commanded by OOM-9 was outfitted with enhanced armor and additional weaponry in order to subdue the Gungan insurrection on the Naboo battle plains. Enclosed.");
        setGameText("May add 1 pilot and 3 passengers. While piloted at same site as another AAT, adds two battle destiny. May deploy Energy Shell Launchers aboard from Reserve Deck; reshuffle. Immune to attrition < 5.");
        addModelType(ModelType.AAT);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
        addKeywords(Keyword.ENCLOSED);
        setPilotCapacity(1);
        setPassengerCapacity(3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotedCondition(self),
                new AtSameSiteAsCondition(self, Filters.and(Filters.other(self), Filters.AAT))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.AAT_ASSAULT_LEADER__DOWNLOAD_ENERGY_SHELL_LAUNCHERS;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Energy_Shell_Launchers)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Energy Shell Launchers aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Energy_Shell_Launchers, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
