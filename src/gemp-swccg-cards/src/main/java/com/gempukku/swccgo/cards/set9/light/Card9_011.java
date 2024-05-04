package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AllAbilityAtLocationProvidedByCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponFiredByModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Rebel
 * Title: Corporal Midge
 */
public class Card9_011 extends AbstractRebel {
    public Card9_011() {
        super(Side.LIGHT, 2, 2, 2, 2, 3, "Corporal Midge", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Self-taught marksman and Scout from a rural region of Chandrila. Inspired by Mon Mothma to join the Alliance. Newly recruited into Madine's commandos as a field technician.");
        setGameText("Adds 1 to each of his character weapon destiny draws (2 when firing a rifle and all your ability here is provided by scouts). While on Endor, adds 1 to your Force drains at your exterior Endor sites where you have a scout of ability > 2 (and no Ewoks).");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition allAbilityProvidedByScouts = new AllAbilityAtLocationProvidedByCondition(self, playerId, Filters.here(self), Filters.scout);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, 1, Filters.not(Filters.rifle), Filters.any));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, new NotCondition(allAbilityProvidedByScouts), 1, Filters.rifle, Filters.any));
        modifiers.add(new EachWeaponDestinyForWeaponFiredByModifier(self, allAbilityProvidedByScouts, 2, Filters.rifle, Filters.any));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.your(self), Filters.exterior_Endor_site,
                Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.scout, Filters.abilityMoreThan(2))),
                Filters.not(Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Ewok)))),
                new OnCondition(self, Title.Endor), 1, playerId));
        return modifiers;
    }
}
