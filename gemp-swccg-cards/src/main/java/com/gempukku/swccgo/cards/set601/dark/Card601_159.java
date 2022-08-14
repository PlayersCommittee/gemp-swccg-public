package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Effect
 * Title: Ket Maliss (V)
 */
public class Card601_159 extends AbstractNormalEffect {
    public Card601_159() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ket_Maliss, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Assassins are highly valued by Jabba the Hutt and other gangsters. Prince Xizor's 'shadow killer,' has unknown but undoubtably lethal business in Mos Eisley.");
        setGameText("Deploy on table.  Unless Emperor Palpatine on table, Black Sun agents are defense value +1 and forfeit +2.  Once per turn, may use 1 Force to deploy one Beedo, Dannik Jerriko, Greedo, Hem Dazon, Reegesk, or a docking bay from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.LEGACY_BLOCK_1);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefenseValueModifier(self, Filters.Black_Sun_agent, new NotCondition(new OnTableCondition(self, Filters.title("Emperor Palpatine"))), 1));
        modifiers.add(new ForfeitModifier(self, Filters.Black_Sun_agent, new NotCondition(new OnTableCondition(self, Filters.title("Emperor Palpatine"))), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__KET_MALISS_V__DEPLOY_CARD_FROM_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Beedo, Dannik Jerriko, Greedo, Hem Dazon, Reegesk, or a docking bay from Reserve Deck");

            Filter filter = Filters.or(Filters.title("Beedo"), Filters.persona(Persona.DANNIK), Filters.Greedo, Filters.title("Hem Dazon"), Filters.Reegesk, Filters.docking_bay);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, filter, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}