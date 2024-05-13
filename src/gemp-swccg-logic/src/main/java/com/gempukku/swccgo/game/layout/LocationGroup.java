package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a part of the location layout where the cards meeting a specified filter deploy.
 */
public class LocationGroup implements Snapshotable<LocationGroup> {

    private String _humanReadable;
    // Filters for locations in this group
    private Filter _filters;
    // Cards in order (left to right)
    private List<List<PhysicalCard>> _cardsInGroup = new LinkedList<List<PhysicalCard>>();

    /**
     * Needed to generate snapshot.
     */
    public LocationGroup() {
    }

    @Override
    public void generateSnapshot(LocationGroup selfSnapshot, SnapshotData snapshotData) {
        LocationGroup snapshot = selfSnapshot;

        // Set each field
        snapshot._humanReadable = _humanReadable;
        snapshot._filters = _filters;
        for (List<PhysicalCard> groupList : _cardsInGroup) {
            List<PhysicalCard> snapShotList = new LinkedList<PhysicalCard>();
            snapshot._cardsInGroup.add(snapShotList);
            for (PhysicalCard card : groupList) {
                snapShotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
    }

    /**
     * Creates a location group for locations accepted by the specified filter.
     * @param humanReadable the name of the group
     * @param filters the filter
     */
    public LocationGroup(String humanReadable, Filter filters) {
        _humanReadable = humanReadable;
        _filters = filters;
    }

    /**
     * Gets the name of the group
     * @return the name
     */
    public String getHumanReadable() {
        return _humanReadable;
    }

    /**
     * Gets the filter for the group.
     * @return the filter
     */
    public Filter getFilters()
    {
        return _filters;
    }

    /**
     * Determines if this group is enabled for cards to deploy to it.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @return true or false
     */
    public boolean isGroupEnabled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return true;
    }

    /**
     * Gets the location zone index of a location in this group. This determines the left-to-right ordering on the table
     * relative to other groups (lower index is left of a higher index).
     * @return the location index for a location in the group.
     */
    public Integer getLocationZoneIndex() {
        List<PhysicalCard> topCards = getTopCardsInGroup();
        if (topCards.isEmpty())
            return null;

        return topCards.get(0).getLocationZoneIndex();
    }


    /**
     * Gets the locations in the group in order (left to right). Within each sub-list, the top location
     * is first.
     * @return the locations
     */
    public List<List<PhysicalCard>> getCardsInGroup() {
        return _cardsInGroup;
    }

    /**
     * Gets the top locations in the group in order (left to right).
     * @return the top locations
     */
    public List<PhysicalCard> getTopCardsInGroup() {
        List<PhysicalCard> topCardsInGroup = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty()) {
                topCardsInGroup.add(locationStack.get(0));
            }
        }
        return topCardsInGroup;
    }

    /**
     * Gets the converted locations in the group in order (left to right).
     * @return the converted locations
     */
    public List<List<PhysicalCard>> getConvertedCardsInGroup() {
        List<List<PhysicalCard>> nonTopCardsInGroup = new LinkedList<List<PhysicalCard>>();
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty()) {
                List<PhysicalCard> nonTopCards = new LinkedList<PhysicalCard>();
                nonTopCards.addAll(locationStack.subList(1, locationStack.size()));
                nonTopCardsInGroup.add(nonTopCards);
            }
        }
        return nonTopCardsInGroup;
    }

    /**
     * Converts (or rebuild) the old location with the new location.
     * @param newLocation the new location
     * @param oldLocation the old location
     */
    public void convertOrRebuildLocation(PhysicalCard newLocation, PhysicalCard oldLocation) {
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            if (!locationStack.isEmpty() && locationStack.get(0).getCardId() == oldLocation.getCardId()) {
                // Set the inverted value of the location to the same at the previous top location
                newLocation.setInverted(locationStack.get(0).isInverted());
                locationStack.add(0, newLocation);
                // The collapsed and inverted value of the new location is set for any converted locations
                for (PhysicalCard locationInStack : locationStack) {
                    locationInStack.setCollapsed(newLocation.isCollapsed());
                    locationInStack.setInverted(newLocation.isInverted());
                }
                return;
            }
        }
    }

    /**
     * Adds the location (not conversion) to the location group in the specified place in the group.
     * @param index the count (from the left) where to insert the location.
     * @param card the location
     */
    public void addLocation(int index, PhysicalCard card) {
        LinkedList<PhysicalCard> cardStack = new LinkedList<PhysicalCard>();
        cardStack.add(card);
        if (index >= _cardsInGroup.size()) {
            _cardsInGroup.add(cardStack);
        }
        else {
            _cardsInGroup.add(index, cardStack);
        }
    }

    /**
     * Removes the location from the group (if it exists).
     * @param location the location
     * @return true if location found and removed, otherwise false
     */
    public boolean removeLocation(PhysicalCard location) {
        for (List<PhysicalCard> locationStack : _cardsInGroup) {
            Iterator<PhysicalCard> iterator = locationStack.iterator();

            while (iterator.hasNext()) {
                PhysicalCard card = iterator.next();
                if (card == location) {
                    iterator.remove();
                    if (locationStack.isEmpty()) {
                        _cardsInGroup.remove(locationStack);
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
