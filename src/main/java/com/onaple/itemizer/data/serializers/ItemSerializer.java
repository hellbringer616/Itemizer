package com.onaple.itemizer.data.serializers;

import com.google.common.reflect.TypeToken;
import com.onaple.itemizer.data.beans.AttributeBean;
import com.onaple.itemizer.data.beans.ItemNbtFactory;
import com.onaple.itemizer.data.beans.ItemBean;
import com.onaple.itemizer.data.beans.ItemEnchant;
import com.onaple.itemizer.service.ItemNBTModule;
import com.onaple.itemizer.service.ItemService;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemSerializer implements TypeSerializer<ItemBean> {

    @Override
    public ItemBean deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {

        // Item characteristics
        String id = value.getNode("id").getString();
        String itemType = value.getNode("type").getString();
        String name = value.getNode("name").getString();
        String lore = value.getNode("lore").getString();
        int durability = value.getNode("durability").getInt();
        boolean unbreakable = value.getNode("unbreakable").getBoolean();

        // Item enchantments
        Map<String, Integer> enchants = new HashMap<>();
        Map<Object, ?> enchantsNode = value.getNode("enchants").getChildrenMap();
        for (Map.Entry<Object, ?> entry : enchantsNode.entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof ConfigurationNode) {
                enchants.put((String) entry.getKey(), ((ConfigurationNode) entry.getValue()).getNode("level").getInt());
            }
        }

        Map<String, Object> nbtList = new HashMap<>();
        for (Map.Entry<Object, ?> entry : value.getNode("nbt").getChildrenMap().entrySet()) {
            if (entry.getKey() instanceof String && entry.getValue() instanceof ConfigurationNode) {
                nbtList.put((String) entry.getKey(), ((ConfigurationNode) entry.getValue()).getValue());
            }
        }

        // IDs of the miners abilities
        List<String> miners = new ArrayList<>();
        List<? extends ConfigurationNode> minerList = value.getNode("miners").getChildrenList();
        for (ConfigurationNode minerNode : minerList) {
            String miner = minerNode.getString();
            if (!miner.equals("")) {
                miners.add(miner);
            }
        }

        /**
         * items = [
         *   {
         *     id: 1,
         *     type: "minecraft:stone_axe",
         *     lore: "This axe is not really efficient...\nHowever it is sharp on your finger.",
         *     unbreakable: true,
         *     enchants {
         *       efficiency {level = 3}
         *     },
         *     miners: [
         *       2
         *     ]
         *     plugin-modules: [
         *         {
         *            key: factoryId
         *            data: {
         *                (data to be handled within the factory implementation)
         *            }
         *          }
         *     ]
         *   },
         */
        List<? extends ConfigurationNode> data = value.getNode("plugin-modules").getChildrenList();
        List<ItemNbtFactory> itemNbtFactories = new ArrayList<>();

        for (ConfigurationNode node : data) {
            String key = node.getChildrenMap().get("key").getValue().toString();
            Optional<ItemNBTModule> optional = ItemService.INSTANCE.getFactoryByKeyId(key);
            if (!optional.isPresent()) {
                throw new IllegalStateException("No plugin registered module having key " + key);
            }
            ItemNBTModule itemNBTModule = optional.get();
            ConfigurationNode data4fct = node.getNode("data");
            ItemNbtFactory build = itemNBTModule.build(data4fct);
            itemNbtFactories.add(build);
        }
        // tool
        String toolType = value.getNode("toolType").getString();
        int toolLevel = value.getNode("toolLevel").getInt();
        List<AttributeBean> attributes = value.getNode("attributes").getList(TypeToken.of(AttributeBean.class));
      //  ItemBean item = new ItemBean(id, itemType, name, lore, durability,
      //          unbreakable, enchants, miners, attributes, nbtList, iItemBeanConfigurations);
     //   item.setToolType(toolType);
      //  item.setToolLevel(toolLevel);
        return null;
    }

    @Override
    public void serialize(TypeToken<?> type, ItemBean obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("id").setValue(obj.getId());
        value.getNode("type").setValue(obj.getType());
        if (obj.getName() != null && !obj.getName().isEmpty()) {
            value.getNode("name").setValue(obj.getName());
        }
        if (obj.getLore() != null && !obj.getLore().isEmpty()) {
            value.getNode("lore").setValue(obj.getLore());
        }

        value.getNode("durability").setValue(obj.getDurability());
        value.getNode("unbreakable").setValue(obj.isUnbreakable());

        if (obj.getToolLevel() != 0) {
            value.getNode("toolLevel").setValue(obj.getToolLevel());
        }
        if (obj.getToolType() != null) {
            value.getNode("toolType").setValue(obj.getToolLevel());
        }

        if (!obj.getEnchants().isEmpty()) {

            Map<String, Map<String, Integer>> enchantList = new HashMap<>();
            for (Map.Entry<String, ItemEnchant> entry : obj.getEnchants().entrySet()) {

                Map<String, Integer> level = new HashMap<String, Integer>();
                level.put("level", entry.getValue().getLevel());
                enchantList.put(entry.getKey(), level);
            }
            value.getNode("enchants").setValue(enchantList);
        }

        final TypeToken<List<AttributeBean>> token = new TypeToken<List<AttributeBean>>() {};

        if (!obj.getAttributeList().isEmpty()) {
            value.getNode("attributes").setValue(token, obj.getAttributeList());
        }

        if (obj.getThirdpartyConfigs() != null && obj.getThirdpartyConfigs().size() > 0) {
            List<ItemNbtFactory> thirdpartyConfigs = obj.getThirdpartyConfigs();

            List<CommentedConfigurationNode> commentedConfigurationNodes = new ArrayList<>();
            for (ItemNbtFactory config : thirdpartyConfigs) {
                CommentedConfigurationNode nodee = config.toNode();
                commentedConfigurationNodes.add(nodee);
            }


            value.getNode("plugin-modules").getList(new TypeToken<CommentedConfigurationNode>() {})
                    .addAll(commentedConfigurationNodes);

        }
    }
}