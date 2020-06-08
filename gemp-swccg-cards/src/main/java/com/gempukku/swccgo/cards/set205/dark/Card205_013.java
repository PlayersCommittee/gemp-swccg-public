package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 5
 * Type: Character
 * Subtype: Alien
 * Title: Hondo Ohnaka
 */
public class Card205_013 extends AbstractAlien {
    public Card205_013() {
        super(Side.DARK, 2, 3, 4, 3, 5, "Hondo Ohnaka", Uniqueness.UNIQUE);
        setLore("Weequay gangster, leader, and pirate.");
        setGameText("Draws one battle destiny if not able to otherwise. While at Audience Chamber (or stacked on your Objective), your other Weequays are deploy -1, power +1, and forfeit +2. Once per game, may [download] Aurra Sing, Slave I, or a Kowakian.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_5);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER, Keyword.PIRATE);
        setSpecies(Species.WEEQUAY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourOtherWeequays = Filters.and(Filters.your(self), Filters.other(self), Filters.Weequay);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, 1));
        modifiers.add(new DeployCostModifier(self, yourOtherWeequays, atAudienceChamber, -1));
        modifiers.add(new PowerModifier(self, yourOtherWeequays, atAudienceChamber, 1));
        modifiers.add(new ForfeitModifier(self, yourOtherWeequays, atAudienceChamber, 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, final PhysicalCard self) {
        Condition stackedOnYourObjective = new StackedOnCondition(self, Filters.and(Filters.your(self), Filters.Objective));
        Filter yourOtherWeequays = Filters.and(Filters.your(self), Filters.other(self), Filters.Weequay);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourOtherWeequays, stackedOnYourObjective, -1));
        modifiers.add(new PowerModifier(self, yourOtherWeequays, stackedOnYourObjective, 1));
        modifiers.add(new ForfeitModifier(self, yourOtherWeequays, stackedOnYourObjective, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.HONDO_OHNAKA__DOWNLOAD_AURRA_SING_SLAVE_I_OR_KOWAKIAN;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy Aurra Sing, Slave I, or a Kowakian from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.Aurra, Filters.Slave_I, Filters.Kowakian), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
