package com.dbsoftwares.bungeeutilisals.api.user;

import com.dbsoftwares.bungeeutilisals.api.experimental.inventory.Inventory;

public interface IExperimentalUser {

    /**
     * Opens an inventory.
     * @param inventory The inventory to be opened. If the user already has an open inventory, it will be closed.
     */
    void openInventory(Inventory inventory);

    /**
     * Opens an inventory.
     * @param inventory The inventory to be opened. If the user already has an open inventory, it will be closed.
     * @param resetCursor True if cursor should be in center again, false if it should remain at its current position.
     */
    void openInventory(Inventory inventory, Boolean resetCursor);

    /**
     * @return True if an inventory is opened, false if not.
     */
    Boolean hasOpenInventory();

    /**
     * Closes the currently opened inventory. Cursor will be reset (default behaviour).
     */
    void closeInventory();

    /**
     * Closes the currently opened inventory.
     * @param resetCursor True if cursor should be reset to center, false if not.
     */
    void closeInventory(Boolean resetCursor);
}