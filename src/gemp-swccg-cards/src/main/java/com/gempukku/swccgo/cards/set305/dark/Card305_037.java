package com.gempukku.swccgo.cards.set305.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
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
 * Set: A Better Tomorrow
 * Type: Vehicle
 * Subtype: Combat
 * Title: NR-N99 Assault Leader
 */
public class Card305_037 extends AbstractCombatVehicle {
    public Card305_037() {
        super(Side.DARK, 3, 4, 6, 5, null, 2, 7, "NR-N99 Assault Leader", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.R);
        setLore("NR-N99 HTD-530 was a veteran of the Clone Wars. Following the war it was smuggled to a variety of different private armies to continue fighting.");
        setGameText("May carry 4 battle droids. While piloted at same site as another NR-N99, adds two battle destiny. May deploy AAT Laser Cannon aboard from Reserve Deck; reshuffle. Immune to attrition < 5.");
        addModelType(ModelType.NRN99);
        addIcons(Icon.ABT, Icon.PILOT, Icon.PRESENCE);
        setVehicleCapacity(4, Filters.battle_droid);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsBattleDestinyModifier(self, new AndCondition(new PilotedCondition(self),
                new AtSameSiteAsCondition(self, Filters.and(Filters.other(self), Filters.NRN99))), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.NRN99_ASSAULT_LEADER__DOWNLOAD_AAT_LASER_CANNON;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.AAT_Laser_Cannon)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy AAT Laser Cannon aboard " + GameUtils.getCardLink(self) + " from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.AAT_Laser_Cannon, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
