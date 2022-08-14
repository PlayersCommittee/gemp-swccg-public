package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Rendili StarDrive
 */
public class Card7_238 extends AbstractNormalEffect {
    public Card7_238() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "Rendili StarDrive", Uniqueness.UNIQUE);
        setLore("Responsible for early Imperial space supremacy. Rendili designs provide extremely stable weapons platforms for capital ship weapons.");
        setGameText("Deploy on Rendili system. Your Victory-class Star Destroyers are deploy -2 here. Each of your Turbolaser Batteries deploys free, fires for free and adds 1 to each of its weapon destiny draws. (Immune to Alter while you occupy Rendili.)");
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Rendili_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourTurbolaserBatteries = Filters.and(Filters.your(self), Filters.turbolaser_battery);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Victory_class_Star_Destroyer), -2, Filters.here(self)));
        modifiers.add(new DeploysFreeModifier(self, yourTurbolaserBatteries));
        modifiers.add(new FiresForFreeModifier(self, yourTurbolaserBatteries));
        modifiers.add(new EachWeaponDestinyModifier(self, yourTurbolaserBatteries, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, new OccupiesCondition(playerId, Filters.Rendili_system), Title.Alter));
        return modifiers;
    }
}