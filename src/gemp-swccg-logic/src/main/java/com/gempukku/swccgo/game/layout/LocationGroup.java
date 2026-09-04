package com.gempukku.swccgo.game.layout;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;

import java.util.ArrayList;
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

    /**
     * Reorders the location stacks in this group. Each stack stays together, so
     * converted locations remain under the same top site. newOrder is a
     * permutation of 0..size-1 describing the new left-to-right order of the
     * current stacks.
     * @param newOrder permutation of current stack indexes
     * @return true if the stacks were reordered; false if newOrder is not a valid permutation
     */
    public boolean reorderStacks(List<Integer> newOrder) {
        int size = _cardsInGroup.size();
        if (newOrder == null || newOrder.size() != size) {
            return false;
        }
        boolean[] seen = new boolean[size];
        for (Integer index : newOrder) {
            if (index == null || index < 0 || index >= size || seen[index]) {
                return false;
            }
            seen[index] = true;
        }
        List<List<PhysicalCard>> reordered = new LinkedList<List<PhysicalCard>>();
        for (Integer index : newOrder) {
            reordered.add(_cardsInGroup.get(index));
        }
        _cardsInGroup.clear();
        _cardsInGroup.addAll(reordered);
        return true;
    }

    /**
     * Reorders stacks so the given top locations appear in that left-to-right
     * order. Converted cards stay in the same stack under the same top. If
     * newTopOrder is a subset of this group's tops, only those stacks swap
     * among the slots they currently occupy; other stacks stay put. An empty
     * order does nothing.
     * @param newTopOrder requested left-to-right order of top location cards
     * @return true if applied or already matched; false if a card is not a top in this group or the list has duplicates
     */
    public boolean reorderTopLocations(List<? extends PhysicalCard> newTopOrder) {
        if (newTopOrder == null || newTopOrder.isEmpty()) {
            return true;
        }
        List<Integer> permutation = permutationForTopOrder(newTopOrder);
        if (permutation == null) {
            return false;
        }
        return reorderStacks(permutation);
    }

    /**
     * What the top row would look like after reorderTopLocations, without changing
     * the group. Returns null if the order is not valid.
     * @param newTopOrder requested left-to-right order of top location cards
     * @return the resulting top cards, or null if the order is invalid
     */
    public List<PhysicalCard> previewTopLocations(List<? extends PhysicalCard> newTopOrder) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        if (newTopOrder == null || newTopOrder.isEmpty()) {
            return new ArrayList<PhysicalCard>(tops);
        }
        List<Integer> permutation = permutationForTopOrder(newTopOrder);
        if (permutation == null) {
            return null;
        }
        List<PhysicalCard> preview = new ArrayList<PhysicalCard>();
        for (Integer index : permutation) {
            preview.add(tops.get(index));
        }
        return preview;
    }

    private List<Integer> permutationForTopOrder(List<? extends PhysicalCard> newTopOrder) {
        List<PhysicalCard> tops = getTopCardsInGroup();
        if (hasDuplicateTops(newTopOrder)) {
            return null;
        }
        for (PhysicalCard card : newTopOrder) {
            if (indexOfTop(tops, card) < 0) {
                return null;
            }
        }
        List<Integer> selectedSlots = new ArrayList<Integer>();
        for (int i = 0; i < tops.size(); ++i) {
            if (containsTop(newTopOrder, tops.get(i))) {
                selectedSlots.add(i);
            }
        }
        if (selectedSlots.size() != newTopOrder.size()) {
            return null;
        }
        List<Integer> permutation = new ArrayList<Integer>();
        for (int i = 0; i < tops.size(); ++i) {
            permutation.add(i);
        }
        for (int i = 0; i < selectedSlots.size(); ++i) {
            permutation.set(selectedSlots.get(i), indexOfTop(tops, newTopOrder.get(i)));
        }
        return permutation;
    }

    private int indexOfTop(List<PhysicalCard> tops, PhysicalCard card) {
        for (int i = 0; i < tops.size(); ++i) {
            if (tops.get(i).getCardId() == card.getCardId()) {
                return i;
            }
        }
        return -1;
    }

    private boolean containsTop(List<? extends PhysicalCard> cards, PhysicalCard card) {
        return indexOfTop(new LinkedList<PhysicalCard>(cards), card) >= 0;
    }

    private boolean hasDuplicateTops(List<? extends PhysicalCard> cards) {
        for (int i = 0; i < cards.size(); ++i) {
            for (int j = i + 1; j < cards.size(); ++j) {
                if (cards.get(i).getCardId() == cards.get(j).getCardId()) {
                    return true;
                }
            }
        }
        return false;
    }
}