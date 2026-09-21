package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Velken Tezeri
 */
public class Card6_128 extends AbstractAlien {
    public Card6_128() {
        super(Side.DARK, 3, 3, 2, 1, 2, "Velken Tezeri", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Assigned by Jabba to work with Hermi Odle. Former technician for the Empire. Developed a method to remotely control seekers. Plotting to kill Jabba.");
        setGameText("When at a site you control, allows your Seekers to deploy free there. Also allows your Seekers to move for free and to ignore any or all potential target(s) whenever you choose.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition atSiteYouControl = new AtCondition(self, Filters.and(Filters.site, Filters.controls(playerId)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationModifier(self, Filters.and(Filters.your(self), Filters.seeker),
                atSiteYouControl, Filters.sameSite(self)));
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.your(self), Filters.seeker),
                atSiteYouControl, Filters.sameSite(self)));
        modifiers.add(new MovesForFreeModifier(self, Filters.and(Filters.your(self), Filters.seeker), atSiteYouControl));
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.SEEKERS_MAY_IGNORE_TARGETS, playerId));
        return modifiers;
    }
}
