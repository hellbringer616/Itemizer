package com.ylinor.itemizer.data.serializers;

import com.google.common.reflect.TypeToken;
import com.ylinor.itemizer.Itemizer;
import com.ylinor.itemizer.data.access.ItemDAO;
import com.ylinor.itemizer.data.beans.ItemBean;
import com.ylinor.itemizer.data.beans.PoolBean;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.*;

public class PoolSerializer implements TypeSerializer<PoolBean> {

    @Override
    public PoolBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        int id = value.getNode("id").getInt();
        // Pool items
        Map<Double, ItemBean> items = new HashMap<>();
        List<? extends ConfigurationNode> itemList = value.getNode("items").getChildrenList();
        for (ConfigurationNode itemNode : itemList) {
            double probability = itemNode.getNode("probability").getDouble();
            int reference = itemNode.getNode("ref").getInt();
            String itemType = itemNode.getNode("type").getString();
            Optional<ItemBean> item = Optional.empty();
            if (reference > 0) {
                item = ItemDAO.getItem(reference);
            }
            if (!item.isPresent() && itemType != null){
                item = Optional.of(new ItemBean(itemType));
            }
            if (probability > 0 && item.isPresent()) {
                items.put(probability, item.get());
            }
        }
        PoolBean pool = new PoolBean(id, items);
        return pool;
    }

    @Override
    public void serialize(TypeToken<?> type, PoolBean obj, ConfigurationNode value) throws ObjectMappingException {

    }
}