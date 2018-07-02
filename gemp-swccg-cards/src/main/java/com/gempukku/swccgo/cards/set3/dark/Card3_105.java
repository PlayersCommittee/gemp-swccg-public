package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Image Of The Dark Lord
 */
public class Card3_105 extends AbstractNormalEffect {
    public Card3_105() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "Image Of The Dark Lord", Uniqueness.UNIQUE);
        setLore("Vader's hologram exacts loyalty from his legions.");
        setGameText("Use 4 Force to deploy at any site if opponent is generating at least 3 Force more than you. At that site, adjacent sites and same site as Vader, players activate Force only if they have presence.");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.HOLOGRAM);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isGeneratingAtLeastXForceMoreThan(game, game.getOpponent(playerId), playerId, 3);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.or(Filters.sameOrAdjacentSite(self), Filters.sameSiteAs(self, Filters.Vader));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new GenerateNoForceModifier(self, Filters.and(filter, Filters.not(Filters.occupies(playerId))), playerId));
        modifiers.add(new GenerateNoForceModifier(self, Filters.and(filter, Filters.not(Filters.occupies(opponent))), opponent));
        return modifiers;
    }
}