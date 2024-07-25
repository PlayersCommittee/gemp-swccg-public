package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.SetImmunityToAttritionUntilEndOfBattleEffect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Starship
 * Subtype: Starfighter
 * Title: Maul In Scimitar
 */

public class Card223_017 extends AbstractStarfighter {
    public Card223_017() {
        super(Side.DARK, 4, 4, 4, null, 5, 6, 6, "Maul In Scimitar", Uniqueness.UNIQUE, ExpansionSet.SET_23, Rarity.V);
        setLore("Maul's Sith Infiltrator.");
        setGameText("May add 1 pilot. Permanent pilot is •Maul, who provides ability of 6. Once per battle, may lose 1 Force to 'cloak' (either add one destiny to total power or make this starship immune to attrition < 6).");
        addPersona(Persona.MAULS_SITH_INFILTRATOR);
        addIcons(Icon.TRADE_FEDERATION, Icon.SCOMP_LINK, Icon.NAV_COMPUTER, Icon.PILOT, Icon.VIRTUAL_SET_23);
        addModelType(ModelType.SITH_INFILTRATOR);
        setPilotCapacity(1);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(Persona.MAUL, 6) {
        });
        return permanentsAboard;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isDuringBattleWithParticipant(game, self)
                && GameConditions.canCloak(game, self)) {

            if (GameConditions.canAddDestinyDrawsToPower(game, playerId)) {
                TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action1.setText("'cloak' to add one destiny to power");
                action1.appendUsage(
                    new OncePerBattleEffect(action1));
                action1.appendCost(
                    new LoseForceEffect(action1, playerId, 1, true));
                action1.appendEffect(
                    new AddDestinyToTotalPowerEffect(action1, 1));
                actions.add(action1);
            }

            TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action2.setText("'cloak' to add immunity");
            action2.appendUsage(
                new OncePerBattleEffect(action2));
            action2.appendCost(
                new LoseForceEffect(action2, playerId, 1, true));
            action2.appendEffect(
                new SetImmunityToAttritionUntilEndOfBattleEffect(action2, 6, GameUtils.getCardLink(self) + "immune to attrition < 6 till end of battle"));
            actions.add(action2);
            
        }
        return actions;
    }

}
