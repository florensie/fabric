/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.content.registry.ShieldRegistryImpl;

/**
 * Registry for defining an item as a shield.
 * Shields should also override serveral {@code use} methods to work properly (see {@link net.minecraft.item.ShieldItem} for reference).
 */
public interface ShieldRegistry {
	ShieldRegistry INSTANCE = ShieldRegistryImpl.INSTANCE;

	/**
	 * @param item the item to define as shield
	 */
	void add(ItemConvertible item);

	/**
	 * @param tag the tag to define as shields
	 */
	void add(Tag<Item> tag);

	/**
	 * @param item the item to remove from the registry
	 */
	void clear(ItemConvertible item);

	/**
	 * @param tag the tag to remove from the registry
	 */
	void clear(Tag<Item> tag);

	boolean isShield(Item item);
}
