package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: Watto's Box
 */
public class Card11_075 extends AbstractNormalEffect {
    public Card11_075() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Wattos_Box, Uniqueness.UNIQUE);
        setLore("Fortunes are won and lost every day on the Outer Rim. Watto is a master at getting his cut.");
        setGameText("Deploy on table if all of your race totals = 0. When a player wins a Podrace, adds 5 to the amount retrieved. When a player loses a Podrace, adds 5 to the amount lost. (Immune to Alter.)");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.WATTOS_BOX__MAY_DEPLOY_REGARDLESS_OF_RACE_TOTAL)) {
            return true;
        }
        return (modifiersQuerying.getHighestRaceTotal(gameState, playerId) == 0);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PodraceForceRetrievalModifier(self, 5));
        modifiers.add(new PodraceForceLossModifier(self, 5));
        return modifiers;
    }
}