package com.onaple.itemizer.data.access;

import com.onaple.itemizer.Itemizer;
import com.onaple.itemizer.data.beans.ItemBean;
import com.onaple.itemizer.data.handlers.ConfigurationHandler;

import java.util.List;
import java.util.Optional;

public class ItemDAO {
    /**
     * Call the configuration handler to retrieve an item from an id
     * @param id ID of the item
     * @return Optional of the item data
     */
    public static Optional<ItemBean> getItem(String id) {
        List<ItemBean> items = Itemizer.getConfigurationHandler().getItemList();
        for(ItemBean item: items) {
            if (item.getId().equals(id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
