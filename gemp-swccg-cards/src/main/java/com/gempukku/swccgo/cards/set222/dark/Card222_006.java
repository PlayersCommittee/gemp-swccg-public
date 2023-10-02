package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AboardAsPassengerCondition;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 22
 * Type: Character
 * Subtype: Alien
 * Title: Danz Borin (V)
 */
public class Card222_006 extends AbstractAlien {
    public Card222_006() {
        super(Side.DARK, 3, 3, 2, 2, 4, Title.Danz_Borin, Uniqueness.UNIQUE, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("Cocky gunner and bounty hunter. Maintains a residence on Nar Shaddaa, the spaceport moon of the Hutt homeworld. To his companions' delight, he's nearly as good as he boasts.");
        setGameText("[Pilot] 3. While a passenger at Nal Hutta or Nar Shaddaa, your battle and weapon destiny draws (and Force drains) are +1 here. " +
                "During your turn, opponent may use 3 Force to cancel Borin's game text until end of turn.");
        addIcons(Icon.A_NEW_HOPE, Icon.PILOT, Icon.VIRTUAL_SET_22);
        addKeywords(Keyword.GUNNER, Keyword.BOUNTY_HUNTER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter narShaddaaOrNalHutta = Filters.or(Filters.Nar_Shadda_system, Filters.Nal_Hutta_system);
        AboardAsPassengerCondition aboardAsPassengerCondition = new AboardAsPassengerCondition(self, Filters.any);
        AtCondition atCondition = new AtCondition(self, narShaddaaOrNalHutta);
        AndCondition aboardAsPassengerAtNarShaddaOrNallHuttaCondition = new AndCondition(aboardAsPassengerCondition, atCondition);
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new EachBattleDestinyModifier(self, narShaddaaOrNalHutta, aboardAsPassengerAtNarShaddaOrNallHuttaCondition, 1, self.getOwner()));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, aboardAsPassengerAtNarShaddaOrNallHuttaCondition, Filters.hasPassenger(self), 1));
        modifiers.add(new ForceDrainModifier(self, narShaddaaOrNalHutta, aboardAsPassengerAtNarShaddaOrNallHuttaCondition, 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isDuringYourTurn(game, opponent)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Game Text");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self) + "'s game text until end of turn");
            // Perform result(s)
            action.appendEffect(
                    new CancelGameTextUntilEndOfTurnEffect(action, self));
            return Collections.singletonList(action);

        }
        return null;
    }
}
