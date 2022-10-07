package com.gempukku.swccgo.cards.set107.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.evaluators.PerTIEEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

import static com.gempukku.swccgo.filters.Filters.Star_Destroyer;

/**
 * Set: Second Anthology
 * Type: Effect
 * Title: Flagship Operations
 */
public class Card107_004 extends AbstractNormalEffect {
    public Card107_004() {
        super(Side.DARK, 5, PlayCardZoneOption.ATTACHED, Title.Flagship_Operations, Uniqueness.UNIQUE);
        setLore("'There's too many of them!'");
        setGameText("Deploy on any Star Destroyer if at least 5 Executor sites on table. Your TIEs are deploy -1, forfeit +2 and destiny +2. At systems where you have a TIE, your Imperial capital starships are deploy -3 and your battle destiny draws are +1 each. (If on Executor, immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Star_Destroyer;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.starship;
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return (GameConditions.canSpotLocation(game, 5, Filters.Executor_site)
                || game.getModifiersQuerying().hasGameTextModification(game.getGameState(), self, ModifyGameTextType.FLAGSHIP_OPERATIONS__MAY_IGNORE_DEPLOYMENT_RESTRICTIONS));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter yourTIEs = Filters.and(Filters.your(self), Filters.TIE);
        Filter systemsWhereYouHaveTIEs = Filters.sameSystemAs(self, yourTIEs);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourTIEs, new PerTIEEvaluator(-1)));
        modifiers.add(new ForfeitModifier(self, yourTIEs, new PerTIEEvaluator(2)));
        modifiers.add(new DestinyModifier(self, yourTIEs, new PerTIEEvaluator(2)));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Imperial_starship,
                Filters.capital_starship), -3, systemsWhereYouHaveTIEs));
        modifiers.add(new EachBattleDestinyModifier(self, systemsWhereYouHaveTIEs, 1, playerId));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new AttachedCondition(self, Filters.Executor), Title.Alter));
        return modifiers;
    }
}